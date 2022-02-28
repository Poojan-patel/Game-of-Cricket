select name, Score, `Balls Played` from
    (select sum(score) "Score", count(distinct ballnumber) "Balls Played", batsman from BallEvents where match_id = ? group by batsman) as IndividualScoreBoard
        inner join Player p on IndividualScoreBoard.batsman = p.player_id;