package com.example.workbycar

import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.AndroidComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.rules.ActivityScenarioRule

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.selectDepartureAddress() {
    // Wait for the source text field to appear
    waitUntil(timeoutMillis = 6000) {
        onAllNodesWithTag("originTextField").fetchSemanticsNodes().isNotEmpty()
    }

    // Write part of the address
    onNodeWithTag("originTextField").performTextInput("Calle las Gardenias")

    waitUntil(timeoutMillis = 8000) {
            onAllNodesWithText("Calle las Gardenias, Alcorcón", substring = true).fetchSemanticsNodes().isNotEmpty()
    }

    // click on the first match
    onAllNodesWithText("Calle las Gardenias, Alcorcón", substring = true)[0].performClick()

    // Wait for it to navigate to the map screen, where you can select the exact address on the map.
    waitUntil(timeoutMillis = 8000) {
        onAllNodesWithText("Adjust your origin on the map").fetchSemanticsNodes().isNotEmpty()
    }

    // Verify that we are on the map screen
    onNodeWithText("Adjust your origin on the map").assertExists()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.selectDestinationAddress() {
    // Wait for the source text field to appear
    waitUntil(timeoutMillis = 6000) {
        onAllNodesWithTag("destinationTextField").fetchSemanticsNodes().isNotEmpty()
    }

    // Write part of the address
    onNodeWithTag("destinationTextField").performTextInput("Bertrandt Technology")

    waitUntil(timeoutMillis = 8000) {
        onAllNodesWithText("Bertrandt Technology Spain, Avenida Leonardo Da Vinci", substring = true).fetchSemanticsNodes().isNotEmpty()
    }

    // click on the first match
    onAllNodesWithText("Bertrandt Technology Spain, Avenida Leonardo Da Vinci", substring = true)[0].performClick()

    // Wait for it to navigate to the map screen, where you can select the exact address on the map.
    waitUntil(timeoutMillis = 8000) {
        onAllNodesWithText("Adjust your destination on the map").fetchSemanticsNodes().isNotEmpty()
    }

    // Verify that we are on the map screen
    onNodeWithText("Adjust your destination on the map").assertExists()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.selectPreferredRoute() {
    // Wait until rout options has loaded
    waitUntil(timeoutMillis = 8000) {
        onAllNodesWithText("Option", substring = true).fetchSemanticsNodes().isNotEmpty()
    }

    // Attempts to select the second route if it exists, otherwise the first
    val routeOptions = onAllNodesWithText("Option", substring = true).fetchSemanticsNodes()

    if (routeOptions.size > 1){
        onAllNodesWithTag("routeRadioButton")[1].performClick()
    } else {
        onAllNodesWithTag("routeRadioButton")[0].performClick()
    }

    // Click on the continue button
    onNodeWithText("Continue").performClick()

    // Wait for the next screen
    waitUntil(timeoutMillis = 8000){
        onAllNodesWithText("Please select the days you offer the trip").fetchSemanticsNodes().isNotEmpty()
    }

    // Verify that we are on the days selector screen
    onNodeWithText("Please select the days you offer the trip").assertExists()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.selectTripDays() {
    // Click on the next month button
    onNodeWithContentDescription("Next month").performClick()

    // Wait for the month to change
    Thread.sleep(1500)

    // Try to select the first 5 days of the month
    val days = listOf("day_1", "day_2", "day_3", "day_4", "day_5")
    var anySelected = false

    for (dayTag in days) {
        try {
            onNodeWithTag(dayTag).performClick()
            anySelected = true
        } catch (_: AssertionError) {
            continue
        }
    }

    // Click on Continue button
    onNodeWithText("Continue").performClick()

    // Wait for the next screen
    waitUntil(timeoutMillis = 8000) {
        onAllNodesWithText("What time will you pick up your passengers?").fetchSemanticsNodes().isNotEmpty()
    }

    // Verify that we are on the departure time selector screen
    onNodeWithText("What time will you pick up your passengers?").assertExists()
}

fun AndroidComposeTestRule<ActivityScenarioRule<MainActivity>, MainActivity>.selectDepartureHour() {
    // Click on open timepicker button
    onNodeWithTag("openTimePicker").performClick()

    // Wait to open timepicker
    waitUntil(timeoutMillis = 5000) {
        onAllNodesWithText("Confirm").fetchSemanticsNodes().isNotEmpty()
    }

    // Adjust Hour
    onNodeWithText("7", useUnmergedTree = true).performClick()

    // Adjust minute
    onNodeWithText("30", useUnmergedTree = true).performClick()

    // Click on the Confirm button
    onNodeWithText("Confirm").performClick()

    // Check timepicker displays the correct hour
    onNodeWithTag("openTimePicker").assertTextContains("07:30")

    // Click on Continue button
    onNodeWithText("Continue").performClick()

    // Wait for the next screen
    waitUntil(timeoutMillis = 8000) {
        onAllNodesWithText("How many passengers are you going to take on the trip? Consider passenger comfort").fetchSemanticsNodes().isNotEmpty()
    }

    // Verify that we are on the passengers number selector screen
    onNodeWithText("How many passengers are you going to take on the trip? Consider passenger comfort").assertExists()
}
