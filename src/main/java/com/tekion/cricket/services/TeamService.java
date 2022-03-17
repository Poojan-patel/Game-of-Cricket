package com.tekion.cricket.services;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.beans.Strike;
import com.tekion.cricket.beans.Team;
import com.tekion.cricket.models.BattingTeam;
import com.tekion.cricket.models.PlayerDTO;
import com.tekion.cricket.models.TeamDTO;

import java.util.List;

//@Service
public interface TeamService{

    String saveTeamWithPlayers(Team team, List<Player> players);

    List<TeamDTO> getAllTeams();

    List<PlayerDTO> getAllAvailableBowlers(Match match, String currentBowlTeamId, Integer maxOvers);

    void setBowlerForThisOver(Match match, String currentBowlTeamId, int bowlerId);

    Strike initializeStrike(Match match, String currentBowlTeamId, Player bowler);

    BattingTeam initializeBattingTeam(Match match, String currentBowlTeamId);

    void updateStrike(Strike strike);

    void updateStrikeOnWicket(Strike strike);

    void insertStrikesForNewInning(String teamId, Match match);

    List<TeamDTO> findAllTeams();
}
