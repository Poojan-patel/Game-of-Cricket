package com.tekion.intern.repo;

import com.tekion.intern.beans.Player;
import org.springframework.data.repository.CrudRepository;

public interface PlayerRepositoryJPA extends CrudRepository<Player, Integer> {


}
