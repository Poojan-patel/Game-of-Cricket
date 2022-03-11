package com.tekion.cricket.repository;

import com.tekion.cricket.models.BattingTeam;
import com.tekion.cricket.models.TeamDTO;

import java.util.List;

public interface TeamRepository {
    Integer save(TeamDTO team);

    List<TeamDTO> findAll();

    BattingTeam fetchTeamScoreFromMatchId(int matchId, int battingTeamId);

    List<Integer> fetchFirstTwoPlayers(int team1Id);
}