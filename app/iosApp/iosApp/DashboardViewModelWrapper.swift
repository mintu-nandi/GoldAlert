import Foundation
import Combine
import SharedLogic

@MainActor
final class DashboardViewModelWrapper: ObservableObject {

    @Published var pricePerOunce: Double? = nil
    @Published var currency: String = "USD"
    @Published var timestamp: Int64? = nil
    @Published var isLoading: Bool = false
    @Published var errorMessage: String? = nil
    @Published var thresholds: [ThresholdEntity] = []

    private let dashVM: DashboardViewModel
    private let thresholdVM: ThresholdListViewModel
    
    private var cancelPrice: (() -> Void)?
    private var cancelSync: (() -> Void)?
    private var cancelThresholds: (() -> Void)?

    init() {
        dashVM = KoinHelper.shared.getDashboardViewModel()
        thresholdVM = KoinHelper.shared.getThresholdListViewModel()
        startObserving()
    }

    private func startObserving() {
        // Observe latest price
        let cancelPriceBlock = dashVM.watchLatestPrice { [weak self] entity in
            print("🟢 iOS: watchLatestPrice emitted \(String(describing: entity?.pricePerOunce))")
            DispatchQueue.main.async {
                self?.pricePerOunce = entity?.pricePerOunce
                self?.currency = entity?.currency ?? "USD"
                self?.timestamp = entity?.timestamp
            }
        }
        cancelPrice = { cancelPriceBlock() }

        // Observe sync state
        let cancelSyncBlock = dashVM.watchSyncState { [weak self] state in
            print("🟢 iOS: watchSyncState emitted \(String(describing: state))")
            DispatchQueue.main.async {
                switch state {
                case is SyncStateLoading:
                    self?.isLoading = true
                    self?.errorMessage = nil
                case is SyncStateSuccess:
                    self?.isLoading = false
                case let err as SyncStateError:
                    self?.isLoading = false
                    self?.errorMessage = err.message
                default:
                    self?.isLoading = false
                }
            }
        }
        cancelSync = { cancelSyncBlock() }

        // Observe thresholds
        let cancelThresholdsBlock = thresholdVM.watchThresholds { [weak self] list in
            print("🟢 iOS: watchThresholds emitted \(list.count) alerts")
            DispatchQueue.main.async {
                self?.thresholds = list
            }
        }
        cancelThresholds = { cancelThresholdsBlock() }
    }

    func refresh() { dashVM.refreshPrice() }

    func addThreshold(price: Double, direction: String, label: String) {
        thresholdVM.addThreshold(price: price, direction: direction, label: label)
    }

    func toggleThreshold(_ t: ThresholdEntity) {
        thresholdVM.toggleThreshold(threshold: t)
    }

    func deleteThreshold(_ t: ThresholdEntity) {
        thresholdVM.deleteThreshold(threshold: t)
    }

    deinit {
        cancelPrice?()
        cancelSync?()
        cancelThresholds?()
    }
}
