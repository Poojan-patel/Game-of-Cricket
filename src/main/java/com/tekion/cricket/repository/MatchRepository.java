package com.tekion.cricket.repository;

import com.tekion.cricket.beans.Match;

public interface MatchRepository {

    int save(Match match);

    Match findByMatchId(Integer matchId);

    void update(Match match);
}
