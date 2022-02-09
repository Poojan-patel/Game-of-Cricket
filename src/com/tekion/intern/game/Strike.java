package com.tekion.intern.game;

class Strike{
    private int currentWickets;
    private int[] strikeHolders;
    private int currentStrike;

    public Strike(){
        this.currentStrike = 0;
        this.strikeHolders = new int[]{0, 1};
        currentWickets = 0;
    }

    public void reInit() {
        this.currentStrike = 0;
        this.strikeHolders = new int[]{0, 1};
        currentWickets = 0;
    }

    public void overChanged(){
        currentStrike = (currentStrike+1)%2;
    }

    public void changeStrike(int run){
        if(run%2 == 1)
            currentStrike = (currentStrike+1)%2;
    }

    public int getCurrentStrike() {
        return strikeHolders[currentStrike];
    }

    public int updateOnWicket(){
        currentWickets++;
        int maxOrder = Integer.max(strikeHolders[0], strikeHolders[1]);
        int outPlayer = strikeHolders[currentStrike];
        strikeHolders[currentStrike] = maxOrder+1;
        return outPlayer;
    }

    public int totalWickets(){
        return currentWickets;
    }
}
