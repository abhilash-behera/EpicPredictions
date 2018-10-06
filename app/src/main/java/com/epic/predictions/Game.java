package com.epic.predictions;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by abhilash on 4/3/18
 */

public class Game implements Serializable {
    @SerializedName("recordID")
    @Expose
    private String recordID;
    @SerializedName("time")
    @Expose
    private String time;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("league")
    @Expose
    private String league;
    @SerializedName("teamA")
    @Expose
    private String teamA;
    @SerializedName("teamAscore")
    @Expose
    private String teamAscore;
    @SerializedName("teamB")
    @Expose
    private String teamB;
    @SerializedName("teamBscore")
    @Expose
    private String teamBscore;
    @SerializedName("tip")
    @Expose
    private String tip;
    @SerializedName("odds")
    @Expose
    private String odds;
    @SerializedName("status")
    @Expose
    private String status;

    private int count;

    public int getCount(){return count;}
    public void setCount(int count){this.count=count;}
    public String getTime() {
        return time;
    }
    public void setTime(String time) {
        this.time = time;
    }
    public String getDate() {
        return date;
    }
    public void setDate(String date) {
        this.date = date;
    }
    public String getLeague() {
        return league;
    }
    public String getTeamA() {
        return teamA;
    }
    public String getTeamAscore() {
        return teamAscore;
    }
    public String getTeamB() {
        return teamB;
    }
    public String getTeamBscore() {
        return teamBscore;
    }
    public String getTip() {
        return tip;
    }
    public String getOdds() {
        return odds;
    }
    public String getStatus() {
        return status;
    }

}
