package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Team;
import com.tekion.cricket.models.BattingTeam;

import java.util.List;

public interface TeamRepository {
    String save(Team team);

    List<Team> findAll();

    BattingTeam fetchTeamScoreFromMatchId(String matchId, String battingTeamId);

    List<Integer> fetchFirstTwoPlayers(String team1Id);

    String getTeamNameByTeamId(String team2Id);
}