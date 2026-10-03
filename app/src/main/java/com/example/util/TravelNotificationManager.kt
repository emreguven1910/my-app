package com.example.util

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.example.MainActivity
import com.example.R

object TravelNotificationManager {

    const val CHANNEL_TRAVEL_TIPS = "channel_travel_tips"
    const val CHANNEL_NEW_POSTS = "channel_new_posts"
    const val CHANNEL_REMINDERS = "channel_reminders"

    const val EXTRA_ACTION = "extra_notification_action"
    const val EXTRA_POST_ID = "extra_post_id"
    const val EXTRA_TAB_INDEX = "extra_tab_index"

    const val ACTION_OPEN_POST = "com.example.action.OPEN_POST"
    const val ACTION_OPEN_JOURNAL = "com.example.action.OPEN_JOURNAL"
    const val ACTION_OPEN_ROUTES = "com.example.action.OPEN_ROUTES"

    private val TRAVEL_TIPS = listOf(
        Pair(
            "🚄 YHT Tren İpucu (Ankara - Konya)",
            "Ankara Garı'ndan kalkan sabah 07:00 YHT treniyle günübirlik Konya ziyareti yapabilir; Mevlana Türbesi ve Alaaddin Tepesi'ni gezip akşam dönebilirsiniz!"
        ),
        Pair(
            "🌊 Çanakkale & Biga Molası",
            "Biga çarşısında meşhur Biga köftesini tatmayı ve 1915 Çanakkale Köprüsü veya feribot geçiş saatlerini önceden kontrol etmeyi unutmayın."
        ),
        Pair(
            "🌲 Gemlik & Narlı Sahili",
            "Bursa'dan Narlı'ya uzanan sahil yolunda zeytinlikler eşliğinde huzurlu bir deniz kenarı yürüyüşü yapabilirsiniz."
        ),
        Pair(
            "🕌 İstanbul Tarihi Yarımada",
            "Gülhane Parkı'nda çayınızı yudumlayıp ardından Eminönü'nden kalkan vapurda martılara simit atmak en güzel İstanbul klasiğidir."
        ),
        Pair(
            "🎒 Bavul Hazırlık Tavsiyesi",
            "Yolculuğa çıkmadan önce powerbank, kimlik/biletler ve ilk yardım malzemelerinizi 'Bavul Hazırlığı' listenizden kontrol edin."
        )
    )

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Travel tips channel
            val tipsChannel = NotificationChannel(
                CHANNEL_TRAVEL_TIPS,
                "Seyahat İpuçları & Öneriler",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Güven Geziyor rotaları, tren ipuçları ve lezzet önerileri"
                enableVibration(true)
            }

            // New blog posts channel
            val postsChannel = NotificationChannel(
                CHANNEL_NEW_POSTS,
                "Yeni Blog Yazıları",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Yeni bir seyahat hikayesi paylaşıldığında anlık bildirim"
                enableVibration(true)
            }

            // Reminders channel
            val remindersChannel = NotificationChannel(
                CHANNEL_REMINDERS,
                "Gezi & Bavul Hatırlatıcıları",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Seyahat öncesi bavul hazırlığı ve rota hatırlatmaları"
                enableVibration(true)
            }

            manager.createNotificationChannels(listOf(tipsChannel, postsChannel, remindersChannel))
        }
    }

    fun hasPermission(context: Context): Boolean {
        val areNotificationsEnabled = NotificationManagerCompat.from(context).areNotificationsEnabled()
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            areNotificationsEnabled && ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            areNotificationsEnabled
        }
    }

    fun showTravelTip(context: Context, customTitle: String? = null, customMessage: String? = null) {
        if (!hasPermission(context)) return

        val (title, message) = if (customTitle != null && customMessage != null) {
            Pair(customTitle, customMessage)
        } else {
            TRAVEL_TIPS.random()
        }

        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_ROUTES
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            1001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_TRAVEL_TIPS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(1001, notification)
    }

    fun showNewPostNotification(context: Context, postId: Int, postTitle: String, city: String) {
        if (!hasPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_POST
            putExtra(EXTRA_POST_ID, postId)
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            2000 + postId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val subtitle = if (city.isNotBlank()) "📍 $city rotasından yeni seyahat yazısı!" else "Yeni bir seyahat hikayesi yayında!"

        val notification = NotificationCompat.Builder(context, CHANNEL_NEW_POSTS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(postTitle)
            .setContentText(subtitle)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .setBigContentTitle(postTitle)
                    .bigText("$subtitle\n\nOkumak ve sesli dinlemek için tıklayın.")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(2000 + postId, notification)
    }

    fun showChecklistReminder(context: Context, uncompletedCount: Int) {
        if (!hasPermission(context)) return

        val intent = Intent(context, MainActivity::class.java).apply {
            action = ACTION_OPEN_JOURNAL
            putExtra(EXTRA_TAB_INDEX, 1) // 1 = Bavul Hazırlığı tab
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            3001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val message = if (uncompletedCount > 0) {
            "Bavul hazırlığında henüz tamamlanmamış $uncompletedCount madde var. Yolculuk öncesi kontrol etmeyi unutmayın!"
        } else {
            "Tebrikler! Bavul hazırlık listenizdeki tüm maddeler tamamlandı. İyi yolculuklar!"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_REMINDERS)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle("🎒 Seyahat Hazırlık Hatırlatıcısı")
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle().bigText(message))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        NotificationManagerCompat.from(context).notify(3001, notification)
    }
}
