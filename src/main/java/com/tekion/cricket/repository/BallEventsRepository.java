package com.tekion.cricket.repository;

import com.tekion.cricket.dbconnector.MySqlConnector;
import com.tekion.cricket.models.MatchResult;
import com.tekion.cricket.util.ReaderUtil;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public interface BallEventsRepository{
    void insertEvent
            (int matchId, int teamId, int ballNumber, int batsmanId, int bowlerId, int score, String extras, String wicket);

    MatchResult generateFinalScoreBoard(int matchId);

    Map<Integer, Integer> fetchBowlersWithThrownOversByTeamAndMatchId(Integer matchId, Integer currentBowlTeamId);

    int fetchScoreToChase(int matchId, int currentBowlTeamId);

}
