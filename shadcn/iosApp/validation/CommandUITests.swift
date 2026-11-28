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
        let submit = app.keyboards.buttons.matching(NSPredicate(format: "label =[c] %@", "Search")).firstMatch
        XCTAssertTrue(submit.waitForExistence(timeout: 5))
        field.typeKey(.downArrow, modifierFlags: [])
        field.typeKey(.downArrow, modifierFlags: [])
        submit.tap()
        XCTAssertEqual(app.staticTexts["command-result"].label, "Profile")
    }

    @MainActor
    func testDialogFocusesSearchAndClearsItBetweenPresentations() {
        let app = launchFixture("--command-dialog-interaction")
        let trigger = app.buttons["Open command palette"]
        let field = app.textFields["Search commands"]
        let search = app.keyboards.buttons.matching(NSPredicate(format: "label =[c] %@", "Search")).firstMatch
        let result = app.staticTexts["command-dialog-result"]

        trigger.tap()
        XCTAssertTrue(field.waitForExistence(timeout: 5))
        XCTAssertTrue(search.waitForExistence(timeout: 5))
        let opened = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        opened.name = "Command Dialog with search focused"
        opened.lifetime = .keepAlways
        add(opened)
        field.typeText("bi")
        XCTAssertFalse(app.buttons["Calendar"].exists)
        app.buttons["Billing"].tap()
        XCTAssertTrue(field.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Billing")

        trigger.tap()
        XCTAssertTrue(field.waitForExistence(timeout: 5))
        XCTAssertTrue(app.buttons["Calendar"].exists)
        app.buttons["Close"].tap()
        XCTAssertTrue(field.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Billing")
    }

    @MainActor
    private func launchFixture(_ mode: String = "--command-interaction") -> XCUIApplication {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launchArguments = [mode, "-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
        app.launch()
        return app
    }
}
