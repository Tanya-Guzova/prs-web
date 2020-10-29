package com.prs.web;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.prs.business.User;
import com.prs.db.UserRepo;

@CrossOrigin
@RestController
@RequestMapping("api/users")
public class UserController {

	@Autowired
	private UserRepo userRepo;

	// list all users
	@GetMapping("")
	public List<User> getAll() {
		return userRepo.findAll();
	}

	// get user by id
	@GetMapping("/{id}")
	public Optional<User> getByPK(@PathVariable int id) {
		Optional<User> u = userRepo.findById(id);
		if (u.isPresent()) {
			return u;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
	}

	// get user by username and password
	@GetMapping("/{username}/{password}")
	public User logIn(@PathVariable String username, @PathVariable String password) {
		User u = userRepo.findByUserNameAndPassword(username, password);
		if (u != null) {
			return u;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
	}

	// add a user
	/*
	 * incoming JSON request must be translated into Java that's why we add
	 * the @RequestBody to the User object
	 */
	@PostMapping("")
	public User insert(@RequestBody User u) {
		return userRepo.save(u);
	}

	// update a user
	/*
	 * incoming JSON request must be translated into Java that's why we add
	 * the @RequestBody to the User object; pass userId to make sure the necessary
	 * user is updated
	 */
	@PutMapping("/{id}")
	public User update(@RequestBody User u, @PathVariable int id) {
		if (id == u.getId()) {
			return userRepo.save(u);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User Id does not match");
		}
	}

	// delete user by primary key (id)
	@DeleteMapping("/{id}")
	public Optional<User> delete(@PathVariable int id) {
		Optional<User> u = userRepo.findById(id);
		if (u.isPresent()) {
			userRepo.deleteById(id);
			return u;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
		}
	}
}
