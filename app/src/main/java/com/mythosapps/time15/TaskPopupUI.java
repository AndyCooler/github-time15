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

import com.mythosapps.time15.types.KindOfDay;

/**
 * Created by andreas on 22.02.18.
 */

// TODO https://fabric.io/kits/android/crashlytics/install

public class TaskPopupUI {

    private final Activity parent;
    private final String title;
    private final String kindOfDay;
    private String okButtonText;
    private DialogInterface.OnClickListener okButtonListener;
    private String cancelButtonText;
    private EditText inputTextField;
    private RadioGroup radioGroup;
    private CheckBox checkBox;
    private boolean taskNameEditable;

    public TaskPopupUI(Activity parent, String title, String kindOfDay, boolean taskNameEditable) {
        this.parent = parent;
        this.title = title;
        this.kindOfDay = kindOfDay;
        this.taskNameEditable = taskNameEditable;
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
        builder.setTitle(title);

        LinearLayout linearLayout = new LinearLayout(parent);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        inputTextField = new EditText(parent);
        inputTextField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_NORMAL);
        inputTextField.setText(kindOfDay);
        if (taskNameEditable) {
            inputTextField.setSelection(0, kindOfDay.length());
        }
        inputTextField.setEnabled(taskNameEditable);


        checkBox = new CheckBox(parent);
        checkBox.setText(R.string.edit_task_with_begin_end);
        checkBox.setChecked(KindOfDay.fromString(kindOfDay).isBeginEndType());

        LinearLayout radioLayout = new LinearLayout(parent);
        radioLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        radioLayout.setOrientation(LinearLayout.HORIZONTAL);
        radioGroup = new RadioGroup(parent);
        radioGroup.setOrientation(LinearLayout.HORIZONTAL);
        RadioButton rb1 = new RadioButton(parent);
        rb1.setText(R.string.edit_task_color_blue);
        radioGroup.addView(rb1, 0);
        RadioButton rb2 = new RadioButton(parent);
        rb2.setText(R.string.edit_task_color_green);
        radioGroup.addView(rb2, 1);
        RadioButton rb3 = new RadioButton(parent);
        rb3.setText(R.string.edit_task_color_gray);
        radioGroup.addView(rb3, 2);
        radioLayout.addView(radioGroup);

        TextView label = new TextView(parent);
        label.setText(R.string.edit_task_color_in_month_overview);

        linearLayout.addView(inputTextField);
        linearLayout.addView(checkBox);
        linearLayout.addView(label);
        linearLayout.addView(radioLayout);

        builder.setView(linearLayout);


        builder.setPositiveButton(okButtonText, okButtonListener);
        builder.setNeutralButton(cancelButtonText, (dialog, which) -> dialog.cancel());

        final AlertDialog dialog = builder.show();
    }

    public EditText getInputTextField() {
        return inputTextField;
    }

    public RadioGroup getInputRadioButtonGroup() {
        return radioGroup;
    }

    public CheckBox getCheckBox() {
        return checkBox;
    }
}
