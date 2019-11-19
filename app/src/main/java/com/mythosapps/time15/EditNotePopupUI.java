package com.mythosapps.time15;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.mythosapps.time15.types.BeginEndTask;
import com.mythosapps.time15.types.KindOfDay;

/**
 * Created by andreas on 07.12.18.
 */

public class EditNotePopupUI {

    private final Activity parent;
    private String okButtonText;
    private DialogInterface.OnClickListener okButtonListener;
    private String cancelButtonText;
    private EditText inputTextField;

    public EditNotePopupUI(Activity parent, String note) {
        this.parent = parent;
        inputTextField = new EditText(parent);
        inputTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
        inputTextField.setMinLines(1);
        inputTextField.setMaxLines(3);
        inputTextField.setMaxLines(5);
        inputTextField.setText(note);
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
        builder.setTitle("Edit note");

        LinearLayout linearLayout = new LinearLayout(parent);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(inputTextField);

        builder.setView(linearLayout);


        builder.setPositiveButton(okButtonText, okButtonListener);
        builder.setNeutralButton(cancelButtonText, (dialog, which) -> dialog.cancel());

        final AlertDialog dialog = builder.show();
    }

    public EditText getInputTextField() {
        return inputTextField;
    }
}
