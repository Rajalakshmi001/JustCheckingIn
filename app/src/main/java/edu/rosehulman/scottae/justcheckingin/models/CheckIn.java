package edu.rosehulman.scottae.justcheckingin.models;

import android.support.annotation.NonNull;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CheckIn implements Comparable<CheckIn>{
    private String mComment;
    private Date mDate;
    private boolean mResponse;

    public CheckIn() {
        //empty default constructor
    }
    public CheckIn(String comment, Date date) {
        mComment = comment;
        mDate = date;
    }

    public String getComment() {
        return mComment;
    }

    public void setComment(String mComment) {
        this.mComment = mComment;
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

    public boolean isResponse() {
        return mResponse;
    }

    public void setResponse(boolean mResponse) {
        this.mResponse = mResponse;
    }

    @Override
    public int compareTo(@NonNull CheckIn o) {
        return 0;
    }
}
