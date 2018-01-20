package com.mythosapps.time15;

import android.app.Activity;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.List;

/**
 * Created by andreas on 25.07.17.
 */

public class RadioButtonUI {

    private Activity activity;
    private LinearLayout radioLayout;
    private RadioGroup rg;
    private List<RadioButtonItem> itemsList;

    public RadioButtonUI(Activity activity, List<RadioButtonItem> itemsList, Object selectedItem) {
        this.activity = activity;
        this.itemsList = itemsList;

        radioLayout = new LinearLayout(activity);
        radioLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        radioLayout.setOrientation(LinearLayout.HORIZONTAL);

        rg = new RadioGroup(activity);
        rg.setOrientation(LinearLayout.HORIZONTAL);

        int index = 0;
        int selectedIndex = 0;
        for (RadioButtonItem item : itemsList) {
            if (item.getValue().equals(selectedItem)) {
                selectedIndex = index;
            }
            RadioButton rb1 = new RadioButton(activity);
            rb1.setText(item.getLabel());
            rg.addView(rb1, index++);
            item.setButtonId(rb1.getId());
        }

        rg.check(itemsList.get(selectedIndex).getButtonId());
        radioLayout.addView(rg);
    }

    public LinearLayout getRadioLayout() {
        return radioLayout;
    }

    public Object getSelectedValue() {
        int selectedButtonId = rg.getCheckedRadioButtonId();
        Object selectedValue = itemsList.get(0).getValue();
        for (RadioButtonItem item : itemsList) {
            if (item.getButtonId() == selectedButtonId) {
                selectedValue = item.getValue();
            }
        }
        return selectedValue;
    }
}
