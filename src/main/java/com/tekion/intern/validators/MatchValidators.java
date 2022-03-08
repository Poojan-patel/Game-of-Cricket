package com.tekion.intern.validators;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.repository.MatchRepository;
import com.tekion.intern.services.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchValidators {
    private MatchService matchService;
    private MatchRepository matchRepository;

    @Autowired
    public void setService(MatchService matchService){
        this.matchService = matchService;
    }

    @Autowired
    public void setRepository(MatchRepository matchRepository){
        this.matchRepository = matchRepository;
    }

    public Match checkMatchIdValidity(Integer matchId) {
        Match match = matchRepository.findByMatchId(matchId);
        if(match == null){
            throw new IllegalStateException("Match Id does not exists");
        }
        return match;
    }

    public Player checkBowlerValidity(Match match, Integer currentBowlTeamId, Integer chosenBowler) {
        List<PlayerDTO> availableBowlers = matchService.fetchAvailableBowlers(match, currentBowlTeamId);

        for(PlayerDTO p: availableBowlers){
            if(p.getPlayerId() == chosenBowler){
                return new Player(p.getName(), p.getPlayerType().toString(), p.getTypeOfBowler().toString(), p.getPlayerId());
            }
        }
        throw new IllegalStateException("Bowler is Not Valid, select another Bowler");
    }
}
