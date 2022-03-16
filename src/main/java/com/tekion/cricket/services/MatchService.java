package com.tekion.cricket.services;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.models.*;
import com.tekion.cricket.repository.BallEventsRepository;
import com.tekion.cricket.repository.MatchRepository;
import com.tekion.cricket.repository.PlayerRepository;
import com.tekion.cricket.repository.TeamRepository;

import java.util.List;

public interface MatchService {

    MatchCreationResponse createNewMatch(MatchCreationRequest matchRequest, List<TeamDTO> selectedTeams);

    TossSimulationResult stimulateTossAndInsertStrike(Match match);

    List<PlayerDTO> fetchAvailableBowlers(Match match, Integer currentBowlTeamId);

    ScoreBoard playTheOver(Match match, int currentBowlTeamId, Player bowler);

    MatchResult generateFinalScoreBoard(Integer matchId);

    MatchRecreateResponse recreateMatch(Match match);

    Match findByMatchId(Integer matchId);
}
