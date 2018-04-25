package edu.rosehulman.scottae.justcheckingin;

import java.util.Date;

public class Appointment {
    private String mTitle;
    private Date mDate;

    public Appointment(String mTitle, Date mDate) {
        this.mTitle = mTitle;
        this.mDate = mDate;
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
}
