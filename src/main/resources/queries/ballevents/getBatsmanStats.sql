select name, Score, `Balls Played` from
    (select sum(score) "Score", count(distinct ball_number) "Balls Played", batsman, batting_team from BallEvents where match_id = ? group by batsman, batting_team) as IndividualScoreBoard
        inner join Player p on IndividualScoreBoard.batsman = p.player_order and IndividualScoreBoard.batting_team = p.team_id;