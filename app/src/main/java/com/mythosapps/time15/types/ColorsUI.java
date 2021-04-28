package com.mythosapps.time15.types;

import android.graphics.Color;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by andreas on 11.02.16.
 */
public class ColorsUI {

    // Colors
    public static final int DARK_BLUE_DEFAULT = Color.rgb(30, 144, 255); // #1E90FF
    public static final int DARK_GREEN_SAVE_SUCCESS = Color.rgb(0, 100, 0);
    public static final int DARK_GREY_SAVE_ERROR = Color.DKGRAY;
    public static final int LIGHT_GREY = Color.LTGRAY;
    public static final int SELECTION_NONE_BG = Color.TRANSPARENT;
    //public static final int SELECTION_BG = Color.rgb(173, 216, 230);//#add8e6
    public static final int SELECTION_BG = Color.rgb(156, 206, 255);//#D79CCEFF
    public static final int DEACTIVATED = Color.LTGRAY;
    public static final int RED_FLAGGED = Color.RED;
    public static final int ACTIVATED = Color.BLACK;

    public static final Map<Integer, Integer> choiceToColor = new HashMap<>();
    public static final Map<Integer, Integer> colorToChoice = new HashMap<>();
    // holo dark orange:
    public static final int DARK_ORANGE = Color.rgb(255, 255, 136); //#ffff8800
    public static final int PURPLE = Color.rgb(128, 10, 128); //#800080

    static {
        choiceToColor.put(0, ColorsUI.DARK_BLUE_DEFAULT);
        choiceToColor.put(1, ColorsUI.DARK_GREEN_SAVE_SUCCESS);
        choiceToColor.put(2, ColorsUI.DARK_GREY_SAVE_ERROR);
        colorToChoice.put(ColorsUI.DARK_BLUE_DEFAULT, 0);
        colorToChoice.put(ColorsUI.DARK_GREEN_SAVE_SUCCESS, 1);
        colorToChoice.put(ColorsUI.DARK_GREY_SAVE_ERROR, 2);
    }
}
