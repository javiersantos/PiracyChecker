package com.github.javiersantos.piracychecker

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.DialogFragment
import com.github.javiersantos.piracychecker.utils.buildUnlicensedDialog

class PiracyCheckerDialog : DialogFragment() {
    fun show(context: Context) {
        (context as? AppCompatActivity)?.let {
            pcDialog?.show(it.supportFragmentManager, "[LICENSE_DIALOG]")
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        super.onCreateDialog(savedInstanceState)
        isCancelable = false
        return activity?.buildUnlicensedDialog(title.orEmpty(), content.orEmpty())!!
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