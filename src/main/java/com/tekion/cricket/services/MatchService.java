package com.tekion.cricket.services;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.beans.Team;
import com.tekion.cricket.models.*;

import java.util.List;

public interface MatchService {

    MatchCreationResponse createNewMatch(MatchCreationRequest matchRequest, List<Team> selectedTeams);

    TossSimulationResult stimulateTossAndInsertStrike(Match match);

    List<PlayerDTO> fetchAvailableBowlers(Match match, String currentBowlTeamId);

    ScoreBoard playTheOver(Match match, String currentBowlTeamId, Player bowler);

    MatchResult generateFinalScoreBoard(String matchId, String team1Id, String team2Id);

    MatchRecreateResponse recreateMatch(Match match);

    Match findByMatchId(String matchId);
}
