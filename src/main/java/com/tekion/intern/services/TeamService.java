package com.tekion.intern.services;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.beans.Team;
import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.models.TeamDTO;
import com.tekion.intern.repo.BallEventsRepository;
import com.tekion.intern.repo.PlayerRepository;
import com.tekion.intern.repo.TeamInPlayRepository;
import com.tekion.intern.repo.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
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

    private Integer saveTeam(TeamDTO teamDTO) {
        Team team = new Team(teamDTO);
        Integer teamId = 0;
        try {
            teamId = teamRepository.save(team);
        } catch(SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){

        }
        return teamId;
    }

    public List<TeamDTO> getAllTeams() {
        List<TeamDTO> allTeams = new ArrayList<>();
        try {
            allTeams = teamRepository.findAll();
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){

        }
        return allTeams;
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
                p.setRemainingOvers(maxOvers - thrownOvers);
                availableBowlers.add(new PlayerDTO(p.getName(), p.getPlayerType().toString(), p.getTypeOfBowler(), p.getPlayerId()));
                if(maxOvers - thrownOvers > maxi){
                    maxi = maxOvers - thrownOvers;
                    minOverPlayer = availableBowlers.get(availableBowlers.size()-1);
                }
            }
        }

        if(allBowlers.size() > 6 || match.getOvers()%5 != 0)
            return availableBowlers;
        if(maxi > sum/2)
            return Collections.singletonList(minOverPlayer);
        return availableBowlers;
    }
}
