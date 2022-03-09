package com.tekion.intern.services;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.beans.Strike;
import com.tekion.intern.models.BattingTeam;
import com.tekion.intern.models.PlayerDTO;
import com.tekion.intern.models.TeamDTO;
import org.springframework.stereotype.Service;

import java.util.List;

//@Service
public interface TeamService{
    public Integer validateTeam(TeamDTO team);

    public List<TeamDTO> getAllTeams();

    public List<PlayerDTO> getAllAvailableBowlers(Match match, Integer currentBowlTeamId, Integer maxOvers);

    public void setBowlerForThisOver(Match match, Integer currentBowlTeamId, int bowlerId);

    public Strike initializeStrike(Match match, int currentBowlTeamId, Player bowler);

    public BattingTeam initializeBattingTeam(Match match, int currentBowlTeamId);

    public void updateStrike(Strike strike);

    public void updateStrikeOnWicket(Strike strike);

    public void insertStrikesForNewMatch(int teamId, int matchId);
}
