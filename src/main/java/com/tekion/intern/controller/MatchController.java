package com.tekion.intern.controller;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.models.*;
import com.tekion.intern.services.MatchService;
import com.tekion.intern.util.MatchUtil;
import com.tekion.intern.validators.MatchValidators;
import com.tekion.intern.validators.TeamValidators;
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
        List<TeamDTO> teamsForMatch = teamValidators.validateTeamsForMatchCreation(matchCreationRequest);
        MatchCreationResponse response = matchService.createNewMatch(matchCreationRequest, teamsForMatch);
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/play/{matchId}")
    public ResponseEntity<ScoreBoard> stimulateMatch(@PathVariable Integer matchId, @RequestBody BowlerForNextOver bowlerForNextOver){
        Match match = matchValidators.checkMatchIdValidity(matchId);
        Integer currentBowlTeamId = MatchUtil.getCurrentBowlingTeam(match);
        Player bowler = matchValidators.checkBowlerValidity(match, currentBowlTeamId, bowlerForNextOver.getBowlerId());
        ScoreBoard scoreBoard = matchService.playTheOver(match, currentBowlTeamId, bowler);
        return ResponseEntity.ok(scoreBoard);
    }

    @GetMapping("/scoreboard/{matchId}")
    public ResponseEntity<MatchResult> getScoreBoard(@PathVariable Integer matchId){
        matchValidators.checkMatchIdValidity(matchId);
        return ResponseEntity.ok(matchService.generateFinalScoreBoard(matchId));
    }

    @GetMapping("/bowlerslist/{matchId}")
    public ResponseEntity<List<PlayerDTO>> getAvailableBowlers(@PathVariable Integer matchId){
        Match match = matchValidators.checkMatchIdValidity(matchId);
        Integer currentBowlTeamId = MatchUtil.getCurrentBowlingTeam(match);
        List<PlayerDTO> availableBowlers =  matchService.fetchAvailableBowlers(match, currentBowlTeamId);
        return ResponseEntity.ok(availableBowlers);
    }

    @GetMapping("/toss/{matchId}")
    public ResponseEntity<TossSimulationResult> stimulateToss(@PathVariable Integer matchId){
        Match match = matchValidators.checkMatchIdValidity(matchId);
        TossSimulationResult tossSimulationResult = matchService.stimulateTossAndInsertStrike(match);
        return ResponseEntity.ok(tossSimulationResult);
    }
}
