package edu.rosehulman.scottae.justcheckingin.models;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Appointment implements Comparable<Appointment>{
    private String mTitle;
    private Date mDate;
    private String mComments;
    private String mKey;

    public Appointment() {
        // required empty constructor
    }


    public Appointment(String mTitle, Date mDate, String comments) {
        this.mTitle = mTitle;
        this.mDate = mDate;
        this.mComments = comments;
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

    public String getComments() {
        return mComments;
    }

    public void setComments(String comments) {
        this.mComments = comments;
    }

    public String getKey() {
        return mKey;
    }

    public void setKey(String key) {
        mKey = key;
    }

    @Override
    public int compareTo(@NonNull Appointment o) {
        return 0;
    }

    public void setValues(Appointment a) {
        mTitle = a.getTitle();
        mDate = a.getDate();
        mKey = a.getKey();
        mComments = a.getComments();
    }
}
