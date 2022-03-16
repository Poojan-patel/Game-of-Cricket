package com.tekion.cricket.enums;

public enum UnfairBallType {
    WIDE,
    NO,
    NA;

    public static UnfairBallType fromStringToEnum(String unfairBall){
        if(unfairBall == null) {
            return UnfairBallType.NA;
        }
        switch(unfairBall){
            case "WIDE":
                return UnfairBallType.WIDE;
            case "NO":
            case "NO BALL":
                return UnfairBallType.NO;
            case "":
                return UnfairBallType.NA;
            default:
                throw new IllegalStateException("State Doesn't Exists");
        }
    }
}
