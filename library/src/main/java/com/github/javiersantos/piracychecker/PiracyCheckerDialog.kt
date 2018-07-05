package com.github.javiersantos.piracychecker

import android.app.Activity
import android.app.Dialog
import android.app.DialogFragment
import android.content.Context
import android.os.Bundle
import com.github.javiersantos.piracychecker.utils.buildUnlicensedDialog

class PiracyCheckerDialog : DialogFragment() {
    fun show(context: Context) {
        (context as? Activity)?.let {
            pcDialog?.show(it.fragmentManager, "[LICENSE_DIALOG]")
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle): Dialog? {
        isCancelable = false
        return activity.buildUnlicensedDialog(title.orEmpty(), content.orEmpty())
    }
    
    companion object {
        private var pcDialog: PiracyCheckerDialog? = null
        private var title: String? = null
        private var content: String? = null
        
        fun newInstance(dialogTitle: String, dialogContent: String): PiracyCheckerDialog? {
            pcDialog = PiracyCheckerDialog()
            title = dialogTitle
            content = dialogContent
            return pcDialog
        }
    }
}