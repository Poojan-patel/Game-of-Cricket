package com.tekion.cricket.validators;

import com.tekion.cricket.beans.Player;
import com.tekion.cricket.beans.Team;
import com.tekion.cricket.constants.Common;
import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.models.MatchCreationRequest;
import com.tekion.cricket.models.PlayerDTO;
import com.tekion.cricket.models.TeamDTO;
import com.tekion.cricket.repository.TeamRepository;
import com.tekion.cricket.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TeamValidators {
    private TeamService teamService;

    @Autowired
    public void setService(TeamService teamService){
        this.teamService = teamService;
    }

    public List<TeamDTO> validateTeamsForMatchCreation(MatchCreationRequest matchRequest) {
        if (matchRequest.getTeam1Id() == matchRequest.getTeam2Id()) {
            throw new IllegalStateException("Both Team Cannot be Same");
        }
        if (matchRequest.getOvers() < Common.MIN_OVERS) {
            throw new IllegalStateException("Match must be 5 overs long");
        }
        return checkTeamExistance(matchRequest.getTeam1Id(), matchRequest.getTeam2Id());
    }

    private List<TeamDTO> checkTeamExistance(int team1Id, int team2Id) {
        List<TeamDTO> allTeams = teamService.findAllTeams();
        TeamDTO team1 = null;
        TeamDTO team2 = null;
        for(TeamDTO t: allTeams){
            if(t.getTeamId() == team1Id){
                team1 = t;
            }
            if(t.getTeamId() == team2Id){
                team2 = t;
            }
        }
        if((team1 == null) || (team2 == null)){
            throw new IllegalStateException("Either or Both of the Team does not Exists");
        }
        return Arrays.asList(team1, team2);
    }

    public Integer validateTeam(TeamDTO teamDTO){
        List<PlayerDTO> playerDTOs = teamDTO.getPlayers();
        Team team = new Team(teamDTO.getTeamName());
        List<Player> players = new ArrayList<>();
        if(playerDTOs == null || playerDTOs.size() != Common.NUM_OF_PLAYERS) {
            throw new IllegalStateException("Players should be 11");
        }
        int numOfBowlers = 0;
        for(PlayerDTO p: playerDTOs){
            if(p.getPlayerType() != PlayerType.BATSMAN) {
                numOfBowlers++;
            }
            players.add(new Player(p));
        }
        if(numOfBowlers < Common.MIN_BOWLERS) {
            throw new IllegalStateException("There must be At least 5 bowlers available in your team");
        }

        return teamService.saveTeamWithPlayers(team, players);
    }
}
