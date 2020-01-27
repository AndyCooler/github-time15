package com.mythosapps.time15.types;

public enum BalanceType {

    /**
     * Counting all begin-to-end tasks this month, how many hours am I above / below the 8 hours
     * average?
     */
    BALANCE,

    /**
     * Counting only the 'work' task, how many hours have I worked this month on average?
     */
    AVERAGE_WORK;
}
