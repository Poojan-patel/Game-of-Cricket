package com.tekion.cricket.validators;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.models.PlayerDTO;
import com.tekion.cricket.repository.MatchRepository;
import com.tekion.cricket.services.MatchService;
import com.tekion.cricket.util.MatchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchValidators {
    private MatchService matchService;

    @Autowired
    public void setService(MatchService matchService){
        this.matchService = matchService;
    }

    public Match checkMatchIdValidity(String matchId) {
        Match match = matchService.findByMatchId(matchId);
        if(match == null){
            throw new IllegalStateException("Match Id does not exists");
        }
        return match;
    }

    public Match isMatchEnded(String matchId){
        Match match = checkMatchIdValidity(matchId);
        if(!MatchUtil.isMatchEnded(match)){
            throw new IllegalStateException("Match has not completed yet");
        }
        return match;
    }

    public Player checkBowlerValidity(Match match, String currentBowlTeamId, Integer chosenBowler) {
        List<PlayerDTO> availableBowlers = matchService.fetchAvailableBowlers(match, currentBowlTeamId);

        for(PlayerDTO p: availableBowlers){
            if(p.getPlayerId() == chosenBowler){
                return new Player(p.getName(), p.getPlayerType().toString(), p.getTypeOfBowler().toString(), p.getPlayerId());
            }
        }
        throw new IllegalStateException("Bowler is Not Valid, select another Bowler");
    }
}
