import Foundation
import SharedLogic

@MainActor
final class CandleChartViewModelWrapper: ObservableObject {

    @Published var candles: [CandleEntry] = []
    @Published var selectedTimeframe: SharedLogic.ChartTimeframe = SharedLogic.ChartTimeframe.m15

    private let vm: CandleChartViewModel
    
    private var cancelCandles: (() -> Void)?
    private var cancelTimeframe: (() -> Void)?

    init() {
        vm = KoinHelper.shared.getCandleChartViewModel()
        startObserving()
    }

    private func startObserving() {
        let cancelCandlesBlock = vm.watchCandles { [weak self] list in
            print("🟢 iOS: watchCandles emitted \(list.count) candles")
            DispatchQueue.main.async {
                self?.candles = list
            }
        }
        cancelCandles = { cancelCandlesBlock() }

        let cancelTimeframeBlock = vm.watchSelectedTimeframe { [weak self] tf in
            print("🟢 iOS: watchSelectedTimeframe emitted \(tf.label)")
            DispatchQueue.main.async {
                self?.selectedTimeframe = tf
            }
        }
        cancelTimeframe = { cancelTimeframeBlock() }
    }

    func selectTimeframe(_ tf: SharedLogic.ChartTimeframe) {
        vm.setTimeframe(tf: tf)
        selectedTimeframe = tf
    }

    deinit {
        cancelCandles?()
        cancelTimeframe?()
    }
}
