package com.hanynemr.s7dquizapp;

import java.io.Serializable;
import java.util.Objects;

public class Player implements Serializable {
    private String name;
    private byte score;

    private byte turns;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte getScore() {
        return score;
    }

    public byte getTurns() { return turns; }

    public void turns(){this.turns++;}


    public void setScore(byte score) {
        this.score = score;
    }

    public Player() {
    }

    public Player(String name, byte score) {
        this.name = name;
        this.score = score;
        this.turns=0;
    }

}
