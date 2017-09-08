package com.mythosapps.time15;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mythosapps.time15.types.KindOfDay;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by andreas on 07.08.17.
 */

public class ListArrayAdapter<T> extends ArrayAdapter {

    private final Activity activity;

    private Comparator comparator;

    public ListArrayAdapter(Activity activity, int item, int taskName, List<T> list, Comparator<T> comparator) {
        super(activity, item, taskName, list);
        this.activity = activity;
        this.comparator = comparator;
        sort(comparator);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        TextView textView = null;
        View view = super.getView(position, convertView, parent);
        if (view instanceof LinearLayout) {
            LinearLayout layout = (LinearLayout) super.getView(position, convertView, parent);
            textView = (TextView) layout.getChildAt(0);
        } else {
            textView = (TextView) view;
        }

        int textColor = ((KindOfDay) getItem(position)).getColor(); // TODO no casting here
        textView.setTextColor(textColor);

        return textView;
    }

    public void addAllListItems(Collection<? extends T> collection) {
        super.addAll(collection);
        sort(comparator);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        //list.reload(configStorage.loadConfigXml(activity));
        //clear();
        //addAll(KindOfDay.listNames); // hier muss nur noch das namen array mit der reihenfoge der objekte in einklang gebracht werden
        //sort(comp);
    }
}
