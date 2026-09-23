package com.sleepinducer.app.session

import android.content.Context
import com.sleepinducer.app.breathing.SessionDuration

class SharedPreferencesSessionStateStore(
    context: Context,
) : SessionStateStore {
    private val preferences = context.getSharedPreferences(
        PREFERENCES_NAME,
        Context.MODE_PRIVATE,
    )

    override fun read(): StoredSessionState? {
        val lifecycle = preferences.getString(KEY_LIFECYCLE, null)
            ?.let { value ->
                ForegroundSessionState.entries.firstOrNull { it.name == value }
            }
            ?: return null
        val duration = preferences.getString(KEY_DURATION, null)
            ?.let(SessionDuration::fromSerialized)
        return StoredSessionState(lifecycle, duration)
    }

    override fun write(state: StoredSessionState) {
        preferences.edit()
            .putString(KEY_LIFECYCLE, state.lifecycle.name)
            .apply {
                if (state.duration == null) {
                    remove(KEY_DURATION)
                } else {
                    putString(KEY_DURATION, state.duration.name)
                }
            }
            .apply()
    }

    private companion object {
        const val PREFERENCES_NAME = "session_operational_state"
        const val KEY_LIFECYCLE = "lifecycle"
        const val KEY_DURATION = "duration"
    }
}
