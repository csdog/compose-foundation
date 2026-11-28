import XCTest

final class ContextMenuUITests: XCTestCase {
    @MainActor
    func testLongPressOnBlankOutlineAndDisabledItem() {
        let app = launchFixture()
        let trigger = app.descendants(matching: .any)["context-menu-trigger"].firstMatch
        let blank = trigger.coordinate(withNormalizedOffset: CGVector(dx: 0.5, dy: 0.5))
            .withOffset(CGVector(dx: -100, dy: 30))
        let disabled = menuElement("Forward", in: app)
        let reload = menuElement("Reload", in: app)
        let result = app.staticTexts["context-menu-result"]

        blank.tap()
        XCTAssertFalse(reload.exists)
        blank.press(forDuration: 1)
        XCTAssertTrue(reload.waitForExistence(timeout: 5))
        XCTAssertTrue(disabled.exists)
        XCTAssertFalse(disabled.isEnabled)
        attachMenu(named: "Context Menu open")

        disabled.tap()
        XCTAssertTrue(reload.exists)
        XCTAssertEqual(result.label, "No action")
        reload.tap()
        XCTAssertTrue(reload.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Reload selected")
    }

    @MainActor
    func testSubmenuSelectionAndOutsideDismissal() {
        let app = launchFixture()
        let trigger = app.descendants(matching: .any)["context-menu-trigger"].firstMatch
        let submenu = menuElement("More Tools", in: app)
        let save = menuElement("Save Page", in: app)
        let result = app.staticTexts["context-menu-result"]

        trigger.press(forDuration: 1)
        XCTAssertTrue(submenu.waitForExistence(timeout: 5))
        submenu.tap()
        XCTAssertTrue(save.waitForExistence(timeout: 5))
        attachMenu(named: "Context Menu submenu")
        save.tap()
        XCTAssertTrue(submenu.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Save Page selected")

        trigger.press(forDuration: 1)
        XCTAssertTrue(submenu.waitForExistence(timeout: 5))
        app.coordinate(withNormalizedOffset: CGVector(dx: 0.91, dy: 0.92)).tap()
        XCTAssertTrue(submenu.waitForNonExistence(timeout: 5))
        XCTAssertEqual(result.label, "Save Page selected")
    }

    @MainActor
    private func menuElement(_ label: String, in app: XCUIApplication) -> XCUIElement {
        app.descendants(matching: .any).matching(NSPredicate(format: "label == %@", label)).firstMatch
    }

    @MainActor
    private func launchFixture() -> XCUIApplication {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launchArguments = ["--context-menu-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
        app.launch()
        return app
    }

    @MainActor
    private func attachMenu(named name: String) {
        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
