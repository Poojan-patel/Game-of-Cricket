package com.tekion.intern.services;

import com.tekion.intern.beans.Match;
import com.tekion.intern.enums.Winner;
import com.tekion.intern.models.MatchCreationRequest;
import com.tekion.intern.models.MatchCreationResponse;
import com.tekion.intern.models.TeamDTO;
import com.tekion.intern.models.TossSimulationResult;
import com.tekion.intern.repo.MatchRepository;
import com.tekion.intern.repo.TeamRepository;
import com.tekion.intern.util.MatchUtil;
import com.tekion.intern.util.ReaderUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class MatchService {
    private TeamRepository teamRepository;
    private MatchRepository matchRepository;

    @Autowired
    public void setRepository(TeamRepository teamRepository, MatchRepository matchRepository) {
         this.teamRepository = teamRepository;
         this.matchRepository = matchRepository;
    }

    public MatchCreationResponse validateTeamsForMatchCreation(MatchCreationRequest matchRequest){
        if(matchRequest.getTeam1Id() == matchRequest.getTeam2Id()){
            throw new IllegalStateException("Both Team Cannot be Same");
        }
        if(matchRequest.getOvers() < 5){
            throw new IllegalStateException("Match must be 5 overs long");
        }
        List<TeamDTO> selectedTeams = checkTeamExistance(matchRequest.getTeam1Id(), matchRequest.getTeam2Id());
        Match match = new Match(matchRequest.getTeam1Id(), matchRequest.getTeam2Id(), matchRequest.getOvers());
        int matchId = 0;
        matchId = matchRepository.save(match);
        MatchCreationResponse response = new MatchCreationResponse(matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName(), matchRequest.getOvers());
        return response;
    }

    private List<TeamDTO> checkTeamExistance(int team1Id, int team2Id) {
        List<TeamDTO> allTeams = new ArrayList<>();
        try {
            allTeams = teamRepository.findAll();
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){

        }
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

    public void startTheMatch(Integer matchId) {
        Match match = matchRepository.findByMatchId(matchId);
        if(match == null)
            throw new IllegalStateException("Match Does not Exists");
    }

    public TossSimulationResult stimulateToss(Integer matchId) {
        Match match = matchRepository.findByMatchId(matchId);
        if(match == null)
            throw new IllegalStateException("Match Does not Exists");
        if(match.getMatchState() != Winner.TOSS_LEFT){
            throw new IllegalStateException("Toss is already Stimulated\n Try to start playing the Game");
        }
        int headOrTail = MatchUtil.stimulateToss();
        int choiceOfInning = MatchUtil.stimulateToss();

        int whichTeamToBatFirst = MatchUtil.decideBatterFirst(headOrTail, choiceOfInning);
        if(whichTeamToBatFirst != 1)
            swapTeams(match);

        match.setMatchState(Winner.TEAM1_BATTING);
        matchRepository.update(match);
        List<TeamDTO> selectedTeams = checkTeamExistance(match.getTeam1Id(), match.getTeam2Id());
        TossSimulationResult tossSimulationResult = new TossSimulationResult(
                matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName()
        );

        return tossSimulationResult;
    }

    private void swapTeams(Match match) {
        int team1Id = match.getTeam1Id();
        match.setTeam1Id(match.getTeam2Id());
        match.setTeam2Id(team1Id);
    }
}
