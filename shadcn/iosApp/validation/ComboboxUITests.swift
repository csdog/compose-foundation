import XCTest

final class ComboboxUITests: XCTestCase {
    @MainActor
    func testTouchSelectionAndChipRemoval() {
        let app = launchFixture()
        let trigger = app.buttons["Multiple sections"]
        trigger.tap()
        let restricted = app.buttons["Restricted"]
        XCTAssertTrue(restricted.waitForExistence(timeout: 5))
        XCTAssertFalse(restricted.isEnabled)
        app.buttons["Advanced"].tap()
        XCTAssertTrue(app.buttons["Advanced"].isSelected)
        app.buttons["Done"].tap()
        let chip = app.buttons["Remove Advanced"]
        XCTAssertTrue(chip.waitForExistence(timeout: 5))
        XCTAssertEqual(trigger.value as? String, "General, Advanced")
        chip.tap()
        XCTAssertFalse(chip.exists)
        XCTAssertEqual(trigger.value as? String, "General")
    }

    @MainActor
    func testSearchFiltersResultsAndSubmitsFromSoftwareKeyboard() {
        let app = launchFixture()
        let trigger = app.buttons["Single section"]
        trigger.tap()
        let search = searchField(in: app)
        XCTAssertTrue(search.waitForExistence(timeout: 5))
        search.tap()
        search.typeText("Adv")
        XCTAssertTrue(app.buttons["Advanced"].exists)
        XCTAssertFalse(app.buttons["General"].exists)
        let submit = app.keyboards.buttons.matching(NSPredicate(format: "label =[c] %@", "Search")).firstMatch
        XCTAssertTrue(submit.waitForExistence(timeout: 5))
        submit.tap()
        XCTAssertTrue(search.waitForNonExistence(timeout: 5))
        XCTAssertEqual(trigger.value as? String, "Advanced")
    }

    @MainActor
    func testAccessibilityDescriptions() throws {
        let app = launchFixture()
        XCTAssertEqual(app.buttons["Multiple sections"].value as? String, "General")
        XCTAssertTrue(app.buttons["Remove General"].exists)
        try app.performAccessibilityAudit(for: .sufficientElementDescription)
        app.buttons["Multiple sections"].tap()
        let selected = app.buttons["General"]
        XCTAssertTrue(selected.waitForExistence(timeout: 5))
        XCTAssertTrue(selected.isSelected)
        XCTAssertFalse(app.buttons["Restricted"].isEnabled)
    }

    @MainActor
    func testHardwareArrowNavigationWithSoftwareSubmit() {
        let app = launchFixture()
        let trigger = app.buttons["Single section"]
        let search = searchField(in: app)
        let submit = app.keyboards.buttons.matching(NSPredicate(format: "label =[c] %@", "Search")).firstMatch
        trigger.tap()
        XCTAssertTrue(search.waitForExistence(timeout: 5))
        XCTAssertTrue(submit.waitForExistence(timeout: 5))
        search.typeKey(XCUIKeyboardKey.downArrow, modifierFlags: [])
        search.typeKey(XCUIKeyboardKey.downArrow, modifierFlags: [])
        submit.tap()
        XCTAssertTrue(search.waitForNonExistence(timeout: 5))
        XCTAssertEqual(trigger.value as? String, "Advanced")

        trigger.tap()
        XCTAssertTrue(search.waitForExistence(timeout: 5))
        XCTAssertTrue(submit.waitForExistence(timeout: 5))
        search.typeKey(XCUIKeyboardKey.upArrow, modifierFlags: [])
        submit.tap()
        XCTAssertTrue(search.waitForNonExistence(timeout: 5))
        XCTAssertEqual(trigger.value as? String, "Network")
    }

    @MainActor
    private func searchField(in app: XCUIApplication) -> XCUIElement {
        app.textFields.matching(NSPredicate(format: "placeholderValue == %@", "Search")).firstMatch
    }

    @MainActor
    private func launchFixture() -> XCUIApplication {
        let app = XCUIApplication()
        app.launchArguments = ["--combobox-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
        app.launch()
        return app
    }
}
