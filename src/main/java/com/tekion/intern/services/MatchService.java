package com.tekion.intern.services;

import com.tekion.intern.beans.*;
import com.tekion.intern.enums.MatchState;
import com.tekion.intern.enums.UnfairBallType;
import com.tekion.intern.models.*;
import com.tekion.intern.repository.BallEventsRepository;
import com.tekion.intern.repository.MatchRepository;
import com.tekion.intern.repository.TeamRepository;
import com.tekion.intern.util.MatchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MatchService {
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

    public MatchCreationResponse createNewMatch(MatchCreationRequest matchRequest, List<TeamDTO> selectedTeams){
        Match match = new Match(matchRequest.getTeam1Id(), matchRequest.getTeam2Id(), matchRequest.getOvers());
        int matchId = 0;
        matchId = matchRepository.save(match);
        return new MatchCreationResponse(matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName(), matchRequest.getOvers());
    }

    public TossSimulationResult stimulateTossAndInsertStrike(Match match) {
        int matchId = match.getMatchId();
        if(match.getMatchState() != MatchState.TOSS_LEFT){
            throw new IllegalStateException("Toss is already Stimulated\n Try to start playing the Game");
        }
        int headOrTail = MatchUtil.stimulateToss();
        int choiceOfInning = MatchUtil.stimulateToss();

        int whichTeamToBatFirst = MatchUtil.decideFirstBatter(headOrTail, choiceOfInning);
        if(whichTeamToBatFirst != 1) {
            swapTeams(match);
        }

        match.setMatchState(MatchState.TEAM1_BATTING);
        matchRepository.update(match);
        List<TeamDTO> selectedTeams = getSelectedTeams(match.getTeam1Id(), match.getTeam2Id());
        TossSimulationResult tossSimulationResult = new TossSimulationResult(
                matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName()
        );
        teamService.insertStrikesForNewMatch(match.getTeam1Id(), matchId);
        return tossSimulationResult;
    }

    public List<PlayerDTO> fetchAvailableBowlers(Match match, Integer currentBowlTeamId) {
        int maxOvers = match.getMaxovers();
        return teamService.getAllAvailableBowlers(match, currentBowlTeamId, maxOvers);
    }

    public ScoreBoard playTheOver(Match match, int currentBowlTeamId, Player bowler) {
        setBowlerForThisOver(match, currentBowlTeamId, bowler.getPlayerId());
        Strike strike = teamService.initializeStrike(match, currentBowlTeamId, bowler);
        Team battingTeam = strike.getBattingTeam();
        UnfairBallType unfairBallType;
        boolean isWicketPossible = true;
        OverCompletionResult overCompletionResult = new OverCompletionResult();
        for (int j = 0; j < 6; j++) {
            unfairBallType = playTheBall(strike, overCompletionResult, isWicketPossible);
            if (battingTeam.isAllOut() || ((battingTeam.getScoreToChase() != -1) && (battingTeam.getScoreToChase() < battingTeam.getTeamScore()))) {
                break;
            }
            if(unfairBallType != UnfairBallType.NA) {
                j--;
            }
            isWicketPossible = (unfairBallType != UnfairBallType.NO);
        }
        strike.changeStrike();
        teamService.updateStrike(strike);
        checkIfInningEnded(match, strike, overCompletionResult);
        if(!isMatchEnded(match)) {
            getOverCompletionResult(strike, match, overCompletionResult, currentBowlTeamId);
            return overCompletionResult;
        }
        else{
            return generateFinalScoreBoard(match.getMatchId());
        }
    }

    public MatchResult generateFinalScoreBoard(Integer matchId){
        return ballEventsRepository.generateFinalScoreBoard(matchId);
    }

    private boolean isMatchEnded(Match match){
        return (match.getMatchState() == MatchState.TEAM1_WON || match.getMatchState() == MatchState.TEAM2_WON || match.getMatchState() == MatchState.TIE);
    }

    private void getOverCompletionResult(Strike strike, Match match, OverCompletionResult overCompletionResult, int currentBowlTeamId) {
        Team battingTeam = strike.getBattingTeam();
        overCompletionResult.setStrike(strike.getCurrentStrikePlayer().toString());

        if(strike.getCurrentNonStrikePlayer() != null) {
            overCompletionResult.setNonStrike(strike.getCurrentNonStrikePlayer().toString());
        }

        overCompletionResult.setTeamScore(battingTeam.toString());

        if(battingTeam.getScoreToChase() != -1 && battingTeam.getScoreToChase() > battingTeam.getTeamScore()){
            overCompletionResult.setScoreToChase((battingTeam.getScoreToChase() - battingTeam.getTeamScore()) + " runs left in " + (match.getOvers()*6 - battingTeam.getPlayedBalls()) + " balls");
        }

        overCompletionResult.setBowler(strike.getCurrentBowler().getName());
        if(match.getMatchState() == MatchState.TEAM1_BATTING)
            overCompletionResult.setBowlerForNextOver(fetchAvailableBowlers(match, match.getTeam2Id()));
        else
            overCompletionResult.setBowlerForNextOver(fetchAvailableBowlers(match, match.getTeam1Id()));
    }

    private void checkIfInningEnded(Match match, Strike strike, OverCompletionResult overCompletionResult){
        Team battingTeam = strike.getBattingTeam();
        if(battingTeam.getScoreToChase() != -1 && battingTeam.getScoreToChase() < battingTeam.getTeamScore()) {
            overCompletionResult.setIntermediateResult(strike.getBattingTeam().getTeamName() + " Win the game");
            match.setMatchState(MatchState.TEAM2_WON);
            matchRepository.update(match);
        }
        else if(battingTeam.isAllOut() || strike.getCurrentOver() == match.getOvers()){
            if(match.getMatchState() == MatchState.TEAM1_BATTING){
                match.setMatchState(MatchState.TEAM2_BATTING);
                overCompletionResult.setIntermediateResult(battingTeam.getTeamName() + " will start fielding");
                teamService.insertStrikesForNewMatch(match.getTeam2Id(), match.getMatchId());
                matchRepository.update(match);
            } else{
                if(battingTeam.getScoreToChase() == battingTeam.getTeamScore()) {
                    match.setMatchState(MatchState.TIE);
                    overCompletionResult.setIntermediateResult("match Tied");
                }
                else {
                    match.setMatchState(MatchState.TEAM1_WON);
                    overCompletionResult.setIntermediateResult("Team 1 Won the game");
                }
                matchRepository.update(match);
            }
        }
    }

    private void outcomeOnWicketBall(Strike strike, OverCompletionResult overCompletionResult){
        strike.incrementTotalBalls();
        int over = strike.getCurrentOver();
        int ballNumber = strike.getBattingTeam().getPlayedBalls() % 6;
        String typeOfWicketFallen = MatchUtil.getRandomTypeOfWicket();
        Team battingTeam = strike.getBattingTeam();
        overCompletionResult.appendBallLogs(over + "." + ballNumber + ": Wicket-" + (battingTeam.getCurrentWickets()+1) + "(" + typeOfWicketFallen + ") || Player: " + battingTeam.getNameOfPlayer(strike.getCurrentStrike()));
        ballEventsRepository.insertEvent(
                strike.getMatchId(), battingTeam.getTeamId(), 0, battingTeam.getPlayedBalls(), battingTeam.getPlayerIdByIndex(strike.getCurrentStrike()),
                strike.getCurrentBowlerPlayerId(), 0, "", typeOfWicketFallen
        );
        teamService.updateStrikeOnWicket(strike);
    }

    private void legitimateBall(Strike strike, int outcomeOfBallBowled, OverCompletionResult overCompletionResult, boolean isTeamScore){
        Team battingTeam = strike.getBattingTeam();
        int over = strike.getCurrentOver();
        int ballNumber = battingTeam.getPlayedBalls()%6 + ((isTeamScore) ?1 :0);
        if(ballNumber == 6){
            ballNumber = 0;
            over++;
        }
        int currentPlayer = strike.getCurrentStrike();
        if(isTeamScore) {
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": " + outcomeOfBallBowled + " run ");
            battingTeam.incrementTeamScoreForUnfair(outcomeOfBallBowled);
        }
        else {
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": " + outcomeOfBallBowled + " run || Player: " + battingTeam.getNameOfPlayer(currentPlayer));
            battingTeam.incrementTeamScore(outcomeOfBallBowled, currentPlayer);
        }

        ballEventsRepository.insertEvent(strike.getMatchId(), battingTeam.getTeamId(), 0, over*6 + ballNumber,
                (isTeamScore) ?-1 :battingTeam.getPlayerIdByIndex(currentPlayer), strike.getCurrentBowlerPlayerId(), outcomeOfBallBowled, "", ""
        );

        if(outcomeOfBallBowled%2 == 1) {
            strike.changeStrike();
            teamService.updateStrike(strike);
        }

        checkIfRunOut(strike, outcomeOfBallBowled, overCompletionResult);
    }

    private void checkIfRunOut(Strike strike, int outcomeOfBallBowled, OverCompletionResult overCompletionResult) {
        Team battingTeam = strike.getBattingTeam();
        if(outcomeOfBallBowled != 4 && outcomeOfBallBowled != 6) {
            int possibilityOfRunOut = ThreadLocalRandom.current().nextInt(0, 10);
            if (possibilityOfRunOut == 9) {
                overCompletionResult.appendBallLogs("RunOut-" + battingTeam.getNameOfPlayer(strike.getCurrentStrike()));
                ballEventsRepository.insertEvent(
                        strike.getMatchId(), battingTeam.getTeamId(), 0, battingTeam.getPlayedBalls(),
                        battingTeam.getPlayerIdByIndex(strike.getCurrentStrike()),
                        -1, 0, "", "RUN OUT"
                );
                teamService.updateStrikeOnWicket(strike);
            }
        }
    }

    private UnfairBallType playTheBall(Strike strike, OverCompletionResult overCompletionResult, boolean wicketPossible){
        Team battingTeam = strike.getBattingTeam();
        int ballNumber = battingTeam.getPlayedBalls()%6 + 1;
        int over = strike.getCurrentOver();
        if(ballNumber == 6){
            over++;
            ballNumber = 0;
        }
        Player currentPlayer = strike.getCurrentStrikePlayer();
        int outcomeOfBallBowled = MatchUtil.generateRandomScore(currentPlayer.getPlayerType(), wicketPossible);

        if(outcomeOfBallBowled == -1){
            outcomeOnWicketBall(strike, overCompletionResult);
            return UnfairBallType.NA;
        }
        else{
            return checkPossibilityOfUnFairBall(strike, overCompletionResult, ballNumber, over, outcomeOfBallBowled);
        }
    }

    private UnfairBallType checkPossibilityOfUnFairBall(Strike strike, OverCompletionResult overCompletionResult, int ballNumber, int over, int outcomeOfBallBowled) {
        Team battingTeam = strike.getBattingTeam();
        int possibilityOfUnFairBall = ThreadLocalRandom.current().nextInt(0,9);

        if(possibilityOfUnFairBall <= 1){
            battingTeam.incrementTeamScoreForUnfair(1);
            String typeOfUnFairBall = (possibilityOfUnFairBall == 0) ?"WIDE" :"NO BALL";
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": 1 run (" + typeOfUnFairBall + ")");
            ballEventsRepository.insertEvent(strike.getMatchId(), battingTeam.getTeamId(), 0, over *6 + ballNumber,
                    -1, strike.getCurrentBowlerPlayerId(), 1, typeOfUnFairBall, ""
            );

            legitimateBall(strike, outcomeOfBallBowled, overCompletionResult, true);
            // returning Enum will ensure the caller, not to update the ball number
            return (possibilityOfUnFairBall == 0) ? UnfairBallType.WIDE : UnfairBallType.NO;
        }
        else {
            battingTeam.incrementTotalBalls(strike.getCurrentStrike());
            legitimateBall(strike, outcomeOfBallBowled, overCompletionResult, false);
            return UnfairBallType.NA;
        }
    }

    private List<TeamDTO> getSelectedTeams(int team1Id, int team2Id) {
        List<TeamDTO> allTeams = teamRepository.findAll();
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
        return Arrays.asList(team1, team2);
    }

    private void swapTeams(Match match) {
        int team1Id = match.getTeam1Id();
        match.setTeam1Id(match.getTeam2Id());
        match.setTeam2Id(team1Id);
    }
    private void setBowlerForThisOver(Match match, Integer currentBowlTeamId, int bowlerId) {
        teamService.setBowlerForThisOver(match, currentBowlTeamId, bowlerId);
    }
}
