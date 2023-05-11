package edu.msu.holsche2.project1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class WaitingDlg extends DialogFragment {

    private boolean dismissed;

    public boolean isDismissed() {
        return dismissed;
    }

    public void setDismissed(boolean dismissed) {
        this.dismissed = dismissed;
    }

    /**
     * Create the dialog box
     */
    @Override
    public Dialog onCreateDialog(Bundle bundle) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Set the title
        builder.setTitle(R.string.waiting);

        builder.setNegativeButton(android.R.string.cancel,
                (dialog, id) -> {
                    dialog.dismiss();
                    setDismissed(true);
                });

        dismissed = false;

        // Create the dialog box
        final AlertDialog dlg = builder.create();
        return dlg;
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        setDismissed(true);
    }
}
