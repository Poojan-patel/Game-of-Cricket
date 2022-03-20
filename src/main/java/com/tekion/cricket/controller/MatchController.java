package com.tekion.cricket.controller;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.beans.Team;
import com.tekion.cricket.models.*;
import com.tekion.cricket.services.MatchService;
import com.tekion.cricket.util.MatchUtil;
import com.tekion.cricket.validators.MatchValidators;
import com.tekion.cricket.validators.TeamValidators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchController {

    private MatchService matchService;
    private MatchValidators matchValidators;
    private TeamValidators teamValidators;

    @Autowired
    public void setService(MatchService matchService){
        this.matchService = matchService;
    }

    @Autowired
    public void setValidators(MatchValidators matchValidators, TeamValidators teamValidators){
        this.matchValidators = matchValidators;
        this.teamValidators = teamValidators;
    }


    @PostMapping("/create")
    public ResponseEntity<MatchCreationResponse> organizeMatch(@RequestBody MatchCreationRequest matchCreationRequest){
        List<Team> teamsForMatch = teamValidators.validateTeamsForMatchCreation(matchCreationRequest);
        MatchCreationResponse response = matchService.createNewMatch(matchCreationRequest, teamsForMatch);
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/play/{matchId}")
    public ResponseEntity<ScoreBoard> stimulateMatch(@PathVariable String matchId, @RequestBody BowlerForNextOver bowlerForNextOver){
        Match match = matchValidators.checkMatchIdValidity(matchId);
        String currentBowlTeamId = MatchUtil.getCurrentBowlingTeam(match);
        Player bowler = matchValidators.checkBowlerValidity(match, currentBowlTeamId, bowlerForNextOver.getBowlerId());
        ScoreBoard scoreBoard = matchService.playTheOver(match, currentBowlTeamId, bowler);
        return ResponseEntity.ok(scoreBoard);
    }

    @GetMapping("/scoreboard/{matchId}")
    public ResponseEntity<MatchResult> getScoreBoard(@PathVariable String matchId){
        matchValidators.isMatchEnded(matchId);
        return ResponseEntity.ok(matchService.generateFinalScoreBoard(matchId));
    }

    @GetMapping("/bowlerslist/{matchId}")
    public ResponseEntity<List<PlayerDTO>> getAvailableBowlers(@PathVariable String matchId){
        Match match = matchValidators.checkMatchIdValidity(matchId);
        String currentBowlTeamId = MatchUtil.getCurrentBowlingTeam(match);
        List<PlayerDTO> availableBowlers =  matchService.fetchAvailableBowlers(match, currentBowlTeamId);
        return ResponseEntity.ok(availableBowlers);
    }

    @GetMapping("/toss/{matchId}")
    public ResponseEntity<TossSimulationResult> stimulateToss(@PathVariable String matchId){
        Match match = matchValidators.checkMatchIdValidity(matchId);
        TossSimulationResult tossSimulationResult = matchService.stimulateTossAndInsertStrike(match);
        return ResponseEntity.ok(tossSimulationResult);
    }

    @GetMapping("/recreate/{matchId}")
    public ResponseEntity<MatchRecreateResponse> recreateMatch(@PathVariable String matchId){
        Match match = matchValidators.isMatchEnded(matchId);
        MatchRecreateResponse matchRecreateResponse = matchService.recreateMatch(match);
        return ResponseEntity.ok(matchRecreateResponse);
    }
}
