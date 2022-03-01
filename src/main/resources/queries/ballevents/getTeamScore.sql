select t.name, `Total Score`, `Total Wickets`, `Total Balls`, Extras from
    (select team, sum(score) "Total Score", count(wicket) "Total Wickets", max(ballnumber) "Total Balls", count(extra) "Extras" from BallEvents where match_id = ? group by team) as scoreboard
        inner join team t on t.team_id = scoreboard.team;