package com.mythosapps.time15.types;

public enum BalanceType {

    /**
     * Counting all begin-to-end tasks this month, how many hours am I above / below the 8 hours
     * average?
     */
    BALANCE,

    /**
     * Counting only the 'work' task, how many hours have I worked this month on average?
     * Works great in a permanent position, where I'm supposed to enter 8 hours on each day that I
     * work on. Consequently, the total hours of work is divided by the number of those days.
     * It's like BALANCE, but shows you not the deviation from the 8 hours day, but instead the
     * hours achived on work days on average.
     */
    AVERAGE_WORK,

    /**
     * Counting only the 'work' task, how many hours have I worked this month on average?
     * Works great for freelancers, who can work that extra hour on a holiday, and no-one expects
     * them to enter 8 hours on a holiday. They can work whenever they like and the totol hours of
     * work is divided by 8.
     */
    AVERAGE_WORK_FREELANCE;
}
