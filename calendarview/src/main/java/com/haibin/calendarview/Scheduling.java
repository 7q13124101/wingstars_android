package com.haibin.calendarview;

import java.io.Serializable;

public class Scheduling implements Serializable {
    private static final long serialVersionUID = 141315161718191143L;

    private String text;

    private int textColor;

    private String time;

    private int timeColor;

    private boolean isHalf = true;

    private int backgroundColor;

    //个人班表API返回的值
    private String shiftDate;
    private int dayOfType;
    private String shiftName;
    private String shiftStart;
    private String shiftEnd;
    private String breakStart;
    private String breakEnd;
    private float shiftWorkhour;
    private String shiftColor;
    private String showType = "all";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public int getTimeColor() {
        return timeColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }

    public boolean isHalf() {
        return isHalf;
    }

    public void setHalf(boolean half) {
        isHalf = half;
    }

    public int getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(int backgroundColor) {
        this.backgroundColor = backgroundColor;
    }


    public String getShiftDate() {
        return shiftDate;
    }

    public void setShiftDate(String shiftDate) {
        this.shiftDate = shiftDate;
    }

    public int getDayOfType() {
        return dayOfType;
    }

    public void setDayOfType(int dayOfType) {
        this.dayOfType = dayOfType;
    }

    public String getShiftName() {
        return shiftName;
    }

    public void setShiftName(String shiftName) {
        this.shiftName = shiftName;
    }

    public String getShiftStart() {
        return shiftStart;
    }

    public void setShiftStart(String shiftStart) {
        this.shiftStart = shiftStart;
    }

    public String getShiftEnd() {
        return shiftEnd;
    }

    public void setShiftEnd(String shiftEnd) {
        this.shiftEnd = shiftEnd;
    }

    public String getBreakStart() {
        return breakStart;
    }

    public void setBreakStart(String breakStart) {
        this.breakStart = breakStart;
    }

    public String getBreakEnd() {
        return breakEnd;
    }

    public void setBreakEnd(String breakEnd) {
        this.breakEnd = breakEnd;
    }

    public float getShiftWorkhour() {
        return shiftWorkhour;
    }

    public void setShiftWorkhour(float shiftWorkhour) {
        this.shiftWorkhour = shiftWorkhour;
    }

    public String getShiftColor() {
        return shiftColor;
    }

    public void setShiftColor(String shiftColor) {
        this.shiftColor = shiftColor;
    }

    public String getShowType() {
        return showType;
    }

    public void setShowType(String showType) {
        this.showType = showType;
    }
}
