package edu.rosehulman.scottae.justcheckingin.models;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Reminder implements Comparable<Reminder> {

    private String mTitle;
    private Date mDate;
    private String mKey;

    public Reminder() {
        // required empty constructor
    }

    public Reminder(String mTitle, Date mDate) {
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

    public Date getDate(Date date) {
        DateFormat format = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
        try {
            date = format.parse(format.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    public void setDate(Date mDate) {
        this.mDate = mDate;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String mKey) {
        this.mKey = mKey;
    }

    public void setValues(Reminder r) {
        mTitle = r.getTitle();
        mDate = r.getDate();
        mKey = r.getKey();
    }

    @Override
    public int compareTo(@NonNull Reminder o) {
        return this.getDate().compareTo(o.getDate());
    }
}
