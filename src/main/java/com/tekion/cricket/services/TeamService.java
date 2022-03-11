package com.tekion.cricket.services;

import com.tekion.cricket.beans.Match;
import com.tekion.cricket.beans.Player;
import com.tekion.cricket.beans.Strike;
import com.tekion.cricket.models.BattingTeam;
import com.tekion.cricket.models.PlayerDTO;
import com.tekion.cricket.models.TeamDTO;
import com.tekion.cricket.repository.BallEventsRepository;
import com.tekion.cricket.repository.PlayerRepository;
import com.tekion.cricket.repository.TeamInPlayRepository;
import com.tekion.cricket.repository.TeamRepository;

import java.util.List;

//@Service
public interface TeamService{
    void setRepository(
            PlayerRepository playerRepository, TeamRepository teamRepo, BallEventsRepository ballEventsRepository, TeamInPlayRepository teamInPlayRepository
    );

    Integer validateTeam(TeamDTO team);

    List<TeamDTO> getAllTeams();

    List<PlayerDTO> getAllAvailableBowlers(Match match, Integer currentBowlTeamId, Integer maxOvers);

    void setBowlerForThisOver(Match match, Integer currentBowlTeamId, int bowlerId);

    Strike initializeStrike(Match match, int currentBowlTeamId, Player bowler);

    BattingTeam initializeBattingTeam(Match match, int currentBowlTeamId);

    void updateStrike(Strike strike);

    void updateStrikeOnWicket(Strike strike);

    void insertStrikesForNewMatch(int teamId, int matchId);
}
