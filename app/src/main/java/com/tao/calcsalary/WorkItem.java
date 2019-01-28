package com.tao.calcsalary;

public class WorkItem{

    private int rowid;
    private int jikyu;
    private int hour;
    private int minute;
    private String changed;
    private String memo;

    public WorkItem(int rowid, int jikyu, int hour, int minute, String changed, String memo){
        this.rowid = rowid;
        this.jikyu = jikyu;
        this.hour = hour;
        this.minute = minute;
        this.changed = changed;
        this.memo = memo;
    }

    public int getRowid(){
        return rowid;
    }

    public int getJikyu(){
        return jikyu;
    }

    public int getHour(){
        return hour;
    }

    public int getMinute(){
        return minute;
    }

    public String getChanged(){
        return changed;
    }

    public String getMemo(){
        return memo;
    }

}
