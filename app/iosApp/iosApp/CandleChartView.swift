import SwiftUI
import Charts
import SharedLogic

struct CandleChartView: View {
    @StateObject private var vm = CandleChartViewModelWrapper()

    private let timeframes: [SharedLogic.ChartTimeframe] = [
        SharedLogic.ChartTimeframe.m1, SharedLogic.ChartTimeframe.m5, SharedLogic.ChartTimeframe.m15, SharedLogic.ChartTimeframe.m30,
        SharedLogic.ChartTimeframe.h1, SharedLogic.ChartTimeframe.h4,
        SharedLogic.ChartTimeframe.d1, SharedLogic.ChartTimeframe.w1,
        SharedLogic.ChartTimeframe.mo1, SharedLogic.ChartTimeframe.mo3, SharedLogic.ChartTimeframe.mo6,
        SharedLogic.ChartTimeframe.ytd, SharedLogic.ChartTimeframe.y1, SharedLogic.ChartTimeframe.y5, SharedLogic.ChartTimeframe.all
    ]

    var body: some View {
        ZStack {
            Color(hex: "0F0F11").ignoresSafeArea()

            VStack(spacing: 0) {
                // MARK: - Timeframe Selector
                ScrollView(.horizontal, showsIndicators: false) {
                    HStack(spacing: 6) {
                        ForEach(timeframes, id: \.self) { tf in
                            TimeframeButton(
                                label: tf.label,
                                isSelected: vm.selectedTimeframe == tf
                            ) {
                                vm.selectTimeframe(tf)
                            }
                        }
                    }
                    .padding(.horizontal, 16)
                    .padding(.vertical, 10)
                }
                .background(Color(hex: "13131A"))

                if vm.candles.isEmpty {
                    Spacer()
                    ProgressView()
                        .tint(Color(hex: "FFD700"))
                    Spacer()
                } else {
                    // MARK: - Candle Chart
                    CandleStickChart(candles: vm.candles, timeframe: vm.selectedTimeframe)
                        .padding(.horizontal, 8)
                        .padding(.top, 12)

                    // MARK: - Stats Row
                    if let first = vm.candles.first, let last = vm.candles.last {
                        StatsRow(first: first, last: last)
                            .padding(.horizontal, 16)
                            .padding(.bottom, 16)
                    }
                }
            }
        }
        .navigationTitle("Gold Chart")
        .navigationBarTitleDisplayMode(.inline)
    }
}

// MARK: - Candlestick Chart

struct CandleStickChart: View {
    let candles: [CandleEntry]
    let timeframe: SharedLogic.ChartTimeframe

    var body: some View {
        Chart {
            ForEach(candles.indices, id: \.self) { i in
                let c = candles[i]
                let date = Date(timeIntervalSince1970: TimeInterval(c.timestamp) / 1000)
                let isBull = c.close >= c.open

                // High-Low wick
                BarMark(
                    x: .value("Time", date),
                    yStart: .value("Low", c.low),
                    yEnd: .value("High", c.high),
                    width: .fixed(1)
                )
                .foregroundStyle(isBull ? Color(hex: "26A69A") : Color(hex: "EF5350"))

                // Body
                BarMark(
                    x: .value("Time", date),
                    yStart: .value("Open", min(c.open, c.close)),
                    yEnd: .value("Close", max(c.open, c.close)),
                    width: candleBodyWidth
                )
                .foregroundStyle(isBull ? Color(hex: "26A69A") : Color(hex: "EF5350"))
            }
        }
        .chartXAxis {
            AxisMarks(values: .stride(by: xStride)) { val in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.3))
                    .foregroundStyle(Color.white.opacity(0.1))
                AxisValueLabel(format: xFormat)
                    .foregroundStyle(Color(hex: "8E8E93"))
                    .font(.system(size: 10))
            }
        }
        .chartYAxis {
            AxisMarks { val in
                AxisGridLine(stroke: StrokeStyle(lineWidth: 0.3, dash: [4]))
                    .foregroundStyle(Color.white.opacity(0.1))
                AxisValueLabel {
                    if let v = val.as(Double.self) {
                        Text(String(format: "$%.0f", v))
                            .font(.system(size: 10))
                            .foregroundColor(Color(hex: "8E8E93"))
                    }
                }
            }
        }
        .chartBackground { _ in Color(hex: "0F0F11") }
        .frame(height: 320)
    }

    private var candleBodyWidth: MarkDimension {
        let count = candles.count
        return count > 60 ? .fixed(2) : count > 30 ? .fixed(4) : .fixed(7)
    }

    private var xStride: Calendar.Component {
        switch timeframe.name {
        case "M1", "M5": return .hour
        case "M15", "M30": return .hour
        case "H1", "H4": return .day
        case "D1", "W1": return .weekOfYear
        default: return .month
        }
    }

    private var xFormat: Date.FormatStyle {
        switch timeframe.name {
        case "M1", "M5", "M15", "M30": return .dateTime.hour().minute()
        case "H1", "H4": return .dateTime.month().day()
        case "D1", "W1", "YTD": return .dateTime.month().day()
        default: return .dateTime.year().month()
        }
    }
}

// MARK: - Stats Row

struct StatsRow: View {
    let first: CandleEntry
    let last: CandleEntry

    var change: Double { last.close - first.open }
    var changePct: Double {
        guard first.open != 0 else { return 0 }
        return (change / first.open) * 100
    }
    var isPositive: Bool { change >= 0 }

    var body: some View {
        HStack {
            StatBadge(label: "Open", value: String(format: "$%.2f", first.open))
            Spacer()
            StatBadge(label: "Close", value: String(format: "$%.2f", last.close))
            Spacer()
            VStack(spacing: 2) {
                Text(String(format: "%+.2f%%", changePct))
                    .font(.subheadline)
                    .fontWeight(.bold)
                    .foregroundColor(isPositive ? Color(hex: "26A69A") : Color(hex: "EF5350"))
                Text("Change")
                    .font(.caption2)
                    .foregroundColor(Color(hex: "8E8E93"))
            }
        }
        .padding(14)
        .background(Color(hex: "1B1B1F"))
        .cornerRadius(14)
    }
}

struct StatBadge: View {
    let label: String
    let value: String
    var body: some View {
        VStack(spacing: 2) {
            Text(value)
                .font(.subheadline)
                .fontWeight(.semibold)
                .foregroundColor(Color(hex: "F5F5F7"))
            Text(label)
                .font(.caption2)
                .foregroundColor(Color(hex: "8E8E93"))
        }
    }
}

// MARK: - Timeframe Button

struct TimeframeButton: View {
    let label: String
    let isSelected: Bool
    let action: () -> Void

    var body: some View {
        Button(action: action) {
            Text(label)
                .font(.system(size: 12, weight: isSelected ? .bold : .regular))
                .foregroundColor(isSelected ? .black : Color(hex: "8E8E93"))
                .padding(.horizontal, 10)
                .padding(.vertical, 6)
                .background(isSelected ? Color(hex: "FFD700") : Color(hex: "1B1B1F"))
                .cornerRadius(8)
        }
    }
}
