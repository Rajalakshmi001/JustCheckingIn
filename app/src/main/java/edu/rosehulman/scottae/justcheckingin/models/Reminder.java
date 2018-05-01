package edu.rosehulman.scottae.justcheckingin.models;

import java.util.Date;

public class Reminder {

    private String mTitle;
    private Date mDate;
    private boolean mIsRecurring;

    public Reminder(String mTitle, Date mDate, boolean mIsRecurring) {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mIsRecurring = mIsRecurring;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public boolean isIsRecurring() {
        return mIsRecurring;
    }

    public void setIsRecurring(boolean mIsRecurring) {
        this.mIsRecurring = mIsRecurring;
    }
}
