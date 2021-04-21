package com.mythosapps.time15;

import android.app.Activity;
import android.content.DialogInterface;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.mythosapps.time15.util.DateInputTextWatcher;

/**
 * Created by andreas on 20.04.21.
 */

public class MultiDayTaskPopupUI {

    private final Activity parent;
    private String okButtonText;
    private DialogInterface.OnClickListener okButtonListener;
    private String cancelButtonText;
    private EditText inputBeginn;
    private EditText inputEnde;

    public MultiDayTaskPopupUI(Activity parent) {
        this.parent = parent;
        inputBeginn = new EditText(parent);
        inputBeginn.setMinLines(1);
        inputBeginn.setMaxLines(1);
        TextWatcher twBeginn = new DateInputTextWatcher(inputBeginn);
        inputBeginn.addTextChangedListener(twBeginn);
        inputEnde = new EditText(parent);
        inputEnde.setMinLines(1);
        inputEnde.setMaxLines(1);
        TextWatcher twEnde = new DateInputTextWatcher(inputEnde);
        inputEnde.addTextChangedListener(twEnde);
    }

    public void setOkButton(String okButtonText, DialogInterface.OnClickListener okButtonListener) {
        this.okButtonText = okButtonText;
        this.okButtonListener = okButtonListener;
    }

    public void setCancelButton(String cancelButtonText) {
        this.cancelButtonText = cancelButtonText;
    }

    public void show() {
        initializeUI();
    }

    private void initializeUI() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle("Urlaub eintragen");

        LinearLayout linearLayout = new LinearLayout(parent);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 10, 20, 10);

        TextView labelVon = new TextView(parent);
        labelVon.setText(R.string.multi_task_from);
        labelVon.setPadding(10, 30, 10, 5);
        linearLayout.addView(labelVon);
        linearLayout.addView(inputBeginn);

        TextView labelBis = new TextView(parent);
        labelBis.setText(R.string.multi_task_to);
        labelBis.setPadding(10, 30, 10, 5);
        linearLayout.addView(labelBis);
        linearLayout.addView(inputEnde);

        builder.setView(linearLayout);


        builder.setPositiveButton(okButtonText, okButtonListener);
        builder.setNeutralButton(cancelButtonText, (dialog, which) -> dialog.cancel());

        final AlertDialog dialog = builder.show();
    }

    public EditText getInputBeginn() {
        return inputBeginn;
    }

    public EditText getInputEnde() {
        return inputEnde;
    }
}
