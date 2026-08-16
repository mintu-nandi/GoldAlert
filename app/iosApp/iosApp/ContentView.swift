import SwiftUI
import SharedLogic

struct ContentView: View {
    @StateObject private var vm = DashboardViewModelWrapper()
    @State private var showAddThreshold = false
    @State private var showChart = false

    private let priceFormatter: NumberFormatter = {
        let f = NumberFormatter()
        f.numberStyle = .decimal
        f.minimumFractionDigits = 2
        f.maximumFractionDigits = 2
        return f
    }()

    var body: some View {
        NavigationStack {
            ZStack {
                Color(hex: "0F0F11").ignoresSafeArea()

                VStack(spacing: 0) {
                    // MARK: - Custom Header
                    HStack {
                        VStack(alignment: .leading, spacing: 4) {
                            Text("GOLD SPOT ALERT")
                                .font(.system(size: 22, weight: .black))
                                .foregroundColor(.white)
                            Text("Live Feed Connected")
                                .font(.system(size: 12, weight: .semibold))
                                .foregroundColor(Color(hex: "26A69A"))
                        }
                        
                        Spacer()
                        
                        HStack(spacing: 12) {
                            // Chart Button
                            Button {
                                showChart = true
                            } label: {
                                Image(systemName: "chart.bar.fill")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(Color(hex: "FFD700"))
                                    .frame(width: 40, height: 40)
                                    .background(Color(hex: "1B1B1F"))
                                    .clipShape(Circle())
                                    .overlay(
                                        Circle().stroke(Color.white.opacity(0.15), lineWidth: 1)
                                    )
                            }
                            
                            // Refresh Button
                            Button {
                                vm.refresh()
                            } label: {
                                Image(systemName: "arrow.clockwise")
                                    .font(.system(size: 16, weight: .bold))
                                    .foregroundColor(Color(hex: "FFD700"))
                                    .rotationEffect(.degrees(vm.isLoading ? 360 : 0))
                                    .animation(vm.isLoading ? .linear(duration: 1).repeatForever(autoreverses: false) : .default, value: vm.isLoading)
                                    .frame(width: 40, height: 40)
                                    .background(Color(hex: "1B1B1F"))
                                    .clipShape(Circle())
                                    .overlay(
                                        Circle().stroke(Color.white.opacity(0.15), lineWidth: 1)
                                    )
                            }
                        }
                    }
                    .padding(.horizontal)
                    .padding(.top, 16)
                    .padding(.bottom, 20)

                    ScrollView {
                        VStack(spacing: 24) {
                            // MARK: - Price Card
                            VStack(spacing: 8) {
                                Text("LIVE PRICE PER OUNCE")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(Color.white.opacity(0.4))
                                    .frame(maxWidth: .infinity, alignment: .center)
                                
                                if let price = vm.pricePerOunce {
                                    let formattedPrice = priceFormatter.string(from: NSNumber(value: price)) ?? ""
                                    Text("$\(formattedPrice)")
                                        .font(.system(size: 38, weight: .black, design: .rounded))
                                        .foregroundColor(Color(hex: "FFD700"))
                                } else {
                                    Text("Loading...")
                                        .font(.system(size: 28, weight: .bold))
                                        .foregroundColor(Color.white.opacity(0.6))
                                }
                                
                                if let ts = vm.timestamp {
                                    HStack(spacing: 6) {
                                        Circle()
                                            .fill(Color(hex: "26A69A"))
                                            .frame(width: 6, height: 6)
                                        Text("Live • Price updated on • \(formatTime(ts))")
                                            .font(.system(size: 11, weight: .medium))
                                            .foregroundColor(Color.white.opacity(0.4))
                                    }
                                }
                            }
                            .padding(.vertical, 24)
                            .padding(.horizontal, 16)
                            .background(Color(hex: "1B1B1F"))
                            .cornerRadius(16)
                            .overlay(
                                RoundedRectangle(cornerRadius: 16)
                                    .stroke(Color(hex: "FFD700").opacity(0.2), lineWidth: 1)
                            )
                            .padding(.horizontal)

                            // MARK: - Active Alerts Section
                            VStack(alignment: .leading, spacing: 16) {
                                Text("ACTIVE ALERTS")
                                    .font(.system(size: 11, weight: .bold))
                                    .foregroundColor(Color.white.opacity(0.4))
                                    .padding(.horizontal)
                                
                                if vm.thresholds.isEmpty {
                                    Text("No alerts set. Tap + to add one.")
                                        .font(.system(size: 14, weight: .medium))
                                        .foregroundColor(Color.white.opacity(0.3))
                                        .padding(.horizontal)
                                } else {
                                    ForEach(vm.thresholds, id: \.id) { t in
                                        AlertCardRow(
                                            threshold: t,
                                            priceFormatter: priceFormatter,
                                            onToggle: { vm.toggleThreshold(t) },
                                            onDelete: { vm.deleteThreshold(t) }
                                        )
                                    }
                                }
                            }
                            
                            Spacer(minLength: 100)
                        }
                    }
                }

                // MARK: - Floating Add Button (FAB)
                VStack {
                    Spacer()
                    HStack {
                        Spacer()
                        Button {
                            showAddThreshold = true
                        } label: {
                            Image(systemName: "plus")
                                .font(.system(size: 24, weight: .bold))
                                .foregroundColor(.black)
                                .frame(width: 56, height: 56)
                                .background(Color(hex: "FFD700"))
                                .clipShape(Circle())
                                .shadow(color: Color.black.opacity(0.3), radius: 6, x: 0, y: 3)
                        }
                        .padding(.trailing, 20)
                        .padding(.bottom, 20)
                    }
                }
            }
            .navigationBarHidden(true)
            .navigationDestination(isPresented: $showChart) {
                CandleChartView()
            }
            .sheet(isPresented: $showAddThreshold) {
                AddThresholdSheet { price, direction, label in
                    vm.addThreshold(price: price, direction: direction, label: label)
                }
            }
        }
        .preferredColorScheme(.dark)
    }

