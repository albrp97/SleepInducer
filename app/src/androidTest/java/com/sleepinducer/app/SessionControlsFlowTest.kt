package com.sleepinducer.app

import android.Manifest
import android.os.Build
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
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
        composeTestRule
            .onNodeWithText("Start breathing session")
            .assertIsDisplayed()
    }

    @Test
    fun startsAndStopsFromVisibleControls() {
        composeTestRule
            .onNodeWithText("Start breathing session")
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
