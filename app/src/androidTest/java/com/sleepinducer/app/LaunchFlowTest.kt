package com.sleepinducer.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LaunchFlowTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun launchesTheOfflineSetupBoundary() {
        composeTestRule.onNodeWithText("Sleep Inducer").assertIsDisplayed()
        composeTestRule
            .onNodeWithText("A quiet breathing aid for winding down before sleep.")
            .assertIsDisplayed()
    }

    @Test
    fun manifestHasNoNetworkPermission() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val permissions = context.packageManager
            .getPackageInfo(context.packageName, 0)
            .requestedPermissions
            .orEmpty()

        check("android.permission.INTERNET" !in permissions.toSet()) {
            "The offline foundation must not request INTERNET permission."
        }
    }
}
