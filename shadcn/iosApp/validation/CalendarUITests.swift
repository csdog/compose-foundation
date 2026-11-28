import XCTest

final class CalendarUITests: XCTestCase {
    @MainActor
    func testMonthAndYearMenusKeepTheRangeAndRespectBounds() {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launchArguments = ["--calendar-navigation", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
        app.launch()

        let january = app.buttons["Month for January 2025"]
        let januaryYear = app.buttons["Year for January 2025"]
        let previous = app.buttons["Previous month"]
        let initialRange = app.staticTexts["Range: 2025-01-20 to 2025-01-20"]
        XCTAssertTrue(january.waitForExistence(timeout: 5))
        XCTAssertEqual(january.value as? String, "Jan")
        XCTAssertFalse(previous.isEnabled)

        january.tap()
        let februaryOption = app.buttons["Feb"]
        XCTAssertTrue(februaryOption.waitForExistence(timeout: 5))
        XCTAssertTrue(app.buttons["Jan"].isSelected)
        attachMenu("Calendar month menu")
        februaryOption.tap()
        XCTAssertTrue(app.buttons["Month for February 2025"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.buttons["Month for March 2025"].exists)
        XCTAssertTrue(initialRange.exists)

        previous.tap()
        XCTAssertTrue(januaryYear.waitForExistence(timeout: 5))
        januaryYear.tap()
        let nextYear = app.buttons["2026"]
        XCTAssertTrue(nextYear.waitForExistence(timeout: 5))
        XCTAssertFalse(app.buttons["2027"].exists)
        XCTAssertTrue(app.buttons["2025"].isSelected)
        attachMenu("Calendar year menu")
        nextYear.tap()
        let nextJanuary = app.buttons["Month for January 2026"]
        XCTAssertTrue(nextJanuary.waitForExistence(timeout: 5))
        XCTAssertTrue(initialRange.exists)

        nextJanuary.tap()
        let march = app.buttons["Mar"]
        XCTAssertTrue(march.waitForExistence(timeout: 5))
        XCTAssertFalse(app.buttons["Apr"].exists)
        march.tap()
        XCTAssertTrue(app.buttons["Month for March 2026"].waitForExistence(timeout: 5))
        XCTAssertTrue(app.buttons["Month for February 2026"].exists)
        XCTAssertFalse(app.buttons["Next month"].isEnabled)
        XCTAssertTrue(initialRange.exists)
    }

    @MainActor
    private func attachMenu(_ name: String) {
        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
