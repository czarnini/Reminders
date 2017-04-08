package com.apress.gerber.reminders;

/**
 * Created by Michał on 08.04.2017.
 */

public class Reminder
{
    private int mID;
    private String mContent;
    private int mImportant;
    public Reminder()
    {

    }

    public Reminder(int id, String content, int important) {
        mID = id;
        mContent = content;
        mImportant = important;
    }

    public int getmID() {
        return mID;
    }

    public void setmID(int mID) {
        this.mID = mID;
    }

    public String getmContent() {
        return mContent;
    }

    public void setmContent(String mContent) {
        this.mContent = mContent;
    }

    public int getmImportant() {
        return mImportant;
    }

    public void setmImportant(int mImportant) {
        this.mImportant = mImportant;
    }
}
