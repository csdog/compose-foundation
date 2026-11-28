import XCTest

final class ChartUITests: XCTestCase {
    @MainActor
    func testLegendSeriesAndPlotSelectionShareAnAccessibleSummary() {
        verifyPlotSelection(with: [])
    }

    @MainActor
    func testTooltipKeepsPlotSelectionInteractive() {
        verifyPlotSelection(with: ["--chart-tooltip"])
    }

    @MainActor
    private func verifyPlotSelection(with options: [String]) {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launchArguments = ["--chart-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"] + options
        app.launch()

        func chart(labeled label: String) -> XCUIElement {
            app.descendants(matching: .any).matching(NSPredicate(format: "label == %@", label)).firstMatch
        }

        let initial = chart(labeled: "Chart, 6 data points, Desktop, Mobile")
        XCTAssertTrue(initial.waitForExistence(timeout: 5))
        initial.coordinate(withNormalizedOffset: CGVector(dx: 0.60, dy: 0.25)).tap()

        let april = chart(labeled: "April, Desktop: 73, Mobile: 190")
        XCTAssertTrue(april.waitForExistence(timeout: 5))
        april.coordinate(withNormalizedOffset: CGVector(dx: 0.25, dy: 0.25)).tap()
        let february = chart(labeled: "February, Desktop: 305, Mobile: 200")
        XCTAssertTrue(february.waitForExistence(timeout: 5))

        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = options.isEmpty ? "February chart selection and legend" : "February chart tooltip"
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
