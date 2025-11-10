package com.example.workbycar

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
class TripReservationInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun tripReservation() {
        // We wait for the SplashScreen to finish
        composeTestRule.waitUntil(timeoutMillis = 6000) {
            composeTestRule
                .onAllNodesWithText("Sign In")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Fill in the login form
        composeTestRule.onNodeWithTag("emailTestView").performTextInput("diegogonzalez@gmail.com")
        composeTestRule.onNodeWithTag("passwordTextView").performTextInput("123456")

        // Click on the Sign In button
        composeTestRule.onNodeWithText("Sign In").performClick()

        // We wait for the Home page to load
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Welcome\nDiego González Rodríguez").fetchSemanticsNodes().isNotEmpty()
        }

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

        // Ir al mes siguiente
        composeTestRule.onNodeWithContentDescription("Next month").performClick()

        // Esperar a que se actualice el calendario
        composeTestRule.waitForIdle()

        // Buscar el primer día disponible del mes siguiente
        val nextMonth = YearMonth.now().plusMonths(1)
        val firstDay = LocalDate.of(nextMonth.year, nextMonth.month, 1)
        val firstWeekStart = firstDay.minusDays(firstDay.dayOfWeek.value.toLong() - 1)

        // Hacer click en el primer día del mes (activará la semana)
        composeTestRule.onNodeWithTag("calendar_day_${firstDay}").performClick()

        // Esperar que se active el botón Continue
        composeTestRule.waitUntil(timeoutMillis = 3000) {
            composeTestRule.onAllNodesWithText("Continue").fetchSemanticsNodes().isNotEmpty()
        }

        // Pulsar el botón Continue
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

        // Wait for Reservation button to load
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithTag("reservationButton").fetchSemanticsNodes().isNotEmpty()
        }

        // Click on the reservation button
        composeTestRule.onNodeWithTag("reservationButton").performClick()

        // Wait for the next screen
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Trips").fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.waitUntil(timeoutMillis = 10000) {
            composeTestRule.onAllNodesWithText("My booked trips").fetchSemanticsNodes().isNotEmpty()
        }

        // Scroll to the heading "My booked trips"
        composeTestRule.onNodeWithTag("TripsList", useUnmergedTree = true)
            .performScrollToNode(hasText("My booked trips", ignoreCase = true))

        // Wait until that text is visible
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule.onAllNodesWithText("My booked trips", useUnmergedTree = true)
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