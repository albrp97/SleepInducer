package com.sleepinducer.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performScrollTo
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SetupFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun showsWellnessPurposeAndLimitations() {
        composeTestRule
            .onNodeWithText("A quiet breathing aid for winding down before sleep.")
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText("This is a relaxation aid, not medical treatment for insomnia.")
            .assertIsDisplayed()
    }

    @Test
    fun showsStopIfUncomfortableGuidance() {
        composeTestRule
            .onNodeWithText(
                "Stop immediately if you feel dizziness, shortness of breath, pain, panic, or discomfort.",
            )
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun opensWithoutAccountOrNetwork() {
        composeTestRule
            .onNodeWithText("Works offline. No account or network connection is needed.")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun explainsTheTechniqueAndUsage() {
        composeTestRule
            .onNodeWithText("How it works")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(
                "This app uses slow-paced breathing with a six-second inhale and a six-second exhale by default. Adjust each phase in whole or half seconds with the timing sliders. Hold time is zero seconds. Keep the breath natural and comfortable rather than forcing its depth.",
            )
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(
                "By default, breathe in gently for 6 seconds, breathe out gently for 6 seconds, then repeat. Use the timing sliders to choose each phase in whole or half seconds. There is no breath hold between phases.",
            )
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithText(
                "1. Choose a session length, then adjust inhale and exhale timing in whole or half seconds.",
            )
            .performScrollTo()
            .assertIsDisplayed()
    }
}
