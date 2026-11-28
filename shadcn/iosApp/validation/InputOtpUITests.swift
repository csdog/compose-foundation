import XCTest

final class InputOtpUITests: XCTestCase {
    @MainActor
    func testLettersAndDigitsKeepTheirCaseAndRespectLength() {
        continueAfterFailure = false
        let app = XCUIApplication()
        app.launchArguments = ["--input-otp-interaction", "-AppleLanguages", "(en)", "-AppleLocale", "en_US"]
        app.launch()

        let field = app.textFields["Verification code"]
        XCTAssertTrue(field.waitForExistence(timeout: 5))
        field.tap()
        field.typeText("Ab-3 Z9x0")
        XCTAssertTrue(app.staticTexts["Accepted: Ab3Z9x"].waitForExistence(timeout: 5))

        let attachment = XCTAttachment(screenshot: XCUIScreen.main.screenshot())
        attachment.name = "Alphanumeric OTP after filtering and length limit"
        attachment.lifetime = .keepAlways
        add(attachment)
    }
}
