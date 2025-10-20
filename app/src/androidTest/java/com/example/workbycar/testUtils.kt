package com.example.workbycar

import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.loginAsTestUser() {
    // We wait for the SplashScreen to finish
    waitUntil(timeoutMillis = 6000) {
        onAllNodesWithText("Sign In").fetchSemanticsNodes().isNotEmpty()
    }

    // Fill in the login form
    onNodeWithTag("emailTestView").performTextInput("juanaedo@gmail.com")
    onNodeWithTag("passwordTextView").performTextInput("123456")

    // Click on the Sign In button
    onNodeWithText("Sign In").performClick()

    // We wait for the Home page to load
    // Check that the home page has been navigated to
    waitUntil(timeoutMillis = 8000) {
        onAllNodesWithText("Welcome\nJuan Martínez Aedo").fetchSemanticsNodes().isNotEmpty()
    }
}

fun AndroidComposeTestRule<*, *>.waitUntil(
    timeoutMillis: Long = 5_000,
    condition: () -> Boolean
) {
    val startTime = System.currentTimeMillis()
    while (System.currentTimeMillis() - startTime < timeoutMillis) {
        if (condition()) return
        Thread.sleep(100)
    }
    throw AssertionError("Condition not met within $timeoutMillis ms")
}
