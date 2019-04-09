package com.fjd.controller;

import com.fjd.model.User;
import com.fjd.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.persistence.OptimisticLockException;
import java.util.Optional;

@Controller
@RequestMapping(path="/Users")
public class UserController {
	@Autowired
	private UserRepository userRepository;

	@PostMapping(path="/")
	public ResponseEntity<Void> create (@RequestBody User user) {
		userRepository.save(user);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@PutMapping(path="/{id}")
	public ResponseEntity<Void> update (@PathVariable long id,@RequestBody User user) {
		Optional<User> oldUserOpt = userRepository.findById(id);
		User oldUser = oldUserOpt.get();
		oldUser.setName(user.getName());
		oldUser.setEmail(user.getEmail());
		if(user.getVersion()<oldUser.getVersion()){
			throw new OptimisticLockException("this record has been updated by others");
		}
		userRepository.save(oldUser);
		return new ResponseEntity<>(HttpStatus.OK);
	}

	@GetMapping(path="/")
	public @ResponseBody Iterable<User> list() {
		return userRepository.findAll();
	}
}