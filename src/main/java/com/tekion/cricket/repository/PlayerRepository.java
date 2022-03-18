package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Player;
import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.models.BatsmanStats;

import java.util.List;
import java.util.Map;

public interface PlayerRepository {

    List<Player> fetchBowlersForBowlingTeamByTeamId(String teamId);

    List<BatsmanStats> fetchOnFieldBatsmenData(int strike, int nonStrike, String matchId, String battingTeam);

    int fetchNextBatsman(String teamId, int maxOrder);

    PlayerType fetchPlayerType(int playerOrder, String team);

    String fetchPlayerNameByPlayerId(int playerOrder, String teamId);

    Map<Integer, String> fetchPlayerNamesByTeamId(String teamId, int offset);

    void saveBatch(List<Player> players, String teamId);
}
