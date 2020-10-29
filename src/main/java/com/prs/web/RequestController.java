package com.prs.web;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.prs.business.Request;
import com.prs.db.RequestRepo;

@CrossOrigin
@RestController
@RequestMapping("api/requests")
public class RequestController {
	final String NEW = "New";
	final String REVIEW = "Review";
	final String APPROVED = "Approved";
	final String REJECTED = "Rejected";

	@Autowired
	private RequestRepo requestRepo;

	// list all requests
	@GetMapping("")
	public List<Request> getAll() {
		return requestRepo.findAll();
	}

	// get request by primary key (id)
	@GetMapping("/{id}")
	public Optional<Request> getByPk(@PathVariable int id) {
		Optional<Request> r = requestRepo.findById(id);
		if (r.isPresent()) {
			return r;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found");
		}
	}

	// add new request
	/*
	 * incoming JSON request must be translated into Java that's why we add
	 * the @RequestBody to the Request object
	 */
	@PostMapping("")
	public Request insert(@RequestBody Request r) {
		r.setStatus(NEW);
		LocalDateTime submittedDate = LocalDateTime.now();
		r.setSubmittedDate(submittedDate);
		return requestRepo.save(r);
	}

	// list all requests for reviewing
	/*
	 * pass userId to make sure reviewer is not be allowed to approve their own
	 * requests
	 */
	@GetMapping("/reviews/{id}")
	public List<Request> getRequests(@PathVariable int id) {
		List<Request> requests = requestRepo.findByStatus(REVIEW);
		List<Request> newRequestsList = new ArrayList<>();
		for (Request rst : requests) {
			int userId = rst.getUser().getId();
			if (userId != id) {
				newRequestsList.add(rst);
			}
		}
		return newRequestsList;
	}

	// set request to "Review" status
	@PutMapping("/review")
	public Request setRequests(@RequestBody Request r) {
		if (r.getStatus().equalsIgnoreCase(NEW) && r.getTotal() <= 50.00) {
			r.setStatus(APPROVED);
		} else if (r.getStatus().equalsIgnoreCase(NEW) && r.getTotal() > 50.00) {
			r.setStatus(REVIEW);
		}
		LocalDateTime submittedDate = LocalDateTime.now();
		r.setSubmittedDate(submittedDate);
		return requestRepo.save(r);
	}

	// set request to "Approved" status
	@PutMapping("/approve")
	public Request setApproved(@RequestBody Request r) {
		if (r.getStatus().equalsIgnoreCase(REVIEW)) {
			r.setStatus(APPROVED);
		}
		return requestRepo.save(r);
	}

	// set request to "Rejected" status
	@PutMapping("/reject")
	public Request setRejected(@RequestBody Request r) {
		if (r.getStatus().equalsIgnoreCase(REVIEW)) {
			r.setStatus(REJECTED);
		}
		return requestRepo.save(r);
	}

	// update a request
	@PutMapping("/{id}")
	/*
	 * incoming JSON request must be translated into Java that's why we add
	 * the @RequestBody to the Request object; pass requestId to make sure the
	 * necessary request is updated
	 */
	public Request update(@RequestBody Request r, @PathVariable int id) {
		if (id == r.getId()) {
			return requestRepo.save(r);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request Id does not match");
		}
	}

	// delete request by primary key (id)
	@DeleteMapping("/{id}")
	public Optional<Request> delete(@PathVariable int id) {
		Optional<Request> r = requestRepo.findById(id);
		if (r.isPresent()) {
			requestRepo.deleteById(id);
			;
			return r;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found");
		}
	}
}
