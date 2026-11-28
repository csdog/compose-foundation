import XCTest

final class CommandUITests: XCTestCase {
    @MainActor
    func testSearchAndTouchActivation() {
        let app = launchFixture()
        let field = app.textFields["Search commands"]
        let calculator = app.buttons["Calculator"]
        let billing = app.buttons["Billing"]
        let result = app.staticTexts["command-result"]

        XCTAssertTrue(calculator.waitForExistence(timeout: 5))
        XCTAssertFalse(calculator.isEnabled)
        field.tap()
        field.typeText("zzz")
        XCTAssertTrue(app.staticTexts["No matching commands"].waitForExistence(timeout: 5))
        XCTAssertFalse(billing.exists)
        XCTAssertEqual(result.label, "No action")

        field.typeText(String(repeating: XCUIKeyboardKey.delete.rawValue, count: 3) + "bi")
        XCTAssertTrue(billing.waitForExistence(timeout: 5))
        XCTAssertFalse(calculator.exists)
        billing.tap()
        XCTAssertEqual(result.label, "Billing")
    }

    @MainActor
    func testKeyboardSkipsDisabledAndCrossesGroup() {
        let app = launchFixture()
        let field = app.textFields["Search commands"]
        field.tap()
        field.typeKey(.downArrow, modifierFlags: [])
        field.typeKey(.downArrow, modifierFlags: [])
        field.typeKey(.return, modifierFlags: [])
        XCTAssertEqual(app.staticTexts["command-result"].label, "Profile")
    }

    @MainActor
    private func launchFixture() -> XCUIApplication {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launchArguments = ["--command-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
        app.launch()
        return app
    }
}
