package com.tekion.cricket.services;

import com.tekion.cricket.beans.*;
import com.tekion.cricket.constants.Common;
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
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class MatchServiceImpl implements  MatchService{
    private TeamRepository teamRepository;
    private MatchRepository matchRepository;
    private BallEventsRepository ballEventsRepository;
    private TeamService teamService;
    private PlayerRepository playerRepository;
    private static final String LINE_BREAKER1 = "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~";
    private static final String LINE_BREAKER2 = "-------------------------------------------";

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
        int matchId = matchRepository.save(match);
        return new MatchCreationResponse(matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName(), matchRequest.getOvers());
    }

    @Override
    public TossSimulationResult stimulateTossAndInsertStrike(Match match) {
        int matchId = match.getMatchId();
        if(MatchState.fromStringToEnum(match.getMatchState()) != MatchState.TOSS_LEFT){
            throw new IllegalStateException("Toss is already Stimulated\n Try to start playing the Game");
        }
        int headOrTail = MatchUtil.stimulateToss();
        int choiceOfInning = MatchUtil.stimulateToss();

        int whichTeamToBatFirst = MatchUtil.decideFirstBatter(headOrTail, choiceOfInning);
        if(whichTeamToBatFirst != 1) {
            swapTeams(match);
        }

        match.setMatchState(MatchState.TEAM1_BATTING.toString());
        matchRepository.update(match);
        List<TeamDTO> selectedTeams = getSelectedTeams(match.getTeam1Id(), match.getTeam2Id());
        TossSimulationResult tossSimulationResult = new TossSimulationResult(
                matchId, selectedTeams.get(0).getTeamName(), selectedTeams.get(1).getTeamName()
        );
        teamService.insertStrikesForNewInning(match.getTeam1Id(), matchId);
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
        for (int j = 0; j < Common.BALLS_IN_ONE_OVER; j++) {
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
        return generateIntermediateScoreBoard(match, currentBowlTeamId, strike, battingTeam, overCompletionResult);
    }

    @Override
    public MatchResult generateFinalScoreBoard(Integer matchId){
        return ballEventsRepository.generateFinalScoreBoard(matchId);
    }

    @Override
    public MatchRecreateResponse recreateMatch(Match match) {
        if(!MatchUtil.isMatchEnded(match)){
            throw new IllegalStateException("Match has not completed yet");
        }
        MatchResult matchResult = generateFinalScoreBoard(match.getMatchId());
        Map<Integer, String> playerIdToNameMap = playerRepository.fetchPlayerNamesByTeamId(match.getTeam1Id());
        playerIdToNameMap.putAll(playerRepository.fetchPlayerNamesByTeamId(match.getTeam2Id()));
        MatchRecreateResponse matchRecreateResponse = new MatchRecreateResponse(playerIdToNameMap);
        matchRecreateResponse.setMatchResult(matchResult);
        setBallByBallStats(matchRecreateResponse, match);
        return matchRecreateResponse;
    }

    @Override
    public Match findByMatchId(Integer matchId) {
        return matchRepository.findByMatchId(matchId);
    }

    private ScoreBoard generateIntermediateScoreBoard(Match match, int currentBowlTeamId, Strike strike, BattingTeam battingTeam, OverCompletionResult overCompletionResult) {
        if(!MatchUtil.isMatchEnded(match)) {
            getOverCompletionResult(strike, battingTeam, match, overCompletionResult, currentBowlTeamId);
            return overCompletionResult;
        }
        else{
            return generateFinalScoreBoard(match.getMatchId());
        }
    }

    private void setBallByBallStats(MatchRecreateResponse matchRecreateResponse, Match match) {
        List<BallEvent> team1BallEvents = ballEventsRepository.fetchAllEventsByMatchAndTeamId(match.getMatchId(), match.getTeam1Id());
        List<BallEvent> team2BallEvents = ballEventsRepository.fetchAllEventsByMatchAndTeamId(match.getMatchId(), match.getTeam2Id());
        String team1Name = teamRepository.getTeamNameByTeamId(match.getTeam1Id());
        String team2Name = teamRepository.getTeamNameByTeamId(match.getTeam2Id());
        setBallByBallStatsForSingleTeam(matchRecreateResponse, team1BallEvents, team1Name);
        setBallByBallStatsForSingleTeam(matchRecreateResponse, team2BallEvents, team2Name);

    }

    private void setBallByBallStatsForSingleTeam(MatchRecreateResponse matchRecreateResponse, List<BallEvent> ballEvents, String teamName) {
        matchRecreateResponse.appendLog(LINE_BREAKER1);
        matchRecreateResponse.appendLog(teamName + " has Started Batting");
        int wicketCounter = 0;
        int currentBall = 0;
        for(BallEvent ballEvent: ballEvents){
            if(ballEvent.getBallNumberForOver() == 1 && currentBall != ballEvent.getBallNumber()){
                    currentBall = ballEvent.getBallNumber();
                    matchRecreateResponse.appendLog(LINE_BREAKER2);
                    matchRecreateResponse.appendLog(Common.OVER + ": " + (currentBall/Common.BALLS_IN_ONE_OVER + 1) + " || " + Common.BOWLER + ": %s", ballEvent.getBowler());
            }
            if(UnfairBallType.fromStringToEnum(ballEvent.getUnfairBallType()) != UnfairBallType.NA){
                appendBallStatForUnFairBall(matchRecreateResponse, ballEvent);
                continue;
            }
            if(ballEvent.getWicketType() != null) {
                wicketCounter++;
                appendBallStatForWicketBall(matchRecreateResponse, ballEvent, wicketCounter);
            } else{
                matchRecreateResponse.appendLog(ballEvent.getOverNumber() + "." + ballEvent.getBallNumberForOver() + ": " + ballEvent.getScore() +
                        Common.SINGLE_SPACE + Common.RUN + " || " + Common.PLAYER + ": %s", ballEvent.getBatsman()
                );
            }
        }
    }

    private void appendBallStatForWicketBall(MatchRecreateResponse matchRecreateResponse, BallEvent ballEvent, int currentWickets) {
        matchRecreateResponse.appendLog(ballEvent.getOverNumber() + "." + ballEvent.getBallNumberForOver() + ": " + Common.WICKET + "-" +
                currentWickets + " (" + ballEvent.getWicketType() + ") || " + Common.PLAYER + ": %s", ballEvent.getBatsman()
        );
    }

    private void appendBallStatForUnFairBall(MatchRecreateResponse matchRecreateResponse, BallEvent ballEvent) {
        matchRecreateResponse.appendLog(ballEvent.getOverNumber() + "." + ballEvent.getBallNumberForOver() + ": " + ballEvent.getUnfairBallType() + Common.SINGLE_SPACE + Common.BALL + " (1 Run)");
        matchRecreateResponse.appendLog(ballEvent.getOverNumber() + "." + ballEvent.getBallNumberForOver() + ": " + (ballEvent.getScore()-1) + Common.SINGLE_SPACE + Common.RUN);
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
            overCompletionResult.setScoreToChase((battingTeam.getScoreToChase() - battingTeam.getTeamScore() + 1) + " runs left in " + (match.getOvers()*6 - battingTeam.getPlayedBalls()) + Common.SINGLE_SPACE + Common.BALLS);
        }

        overCompletionResult.setBowler(playerRepository.fetchPlayerNameByPlayerId(strike.getBowler()));
        if(MatchState.fromStringToEnum(match.getMatchState()) == MatchState.TEAM1_BATTING)
            overCompletionResult.setBowlerForNextOver(fetchAvailableBowlers(match, match.getTeam2Id()));
        else
            overCompletionResult.setBowlerForNextOver(fetchAvailableBowlers(match, match.getTeam1Id()));
    }

    private void checkIfInningEnded(Match match, Strike strike, BattingTeam battingTeam, OverCompletionResult overCompletionResult){
        if(battingTeam.getScoreToChase() != -1 && battingTeam.getScoreToChase() < battingTeam.getTeamScore()) {
            overCompletionResult.setIntermediateResult(battingTeam.getTeamName() + " Win the game");
            match.setMatchState(MatchState.TEAM2_WON.toString());
            matchRepository.update(match);
        }
        else if(strike.isAllOut() || battingTeam.getCurrentOver() == match.getOvers()){
            if(MatchState.fromStringToEnum(match.getMatchState()) == MatchState.TEAM1_BATTING){
                match.setMatchState(MatchState.TEAM2_BATTING.toString());
                overCompletionResult.setIntermediateResult(battingTeam.getTeamName() + " will start fielding");
                teamService.insertStrikesForNewInning(match.getTeam2Id(), match.getMatchId());
                matchRepository.update(match);
            } else{
                if(battingTeam.getScoreToChase() == battingTeam.getTeamScore()) {
                    match.setMatchState(MatchState.TIE.toString());
                    overCompletionResult.setIntermediateResult("match Tied");
                }
                else {
                    match.setMatchState(MatchState.TEAM1_WON.toString());
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
            String typeOfUnFairBall = ((possibilityOfUnFairBall == 0) ?UnfairBallType.WIDE :UnfairBallType.NO).toString();
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": 1 run (" + typeOfUnFairBall + ")");
            legitimateBall(strike, battingTeam, outcomeOfBallBowled, overCompletionResult, typeOfUnFairBall);
            // returning Enum will ensure the caller, not to update the ball number
            return (possibilityOfUnFairBall == 0) ? UnfairBallType.WIDE : UnfairBallType.NO;
        }
        else {
            battingTeam.incrementTotalBalls();
            legitimateBall(strike, battingTeam, outcomeOfBallBowled, overCompletionResult, Common.EMPTYSTRING);
            return UnfairBallType.NA;
        }
    }

    private void outcomeOnWicketBall(Strike strike, BattingTeam battingTeam, OverCompletionResult overCompletionResult){
        battingTeam.incrementTotalBalls();
        int over = battingTeam.getCurrentOver();
        int ballNumber = battingTeam.getPlayedBalls() % 6;
        String typeOfWicketFallen = MatchUtil.getRandomTypeOfWicket();
        overCompletionResult.appendBallLogs(over + "." + ballNumber + ": " + Common.WICKET + "-" + (strike.getCurrentWickets()+1) + "(" + typeOfWicketFallen + ") || " + Common.PLAYER + ": %s", strike.getStrike());
        ballEventsRepository.save(new BallEvent(
                strike.getMatchId(), battingTeam.getTeamId(), battingTeam.getPlayedBalls(), strike.getStrike(),
                strike.getBowler(), 0, Common.EMPTYSTRING, typeOfWicketFallen)
        );
        teamService.updateStrikeOnWicket(strike);
    }

    private void legitimateBall(Strike strike, BattingTeam battingTeam, int outcomeOfBallBowled, OverCompletionResult overCompletionResult, String typeOfUnFairBall){
        boolean isTeamScore = !Common.EMPTYSTRING.equals(typeOfUnFairBall);
        int over = battingTeam.getCurrentOver();
        int ballNumber = battingTeam.getPlayedBalls()%6 + ((isTeamScore) ?1 :0);
        if(ballNumber == Common.BALLS_IN_ONE_OVER){
            ballNumber = 0;
            over++;
        }
        int currentPlayer = strike.getStrike();
        if(isTeamScore) {
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": " + outcomeOfBallBowled + Common.SINGLE_SPACE + Common.RUN);
            battingTeam.incrementRuns(outcomeOfBallBowled);
        }
        else {
            overCompletionResult.appendBallLogs(over + "." + ballNumber + ": " + outcomeOfBallBowled + Common.SINGLE_SPACE + Common.RUN + " || " + Common.PLAYER +": %s", currentPlayer);
            battingTeam.incrementRuns(outcomeOfBallBowled);
        }

        ballEventsRepository.save(new BallEvent(
                strike.getMatchId(), battingTeam.getTeamId(), over*6 + ballNumber,
                (isTeamScore) ?-1 :currentPlayer, strike.getBowler(), outcomeOfBallBowled + ((isTeamScore) ?1 :0), typeOfUnFairBall, Common.EMPTYSTRING)
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
                overCompletionResult.appendBallLogs(Common.RUN_OUT + "-%s", strike.getStrike());

                ballEventsRepository.save(new BallEvent(
                        strike.getMatchId(), battingTeam.getTeamId(), currentBallNumber,
                        strike.getStrike(), -1, 0, Common.EMPTYSTRING, Common.RUN_OUT)
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
