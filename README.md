# Game-of-Cricket

This is an OOP based Cricket game code, where several entities are there

MatchController: Serves the Main method, takes input from users and constructs the Match
Match: Match includes 2 teams which are playing the game, along with the current players on strike, tossWinner, etc.
Team: Team includes all the players of team, along with currentScore, currentNoOfWickets, etc..
Player: Player is a single player of a team, which has name, personalScore, personalPlayedBalls, etc.
Strike: Strike will handle who will bat next after wicket, strike updation on odd runs, etc.
MatchUtil: miscellaneous calculations are computed by this class

Flow of algorithm:

MatchController
1. MatchController's main() will take necessary input from user like numOfPlayers, numOfOvers, playerName, playerType, teamName, etc
2. MatchController's main() will create a new Match instance with constructor with these read args
3. MatchController will ask for bat or bowl to matchState of toss
4. call will be passes to match object

Match
1. Match will have fields like team1, team2, matchState, totalAvailableBalls and a strike object to manage players on strike
2. when MatchController calls stimulateGame(), based on the tossWinner's decision, game will started
3. startInning() will be called and scoreToChase will be taken into consideration for the chaser team
  1. Inning will have total overs given during construction. method' call returns back when 1. Either all over completes 2. All out declared
4. startInning() will be called for chaser team
  1. same as above, but one more condition, 3. if scoreToChase < currentInningScore, then also methods call will return
5. during an inning, strike will be changed on odd runs or over completion
5. matchState will be declared based on the score of both teams

Team
1. Match constructor will call Team constructor to construct both the team one by one
2. Team will maintain numberOfWickets, totalNumberOfBallPlayed, currentWickets, list of players, etc
3. on a run/wicket, appropriate team will be called to update its run/wicket and balls will be incremented by one
4. each updation method will also call the method of current Strike player to update his score and balls

Player
1. Player will be batsman or bowler along with his name, personalscore, personalBalls, etc..
2. Player is the inner most class which will be called by team for updation

Strike:
1. It will maintain currentOrder on the strike
2. It will update the strike on a ball, on an over completion

Util:
1. Util Contains different methods like random number generation, matchState decider, etc.


