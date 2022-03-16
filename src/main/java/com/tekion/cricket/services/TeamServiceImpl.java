package com.tekion.cricket.services;

import com.tekion.cricket.beans.*;
import com.tekion.cricket.enums.MatchState;
import com.tekion.cricket.enums.PlayerType;
import com.tekion.cricket.models.BattingTeam;
import com.tekion.cricket.models.PlayerDTO;
import com.tekion.cricket.models.TeamDTO;
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
    public Integer saveTeamWithPlayers(Team team, List<Player> players) {
        Integer teamId = teamRepository.save(team);
        if(teamId <= 0) {
            throw new IllegalStateException("Team could not be saved");
        }
        playerRepository.saveBatch(players, teamId);
        return teamId;
    }

    @Override
    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll();
    }

    @Override
    public List<PlayerDTO> getAllAvailableBowlers(Match match, Integer currentBowlTeamId, Integer maxOvers) {
        List<Player> allBowlers = playerRepository.fetchBowlersForBowlingTeamByTeamId(currentBowlTeamId);
        Map<Integer,Integer> bowlersWhoThrownOvers = ballEventsRepository.fetchBowlersWithThrownOversByTeamAndMatchId(match.getMatchId(), currentBowlTeamId);
        int lastBowler = strikeRepository.fetchTheLastOver(match.getMatchId(), currentBowlTeamId);
        List<PlayerDTO> availableBowlers = new ArrayList<>();
        int maxi = -1;
        PlayerDTO minOverPlayer = null;
        int sum = match.getOvers();
        int thrownOvers;
        for(Player p:allBowlers){
            thrownOvers = ((bowlersWhoThrownOvers.get(p.getPlayerId()) == null) ?0 :bowlersWhoThrownOvers.get(p.getPlayerId()));
            sum -= thrownOvers;
            if(p.getPlayerId() != lastBowler && thrownOvers < maxOvers){
                availableBowlers.add(new PlayerDTO(p.getName(), p.getPlayerType(), p.getTypeOfBowler(), p.getPlayerId(), maxOvers - thrownOvers));
                if(maxOvers - thrownOvers > maxi){
                    maxi = maxOvers - thrownOvers;
                    minOverPlayer = availableBowlers.get(availableBowlers.size()-1);
                }
            }
        }

        if(allBowlers.size() > 6 || match.getOvers()%5 != 0) {
            return availableBowlers;
        }
        if(maxi > sum/2) {
            return Collections.singletonList(minOverPlayer);
        }
        return availableBowlers;
    }

    @Override
    public void setBowlerForThisOver(Match match, Integer currentBowlTeamId, int bowlerId) {
        strikeRepository.updateBowlerByTeamAndMatchId(bowlerId, match.getMatchId(), currentBowlTeamId);
    }

    @Override
    public Strike initializeStrike(Match match, int currentBowlTeamId, Player bowler) {
        int currentBatTeamId = (match.getTeam1Id() == currentBowlTeamId) ? match.getTeam2Id() : match.getTeam1Id();
        return strikeRepository.fetchStrikeDetails(match.getMatchId(), currentBatTeamId);
    }

    @Override
    public BattingTeam initializeBattingTeam(Match match, int currentBowlTeamId) {
        int scoreToChase = -1;
        int currentBatTeamId = (match.getTeam1Id() == currentBowlTeamId) ? match.getTeam2Id() : match.getTeam1Id();
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
        int newBatter = playerRepository.fetchNextBatsman(strike.getTeamId(), maxOrder);
        strike.setNewBatsman(newBatter);
        strikeRepository.updateStrikesByTeamAndMatchId(strike);
    }

    @Override
    public void insertStrikesForNewInning(int teamId, int matchId) {
        List<Integer> playerIds = teamRepository.fetchFirstTwoPlayers(teamId);
        strikeRepository.insertStrike(new Strike(playerIds.get(0), playerIds.get(1), matchId, teamId));
    }

    @Override
    public List<TeamDTO> findAllTeams() {
        return teamRepository.findAll();
    }

}
