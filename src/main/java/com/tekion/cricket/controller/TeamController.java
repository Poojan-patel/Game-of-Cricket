package com.tekion.cricket.controller;

import com.tekion.cricket.models.TeamDTO;
import com.tekion.cricket.services.TeamService;
import com.tekion.cricket.validators.TeamValidators;
import net.bytebuddy.asm.Advice;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/team")
public class TeamController {
    private TeamService teamService;
    private TeamValidators teamValidators;

    @Autowired
    public void setService(TeamService teamService){
        this.teamService = teamService;
    }

    @Autowired
    public void setValidators(TeamValidators teamValidators){
        this.teamValidators = teamValidators;
    }

    @PostMapping("/create")
    public ResponseEntity<String> createTeam(@RequestBody TeamDTO team){
        Integer teamId = teamValidators.validateTeam(team);
        return ResponseEntity.accepted().body(teamId.toString());
    }

    @GetMapping("/list")
    public ResponseEntity<List<TeamDTO>> getAllTeams(){
        List<TeamDTO> allTeams = teamService.getAllTeams();
        return ResponseEntity.ok(allTeams);
    }
}
