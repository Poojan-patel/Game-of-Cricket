package com.tekion.intern.validators;

import com.tekion.intern.models.MatchCreationRequest;
import com.tekion.intern.models.MatchCreationResponse;
import com.tekion.intern.models.TeamDTO;
import com.tekion.intern.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class TeamValidators {
    private TeamRepository teamRepository;

    @Autowired
    public void setRepository(TeamRepository teamRepository){
        this.teamRepository = teamRepository;
    }

    public List<TeamDTO> validateTeamsForMatchCreation(MatchCreationRequest matchRequest) {
        if (matchRequest.getTeam1Id() == matchRequest.getTeam2Id()) {
            throw new IllegalStateException("Both Team Cannot be Same");
        }
        if (matchRequest.getOvers() < 5) {
            throw new IllegalStateException("Match must be 5 overs long");
        }
        return checkTeamExistance(matchRequest.getTeam1Id(), matchRequest.getTeam2Id());
    }

    private List<TeamDTO> checkTeamExistance(int team1Id, int team2Id) {
        List<TeamDTO> allTeams = teamRepository.findAll();
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
}
