package com.mobileappclass.assignment3;

/**
 * Created by Careena on 11/6/16.
 */
public class Students {
    String date ;
    String netid ;
    String x ;
    String y;

    public Students(){
    }

    public Students(String date, String netid, String xCord, String yCord){
        this.date = date;
        this.netid = netid;
        this.x = xCord;
        this.y = yCord;
    }

    public String getDate(){
        return date;
    }

    public String getNetid() {
        return netid;
    }

    public String getX() {
        return x;
    }

    public String getY() {
        return y;
    }
}
