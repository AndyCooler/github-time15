package com.mythosapps.time15;

/**
 * Created by andreas on 28.07.17.
 */

public class RadioButtonItem {

    private String label;

    private Object value;

    private int buttonId;

    public RadioButtonItem(String label, Object value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public Object getValue() {
        return value;
    }

    public int getButtonId() {
        return buttonId;
    }

    public void setButtonId(int buttonId) {
        this.buttonId = buttonId;
    }
}
