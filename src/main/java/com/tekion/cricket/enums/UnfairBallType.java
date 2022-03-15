package com.tekion.cricket.enums;

public enum UnfairBallType {
    WIDE,
    NO,
    NA;

    public static UnfairBallType fromStringToEnum(String unfairBall){
        if(unfairBall == null)
            return UnfairBallType.NA;
        else if(unfairBall.equals("WIDE"))
            return UnfairBallType.WIDE;
        else
            return UnfairBallType.NO;
    }
}
