import XCTest

final class ResizableUITests: XCTestCase {
    @MainActor
    func testHorizontalAndRTLDragFromTheDividerHitArea() {
        for rightToLeft in [false, true] {
            let app = launch(rightToLeft ? ["--resizable-rtl"] : [])
            let split = pane(app)
            let divider: CGFloat = 319 * 0.25 + 0.5
            let x = rightToLeft ? 320 - divider + 12 : divider - 12
            drag(in: split, from: CGVector(dx: x, dy: 90),
                 by: CGVector(dx: rightToLeft ? -32 : 32, dy: 0))
            XCTAssertTrue(app.staticTexts["First pane: 35%"].waitForExistence(timeout: 5))
            attach(rightToLeft ? "Resizable RTL touch" : "Resizable LTR touch")
            app.terminate()
        }
    }

    @MainActor
    func testVerticalDragRespectsBoundsAndDisabledState() {
        let app = launch(["--resizable-vertical"])
        let split = pane(app)
        drag(in: split, from: CGVector(dx: 160, dy: 53), by: CGVector(dx: 0, dy: 115))
        XCTAssertTrue(app.staticTexts["First pane: 80%"].waitForExistence(timeout: 5))
        attach("Resizable vertical touch at upper bound")
        app.terminate()

        let disabled = launch(["--resizable-vertical", "--resizable-disabled"])
        drag(in: pane(disabled), from: CGVector(dx: 160, dy: 53), by: CGVector(dx: 0, dy: 52))
        XCTAssertTrue(disabled.staticTexts["First pane: 25%"].exists)
    }

    @MainActor
    private func launch(_ arguments: [String]) -> XCUIApplication {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launchArguments = ["--resizable-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"] + arguments
        app.launch()
        XCTAssertTrue(app.staticTexts["First pane: 25%"].waitForExistence(timeout: 5))
        return app
    }

    @MainActor
    private func pane(_ app: XCUIApplication) -> XCUIElement {
        let split = app.descendants(matching: .any)["Resizable split"].firstMatch
        XCTAssertTrue(split.waitForExistence(timeout: 5))
        XCTAssertEqual(split.frame.width, 320, accuracy: 1)
        XCTAssertEqual(split.frame.height, 180, accuracy: 1)
        return split
    }

    @MainActor
    private func drag(in split: XCUIElement, from start: CGVector, by delta: CGVector) {
        let origin = split.coordinate(withNormalizedOffset: .zero)
        let beginning = origin.withOffset(start)
        beginning.press(forDuration: 0.1, thenDragTo: beginning.withOffset(delta))
    }

    @MainActor
    private func attach(_ name: String) {
        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = name
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
