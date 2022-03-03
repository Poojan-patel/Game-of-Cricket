package com.tekion.intern.controller;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.models.MatchCreationRequest;
import com.tekion.intern.models.MatchCreationResponse;
import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.models.TossSimulationResult;
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

    @GetMapping("/play/{matchId}")
    public ResponseEntity<String> stimulateMatch(@PathVariable Integer matchId){
        //matchService.startTheMatch(matchId);
        return ResponseEntity.internalServerError().body("UnderConstruction");
    }

    @GetMapping("/bowlerslist/{matchId}")
    public ResponseEntity<List<PlayerDTO>> getAvailableBowlers(@PathVariable Integer matchId){
        Match match = matchService.checkMatchIdValidity(matchId);
        Integer currentBowlTeamId = matchService.getCurrentBowlingTeam(match);
        List<PlayerDTO> availableBowlers =  matchService.fetchAvailableBowlers(match, currentBowlTeamId, match.getMaxovers());
        return ResponseEntity.ok(availableBowlers);
    }

    @GetMapping("/toss/{matchId}")
    public ResponseEntity<TossSimulationResult> stimulateToss(@PathVariable Integer matchId){
        TossSimulationResult tossSimulationResult = matchService.stimulateToss(matchId);
        return ResponseEntity.ok(tossSimulationResult);
    }
}
