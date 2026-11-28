import XCTest

final class DropdownMenuUITests: XCTestCase {
    @MainActor
    func testHardwareArrowsSkipDisabledAction() {
        let app = launchFixture()
        let trigger = app.buttons["Open actions"]
        let row = menuElement("Profile", in: app)
        let result = app.staticTexts["dropdown-result"]

        trigger.tap()
        XCTAssertTrue(row.waitForExistence(timeout: 5))
        XCTAssertFalse(menuElement("API", in: app).isEnabled)
        app.typeKey(.downArrow, modifierFlags: [])
        attachOpenMenu(named: "Dropdown Menu after first Down")
        app.typeKey(.downArrow, modifierFlags: [])
        attachOpenMenu(named: "Dropdown Menu after second Down")
        app.typeKey(.return, modifierFlags: [])
        XCTAssertTrue(row.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Settings selected")
    }

    @MainActor
    func testEscapeRestoresKeyboardActivation() {
        let app = launchFixture()
        let trigger = app.buttons["Open actions"]
        let row = menuElement("Profile", in: app)
        let result = app.staticTexts["dropdown-result"]

        trigger.tap()
        XCTAssertTrue(row.waitForExistence(timeout: 5))
        app.typeKey(.escape, modifierFlags: [])
        XCTAssertTrue(row.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "No action")
        app.typeKey(.space, modifierFlags: [])
        XCTAssertTrue(row.waitForExistence(timeout: 5))
        XCTAssertEqual(result.label, "No action")
    }

    @MainActor
    func testTouchSelectionSkipsDisabledAction() {
        let app = launchFixture()
        let trigger = app.buttons["Open actions"]
        let row = menuElement("Profile", in: app)
        let disabled = menuElement("API", in: app)
        let result = app.staticTexts["dropdown-result"]

        trigger.tap()
        XCTAssertTrue(row.waitForExistence(timeout: 5))
        XCTAssertTrue(menuElement("My Account", in: app).exists)
        XCTAssertTrue(disabled.exists)
        XCTAssertFalse(disabled.isEnabled)
        attachOpenMenu(named: "Dropdown Menu open")

        disabled.tap()
        XCTAssertTrue(row.exists)
        XCTAssertEqual(result.label, "No action")
        row.tap()
        XCTAssertTrue(row.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Profile selected")
    }

    @MainActor
    func testSubmenuSelectionAndOutsideDismissal() {
        let app = launchFixture()
        let trigger = app.buttons["Open actions"]
        let submenu = menuElement("Invite users", in: app)
        let action = menuElement("Email", in: app)
        let result = app.staticTexts["dropdown-result"]

        trigger.tap()
        XCTAssertTrue(submenu.waitForExistence(timeout: 5))
        submenu.tap()
        XCTAssertTrue(action.waitForExistence(timeout: 5))
        attachOpenMenu(named: "Dropdown Menu submenu")
        action.tap()
        XCTAssertTrue(submenu.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Email selected")

        trigger.tap()
        XCTAssertTrue(submenu.waitForExistence(timeout: 5))
        app.coordinate(withNormalizedOffset: CGVector(dx: 0.92, dy: 0.8)).tap()
        XCTAssertTrue(submenu.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Email selected")
    }

    @MainActor
    private func menuElement(_ label: String, in app: XCUIApplication) -> XCUIElement {
        app.descendants(matching: .any).matching(NSPredicate(format: "label == %@", label)).firstMatch
    }

    @MainActor
    private func launchFixture() -> XCUIApplication {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launchArguments = ["--dropdown-menu-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
        app.launch()
        return app
    }

    @MainActor
    private func attachOpenMenu(named name: String) {
        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
