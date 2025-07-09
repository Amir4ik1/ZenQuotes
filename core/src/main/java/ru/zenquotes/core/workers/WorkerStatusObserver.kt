package ru.zenquotes.core.workers

import android.content.Context
import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.work.WorkManager
import ru.zenquotes.common.utils.Constants

object WorkerStatusObserver {

    fun observeQuotesWorkers(context: Context, lifecycleOwner: LifecycleOwner) {
        val workManager = WorkManager.getInstance(context)

        observeWorkStatus(workManager, lifecycleOwner, "quotes_widget_update", "WidgetUpdate")
        observeWorkStatus(workManager, lifecycleOwner, "quotes_notification", Constants.WORK_MANAGER_STATUS_NOTIFY)
    }

    private fun observeWorkStatus(
        workManager: WorkManager,
        lifecycleOwner: LifecycleOwner,
        uniqueWorkName: String,
        logTag: String
    ) {
        workManager.getWorkInfosForUniqueWorkLiveData(uniqueWorkName)
            .observe(lifecycleOwner) { workInfoList ->
                if (workInfoList.isNotEmpty()) {
                    workInfoList.forEach { workInfo ->
                        Log.d(logTag, "Work State: ${workInfo.state}")
                    }
                } else {
                    Log.d(logTag, "No Any Work Found")
                }
            }
    }
}