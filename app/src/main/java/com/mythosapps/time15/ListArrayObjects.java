package com.mythosapps.time15;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by andreas on 16.08.17.
 */

public class ListArrayObjects<T> {

    List<T> list;
    String[] itemsArray = null;

    public ListArrayObjects(List<T> list) {
        this.list = list;
    }

    public String[] getListItems() {

        if (itemsArray == null) {
            ArrayList<String> items = new ArrayList<String>();
            for (Object item : list) {
                items.add(item.toString());
            }
            itemsArray = items.toArray(new String[]{});
        }
        return itemsArray;
    }

    public T get(int position) {
        return list.get(position);
    }

    public void reload(List<T> list) {
        itemsArray = null;
        this.list = list;
    }
}