    private func formatTime(_ ts: Int64) -> String {
        let date = Date(timeIntervalSince1970: TimeInterval(ts) / 1000)
        let formatter = DateFormatter()
        formatter.dateFormat = "hh:mm:ss a"
        return formatter.string(from: date)
    }
}

// MARK: - Alert Card Row

struct AlertCardRow: View {
    let threshold: ThresholdEntity
    let priceFormatter: NumberFormatter
    let onToggle: () -> Void
    let onDelete: () -> Void

    var isAbove: Bool { threshold.direction == "ABOVE" }

    var body: some View {
        HStack(spacing: 16) {
            VStack(alignment: .leading, spacing: 8) {
                // Action badge
                HStack(spacing: 4) {
                    Text(isAbove ? "CROSSES ABOVE" : "DROPS BELOW")
                        .font(.system(size: 9, weight: .bold))
                        .foregroundColor(isAbove ? Color(hex: "26A69A") : Color(hex: "EF5350"))
                    
                    if !threshold.label.isEmpty {
                        Text("• \(threshold.label)")
                            .font(.system(size: 9, weight: .bold))
                            .foregroundColor(Color.white.opacity(0.4))
                    }
                }
                
                let formattedPrice = priceFormatter.string(from: NSNumber(value: threshold.targetPrice)) ?? ""
                Text("$\(formattedPrice)")
                    .font(.system(size: 22, weight: .bold, design: .rounded))
                    .foregroundColor(.white)
            }
            
            Spacer()
            
            // Switch toggle
            Toggle("", isOn: Binding(
                get: { threshold.isActive },
                set: { _ in onToggle() }
            ))
            .labelsHidden()
            .tint(Color(hex: "FFD700"))
            
            // Delete button
            Button(action: onDelete) {
                Image(systemName: "trash.fill")
                    .font(.system(size: 16))
                    .foregroundColor(Color(hex: "EF5350"))
                    .frame(width: 36, height: 36)
                    .background(Color.white.opacity(0.05))
                    .clipShape(Circle())
            }
        }
        .padding(.vertical, 16)
        .padding(.horizontal, 16)
        .background(Color(hex: "1B1B1F"))
        .cornerRadius(12)
        .padding(.horizontal)
    }
}

// MARK: - Add Threshold Sheet

struct AddThresholdSheet: View {
    let onAdd: (Double, String, String) -> Void
    @Environment(\.dismiss) private var dismiss

    @State private var priceText = ""
    @State private var direction = "ABOVE"
    @State private var label = ""

    var body: some View {
        NavigationStack {
            ZStack {
                Color(hex: "0F0F11").ignoresSafeArea()
                VStack(spacing: 24) {
                    VStack(alignment: .leading, spacing: 8) {
                        Text("Target Price (USD)")
                            .font(.caption)
                            .foregroundColor(Color(hex: "8E8E93"))
                        TextField("e.g. 3500.00", text: $priceText)
                            .keyboardType(.decimalPad)
                            .padding(12)
                            .background(Color(hex: "1B1B1F"))
                            .cornerRadius(10)
                            .foregroundColor(.white)
                    }

                    Picker("Direction", selection: $direction) {
                        Text("Above").tag("ABOVE")
                        Text("Below").tag("BELOW")
                    }
                    .pickerStyle(.segmented)
                    .background(Color(hex: "1B1B1F"))
                    .cornerRadius(10)

                    VStack(alignment: .leading, spacing: 8) {
                        Text("Label (optional)")
                            .font(.caption)
                            .foregroundColor(Color(hex: "8E8E93"))
                        TextField("e.g. Sell signal", text: $label)
                            .padding(12)
                            .background(Color(hex: "1B1B1F"))
                            .cornerRadius(10)
                            .foregroundColor(.white)
                    }

                    Button {
                        guard let price = Double(priceText.replacingOccurrences(of: ",", with: ".")) else { return }
                        onAdd(price, direction, label)
                        dismiss()
                    } label: {
                        Text("Add Alert")
                            .fontWeight(.bold)
                            .frame(maxWidth: .infinity)
                            .padding(.vertical, 16)
                            .background(Color(hex: "FFD700"))
                            .foregroundColor(.black)
                            .cornerRadius(14)
                    }
                    .disabled(priceText.isEmpty)

                    Spacer()
                }
                .padding(24)
            }
            .navigationTitle("New Price Alert")
            .navigationBarTitleDisplayMode(.inline)
            .toolbar {
                ToolbarItem(placement: .cancellationAction) {
                    Button("Cancel") { dismiss() }
                        .foregroundColor(Color(hex: "FFD700"))
                }
            }
        }
    }
}

// MARK: - Color Extension

extension Color {
    init(hex: String) {
        let scanner = Scanner(string: hex)
        var value: UInt64 = 0
        scanner.scanHexInt64(&value)
        let r = Double((value >> 16) & 0xFF) / 255
        let g = Double((value >> 8) & 0xFF) / 255
        let b = Double(value & 0xFF) / 255
        self.init(red: r, green: g, blue: b)
    }
}