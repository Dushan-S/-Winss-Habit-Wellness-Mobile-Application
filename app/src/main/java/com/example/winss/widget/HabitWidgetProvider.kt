package com.example.winss.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.winss.MainActivity
import com.example.winss.R
import com.example.winss.utils.SharedPrefManager

class HabitWidgetProvider : AppWidgetProvider() {

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Called when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Called when the last widget is deleted
    }

    companion object {
        fun updateAppWidget(
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetId: Int
        ) {
            val sharedPrefManager = SharedPrefManager(context)
            val completedCount = sharedPrefManager.getCompletedHabitsCount()
            val totalCount = sharedPrefManager.getTotalHabitsCount()
            
            val percentage = if (totalCount > 0) {
                (completedCount * 100) / totalCount
            } else {
                0
            }

            val views = RemoteViews(context.packageName, R.layout.widget_habit_progress)
            
            // Set progress text
            views.setTextViewText(R.id.widget_progress_text, "$completedCount/$totalCount")
            views.setTextViewText(R.id.widget_percentage, "$percentage%")
            
            // Set progress bar
            views.setProgressBar(R.id.widget_progress_bar, 100, percentage, false)
            
            // Set click intent
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_container, pendingIntent)
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }
}
