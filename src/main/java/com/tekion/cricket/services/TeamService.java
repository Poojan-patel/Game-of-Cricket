package com.tekion.cricket.services;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.beans.Strike;
import com.tekion.cricket.models.BattingTeam;
import com.tekion.cricket.models.PlayerDTO;
import com.tekion.cricket.models.TeamDTO;

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
