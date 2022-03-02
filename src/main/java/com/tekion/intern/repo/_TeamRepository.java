package com.tekion.intern.repo;

import com.tekion.intern.beans.Team;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface _TeamRepository extends CrudRepository<Team, Integer> {

}