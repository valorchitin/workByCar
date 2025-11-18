package com.example.workbycar

import androidx.activity.viewModels
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.workbycar.ui.view_models.SignUpViewModel
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ASignUpInstrumentedTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun signUpFeatureTest() {
        // We wait for the SplashScreen to finish
        composeTestRule.waitUntil(timeoutMillis = 6000) {
            composeTestRule
                .onAllNodesWithText("Sign up")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // Click on the login screen "Sign up" link
        composeTestRule.onNodeWithText("Sign up").performClick()

        // wait for the sign up screen
        composeTestRule.waitUntil(timeoutMillis = 5000) {
            composeTestRule
                .onAllNodesWithText("Complete Your Profile\nTo Get Started")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // fill in the form
        composeTestRule.onNodeWithTag("emailTextView").performTextInput("user@test.com")
        composeTestRule.onNodeWithTag("nameTextView").performTextInput("Juan")
        composeTestRule.onNodeWithTag("surnameTextView").performTextInput("Perez")
        composeTestRule.onNodeWithTag("passwordTextView").performTextInput("123456")
        composeTestRule.onNodeWithTag("descriptionTextView").performTextInput("Description about Juan Perez")

        // adjust the birthdate from viewmodel
        composeTestRule.activity.runOnUiThread {
            val viewModel = composeTestRule.activity
                .viewModels<SignUpViewModel>()
                .value
            viewModel.birthDate = System.currentTimeMillis()
        }

        // click on the "Continue" button
        composeTestRule.onNodeWithText("Continue").performClick()

        // wait for add phone screen
        composeTestRule.waitUntil(timeoutMillis = 7000) {
            composeTestRule
                .onAllNodesWithText("Enter Your\nPhone Number")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        // check that we are on the add phone screen
        composeTestRule.onNodeWithText("Enter Your\nPhone Number").assertIsDisplayed()

        // fill in phone number form
        composeTestRule.onNodeWithTag("prefixDropdown").performClick()
        composeTestRule.onNode(hasScrollAction())
            .performScrollToNode(hasText("Spain (+34)"))
        composeTestRule.onNodeWithText("Spain (+34)").performClick()
        composeTestRule.onNodeWithTag("phoneTextView").performTextInput("612345678")

        // Click on the add phone button
        composeTestRule.onNodeWithText("Add phone").performClick()

        // wait and check that the email as been sent
        composeTestRule.waitUntil(timeoutMillis = 8000) {
            composeTestRule.onAllNodesWithText("Message sent.\nPlease verify your email address through mail")
                .fetchSemanticsNodes().isNotEmpty()
        }

        composeTestRule.onNodeWithText("Message sent.\nPlease verify your email address through mail")
            .assertIsDisplayed()
    }
}