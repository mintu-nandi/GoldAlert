import Foundation
import SharedLogic
import UserNotifications

// MARK: - Koin Bootstrap

final class KoinHelper {
    static let shared = KoinHelper()
    private init() {}

    private var kotlinKoinHelper: SharedLogic.KoinHelper?

    func start() {
        InitKoinKt.doInitKoinIos { title, body in
            let content = UNMutableNotificationContent()
            content.title = title
            content.body = body
            content.sound = .default
            let req = UNNotificationRequest(
                identifier: UUID().uuidString,
                content: content,
                trigger: nil
            )
            UNUserNotificationCenter.current().add(req, withCompletionHandler: nil)
        }
        
        // Initialize the Kotlin KoinHelper helper class
        self.kotlinKoinHelper = SharedLogic.KoinHelper()
    }
    
    func getDashboardViewModel() -> DashboardViewModel {
        return kotlinKoinHelper!.getDashboardViewModel()
    }
    
    func getThresholdListViewModel() -> ThresholdListViewModel {
        return kotlinKoinHelper!.getThresholdListViewModel()
    }
    
    func getCandleChartViewModel() -> CandleChartViewModel {
        return kotlinKoinHelper!.getCandleChartViewModel()
    }
}
