package com.tekion.cricket.repository;

import com.tekion.cricket.beans.BallEvent;
import com.tekion.cricket.models.MatchResult;

import java.util.List;
import java.util.Map;

public interface BallEventsRepository{
    void save
            (BallEvent ballEvent);

    MatchResult generateFinalScoreBoard(int matchId);

    Map<Integer, Integer> fetchBowlersWithThrownOversByTeamAndMatchId(Integer matchId, Integer currentBowlTeamId);

    int fetchScoreToChase(int matchId, int currentBowlTeamId);

    List<BallEvent> fetchAllEventsByMatchAndTeamId(int matchId, int teamId);
}
