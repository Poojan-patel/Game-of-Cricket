select * from team inner join
    (select count(distinct ball_number) Balls, sum(score) Score, batting_team from BallEvents where match_id = ? and batting_team = ?)
    as ScoreBoard on ScoreBoard.batting_team = team.team_id