package com.tekion.intern.controller;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.models.*;
import com.tekion.intern.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/match")
public class MatchController {

    @Autowired
    private MatchService matchService;

    @PostMapping("/create")
    public ResponseEntity<MatchCreationResponse> organizeMatch(@RequestBody MatchCreationRequest match){
        MatchCreationResponse response = matchService.validateTeamsForMatchCreation(match);
        if(response == null)
            return ResponseEntity.internalServerError().body(null);
        return ResponseEntity.ok(response);
    }

    @RequestMapping("/play/{matchId}")
    public ResponseEntity<String> stimulateMatch(@PathVariable Integer matchId, @RequestBody BowlerForNextOver bowlerForNextOver){
        //matchService.startTheMatch(matchId);
        Match match = matchService.checkMatchIdValidity(matchId);
        Integer currentBowlTeamId = matchService.getCurrentBowlingTeam(match);
        Player bowler = matchService.checkBowlerValidity(match, currentBowlTeamId, bowlerForNextOver.getBowlerId());
        matchService.setBowlerForThisOver(match, currentBowlTeamId, bowler.getPlayerId());
        matchService.playTheOver(match, currentBowlTeamId, bowler);
        return ResponseEntity.internalServerError().body("UnderConstruction");
    }

    @GetMapping("/bowlerslist/{matchId}")
    public ResponseEntity<List<PlayerDTO>> getAvailableBowlers(@PathVariable Integer matchId){
        Match match = matchService.checkMatchIdValidity(matchId);
        Integer currentBowlTeamId = matchService.getCurrentBowlingTeam(match);
        List<PlayerDTO> availableBowlers =  matchService.fetchAvailableBowlers(match, currentBowlTeamId);
        return ResponseEntity.ok(availableBowlers);
    }

    @GetMapping("/toss/{matchId}")
    public ResponseEntity<TossSimulationResult> stimulateToss(@PathVariable Integer matchId){
        TossSimulationResult tossSimulationResult = matchService.stimulateToss(matchId);
        return ResponseEntity.ok(tossSimulationResult);
    }
}
