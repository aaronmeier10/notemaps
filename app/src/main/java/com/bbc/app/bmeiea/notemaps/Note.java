package com.bbc.app.bmeiea.notemaps;

/**
 * Created by bmeiea on 29.03.2016.
 */
public class Note {
    String titel = "";
    String text = "";
    long time = 0;
    float x = 0;
    float y = 0;

    public Note(String titel, String text, long time, float x, float y){
        this.titel = titel;
        this.text = text;
        this.time = time;
        this.x = x;
        this.y = y;
    }

}
