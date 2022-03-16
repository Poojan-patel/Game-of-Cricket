package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Strike;

public interface StrikeRepository {

    void updateBowlerByTeamAndMatchId(int bowlerId, int matchId, int teamId);

    void updateStrikesByTeamAndMatchId(Strike strike);

    void insertStrike(Strike strike);

    int fetchTheLastOver(Integer matchId, Integer currentBowlTeamId);

    Strike fetchStrikeDetails(int matchId, int currentBatTeamId);
}
