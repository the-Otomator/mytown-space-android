package pro.gesher.twa

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.HttpURLConnection
import java.net.URL
import java.util.UUID

/**
 * Registers the FCM token with Gesher from both [onNewToken] and [MainActivity] initial fetch.
 */
fun registerFcmTokenWithGesher(context: Context, fcmToken: String) {
    val apiUrl = context.getString(R.string.gesher_api_url)
    val apiKey = context.getString(R.string.gesher_api_key)
    val userId = getOrCreateDeviceId(context)

    CoroutineScope(Dispatchers.IO).launch {
        try {
            val url = URL("$apiUrl/subscribe/fcm")
            val conn = url.openConnection() as HttpURLConnection
            conn.requestMethod = "POST"
            conn.setRequestProperty("Content-Type", "application/json")
            conn.setRequestProperty("Authorization", "Bearer $apiKey")
            conn.doOutput = true

            val body = """{"user_id":"$userId","token":"$fcmToken"}"""
            conn.outputStream.use { it.write(body.toByteArray()) }

            val code = conn.responseCode
            if (code !in 200..299) {
                // Log error — token registration failed
            }
            conn.disconnect()
        } catch (_: Exception) {
            // Network error — FCM will retry onNewToken on next launch
        }
    }
}

private fun getOrCreateDeviceId(context: Context): String {
    val prefs: SharedPreferences = context.getSharedPreferences("gesher", Context.MODE_PRIVATE)
    return prefs.getString("device_id", null) ?: UUID.randomUUID().toString().also {
        prefs.edit().putString("device_id", it).apply()
    }
}

class GesherMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        registerFcmTokenWithGesher(this, token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // Web push handled by browser service worker in TWA.
        // FCM data messages (silent push) can be handled here if needed.
    }
}
