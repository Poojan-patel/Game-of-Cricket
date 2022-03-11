package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Player;
import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.models.BatsmanStats;

import java.util.List;
import java.util.Map;

public interface PlayerRepository {

    List<Player> fetchBowlersForBowlingTeamByTeamId(Integer teamId);

    List<BatsmanStats> fetchOnFieldBatsmenData(int strike, int nonStrike, int matchId);

    int fetchNextBatsman(Integer teamId, int maxOrder);

    PlayerType fetchPlayerType(int playerId);

    String fetchPlayerNameByPlayerId(int bowlerId);

    Map<Integer, String> fetchPlayerNamesByTeamId(int teamId);
}
