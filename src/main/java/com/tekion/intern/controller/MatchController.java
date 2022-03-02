package com.tekion.intern.controller;

import com.tekion.intern.models.MatchCreationRequest;
import com.tekion.intern.models.MatchCreationResponse;
import com.tekion.intern.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
