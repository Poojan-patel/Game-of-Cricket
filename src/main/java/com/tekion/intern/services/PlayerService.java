package com.tekion.intern.services;

import com.tekion.intern.repo.BallEventsRepository;
import com.tekion.intern.repo.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PlayerService {
    private PlayerRepository playerRepository;
    private BallEventsRepository ballEventsRepository;

    @Autowired
    public void setRepository(PlayerRepository playerRepository, BallEventsRepository ballEventsRepository){
        this.playerRepository = playerRepository;
        this.ballEventsRepository = ballEventsRepository;
    }


}
