package com.example.workbycar

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LoginInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun loginFeatureTest() {
        // We wait for the SplashScreen to finish
        composeTestRule.waitUntil(timeoutMillis = 6000) {
            composeTestRule
                .onAllNodesWithText("Sign In")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Fill in the login form
        composeTestRule.onNodeWithTag("emailTestView").performTextInput("juanaedo@gmail.com")
        composeTestRule.onNodeWithTag("passwordTextView").performTextInput("123456")

        // Click on the Sign In button
        composeTestRule.onNodeWithText("Sign In").performClick()

        // We wait for the Home page to load
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Welcome\nJuan Martínez Aedo").fetchSemanticsNodes().isNotEmpty()
        }

        // Check that the home page has been navigated to
        composeTestRule.onNodeWithText("Welcome\nJuan Martínez Aedo").assertIsDisplayed()
    }

    @Test
    fun loginFlow_showsErrorAndDoesNotNavigateOnWrongCredentials() {
        // We wait for the SplashScreen to finish
        composeTestRule.waitUntil(timeoutMillis = 6000) {
            composeTestRule
                .onAllNodesWithText("Sign In")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        //Enter invalid credentials
        composeTestRule.onNodeWithTag("emailTestView").performTextInput("wronguser@test.com")
        composeTestRule.onNodeWithTag("passwordTextView").performTextInput("wrongpassword")

        // Click on the Sign In button
        composeTestRule.onNodeWithText("Sign In").performClick()

        // Wait a bit in case he tries to navigate
        composeTestRule.waitForIdle()

        // We check that we are still on the login screen
        composeTestRule.onNodeWithText("Your route awaits,\nlet's get started")
            .assertIsDisplayed()
    }
}