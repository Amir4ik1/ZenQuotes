package ru.graduate.zenquotes

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color.Companion.Black
import androidx.core.content.ContextCompat
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import dagger.hilt.android.AndroidEntryPoint
import ru.graduate.zenquotes.navigation.AppNavigation
import ru.graduate.zenquotes.navigation.BottomNavAnimation
import ru.zenquotes.common.callback.MainActivityCallback
import ru.zenquotes.common.utils.Constants
import ru.zenquotes.common.utils.Screen
import ru.zenquotes.core.workers.WorkerStatusObserver
import ru.zenquotes.feature_widget.workmanager.notification.SchedulingNotification
import ru.zenquotes.feature_widget.workmanager.widget.SchedulingWidgetRefresh
import ru.zenquotes.theme.theme.ZenQuotesTheme
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity(), MainActivityCallback {

    @Inject
    lateinit var scheduleNotification: SchedulingNotification

    @Inject
    lateinit var scheduleWidget: SchedulingWidgetRefresh

    override fun getActivity(): ComponentActivity = this

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.auto(
                Color.TRANSPARENT,
                Color.TRANSPARENT
            )
        )
        setContent {
            ZenQuotesTheme {

                scheduleNotification.schedulingNotification()

                Handler(Looper.getMainLooper()).postDelayed({
                    scheduleWidget.scheduleWidgetRefresh()
                }, 5000)

                requestNecessaryPermissions()
                WorkerStatusObserver.observeQuotesWorkers(this, this)

                val navHost = rememberNavController()

                Scaffold(
                    containerColor = Black, bottomBar = {

                        val currentBackStackEntry by navHost.currentBackStackEntryAsState()
                        val currentDestination = currentBackStackEntry?.destination?.route

                        val currentScreen = Screen.values.firstOrNull {
                            it.route == currentDestination
                        }

                        currentScreen?.let {
                            if (it.needBottomNav) {
                                BottomNavAnimation(navHost)
                            }
                        }

                    })
                { paddingValues ->
                    AppNavigation(navHost = navHost, paddingValues = paddingValues)
                }

            }
        }
    }

    private fun requestNecessaryPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            requestNotificationPermission()
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            requestWriteExternalStoragePermission()
        }
    }

    private fun requestWriteExternalStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                Constants.REQUEST_CODE_WRITE_STORAGE
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestNotificationPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                Constants.REQUEST_CODE_NOTIFICATION
            )
        }
    }

}