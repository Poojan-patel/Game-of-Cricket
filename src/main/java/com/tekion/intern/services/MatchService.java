package com.tekion.intern.services;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.enums.Winner;
import com.tekion.intern.models.*;
import com.tekion.intern.repo.BallEventsRepository;
import com.tekion.intern.repo.MatchRepository;
import com.tekion.intern.repo.PlayerRepository;
import com.tekion.intern.repo.TeamRepository;
import com.tekion.intern.util.MatchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Service
public class MatchService {
    private TeamRepository teamRepository;
    private MatchRepository matchRepository;
    private PlayerRepository playerRepository;
    private BallEventsRepository ballEventsRepository;

    private TeamService teamService;

    @Autowired
    public void setRepository(
            TeamRepository teamRepository, MatchRepository matchRepository, PlayerRepository playerRepository, BallEventsRepository ballEventsRepository
    ) {
         this.teamRepository = teamRepository;
         this.matchRepository = matchRepository;
         this.playerRepository = playerRepository;
         this.ballEventsRepository = ballEventsRepository;
    }

    @Autowired
    public void setService(TeamService teamService){
        this.teamService = teamService;
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

//    public void fetchAvailableBowlers(Integer matchId) {
//        teamRepository.
//    }

    public Match checkMatchIdValidity(Integer matchId) {
        Match match = matchRepository.findByMatchId(matchId);
        if(match == null){
            throw new IllegalStateException("Match Id does not exists");
        }
        return match;
    }

    public Integer getCurrentBowlingTeam(Match match) {
        Winner currentMatchStat = match.getMatchState();
        if(currentMatchStat != Winner.TEAM1_BATTING && currentMatchStat != Winner.TEAM2_BATTING){
            throw new IllegalStateException("Either match is finished or not started yet!");
        }
        if(currentMatchStat == Winner.TEAM1_BATTING)
            return match.getTeam2Id();
        else
            return match.getTeam1Id();
    }

    public List<PlayerDTO> fetchAvailableBowlers(Match match, Integer currentBowlTeamId, Integer maxOvers) {
        List<PlayerDTO> allAvailableBowlers = teamService.getAllAvailableBowlers(match, currentBowlTeamId, maxOvers);
        return allAvailableBowlers;
    }
}
