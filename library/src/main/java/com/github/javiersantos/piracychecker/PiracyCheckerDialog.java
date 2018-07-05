package com.github.javiersantos.piracychecker;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;

public class PiracyCheckerDialog extends DialogFragment {
    private static PiracyCheckerDialog dialog;
    private static String mTitle, mContent;

    public PiracyCheckerDialog() {
    }

    public static PiracyCheckerDialog newInstance(String dialogTitle, String dialogContent) {
        dialog = new PiracyCheckerDialog();
        mTitle = dialogTitle;
        mContent = dialogContent;

        return dialog;
    }

    protected void show(Context context) {
        dialog.show(((Activity) context).getFragmentManager(), "[LICENSE_DIALOG]");
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        setCancelable(false);
        return LibraryUtils.INSTANCE.buildUnlicensedDialog(getActivity(), mTitle,
                                                           mContent);
    }

}
