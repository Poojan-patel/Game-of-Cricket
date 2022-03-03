package com.tekion.intern.controller;

import com.tekion.intern.models.MatchCreationRequest;
import com.tekion.intern.models.MatchCreationResponse;
import com.tekion.intern.models.TossSimulationResult;
import com.tekion.intern.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/stimulate/{matchId}")
    public ResponseEntity<String> stimulateMatch(@PathVariable Integer matchId){
        //matchService.startTheMatch(matchId);
        return ResponseEntity.internalServerError().body("UnderConstruction");
    }

    @GetMapping("/toss/{matchId}")
    public ResponseEntity<TossSimulationResult> stimulateToss(@PathVariable Integer matchId){
        TossSimulationResult tossSimulationResult = matchService.stimulateToss(matchId);
        return ResponseEntity.ok(tossSimulationResult);
    }
}
