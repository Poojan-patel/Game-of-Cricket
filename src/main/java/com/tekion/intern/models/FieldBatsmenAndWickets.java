package com.tekion.intern.models;

public class FieldBatsmenAndWickets {
    private int strike;
    private int nonStrike;
    private int currentWickets;

    public FieldBatsmenAndWickets(int strike, int nonStrike, int currentWickets) {
        this.strike = strike;
        this.nonStrike = nonStrike;
        this.currentWickets = currentWickets;
    }

    public int getStrike() {
        return strike;
    }

    public int getNonStrike() {
        return nonStrike;
    }

    public int getCurrentWickets() {
        return currentWickets;
    }
}
