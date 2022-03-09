select * from team inner join (select count(distinct ballnumber) Balls, sum(score) Score, team from BallEvents where match_id = ? and team = ?)
    as ScoreBoard on ScoreBoard.team = team.team_id