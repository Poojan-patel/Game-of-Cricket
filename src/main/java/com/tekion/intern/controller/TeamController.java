package com.tekion.intern.controller;

import com.tekion.intern.models.TeamDTO;
import com.tekion.intern.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@RequestMapping("/team")
public class TeamController {
    private TeamService teamService;

    @Autowired
    public void setService(TeamService teamService){
        this.teamService = teamService;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTeam(@RequestBody TeamDTO team){
        Integer teamId = teamService.validateTeam(team);
        return ResponseEntity.accepted().body(teamId.toString());
    }

    @GetMapping("/list")
    public ResponseEntity<List<TeamDTO>> getAllTeams(){
        List<TeamDTO> allTeams = teamService.getAllTeams();
        return ResponseEntity.ok(allTeams);
    }
}
