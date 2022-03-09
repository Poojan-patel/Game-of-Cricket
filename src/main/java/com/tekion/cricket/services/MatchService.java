package com.tekion.cricket.services;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.models.*;

import java.util.List;

//@Service
public interface MatchService {

    public MatchCreationResponse createNewMatch(MatchCreationRequest matchRequest, List<TeamDTO> selectedTeams);

    public TossSimulationResult stimulateTossAndInsertStrike(Match match);

    public List<PlayerDTO> fetchAvailableBowlers(Match match, Integer currentBowlTeamId);

    public ScoreBoard playTheOver(Match match, int currentBowlTeamId, Player bowler);

    public MatchResult generateFinalScoreBoard(Integer matchId);
}
