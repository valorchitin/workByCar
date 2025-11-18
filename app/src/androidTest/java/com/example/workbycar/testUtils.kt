package com.example.workbycar

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.loginAsTestUser(email: String, password: String, welcome: String) {
    // We wait for the SplashScreen to finish
    waitUntil(timeoutMillis = 6000) {
        onAllNodesWithText("Sign In").fetchSemanticsNodes().isNotEmpty()
    }

    // Fill in the login form
    onNodeWithTag("emailTestView").performTextInput(email)
    onNodeWithTag("passwordTextView").performTextInput(password)

    // Click on the Sign In button
    onNodeWithText("Sign In").performClick()

    // We wait for the Home page to load
    // Check that the home page has been navigated to
    waitUntil(timeoutMillis = 8000) {
        onAllNodesWithText(welcome).fetchSemanticsNodes().isNotEmpty()
    }
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.checkUserInfo(name: String, surname: String, age: String, phone: String, description: String) {
    onNodeWithText("Name").assertExists()
    onNodeWithText(name, substring = true).assertExists()

    onNodeWithText("Surname").assertExists()
    onNodeWithText(surname, substring = true).assertExists()

    onNodeWithText("Age").assertExists()
    onNodeWithText(age, substring = true).assertExists()

    onNodeWithText("Phone").assertExists()
    onNodeWithText(phone, substring = true).assertExists()

    onNodeWithText("Description").assertExists()
    onNodeWithText(description, substring = true).assertExists()
}