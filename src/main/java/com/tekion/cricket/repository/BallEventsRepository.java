package com.tekion.cricket.repository;

import com.tekion.cricket.beans.BallEvent;
import com.tekion.cricket.models.MatchResult;

import java.util.List;
import java.util.Map;

public interface BallEventsRepository{
    void save
            (BallEvent ballEvent);

    MatchResult generateFinalScoreBoard(String matchId);

    Map<Integer, Integer> fetchBowlersWithThrownOversByTeamAndMatchId(String matchId, String currentBowlTeamId);

    int fetchScoreToChase(String matchId, String currentBowlTeamId);

    List<BallEvent> fetchAllEventsByMatchAndTeamId(String matchId, String teamId);
}
