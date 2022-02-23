package com.tekion.intern.game;

public class Player {
    public enum PlayerType{
        BOWLER,
        BATSMAN,
        ALLROUNDER
    }

    enum TypeOfBowler{
        FAST,
        SPIN,
        MEDIUM,
        NA;

        public static TypeOfBowler fromStringToEnum(String type){
            if(type.equals("FAST"))
                return TypeOfBowler.FAST;
            else if(type.equals("SPIN"))
                return TypeOfBowler.SPIN;
            else if(type.equals("MEDIUM"))
                return TypeOfBowler.MEDIUM;
            else
                return TypeOfBowler.NA;
        }
    }
    private String name;
    private int score;
    private int ballsPlayed;
    private PlayerType playerType;
    private TypeOfBowler typeOfBowler;
    private int[] scoreDistribution;
    private int currentlyThrownBalls;
    private int wicketsTaken;

    public Player(String name, String type){
        this.name = name;
        this.score = 0;
        this.ballsPlayed = 0;
        if(type.equals("BATSMAN")){
            playerType = PlayerType.BATSMAN;
            typeOfBowler = TypeOfBowler.NA;
        }
        else{
            String[] bowlerType = type.split(",");
            if(bowlerType[0].equals("BOWLER"))
                playerType = PlayerType.BOWLER;
            else
                playerType = PlayerType.ALLROUNDER;
            typeOfBowler = TypeOfBowler.fromStringToEnum(bowlerType[1]);
        }
        scoreDistribution = new int[7];
        currentlyThrownBalls = 0;
    }

    public String getName() {
        return name;
    }

    public int getScore() {
        return score;
    }

    public void incrementScore(int score) {
        this.score += score;
        scoreDistribution[score]++;
    }

    public int getBallsPlayed() {
        return ballsPlayed;
    }

    public void incrementBallsPlayed() {
        this.ballsPlayed++;
    }

    public PlayerType getPlayerType() {
        return playerType;
    }

    public int remainingOvers(int maxOversCanBeThrown) {
        return (maxOversCanBeThrown - (int)Math.ceil(currentlyThrownBalls/6.0));
    }

    public void incrementNumberOfBallsThrown(){
        currentlyThrownBalls++;
    }

    public void incrementWicketsTaken() {
        wicketsTaken++;
    }

    public String getTypeOfBowler() {
        return typeOfBowler.toString();
    }

    @Override
    public String toString() {
        String scoreDistributionToString = "[";
        for(int i = 0; i < 6; i++)
            scoreDistributionToString += (i + ":" + scoreDistribution[i] + ", ");
        scoreDistributionToString += ("6:" + scoreDistribution[6] + "]");
        String objectAsString = String.format("%s: %d runs in %d balls, scorewise:%s", name, score, ballsPlayed, scoreDistributionToString);
        if(this.playerType != PlayerType.BATSMAN){
            objectAsString += String.format(" Overs taken:%d.%d, Wickets Taken:%d", currentlyThrownBalls/6, currentlyThrownBalls%6, wicketsTaken);
        }
        return objectAsString;
    }
}
