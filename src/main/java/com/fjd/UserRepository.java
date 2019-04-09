package com.fjd;

import com.fjd.model.User;
import org.springframework.data.repository.CrudRepository;



public interface UserRepository extends CrudRepository<User, Long> {

}