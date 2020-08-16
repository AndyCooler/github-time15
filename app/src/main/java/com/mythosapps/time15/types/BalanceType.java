package com.mythosapps.time15.types;

public enum BalanceType {

    /**
     * Counting all begin-to-end tasks this month, how many hours am I above / below the 8 hours
     * average?
     */
    BALANCE,

    /**
     * Counting only the 'work' task, how many hours have I worked this month on average?
     * I'm supposed to work 8 hours each weeek day of the week, i.e. Monday thru Friday.
     * The total hours I worked in a week is divided by the number of those 5 days.
     * So if I work an extra hour on a Sunday, it's still in that week and it's counted as if
     * I stayed an hour longer on Friday (so Saturdays and Sundays don't count, it's still divided
     * by 5 no matter what because no-one expects us to work 8 hours on a weekend day).
     * It's like BALANCE, but shows you not the deviation from the 8 hours day, but instead the
     * hours achived on work days on average.
     */
    AVERAGE_WORK;
}
