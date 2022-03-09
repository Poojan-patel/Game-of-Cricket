package com.tekion.intern.services;

import com.tekion.intern.beans.*;
import com.tekion.intern.enums.MatchState;
import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.models.BattingTeam;
import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.models.TeamDTO;
import com.tekion.intern.repository.BallEventsRepository;
import com.tekion.intern.repository.PlayerRepository;
import com.tekion.intern.repository.TeamInPlayRepository;
import com.tekion.intern.repository.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class TeamService {

    private TeamRepository teamRepository;
    private PlayerRepository playerRepository;
    private TeamInPlayRepository teamInPlayRepository;
    private BallEventsRepository ballEventsRepository;

    @Autowired
    public void setRepository(
            PlayerRepository playerRepository, TeamRepository teamRepo, BallEventsRepository ballEventsRepository, TeamInPlayRepository teamInPlayRepository
    ){
        this.teamRepository = teamRepo;
        this.ballEventsRepository = ballEventsRepository;
        this.playerRepository = playerRepository;
        this.teamInPlayRepository = teamInPlayRepository;
    }

    public Integer validateTeam(TeamDTO team) throws IllegalStateException{
        List<PlayerDTO> players = team.getPlayers();
        if(players == null || players.size() != 11) {
            throw new IllegalStateException("Players should be 11");
        }
        int numOfBowlers = 0;
        for(PlayerDTO p: players){
            if(p.getPlayerType() != PlayerType.BATSMAN) {
                numOfBowlers++;
            }
        }
        if(numOfBowlers < 5) {
            throw new IllegalStateException("There must be At least 5 bowlers available in your team");
        }

        return saveTeam(team);
    }

    public List<TeamDTO> getAllTeams() {
        return teamRepository.findAll();
    }

    public List<PlayerDTO> getAllAvailableBowlers(Match match, Integer currentBowlTeamId, Integer maxOvers) {
        List<Player> allBowlers = playerRepository.fetchBowlersForBowlingTeamByTeamId(currentBowlTeamId);
        Map<Integer,Integer> bowlersWhoThrownOvers = ballEventsRepository.fetchBowlersWithThrownOversByTeamAndMatchId(match.getMatchId(), currentBowlTeamId);
        int lastBowler = teamInPlayRepository.fetchTheLastOver(match.getMatchId(), currentBowlTeamId);
        List<PlayerDTO> availableBowlers = new ArrayList<>();
        int maxi = -1;
        PlayerDTO minOverPlayer = null;
        int sum = match.getOvers();
        int thrownOvers;
        for(Player p:allBowlers){
            thrownOvers = ((bowlersWhoThrownOvers.get(p.getPlayerId()) == null) ?0 :bowlersWhoThrownOvers.get(p.getPlayerId()));
            sum -= thrownOvers;
            if(p.getPlayerId() != lastBowler && thrownOvers < maxOvers){
                availableBowlers.add(new PlayerDTO(p.getName(), p.getPlayerType().toString(), p.getTypeOfBowler(), p.getPlayerId(), maxOvers - thrownOvers));
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

    public void setBowlerForThisOver(Match match, Integer currentBowlTeamId, int bowlerId) {
        teamInPlayRepository.updateBowlerByTeamAndMatchId(bowlerId, match.getMatchId(), currentBowlTeamId);
    }

    public Strike initializeStrike(Match match, int currentBowlTeamId, Player bowler) {
        int currentBatTeamId = (match.getTeam1Id() == currentBowlTeamId) ? match.getTeam2Id() : match.getTeam1Id();
        return teamInPlayRepository.fetchStrikeDetails(match.getMatchId(), currentBatTeamId);
    }

    public BattingTeam initializeBattingTeam(Match match, int currentBowlTeamId) {
        int scoreToChase = -1;
        int currentBatTeamId = (match.getTeam1Id() == currentBowlTeamId) ? match.getTeam2Id() : match.getTeam1Id();
        if(match.getMatchState() == MatchState.TEAM2_BATTING){
            scoreToChase = ballEventsRepository.fetchScoreToChase(match.getMatchId(), currentBowlTeamId);
        }
        BattingTeam battingTeam = teamRepository.fetchTeamScoreFromMatchId(match.getMatchId(), currentBatTeamId);
        battingTeam.setScoreToChase(scoreToChase);

        return battingTeam;
    }

    public void updateStrike(Strike strike) {
        teamInPlayRepository.updateStrikesByTeamAndMatchId(strike);
    }

    public void updateStrikeOnWicket(Strike strike) {
        int maxOrder = strike.getMaxOrderedPlayer();
        strike.incrementWickets();
        int newBatter = playerRepository.fetchNextBatsman(strike.getTeamId(), maxOrder);
        strike.setNewBatsman(newBatter);
        teamInPlayRepository.updateStrikesByTeamAndMatchId(strike);
    }

    public void insertStrikesForNewMatch(int teamId, int matchId) {
        List<Integer> playerIds = teamRepository.fetchFirstTwoPlayers(teamId);
        teamInPlayRepository.insertStrike(playerIds.get(0), playerIds.get(1), matchId, teamId);
    }

    private Integer saveTeam(TeamDTO teamDTO) {
        Team team = new Team(teamDTO);
        Integer teamId = teamRepository.save(team);
        if(teamId <= 0) {
            throw new IllegalStateException("Team could not be saved");
        }
        return teamId;
    }
}
