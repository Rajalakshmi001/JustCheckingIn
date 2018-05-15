package edu.rosehulman.scottae.justcheckingin.models;

import android.support.annotation.NonNull;

import java.util.Date;

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
