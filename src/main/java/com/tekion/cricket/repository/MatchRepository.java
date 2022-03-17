package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Match;

public interface MatchRepository {

    String save(Match match);

    Match findByMatchId(String matchId);

    void update(Match match);
}
