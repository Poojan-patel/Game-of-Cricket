package com.tekion.intern.beans;

import com.tekion.intern.enums.PlayerType;
import com.tekion.intern.enums.TypeOfBowler;
import com.tekion.intern.models.PlayerDTO;

import javax.persistence.*;

@Entity
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer playerId;
    private String name;
    private PlayerType playerType;
    private TypeOfBowler typeOfBowler;

    @ManyToOne
    private Team team;

    public Player(PlayerDTO p){
        name = p.getName();
        playerType = p.getPlayerType();
        typeOfBowler = p.getTypeOfBowler();
    }

    public Player() {

    }

    public void setTeam(Team t){
        team = t;
    }
}
