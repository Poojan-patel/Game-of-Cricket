package com.tekion.cricket.services;

import com.tekion.cricket.beans.*;
import com.tekion.cricket.enums.MatchState;
import com.tekion.cricket.enums.UnfairBallType;
import com.tekion.cricket.models.*;
import com.tekion.cricket.repository.BallEventsRepository;
import com.tekion.cricket.repository.MatchRepository;
import com.tekion.cricket.repository.PlayerRepository;
import com.tekion.cricket.repository.TeamRepository;
import com.tekion.cricket.util.MatchUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MatchServiceImpl implements  MatchService{
    private TeamRepository teamRepository;
    private MatchRepository matchRepository;
    private BallEventsRepository ballEventsRepository;
    private TeamService teamService;
    private PlayerRepository playerRepository;

    @Autowired
    public void setRepository(
            TeamRepository teamRepository, MatchRepository matchRepository, BallEventsRepository ballEventsRepository, PlayerRepository playerRepository
    ) {
         this.teamRepository = teamRepository;
         this.matchRepository = matchRepository;
         this.ballEventsRepository = ballEventsRepository;
         this.playerRepository = playerRepository;
    }

    @Autowired
    public void setService(TeamService teamService){
        this.teamService = teamService;
    }

    @Override
    public MatchCreationResponse createNewMatch(MatchCreationRequest matchRequest, List<TeamDTO> selectedTeams){
        Match match = new Match(matchRequest.getTeam1Id(), matchRequest.getTeam2Id(), matchRequest.getOvers());
        int matchId = 0;
        matchId = matchRepository.save(match);
        return new MatchCreationResponse(matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName(), matchRequest.getOvers());
    }

    @Override
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

    @Override
    public List<PlayerDTO> fetchAvailableBowlers(Match match, Integer currentBowlTeamId) {
        int maxOvers = match.getMaxovers();
        return teamService.getAllAvailableBowlers(match, currentBowlTeamId, maxOvers);
    }

    @Override
    public ScoreBoard playTheOver(Match match, int currentBowlTeamId, Player bowler) {
        setBowlerForThisOver(match, currentBowlTeamId, bowler.getPlayerId());
        Strike strike = teamService.initializeStrike(match, currentBowlTeamId, bowler);
        BattingTeam battingTeam = teamService.initializeBattingTeam(match, currentBowlTeamId);
        UnfairBallType unfairBallType;
        boolean isWicketPossible = true;
        OverCompletionResult overCompletionResult = new OverCompletionResult(playerRepository.fetchPlayerNamesByTeamId(battingTeam.getTeamId()));
        for (int j = 0; j < 6; j++) {
            unfairBallType = playTheBall(strike, battingTeam, overCompletionResult, isWicketPossible);
            if (strike.isAllOut() || ((battingTeam.getScoreToChase() != -1) && (battingTeam.getScoreToChase() < battingTeam.getTeamScore()))) {
                break;
            }
            if(unfairBallType != UnfairBallType.NA) {
                j--;
            }
            isWicketPossible = (unfairBallType != UnfairBallType.NO);
        }
        strike.changeStrike();
        teamService.updateStrike(strike);
        checkIfInningEnded(match, strike, battingTeam, overCompletionResult);
        if(!isMatchEnded(match)) {
            getOverCompletionResult(strike, battingTeam, match, overCompletionResult, currentBowlTeamId);
            return overCompletionResult;
        }
        else{
            return generateFinalScoreBoard(match.getMatchId());
        }
    }

    @Override
    public MatchResult generateFinalScoreBoard(Integer matchId){
        return ballEventsRepository.generateFinalScoreBoard(matchId);
    }

    private boolean isMatchEnded(Match match){
        return (match.getMatchState() == MatchState.TEAM1_WON || match.getMatchState() == MatchState.TEAM2_WON || match.getMatchState() == MatchState.TIE);
    }

    private void getOverCompletionResult(Strike strike, BattingTeam battingTeam, Match match, OverCompletionResult overCompletionResult, int currentBowlTeamId) {
        List<BatsmanStats> batsmanStats = playerRepository.fetchOnFieldBatsmenData(strike.getStrike(), strike.getNonStrike(), strike.getMatchId());
        if(batsmanStats.size() == 1)
            overCompletionResult.setNonStrike(batsmanStats.get(0).toString());
        else{
            overCompletionResult.setNonStrike(batsmanStats.get(1).toString());
            overCompletionResult.setStrike(batsmanStats.get(0).toString());
        }
        overCompletionResult.setTeamScore(battingTeam.convertToLog(strike.getCurrentWickets()));

        if(battingTeam.getScoreToChase() != -1 && battingTeam.getScoreToChase() > battingTeam.getTeamScore()){
            overCompletionResult.setScoreToChase((battingTeam.getScoreToChase() - battingTeam.getTeamScore() + 1) + " runs left in " + (match.getOvers()*6 - battingTeam.getPlayedBalls()) + " balls");
        }

        overCompletionResult.setBowler(playerRepository.fetchPlayerNameByPlayerId(strike.getBowler()));
        if(match.getMatchState() == MatchState.TEAM1_BATTING)
            overCompletionResult.setBowlerForNextOver(fetchAvailableBowlers(match, match.getTeam2Id()));
        else
            overCompletionResult.setBowlerForNextOver(fetchAvailableBowlers(match, match.getTeam1Id()));
    }

    private void checkIfInningEnded(Match match, Strike strike, BattingTeam battingTeam, OverCompletionResult overCompletionResult){

        if(battingTeam.getScoreToChase() != -1 && battingTeam.getScoreToChase() < battingTeam.getTeamScore()) {
            overCompletionResult.setIntermediateResult(battingTeam.getTeamName() + " Win the game");
            match.setMatchState(MatchState.TEAM2_WON);
            matchRepository.update(match);
        }
        else if(strike.isAllOut() || battingTeam.getCurrentOver() == match.getOvers()){
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

    private UnfairBallType playTheBall(Strike strike, BattingTeam battingTeam, OverCompletionResult overCompletionResult, boolean wicketPossible){
        int ballNumber = battingTeam.getPlayedBalls()%6 + 1;
        int over = battingTeam.getCurrentOver();
        if(ballNumber == 6){
            over++;
            ballNumber = 0;
        }
        int currentPlayer = strike.getStrike();
        int outcomeOfBallBowled = MatchUtil.generateRandomScore(playerRepository.fetchPlayerType(currentPlayer), wicketPossible);

        if(outcomeOfBallBowled == -1){
            outcomeOnWicketBall(strike, battingTeam, overCompletionResult);
            return UnfairBallType.NA;
        }
        else{
            return checkPossibilityOfUnFairBall(strike, battingTeam, overCompletionResult, ballNumber, over, outcomeOfBallBowled);
        }
    }

    private UnfairBallType checkPossibilityOfUnFairBall(Strike strike, BattingTeam battingTeam, OverCompletionResult overCompletionResult, int ballNumber, int over, int outcomeOfBallBowled) {
        int possibilityOfUnFairBall = ThreadLocalRandom.current().nextInt(0,9);

        if(possibilityOfUnFairBall <= 1){
            battingTeam.incrementRuns(1);
            String typeOfUnFairBall = (possibilityOfUnFairBall == 0) ?"WIDE" :"NO BALL";
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": 1 run (" + typeOfUnFairBall + ")");
            legitimateBall(strike, battingTeam, outcomeOfBallBowled, overCompletionResult, typeOfUnFairBall);
            // returning Enum will ensure the caller, not to update the ball number
            return (possibilityOfUnFairBall == 0) ? UnfairBallType.WIDE : UnfairBallType.NO;
        }
        else {
            battingTeam.incrementTotalBalls();
            legitimateBall(strike, battingTeam, outcomeOfBallBowled, overCompletionResult, "");
            return UnfairBallType.NA;
        }
    }

    private void outcomeOnWicketBall(Strike strike, BattingTeam battingTeam, OverCompletionResult overCompletionResult){
        battingTeam.incrementTotalBalls();
        int over = battingTeam.getCurrentOver();
        int ballNumber = battingTeam.getPlayedBalls() % 6;
        String typeOfWicketFallen = MatchUtil.getRandomTypeOfWicket();
        overCompletionResult.appendBallLogs(over + "." + ballNumber + ": Wicket-" + (strike.getCurrentWickets()+1) + "(" + typeOfWicketFallen + ") || Player: %s", strike.getStrike());
        ballEventsRepository.insertEvent(
                strike.getMatchId(), battingTeam.getTeamId(), battingTeam.getPlayedBalls(), strike.getStrike(),
                strike.getBowler(), 0, "", typeOfWicketFallen
        );
        teamService.updateStrikeOnWicket(strike);
    }

    private void legitimateBall(Strike strike, BattingTeam battingTeam, int outcomeOfBallBowled, OverCompletionResult overCompletionResult, String typeOfUnFairBall){
        boolean isTeamScore = !typeOfUnFairBall.equals("");
        int over = battingTeam.getCurrentOver();
        int ballNumber = battingTeam.getPlayedBalls()%6 + ((isTeamScore) ?1 :0);
        if(ballNumber == 6){
            ballNumber = 0;
            over++;
        }
        int currentPlayer = strike.getStrike();
        if(isTeamScore) {
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": " + outcomeOfBallBowled + " run ");
            battingTeam.incrementRuns(outcomeOfBallBowled);
        }
        else {
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": " + outcomeOfBallBowled + " run || Player: %s", currentPlayer);
            battingTeam.incrementRuns(outcomeOfBallBowled);
        }

        ballEventsRepository.insertEvent(strike.getMatchId(), battingTeam.getTeamId(), over*6 + ballNumber,
                (isTeamScore) ?-1 :currentPlayer, strike.getBowler(), outcomeOfBallBowled + ((isTeamScore) ?1 :0), typeOfUnFairBall, ""
        );

        if(outcomeOfBallBowled%2 == 1) {
            strike.changeStrike();
            teamService.updateStrike(strike);
        }

        checkIfRunOut(strike, battingTeam, outcomeOfBallBowled, overCompletionResult, isTeamScore);
    }

    private void checkIfRunOut(Strike strike, BattingTeam battingTeam, int outcomeOfBallBowled, OverCompletionResult overCompletionResult, boolean isTeamScore) {
        int currentBallNumber = battingTeam.getPlayedBalls() + ((isTeamScore) ?1 :0);
        if(outcomeOfBallBowled != 4 && outcomeOfBallBowled != 6) {
            int possibilityOfRunOut = ThreadLocalRandom.current().nextInt(0, 10);
            if (possibilityOfRunOut == 9) {
                // overCompletionResult.appendBallLogs("RunOut-" + battingTeam.getNameOfPlayer(strike.getStrike()));
                overCompletionResult.appendBallLogs("RunOut-%s", strike.getStrike());
                ballEventsRepository.insertEvent(
                        strike.getMatchId(), battingTeam.getTeamId(), currentBallNumber,
                        strike.getStrike(), -1, 0, "", "RUN OUT"
                );
                teamService.updateStrikeOnWicket(strike);
            }
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
