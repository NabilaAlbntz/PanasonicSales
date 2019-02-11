package pogumedia.panasonic.sales.service.fcm

import android.app.NotificationManager
import android.content.Intent
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import pogumedia.panasonic.sales.db.SessionManager


/**
 * Created by crocodicstudio on 4/26/18.
 */
class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(p0: String?) {

    }


    lateinit var intent: Intent
    lateinit var notificationManager: NotificationManager


    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        val sessionManager = SessionManager(this)
        sendNotification(remoteMessage?.data!!)
    }

    private fun sendNotification(messageBody: Map<String, String>) {

    }


}