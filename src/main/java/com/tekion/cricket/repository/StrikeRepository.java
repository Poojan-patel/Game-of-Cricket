package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Strike;

public interface StrikeRepository {

    void updateBowlerByTeamAndMatchId(int bowlerOrder, String matchId, String bowlingTeam);

    void updateStrikesByTeamAndMatchId(Strike strike);

    void save(Strike strike);

    int fetchTheLastOver(String matchId, String currentBowlTeamId);

    Strike fetchStrikeDetails(String matchId, String currentBatTeamId);
}
