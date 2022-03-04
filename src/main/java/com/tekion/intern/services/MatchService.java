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
import com.tekion.intern.repo.PlayerRepository;
import com.tekion.intern.repo.TeamRepository;
import com.tekion.intern.util.MatchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MatchService {
    private TeamRepository teamRepository;
    private MatchRepository matchRepository;
    private PlayerRepository playerRepository;
    private BallEventsRepository ballEventsRepository;

    private TeamService teamService;

    @Autowired
    public void setRepository(
            TeamRepository teamRepository, MatchRepository matchRepository, PlayerRepository playerRepository, BallEventsRepository ballEventsRepository
    ) {
         this.teamRepository = teamRepository;
         this.matchRepository = matchRepository;
         this.playerRepository = playerRepository;
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

    public TossSimulationResult stimulateToss(Integer matchId) {
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
                return;
            }
            if(unfairBallType != UnfairBallType.NA)
                j--;
            isWicketPossible = (unfairBallType != UnfairBallType.NO);
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
        strike.changeStrike(outcomeOfBallBowled);

        ballEventsRepository.insertEvent(
                strike.getMatchId(), battingTeam.getTeamId(), 0, battingTeam.getPlayedBalls(),
                    (isTeamScore) ?-1 :battingTeam.getPlayerIdByIndex(currentPlayer),
                    strike.getCurrentBowlerPlayerId(), outcomeOfBallBowled, "", ""
        );


        if(outcomeOfBallBowled%2 == 1)
            teamService.updateStrike(strike);

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

    /*
        When a ball is delivered, choices can be, 1.. Wicket (C&B, B, Stumped, .., all except runout)
        2.. ball with runs possible, can be zero
        for 2nd, after generation of random run, we can randomly generate whether it was wide/no ball
            if it is, then increment one run and play again the same ball, till there is one legitimate ball thrown where pattern ends
                on a wide ball, any wicket can be possible, same as legitimate ball
                on a no ball, on that ball as well as on next ball, free hit, only runout is possible. this possibility of wicket (mentioned is 1st type)
                is handle by wicketPossible parameter.
            if not, we will call legitimateBall, where also we will generate a random number for possibility of run out.
     */
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
                        strike.getMatchId(), battingTeam.getTeamId(), 0, battingTeam.getPlayedBalls(),
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
