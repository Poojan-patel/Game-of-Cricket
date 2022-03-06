package com.tekion.intern.services;

import com.tekion.intern.beans.Match;
import com.tekion.intern.beans.Player;
import com.tekion.intern.beans.Strike;
import com.tekion.intern.beans.Team;
import com.tekion.intern.enums.UnfairBallType;
import com.tekion.intern.enums.Winner;
import com.tekion.intern.models.*;
import com.tekion.intern.repo.BallEventsRepository;
import com.tekion.intern.repo.MatchRepository;
import com.tekion.intern.repo.TeamRepository;
import com.tekion.intern.util.MatchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MatchService {
    private Scanner sc = new Scanner(System.in);
    private TeamRepository teamRepository;
    private MatchRepository matchRepository;

    private BallEventsRepository ballEventsRepository;

    private TeamService teamService;

    @Autowired
    public void setRepository(
            TeamRepository teamRepository, MatchRepository matchRepository, BallEventsRepository ballEventsRepository
    ) {
         this.teamRepository = teamRepository;
         this.matchRepository = matchRepository;

         this.ballEventsRepository = ballEventsRepository;
    }

    @Autowired
    public void setService(TeamService teamService){
        this.teamService = teamService;
    }

    public MatchCreationResponse validateTeamsForMatchCreation(MatchCreationRequest matchRequest){
        if(matchRequest.getTeam1Id() == matchRequest.getTeam2Id()){
            throw new IllegalStateException("Both Team Cannot be Same");
        }
        if(matchRequest.getOvers() < 5){
            throw new IllegalStateException("Match must be 5 overs long");
        }
        List<TeamDTO> selectedTeams = checkTeamExistance(matchRequest.getTeam1Id(), matchRequest.getTeam2Id());
        Match match = new Match(matchRequest.getTeam1Id(), matchRequest.getTeam2Id(), matchRequest.getOvers());
        int matchId = 0;
        matchId = matchRepository.save(match);
        MatchCreationResponse response = new MatchCreationResponse(matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName(), matchRequest.getOvers());
        return response;
    }

    private List<TeamDTO> checkTeamExistance(int team1Id, int team2Id) {
        List<TeamDTO> allTeams = new ArrayList<>();
        try {
            allTeams = teamRepository.findAll();
        } catch (SQLException sqle){
            sqle.printStackTrace();
        } catch (Exception e){

        }
        TeamDTO team1 = null;
        TeamDTO team2 = null;
        for(TeamDTO t: allTeams){
            if(t.getTeamId() == team1Id){
                team1 = t;
            }
            if(t.getTeamId() == team2Id){
                team2 = t;
            }
        }
        if((team1 == null) || (team2 == null)){
            throw new IllegalStateException("Either or Both of the Team does not Exists");
        }
        return Arrays.asList(team1, team2);
    }

    public void startTheMatch(Integer matchId) {
        Match match = matchRepository.findByMatchId(matchId);
        if(match == null)
            throw new IllegalStateException("Match Does not Exists");
    }

    public TossSimulationResult stimulateTossAndInsertStrike(Integer matchId) {
        Match match = matchRepository.findByMatchId(matchId);
        if(match == null)
            throw new IllegalStateException("Match Does not Exists");
        if(match.getMatchState() != Winner.TOSS_LEFT){
            throw new IllegalStateException("Toss is already Stimulated\n Try to start playing the Game");
        }
        int headOrTail = MatchUtil.stimulateToss();
        int choiceOfInning = MatchUtil.stimulateToss();

        int whichTeamToBatFirst = MatchUtil.decideBatterFirst(headOrTail, choiceOfInning);
        if(whichTeamToBatFirst != 1)
            swapTeams(match);

        match.setMatchState(Winner.TEAM1_BATTING);
        matchRepository.update(match);
        List<TeamDTO> selectedTeams = checkTeamExistance(match.getTeam1Id(), match.getTeam2Id());
        TossSimulationResult tossSimulationResult = new TossSimulationResult(
                matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName()
        );
        teamService.insertStrikesForNewMatch(match.getTeam1Id(), matchId);
        return tossSimulationResult;
    }

    private void swapTeams(Match match) {
        int team1Id = match.getTeam1Id();
        match.setTeam1Id(match.getTeam2Id());
        match.setTeam2Id(team1Id);
    }

//    public void fetchAvailableBowlers(Integer matchId) {
//        teamRepository.
//    }

    public Match checkMatchIdValidity(Integer matchId) {
        Match match = matchRepository.findByMatchId(matchId);
        if(match == null){
            throw new IllegalStateException("Match Id does not exists");
        }
        return match;
    }

    public Integer getCurrentBowlingTeam(Match match) {
        Winner currentMatchStat = match.getMatchState();
        if(currentMatchStat != Winner.TEAM1_BATTING && currentMatchStat != Winner.TEAM2_BATTING){
            throw new IllegalStateException("Either match is finished or not started yet!");
        }
        if(currentMatchStat == Winner.TEAM1_BATTING)
            return match.getTeam2Id();
        else
            return match.getTeam1Id();
    }

    public List<PlayerDTO> fetchAvailableBowlers(Match match, Integer currentBowlTeamId) {
        int maxOvers = match.getMaxovers();
        return teamService.getAllAvailableBowlers(match, currentBowlTeamId, maxOvers);
    }

    public Player checkBowlerValidity(Match match, Integer currentBowlTeamId, Integer chosenBowler) {
        List<PlayerDTO> availableBowlers = fetchAvailableBowlers(match, currentBowlTeamId);

        for(PlayerDTO p: availableBowlers){
            if(p.getPlayerId() == chosenBowler){
                return new Player(p.getName(), p.getPlayerType().toString(), p.getTypeOfBowler().toString(), p.getPlayerId());
            }
        }
        throw new IllegalStateException("Bowler is Not Valid, select another Bowler");
    }

    public void setBowlerForThisOver(Match match, Integer currentBowlTeamId, int bowlerId) {
        teamService.setBowlerForThisOver(match, currentBowlTeamId, bowlerId);
    }

    public void playTheOver(Match match, int currentBowlTeamId, Player bowler) {
        Strike strike = teamService.initializeStrike(match, currentBowlTeamId, bowler);
        Team battingTeam = strike.getBattingTeam();
        UnfairBallType unfairBallType;
        boolean isWicketPossible = true;
        for (int j = 0; j < 6; j++) {
            unfairBallType = playTheBall(strike, j + 1, strike.getCurrentOver(), isWicketPossible);
            if (strike.isAllOut() || ((battingTeam.getScoreToChase() != -1) && (battingTeam.getScoreToChase() < battingTeam.getTeamScore()))) {
                break;
            }
            if(unfairBallType != UnfairBallType.NA)
                j--;
            isWicketPossible = (unfairBallType != UnfairBallType.NO);
            sc.nextLine();
        }
        strike.changeStrike();
        teamService.updateStrike(strike);
        checkIfInningEnded(match, strike);
    }

    private void checkIfInningEnded(Match match, Strike strike){
        Team battingTeam = strike.getBattingTeam();
        if(battingTeam.getScoreToChase() != -1 && battingTeam.getScoreToChase() < battingTeam.getTeamScore()) {
            match.setMatchState(Winner.TEAM2);
            matchRepository.update(match);
        }
        else if(strike.isAllOut() || strike.getCurrentOver() == match.getOvers()){
            System.out.println("Inning Ended");
            if(match.getMatchState() == Winner.TEAM1_BATTING){
                match.setMatchState(Winner.TEAM2_BATTING);
                teamService.insertStrikesForNewMatch(match.getTeam2Id(), match.getMatchId());
                matchRepository.update(match);
            } else{
                if(battingTeam.getScoreToChase() == battingTeam.getTeamScore())
                    match.setMatchState(Winner.TIE);
                else
                    match.setMatchState(Winner.TEAM1);
                matchRepository.update(match);
            }
        }
    }

    private void outcomeOnWicketBall(Strike strike, int ballNumber, int over){
        String typeOfWicketFallen = MatchUtil.getRandomTypeOfWicket();
        Team battingTeam = strike.getBattingTeam();
        System.out.println(typeOfWicketFallen);
        System.out.println(over + "." + ballNumber + ": Wicket-" + (battingTeam.getCurrentWickets()+1) + " || Player: " + battingTeam.getNameOfPlayer(strike.getCurrentStrike()));
        ballEventsRepository.insertEvent(
                strike.getMatchId(), strike.getTeamId(), 0, battingTeam.getPlayedBalls(), battingTeam.getPlayerIdByIndex(strike.getCurrentStrike()),
                strike.getCurrentBowlerPlayerId(), 0, "", typeOfWicketFallen
        );
        teamService.updateStrikeOnWicket(strike);
    }

    private void legitimateBall(Strike strike, int ballNumber, int over, int outcomeOfBallBowled, boolean isTeamScore){
        Team battingTeam = strike.getBattingTeam();
        int currentPlayer = strike.getCurrentStrike();
        System.out.print(over + "." + ballNumber + ": " + outcomeOfBallBowled + " run ");
        if(isTeamScore)
            System.out.print("\n");
        else
            System.out.println("|| Player: " + battingTeam.getNameOfPlayer(currentPlayer));
        battingTeam.incrementTeamScore(outcomeOfBallBowled, currentPlayer);

        ballEventsRepository.insertEvent(
                strike.getMatchId(), battingTeam.getTeamId(), 0, over*6 + ballNumber,
                    (isTeamScore) ?-1 :battingTeam.getPlayerIdByIndex(currentPlayer),
                    strike.getCurrentBowlerPlayerId(), outcomeOfBallBowled, "", ""
        );

        if(outcomeOfBallBowled%2 == 1) {
            strike.changeStrike();
            teamService.updateStrike(strike);
        }

        if(outcomeOfBallBowled != 4 && outcomeOfBallBowled != 6) {
            int possibilityOfRunOut = ThreadLocalRandom.current().nextInt(0, 10);
            if (possibilityOfRunOut == 9) {
                System.out.println("RunOut-" + battingTeam.getNameOfPlayer(strike.getCurrentStrike()));
                ballEventsRepository.insertEvent(
                        strike.getMatchId(), battingTeam.getTeamId(), 0, battingTeam.getPlayedBalls(),
                        battingTeam.getPlayerIdByIndex(strike.getCurrentStrike()),
                        -1, 0, "", "RUN OUT"
                );
                teamService.updateStrikeOnWicket(strike);
            }
        }
    }

    private UnfairBallType playTheBall(Strike strike, int ballNumber, int over, boolean wicketPossible){
        if(ballNumber == 6){
            over++;
            ballNumber = 0;
        }
        Team battingTeam = strike.getBattingTeam();
        Player currentPlayer = strike.getCurrentStrikePlayer();
        int outcomeOfBallBowled = MatchUtil.generateRandomScore(currentPlayer.getPlayerType(), wicketPossible);

        if(outcomeOfBallBowled == -1){
            strike.incrementTotalBalls();
            outcomeOnWicketBall(strike, ballNumber, over);
            return UnfairBallType.NA;
        }
        else{
            int possibilityOfUnFairBall = ThreadLocalRandom.current().nextInt(0,9);
            if(possibilityOfUnFairBall <= 1){
                battingTeam.incrementTeamScoreForUnfair();
                String typeOfUnFairBall;
                System.out.println((typeOfUnFairBall = (possibilityOfUnFairBall == 0) ?"WIDE" :"NO BALL") + " : 1 run");
                ballEventsRepository.insertEvent(
                        strike.getMatchId(), battingTeam.getTeamId(), 0, over*6 + ballNumber,
                            -1,
                            strike.getCurrentBowlerPlayerId(), 1, typeOfUnFairBall, ""
                    );

                legitimateBall(strike, ballNumber, over, outcomeOfBallBowled, true);
                System.out.println("-----------------------------------------------------------------");
                // returning Enum will ensure the caller, not to update the ball number
                return (possibilityOfUnFairBall == 0) ?UnfairBallType.WIDE : UnfairBallType.NO;
            }
            else {
                battingTeam.incrementTotalBalls(strike.getCurrentStrike());
                legitimateBall(strike, ballNumber, over, outcomeOfBallBowled, false);
                return UnfairBallType.NA;
            }
        }
    }


}
