package com.eece451.aubcovax

import android.app.Activity
import android.app.Dialog
import java.lang.ref.WeakReference

class ProgressBarManager {

    private var progressBar: Dialog? = null
    private var currentActivity: WeakReference<Activity>? = null

    fun showProgressBar(activity: Activity) {
        currentActivity = WeakReference(activity)
        progressBar = Dialog(activity)
        progressBar?.apply {
            setContentView(R.layout.progress_bar)
            setCancelable(false)
            setCanceledOnTouchOutside(false)
            show()
        }
    }

    fun hideProgressBar() {
        progressBar?.dismiss()
        progressBar = null
        currentActivity?.clear()
        currentActivity = null
    }
}


