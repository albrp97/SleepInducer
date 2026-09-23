package com.sleepinducer.app

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SessionControlsFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val appContext = instrumentation.targetContext

    @Before
    fun grantNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            instrumentation.uiAutomation
                .executeShellCommand(
                    "pm grant ${appContext.packageName} ${Manifest.permission.POST_NOTIFICATIONS}",
                )
                .close()
        }
    }

    @After
    fun stopService() {
        appContext.startService(
            android.content.Intent(
                appContext,
                com.sleepinducer.app.session.BreathingSessionService::class.java,
            ).apply {
                action =
                    com.sleepinducer.app.session.BreathingSessionService.ACTION_STOP
            },
        )
    }

    @Test
    fun showsDurationChoicesAndStartAction() {
        composeTestRule.onNodeWithText("Session length").assertIsDisplayed()
        composeTestRule.onNodeWithText("5 min").assertIsDisplayed()
        composeTestRule.onNodeWithText("10 min").assertIsDisplayed()
        composeTestRule.onNodeWithText("20 min").assertIsDisplayed()
        composeTestRule.onNodeWithText("Custom").assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Start breathing session")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun showsBreathingTimingSlidersWithSixSecondDefaults() {
        composeTestRule.onNodeWithText("Breathing timing").performScrollTo()
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Inhale - 6 seconds").assertIsDisplayed()
        composeTestRule.onNodeWithText("Exhale - 6 seconds").assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("inhale-seconds-slider")
            .performScrollTo()
            .assertIsDisplayed()
        composeTestRule
            .onNodeWithTag("exhale-seconds-slider")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun startsWithConfiguredBreathingTiming() {
        composeTestRule
            .onNodeWithTag("inhale-seconds-slider")
            .performScrollTo()
            .performSemanticsAction(SemanticsActions.SetProgress) {
                it(7.5f)
            }
        composeTestRule
            .onNodeWithTag("exhale-seconds-slider")
            .performScrollTo()
            .performSemanticsAction(SemanticsActions.SetProgress) {
                it(4.5f)
            }
        composeTestRule
            .onNodeWithText("Start breathing session")
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(10_000L) {
            composeTestRule
                .onAllNodesWithText(
                    "7.5 seconds inhale, 4.5 seconds exhale.",
                )
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText("7.5 seconds inhale, 4.5 seconds exhale.")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Stop session").performClick()
    }

    @Test
    fun startsWithSliderCustomDuration() {
        composeTestRule.onNodeWithText("Custom").performClick()
        composeTestRule
            .onNodeWithTag("custom-minutes-slider")
            .performScrollTo()
            .performSemanticsAction(SemanticsActions.SetProgress) {
                it(15f)
            }
        composeTestRule.onNodeWithText("15 minutes").assertIsDisplayed()
        composeTestRule
            .onNodeWithText("Start breathing session")
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(10_000L) {
            composeTestRule
                .onAllNodesWithText(
                    "15 minutes session. Follow the gentle cues with natural breathing.",
                )
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(
                "15 minutes session. Follow the gentle cues with natural breathing.",
            )
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Stop session").performClick()
    }

    @Test
    fun startsAndStopsFromVisibleControls() {
        composeTestRule
            .onNodeWithText("Start breathing session")
            .performScrollTo()
            .performClick()
        composeTestRule.waitUntil(10_000L) {
            composeTestRule
                .onAllNodesWithText("Session active")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Session active").assertIsDisplayed()
        composeTestRule.onNodeWithText("Stop session").performClick()
        composeTestRule.waitUntil(5_000L) {
            composeTestRule
                .onAllNodesWithText("Session stopped. Future cues have been cancelled.")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
    }

    @Test
    fun selectedDurationIsUsedByTheActiveSession() {
        composeTestRule.onNodeWithText("20 min").performClick()
        composeTestRule
            .onNodeWithText("Start breathing session")
            .performScrollTo()
            .performClick()

        composeTestRule.waitUntil(10_000L) {
            composeTestRule
                .onAllNodesWithText(
                    "20 minutes session. Follow the gentle cues with natural breathing.",
                )
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule
            .onNodeWithText(
                "20 minutes session. Follow the gentle cues with natural breathing.",
            )
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Stop session").performClick()
    }
}
