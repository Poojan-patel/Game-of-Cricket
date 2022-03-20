package com.tekion.cricket.services;

import com.tekion.cricket.beans.*;
import com.tekion.cricket.constants.Common;
import com.tekion.cricket.enums.MatchState;
import com.tekion.cricket.models.BattingTeam;
import com.tekion.cricket.models.PlayerDTO;
import com.tekion.cricket.repository.BallEventsRepository;
import com.tekion.cricket.repository.PlayerRepository;
import com.tekion.cricket.repository.StrikeRepository;
import com.tekion.cricket.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamServiceImpl implements  TeamService{

    private TeamRepository teamRepository;
    private PlayerRepository playerRepository;
    private StrikeRepository strikeRepository;
    private BallEventsRepository ballEventsRepository;

    @Autowired
    public void setRepository(
            PlayerRepository playerRepository, TeamRepository teamRepo, BallEventsRepository ballEventsRepository, StrikeRepository strikeRepository
    ){
        this.teamRepository = teamRepo;
        this.ballEventsRepository = ballEventsRepository;
        this.playerRepository = playerRepository;
        this.strikeRepository = strikeRepository;
    }

    @Override
    public String saveTeamWithPlayers(Team team, List<Player> players) {
        String teamId = teamRepository.save(team);
        if(teamId == null) {
            throw new IllegalStateException("Team could not be saved");
        }
        playerRepository.saveBatch(players, teamId);
        return teamId;
    }

    @Override
    public List<Team> getAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    public List<PlayerDTO> getAllAvailableBowlers(Match match, String currentBowlTeamId, Integer maxOvers) {
        List<Player> allBowlers = playerRepository.fetchBowlersForBowlingTeamByTeamId(currentBowlTeamId);
        Map<Integer,Integer> bowlersWhoThrownOvers = ballEventsRepository.fetchBowlersWithThrownOversByTeamAndMatchId(match.getMatchId(), currentBowlTeamId);
        int lastBowler = strikeRepository.fetchTheLastOver(match.getMatchId(), currentBowlTeamId);
        List<PlayerDTO> availableBowlers = new ArrayList<>();
        int maxi = -1;
        PlayerDTO minOverPlayer = null;
        int sum = match.getOvers();
        int thrownOvers;
        for(Player p:allBowlers){
            thrownOvers = bowlersWhoThrownOvers.getOrDefault(p.getPlayerOrder(), 0);
            sum -= thrownOvers;
            if(p.getPlayerOrder() != lastBowler && thrownOvers < maxOvers){
                availableBowlers.add(new PlayerDTO(p.getName(), p.getPlayerType(), p.getTypeOfBowler(), p.getPlayerOrder(), maxOvers - thrownOvers));
                if(maxOvers - thrownOvers > maxi){
                    maxi = maxOvers - thrownOvers;
                    minOverPlayer = availableBowlers.get(availableBowlers.size()-1);
                }
            }
        }
        if(allBowlers.size() > Common.MIN_BOWLERS || match.getOvers() % Common.MIN_OVERS != 0) {
            return availableBowlers;
        }
        if(maxi > sum/2) {
            return Collections.singletonList(minOverPlayer);
        }
        return availableBowlers;
    }

    @Override
    public void setBowlerForThisOver(Match match, String currentBowlTeamId, int bowlerId) {
        strikeRepository.updateBowlerByTeamAndMatchId(bowlerId, match.getMatchId(), currentBowlTeamId);
    }

    @Override
    public Strike initializeStrike(Match match, String currentBowlTeamId, Player bowler) {
        String currentBatTeamId = (match.getTeam1Id().equals(currentBowlTeamId)) ? match.getTeam2Id() : match.getTeam1Id();
        return strikeRepository.fetchStrikeDetails(match.getMatchId(), currentBatTeamId);
    }

    @Override
    public BattingTeam initializeBattingTeam(Match match, String currentBowlTeamId) {
        int scoreToChase = -1;
        String currentBatTeamId = (match.getTeam1Id().equals(currentBowlTeamId)) ? match.getTeam2Id() : match.getTeam1Id();
        if(MatchState.fromStringToEnum(match.getMatchState()) == MatchState.TEAM2_BATTING){
            scoreToChase = ballEventsRepository.fetchScoreToChase(match.getMatchId(), currentBowlTeamId);
        }
        BattingTeam battingTeam = teamRepository.fetchTeamScoreFromMatchId(match.getMatchId(), currentBatTeamId);
        battingTeam.setScoreToChase(scoreToChase);

        return battingTeam;
    }

    @Override
    public void updateStrike(Strike strike) {
        strikeRepository.updateStrikesByTeamAndMatchId(strike);
    }

    @Override
    public void updateStrikeOnWicket(Strike strike) {
        int maxOrder = strike.getMaxOrderedPlayer();
        strike.incrementWickets();
        int newBatter = playerRepository.fetchNextBatsman(strike.getBattingTeam(), maxOrder);
        strike.setNewBatsman(newBatter);
        strikeRepository.updateStrikesByTeamAndMatchId(strike);
    }

    @Override
    public void insertStrikesForNewInning(String battingTeamId, Match match) {
        List<Integer> playerIds = teamRepository.fetchFirstTwoPlayers(battingTeamId);
        String bowlingTeamId = ((battingTeamId.equals(match.getTeam1Id())) ?match.getTeam2Id() :match.getTeam1Id());
        strikeRepository.save(new Strike(playerIds.get(0), playerIds.get(1), match.getMatchId(), battingTeamId, bowlingTeamId));
    }

    @Override
    public List<Team> findAllTeams() {
        return teamRepository.findAll();
    }

}
