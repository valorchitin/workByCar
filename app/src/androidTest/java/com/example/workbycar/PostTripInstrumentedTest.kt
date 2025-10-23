package com.example.workbycar

import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
    }
}