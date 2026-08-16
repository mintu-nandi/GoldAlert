import SwiftUI
import SharedLogic
import UserNotifications
import BackgroundTasks

@main
struct iOSApp: App {
    @Environment(\.scenePhase) private var scenePhase

    init() {
        // Request notification permission for price alerts
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { _, _ in }

        // Boot the shared KMP Koin dependency graph
        KoinHelper.shared.start()
        
        registerBackgroundTasks()
    }

    var body: some Scene {
        WindowGroup {
            RootView()
        }
        .onChange(of: scenePhase) { newPhase in
            if newPhase == .background {
                scheduleAppRefresh()
            }
        }
    }

    private func registerBackgroundTasks() {
        BGTaskScheduler.shared.register(forTaskWithIdentifier: "com.bullish.goldalert.refresh", using: nil) { task in
            self.handleAppRefresh(task: task as! BGAppRefreshTask)
        }
    }

    private func scheduleAppRefresh() {
        let request = BGAppRefreshTaskRequest(identifier: "com.bullish.goldalert.refresh")
        // Request execution in 5 minutes
        request.earliestBeginDate = Date(timeIntervalSinceNow: 5 * 60)
        do {
            try BGTaskScheduler.shared.submit(request)
            print("🟢 iOS: Successfully scheduled next background refresh task")
        } catch {
            print("🔴 iOS: Could not schedule background refresh task: \(error)")
        }
    }

    private func handleAppRefresh(task: BGAppRefreshTask) {
        // Schedule next check immediately
        scheduleAppRefresh()

        let queue = DispatchQueue.global(qos: .background)
        task.expirationHandler = {
            queue.async {
                task.setTaskCompleted(success: false)
            }
        }

        queue.async {
            // Trigger price sync and evaluate alert thresholds
            let dashVM = KoinHelper.shared.getDashboardViewModel()
            dashVM.refreshPrice()
            
            // Allow some time for network request completion
            DispatchQueue.main.asyncAfter(deadline: .now() + 5) {
                print("🟢 iOS: Completed background sync task execution")
                task.setTaskCompleted(success: true)
            }
        }
    }
}