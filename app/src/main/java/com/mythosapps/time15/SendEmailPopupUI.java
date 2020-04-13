package com.mythosapps.time15;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

/**
 * Created by andreas on 07.12.18.
 */

public class SendEmailPopupUI {

    private final Activity parent;
    private String okButtonText;
    private DialogInterface.OnClickListener okButtonListener;
    private String cancelButtonText;
    private EditText inputTextField;
    private String monthYear;
    private String DEFAULT_TEXT = "<email address>";

    public SendEmailPopupUI(Activity parent, String monthYear) {
        this.parent = parent;
        this.monthYear = monthYear;
        inputTextField = new EditText(parent);
        inputTextField.setInputType(InputType.TYPE_CLASS_TEXT);
        inputTextField.setMinLines(1);
        inputTextField.setMaxLines(1);
        inputTextField.setText(DEFAULT_TEXT);
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
        builder.setTitle(parent.getString(R.string.send_email_title) + " " + monthYear);

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
