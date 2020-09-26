package com.mythosapps.time15;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.text.InputType;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.PreferenceManager;

import com.mythosapps.time15.types.ColorsUI;

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
    private String DEFAULT_TEXT = "<Email Empfänger, siehe Settings im Menü>";

    public SendEmailPopupUI(Activity parent) {
        this.parent = parent;
        inputTextField = new EditText(parent);
        inputTextField.setInputType(InputType.TYPE_CLASS_TEXT);
        inputTextField.setMinLines(1);
        inputTextField.setMaxLines(1);
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
        builder.setTitle(parent.getString(R.string.send_email_title));

        LinearLayout linearLayout = new LinearLayout(parent);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        linearLayout.addView(inputTextField);

        builder.setView(linearLayout);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(parent);
        String email = sharedPreferences.getString("settings_backup_email_address", DEFAULT_TEXT);
        if (email == null || "".equals(email.trim())) {
            email = DEFAULT_TEXT;
        }
        inputTextField.setText(email);
        if (DEFAULT_TEXT.equals(email)) {
            inputTextField.setTextColor(ColorsUI.DEACTIVATED);
        }

        builder.setPositiveButton(okButtonText, okButtonListener);
        builder.setNeutralButton(cancelButtonText, (dialog, which) -> dialog.cancel());

        final AlertDialog dialog = builder.show();
    }

    public EditText getInputTextField() {
        return inputTextField;
    }
}
