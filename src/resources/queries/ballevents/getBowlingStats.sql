select name, Score, `Balls Thrown`, `Extra`, `Wicket` from
(select sum(score) "Score", count(distinct ballnumber) "Balls Thrown", count(extra) "Extra", count(wicket) "Wicket", bowler from BallEvents where match_id = ? group by bowler) as BowlingScoreBoard
    inner join Player p on BowlingScoreBoard.bowler = p.player_id;