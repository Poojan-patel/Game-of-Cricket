package com.tekion.intern.services;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.models.*;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
public interface MatchService {

    public MatchCreationResponse createNewMatch(MatchCreationRequest matchRequest, List<TeamDTO> selectedTeams);

    public TossSimulationResult stimulateTossAndInsertStrike(Match match);

    public List<PlayerDTO> fetchAvailableBowlers(Match match, Integer currentBowlTeamId);

    public ScoreBoard playTheOver(Match match, int currentBowlTeamId, Player bowler);

    public MatchResult generateFinalScoreBoard(Integer matchId);
}
