package com.example.workbycar

import androidx.compose.ui.test.hasScrollAction
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.YearMonth

@RunWith(AndroidJUnit4::class)
class ChatInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun chatTest() {
        // Login
        composeTestRule.loginAsTestUser("diegogonzalez@gmail.com", "123456", "Welcome\nDiego González Rodríguez")

        // click on the origin field
        composeTestRule.onNodeWithTag("Origin").performClick()

        // wait until redirect to the place selector screen
        composeTestRule.waitUntil(timeoutMillis = 8000){
            composeTestRule.onAllNodesWithText("Write the full address")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Search and select the origin
        composeTestRule.onNodeWithTag("placeTextField").performTextInput("Calle las Gardenias")

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Calle las Gardenias, Alcorcón", substring = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("Calle las Gardenias, Alcorcón", substring = true)[0].performClick()

        // wait until redirect to the searcher screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Welcome\nDiego González Rodríguez").fetchSemanticsNodes().isNotEmpty()
        }

        // click on the destination field
        composeTestRule.onNodeWithTag("Destination").performClick()

        // wait until redirect to the place selector screen
        composeTestRule.waitUntil(timeoutMillis = 8000){
            composeTestRule.onAllNodesWithText("Write the full address")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Search and select the destination
        composeTestRule.onNodeWithTag("placeTextField").performTextInput("Bertrandt Technology")

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Bertrandt Technology Spain, Avenida Leonardo Da Vinci", substring = true).fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onAllNodesWithText("Bertrandt Technology Spain, Avenida Leonardo Da Vinci", substring = true)[0].performClick()

        // wait until redirect to the searcher screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Welcome\nDiego González Rodríguez").fetchSemanticsNodes().isNotEmpty()
        }

        // click on the week field
        composeTestRule.onNodeWithTag("Week").performClick()

        // Wait until redirect to week selector
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithContentDescription("Next month").fetchSemanticsNodes().isNotEmpty()
        }

        // Go to next month
        composeTestRule.onNodeWithContentDescription("Next month").performClick()

        // Wait for the calendar to update
        composeTestRule.waitForIdle()

        // Find the first available day of the following month
        val nextMonth = YearMonth.now().plusMonths(1)
        val firstDay = LocalDate.of(nextMonth.year, nextMonth.month, 1)
        val firstWeekStart = firstDay.minusDays(firstDay.dayOfWeek.value.toLong() - 1)

        // Click on the first day of the month (this will activate the week)
        composeTestRule.onNodeWithTag("calendar_day_${firstDay}").performClick()

        // Wait for the Continue button to activate
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Continue").fetchSemanticsNodes().isNotEmpty()
        }

        // Press the Continue buttons
        composeTestRule.onNodeWithText("Continue").performClick()


        // wait until redirect to the searcher screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Welcome\nDiego González Rodríguez").fetchSemanticsNodes().isNotEmpty()
        }

        // Click on the search button
        composeTestRule.onNodeWithText("Search").performClick()

        // wait until redirect to the found trips screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Found Trips").fetchSemanticsNodes().isNotEmpty()
        }

        val targetTripText =
            "Calle las Gardenias, Alcorcón, Spain → Bertrandt Technology Spain, Avenida Leonardo Da Vinci, Getafe, Spain"

        // Wait until the list is visible
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithTag("tripsList").fetchSemanticsNodes().isNotEmpty()
        }

        // Scroll until the trip appears
        composeTestRule.onNodeWithTag("tripsList").performScrollToNode(
            hasText(targetTripText, substring = true)
        )

        // Verify that the first node that matches the text exists and click
        composeTestRule.onAllNodesWithText(targetTripText, substring = true)[0]
            .assertExists()
        composeTestRule.onAllNodesWithText(targetTripText, substring = true)[0]
            .performClick()

        // Wait for TripInformationScreen to load
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Trip description:").fetchSemanticsNodes().isNotEmpty()
        }

        // Scroll to the contact button
        composeTestRule.onNode(hasScrollAction())
            .performScrollToNode(hasTestTag("contactButton"))

        // Click on the contact button
        composeTestRule.onNodeWithTag("contactButton").performClick()

        // wait until redirect to the chat screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Juan Martínez Aedo").fetchSemanticsNodes().isNotEmpty()
        }

        // Write a new message
        composeTestRule.onNodeWithTag("messageInput").performTextInput("Hello I'm interested in the trip")

        // Send new message
        composeTestRule.onNodeWithTag("sendMessageButton").performClick()

        // Leave the chat and navigate to profile section
        composeTestRule.onNodeWithTag("arrowBack").performClick()
        composeTestRule.onNodeWithTag("closeTripInformation").performClick()
        composeTestRule.onNodeWithTag("closeFoundTrips").performClick()

        composeTestRule.onNodeWithTag("Profile").performClick()

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("User Profile").fetchSemanticsNodes().isNotEmpty()
        }

        // Log out
        composeTestRule.onNodeWithText("Log out").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Sign In").fetchSemanticsNodes().isNotEmpty()
        }

        // Login as juan
        composeTestRule.loginAsTestUser("juanaedo@gmail.com", "123456", "Welcome\nJuan Martínez Aedo")

        // Navigate to chats section
        composeTestRule.onNodeWithTag("Messages").performClick()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Diego González Rodríguez").fetchSemanticsNodes().isNotEmpty()
        }

        // Open Diego González Rodríguez chat
        composeTestRule.onNode(hasScrollAction())
            .performScrollToNode(hasText("Diego González Rodríguez", substring = true))

        composeTestRule.onNodeWithText("Diego González Rodríguez", substring = true)
            .assertExists()

        composeTestRule.onNodeWithText("Diego González Rodríguez", substring = true)
            .performClick()

        // Check that the message sent by Diego appears
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Hello I'm interested in the trip", substring = true)
                .fetchSemanticsNodes().isNotEmpty()
        }

        val matchingMessages = composeTestRule
            .onAllNodesWithText("Hello I'm interested in the trip", substring = true)
            .fetchSemanticsNodes()

        assert(matchingMessages.isNotEmpty()) {
            "The message Hello I'm interested in the trip did not appear in the chat"
        }

        // Click on the userInfo link
        composeTestRule.onNodeWithTag("userInfo").performClick()

        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("Name").fetchSemanticsNodes().isNotEmpty()
        }

        // Check user info
        composeTestRule.checkUserInfo("Diego", "González Rodríguez", "24", "+34 987654321", "Not description yet")

        // Check associated trip info
        composeTestRule.onNodeWithTag("back").performClick()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Diego González Rodríguez").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithTag("tripInfo").performClick()

        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Trip description:").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Trip description:").assertExists()
        composeTestRule.onNodeWithText("Departure: Calle las Gardenias, Alcorcón, Spain", substring = true).assertExists()
        composeTestRule.onNodeWithText("Destination: Bertrandt Technology Spain, Avenida Leonardo Da Vinci, Getafe, Spain", substring = true).assertExists()

        composeTestRule.onNodeWithText("1 passenger", substring = true).assertExists()
        composeTestRule.onNodeWithText("€", substring = true).assertExists()

        composeTestRule.onNodeWithText("Driver:", substring = true).assertExists()
        composeTestRule.onNodeWithText("Juan Martínez Aedo", substring = true).assertExists()

        composeTestRule.onNodeWithText("Passengers:", substring = true).assertExists()

        composeTestRule.onNodeWithText("Trip description:", substring = true).assertExists()

        composeTestRule.onNodeWithText("The reservation", substring = true).assertExists()

        composeTestRule.onNodeWithText("Contact Juan", substring = true).assertExists()
    }
}