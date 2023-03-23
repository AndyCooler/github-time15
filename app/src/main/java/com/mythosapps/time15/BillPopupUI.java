package com.mythosapps.time15;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.mythosapps.time15.util.BuchungUtil;

/**
 * Created by andreas on 23.03.23.
 */

public class BillPopupUI {

    private final Activity parent;
    private final Integer billableMinutes;

    private double rate;
    private String okButtonText;
    private DialogInterface.OnClickListener okButtonListener;

    private EditText stundensatz;
    private TextView nettoBetrag;

    private TextView steuerBetrag;

    private TextView bruttoBetrag;

    public BillPopupUI(Activity parent, Integer billableMinutes, double rate) {
        this.parent = parent;
        this.billableMinutes = billableMinutes;
        this.rate = rate;
        stundensatz = new EditText(parent);
        stundensatz.setMinLines(1);
        stundensatz.setMaxLines(1);
        stundensatz.setText(String.valueOf(rate));
        stundensatz.addTextChangedListener(new MyTextWatcher());
        stundensatz.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focused) {
                InputMethodManager keyboard = (InputMethodManager) parent.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (focused)
                    keyboard.showSoftInput(stundensatz, 0);
                else
                    keyboard.hideSoftInputFromWindow(stundensatz.getWindowToken(), 0);
            }
        });
        nettoBetrag = new EditText(parent);
        nettoBetrag.setEnabled(false);
        nettoBetrag.setMinLines(1);
        nettoBetrag.setMaxLines(1);
        steuerBetrag = new EditText(parent);
        steuerBetrag.setEnabled(false);
        steuerBetrag.setMinLines(1);
        steuerBetrag.setMaxLines(1);
        bruttoBetrag = new EditText(parent);
        bruttoBetrag.setEnabled(false);
        bruttoBetrag.setMinLines(1);
        bruttoBetrag.setMaxLines(1);
        calc(billableMinutes);
    }

    private void calc(Integer billableMinutes) {
        if (billableMinutes != null) {
            nettoBetrag.setText(BuchungUtil.getNettoForDisplay(rate, billableMinutes));
            steuerBetrag.setText(BuchungUtil.getTaxForDisplay(rate, billableMinutes));
            bruttoBetrag.setText(BuchungUtil.getBruttoForDisplay(rate, billableMinutes));
        }
    }

    public void setOkButton(String okButtonText, DialogInterface.OnClickListener okButtonListener) {
        this.okButtonText = okButtonText;
        this.okButtonListener = okButtonListener;
    }

    public void show() {
        initializeUI();
    }

    private void initializeUI() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(parent);
        builder.setTitle("Rechnung für Aufgabe 'Arbeit'");

        LinearLayout linearLayout = new LinearLayout(parent);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setPadding(20, 10, 20, 10);

        TextView labelStundensatz = new TextView(parent);
        labelStundensatz.setText(R.string.bill_rate);
        labelStundensatz.setPadding(10, 30, 10, 5);
        linearLayout.addView(labelStundensatz);
        linearLayout.addView(stundensatz);

        TextView labelVon = new TextView(parent);
        labelVon.setText(R.string.bill_netto);
        labelVon.setPadding(10, 30, 10, 5);
        linearLayout.addView(labelVon);
        linearLayout.addView(nettoBetrag);

        TextView labelTax = new TextView(parent);
        labelTax.setText(R.string.bill_tax);
        labelTax.setPadding(10, 30, 10, 5);
        linearLayout.addView(labelTax);
        linearLayout.addView(steuerBetrag);

        TextView labelBis = new TextView(parent);
        labelBis.setText(R.string.bill_brutto);
        labelBis.setPadding(10, 30, 10, 5);
        linearLayout.addView(labelBis);
        linearLayout.addView(bruttoBetrag);

        builder.setView(linearLayout);


        builder.setPositiveButton(okButtonText, okButtonListener);

        builder.show();
    }

    public double getRate() {
        return rate;
    }

    private class MyTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // do nothing
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // do nothing
        }

        @Override
        public void afterTextChanged(Editable s) {
            try {
                System.out.println("afterTextChanged");
                rate = Double.valueOf(stundensatz.getText().toString());
                calc(billableMinutes);
            } catch (NumberFormatException e) {
                // ignore
            }
        }
    }
}
