package com.example.workbycar

import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PostTripInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun postTripTest() {
        composeTestRule.loginAsTestUser()

        // Click on the post trip button
        composeTestRule.onNodeWithTag("Post").performClick()

        // Select departure address
        composeTestRule.selectDepartureAddress()

        // Click on the confirm location button
        composeTestRule.onNodeWithText("Confirm Location").performClick()

        // Select destination address
        composeTestRule.selectDestinationAddress()

        // Click on the confirm location button
        composeTestRule.onNodeWithText("Confirm Location").performClick()

        // Select preferred route
        composeTestRule.selectPreferredRoute()

        // Select the trip days
        composeTestRule.selectTripDays()

        // Select the departure hour
        composeTestRule.selectDepartureHour()

        // Select the number of passengers
        composeTestRule.selectPassengersNumber()

        // Select the reservation type
        composeTestRule.selectReservationType()

        // Select the price
        composeTestRule.selectPrice()

        // Post trip
        composeTestRule.postTrip()

        // Complete the process and check that the trip appears on the My Trips page.
        composeTestRule.onNodeWithTag("dontPostReturn").performClick()

        // Wait for the next screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Trips").fetchSemanticsNodes().isNotEmpty()
        }

        // Verify that we are on the Publication Confirmation page
        composeTestRule.onNodeWithText("Trips").assertExists()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("My booked trips").fetchSemanticsNodes().isNotEmpty()
        }

        // Scroll to the heading "My posted trips"
        composeTestRule.onNodeWithTag("TripsList", useUnmergedTree = true)
            .performScrollToNode(hasText("My posted trips", ignoreCase = true))

        // Wait until that text is visible
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("My posted trips", useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        // Scroll to the travel card
        composeTestRule.onNodeWithTag("TripsList", useUnmergedTree = true)
            .performScrollToNode(hasText("Calle las Gardenias, Alcorcón, Spain", substring = true))

        // Verify that the trip exists
        val matchingNodes = composeTestRule
            .onAllNodesWithText(
                "Calle las Gardenias, Alcorcón, Spain → Bertrandt Technology Spain, Avenida Leonardo Da Vinci, Getafe, Spain",
                substring = true,
                useUnmergedTree = true
            )
            .fetchSemanticsNodes()

        assert(matchingNodes.isNotEmpty()) {
            "No trip was found with the expected text"
        }
    }
}