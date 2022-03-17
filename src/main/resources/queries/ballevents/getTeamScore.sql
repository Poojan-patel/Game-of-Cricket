select t.name, `Total Score`, `Total Wickets`, `Total Balls`, Extras from
    (select batting_team, sum(score) "Total Score", count(wicket_type) "Total Wickets", max(ball_number) "Total Balls", count(extra) "Extras" from BallEvents where match_id = ? group by batting_team) as scoreboard
        inner join team t on t.team_id = scoreboard.batting_team;