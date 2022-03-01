package com.tekion.intern.controller;

import com.tekion.intern.models.Team;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("/team")
public class TeamController {
    public static Logger logger = Logger.getLogger("teamcontroller");

    @PostMapping("/create")
    public String createTeam(@RequestBody Team team){
        System.out.println(team);
        return team.getTeamName();
    }
}
