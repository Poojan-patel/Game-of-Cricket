package com.tekion.intern.services;

import com.tekion.intern.beans.Team;
import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.models.TeamDTO;
import com.tekion.intern.repo._TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class TeamService {

    @Autowired
    private _TeamRepository teamRepo;

    public Integer validateTeam(TeamDTO team) throws IllegalStateException{
        List<PlayerDTO> players = team.getPlayers();
        if(players == null || players.size() != 11)
            throw new IllegalStateException("Players should be 11");
        int numOfBowlers = 0;
        for(PlayerDTO p: players){
            if(p.getPlayerType() != PlayerType.BATSMAN)
                numOfBowlers++;
        }
        if(numOfBowlers < 5)
            throw new IllegalStateException("There must be At least 5 bowlers available in your team");

        return saveTeam(team);
    }

    private Integer saveTeam(TeamDTO t) {
        Team team = new Team(t);
        System.out.println(team);
        return teamRepo.save(team).getTeamId();
    }

    public List<TeamDTO> getAllTeams() {
        Iterable<Team> allTeamsIterable = teamRepo.findAll();
        List<TeamDTO> allTeams = new ArrayList<>();
        for(Team t:allTeamsIterable){
            allTeams.add(new TeamDTO(t.getTeamName(), t.getTeamId()));
        }
        return allTeams;
    }
}
