package edu.msu.holsche2.project1;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

public class HowDlg extends DialogFragment {

    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(R.string.how_to);

        // Add action buttons
        builder.setPositiveButton(android.R.string.ok, (dialog, id) -> {});

        builder.setMessage(getString(R.string.how_to_desc));

        final Dialog dlg = builder.create();
        dlg.setOnShowListener(dialog -> {});

        return dlg;
    }
}
