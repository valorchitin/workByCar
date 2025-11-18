package com.example.workbycar

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun profileVisualization() {
        composeTestRule.loginAsTestUser("juanaedo@gmail.com", "123456", "Welcome\nJuan Martínez Aedo")

        // Click on the profile button
        composeTestRule.onNodeWithTag("Profile").performClick()

        // Wait for profile page load
        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("User Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // Check that the profile page has been navigated to and its content
        composeTestRule.onNodeWithText("User Profile").assertIsDisplayed()
        composeTestRule.onNodeWithTag("Name")
            .assertIsDisplayed()
            .assert(hasText("Juan"))

        composeTestRule.onNodeWithTag("Surname")
            .assertIsDisplayed()
            .assert(hasText("Martínez Aedo"))

        composeTestRule.onNodeWithTag("Description")
            .assertIsDisplayed()
            .assertTextContains("Description about Juan", substring = true)

        composeTestRule.onNodeWithTag("Phone")
            .assertIsDisplayed()
            .assert(hasText("+355 123456789"))

        composeTestRule.onNodeWithTag("Email")
            .assertIsDisplayed()
            .assert(hasText("juanaedo@gmail.com"))
    }

    @Test
    fun editUserInfoTest() {
        composeTestRule.loginAsTestUser("juanaedo@gmail.com", "123456", "Welcome\nJuan Martínez Aedo")

        // Click on the profile button
        composeTestRule.onNodeWithTag("Profile").performClick()

        // Wait until redirect to profile section
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("User Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // Click on "Edit user info" link
        composeTestRule.onNodeWithText("Edit user info").performClick()

        // Wait for the editing screen to load
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Personal Data").fetchSemanticsNodes().isNotEmpty()
        }

        // Fill in the inputs
        composeTestRule.onNodeWithTag("Name").performTextClearance()
        composeTestRule.onNodeWithTag("Name").performTextInput("Juana")

        composeTestRule.onNodeWithTag("Surname").performTextClearance()
        composeTestRule.onNodeWithTag("Surname").performTextInput("Aedo")

        composeTestRule.onNodeWithTag("Description").performTextClearance()
        composeTestRule.onNodeWithTag("Description").performTextInput("Updated description")

        composeTestRule.onNodeWithTag("Phone").performTextClearance()
        composeTestRule.onNodeWithTag("Phone").performTextInput("987654321")

        composeTestRule.onNodeWithTag("prefixDropdown").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Andorra (+376)").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Andorra (+376)").performClick()

        // Save changes
        composeTestRule.onNodeWithText("Save").performClick()

        // Check that you are back on the profile screen and that the data has been updated.
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("User Profile").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            val nodes = composeTestRule.onAllNodesWithTag("Name").fetchSemanticsNodes()
            nodes.isNotEmpty() && nodes.first().config.getOrNull(SemanticsProperties.Text)
                ?.joinToString("")?.contains("Juana") == true
        }

        composeTestRule.onNodeWithTag("Name").assertTextContains("Juana")
        composeTestRule.onNodeWithTag("Surname").assertTextContains("Aedo")
        composeTestRule.onNodeWithTag("Description").assertTextContains("Updated description")
        composeTestRule.onNodeWithTag("Phone").assertTextContains("+376 987654321")

        // Return to the editing screen
        composeTestRule.onNodeWithText("Edit user info").performClick()
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Personal Data").fetchSemanticsNodes().isNotEmpty()
        }

        // Restore original values
        composeTestRule.onNodeWithTag("Name").performTextClearance()
        composeTestRule.onNodeWithTag("Name").performTextInput("Juan")

        composeTestRule.onNodeWithTag("Surname").performTextClearance()
        composeTestRule.onNodeWithTag("Surname").performTextInput("Martínez Aedo")

        composeTestRule.onNodeWithTag("Description").performTextClearance()
        composeTestRule.onNodeWithTag("Description").performTextInput("Description about Juan")

        composeTestRule.onNodeWithTag("Phone").performTextClearance()
        composeTestRule.onNodeWithTag("Phone").performTextInput("123456789")

        composeTestRule.onNodeWithTag("prefixDropdown").performClick()
        composeTestRule.waitUntil(timeoutMillis = 2000) {
            composeTestRule.onAllNodesWithText("Albania (+355)").fetchSemanticsNodes().isNotEmpty()
        }
        composeTestRule.onNodeWithText("Albania (+355)").performClick()

        composeTestRule.onNodeWithText("Save").performClick()
    }

    @Test
    fun logOutTest() {
        composeTestRule.loginAsTestUser("juanaedo@gmail.com", "123456", "Welcome\nJuan Martínez Aedo")

        // Click on the profile button
        composeTestRule.onNodeWithTag("Profile").performClick()

        // Click on the profile button
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("User Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // Click on "Log out" button
        composeTestRule.onNodeWithText("Log out").performClick()

        // Wait until you return to the login screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Sign In").fetchSemanticsNodes().isNotEmpty()
        }

        // Check that we are logged in
        composeTestRule.onNodeWithText("Sign In").assertIsDisplayed()
    }

    @Test
    fun deleteAccountTest() {
        composeTestRule.waitUntil(timeoutMillis = 6000) {
            composeTestRule
                .onAllNodesWithText("Sign In")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Fill in the login form
        composeTestRule.onNodeWithTag("emailTestView").performTextInput("user@test.com")
        composeTestRule.onNodeWithTag("passwordTextView").performTextInput("123456")

        // Click on the Sign In button
        composeTestRule.onNodeWithText("Sign In").performClick()

        // We wait for the Home page to load
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Welcome\nJuan Perez").fetchSemanticsNodes().isNotEmpty()
        }

        // Click on the profile button
        composeTestRule.onNodeWithTag("Profile").performClick()

        // Wait until redirect to profile section
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("User Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // Click on "Delete account" button
        composeTestRule.onNodeWithText("Delete account").performClick()

        // Wait until you return to the login screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Sign In").fetchSemanticsNodes().isNotEmpty()
        }

        // fill in the login fields again
        composeTestRule.onNodeWithTag("emailTestView").performTextClearance()
        composeTestRule.onNodeWithTag("passwordTextView").performTextClearance()

        composeTestRule.onNodeWithTag("emailTestView").performTextInput("user@test.com")
        composeTestRule.onNodeWithTag("passwordTextView").performTextInput("123456")
        composeTestRule.onNodeWithText("Sign In").performClick()

        // Wait a bit and verify that it does NOT access the Home
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Sign In")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Verify that the text "Welcome" does NOT appear
        composeTestRule.onAllNodesWithText("Welcome").assertCountEquals(0)
    }
}