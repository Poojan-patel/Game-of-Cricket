package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Strike;

public interface TeamInPlayRepository {

    void updateBowlerByTeamAndMatchId(int bowlerId, int matchId, int teamId);

    void updateStrikesByTeamAndMatchId(Strike strike);

    void insertStrike(int currentStrike, int currentNonStrike, int matchId, int teamId);

    int fetchTheLastOver(Integer matchId, Integer currentBowlTeamId);

    Strike fetchStrikeDetails(int matchId, int currentBatTeamId);
}
