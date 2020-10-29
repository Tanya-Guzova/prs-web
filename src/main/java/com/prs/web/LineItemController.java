package com.prs.web;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.prs.business.LineItem;
import com.prs.business.Request;
import com.prs.db.LineItemRepo;
import com.prs.db.RequestRepo;

@CrossOrigin
@RestController
@RequestMapping("api/lines")
public class LineItemController {
	@Autowired
	private LineItemRepo lineItemRepo;

	@Autowired
	private RequestRepo requestRepo;

	// get all lines for definite request
	@GetMapping("for-req/{id}")
	public List<LineItem> getLinesForRequest(@PathVariable int id) {
		List<LineItem> lines = lineItemRepo.findAllByRequestId(id);
		return lines;
	}

	// get line by primary key (id)
	@GetMapping("/{id}")
	public Optional<LineItem> getByPk(@PathVariable int id) {
		Optional<LineItem> lI = lineItemRepo.findById(id);
		if (lI.isPresent()) {
			return lI;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "LineItem not found");
		}
	}

	// add new line
	/*
	 * incoming JSON lineItem must be translated into Java that's why we add
	 * the @RequestBody to the LineItem object; pass requestId to make sure line is
	 * added to the necessary request
	 */
	@PostMapping("/{reqid}")
	public LineItem insert(@RequestBody LineItem lI, @PathVariable int reqid) {
		// 1 do maintenance
		if (lI.getRequest().getId() == reqid) {
			lI = lineItemRepo.save(lI);
			// 2 recalculate request total
			recalculateRequestTotal(lI.getRequest());
			return lI;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Request not found");
		}
	}

	// update line
	@PutMapping("/{id}")
	/*
	 * incoming JSON lineItem must be translated into Java that's why we add
	 * the @RequestBody to the LineItem object; pass lineItemId to make sure the
	 * necessary line is updated
	 */
	public LineItem put(@RequestBody LineItem lI, @PathVariable int id) {
		if (id == lI.getId()) {
			lI = lineItemRepo.save(lI);
			// 2 recalculate request total
			recalculateRequestTotal(lI.getRequest());
			return lI;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "LineItem Id does not match");
		}
	}

	// delete line by primary key (id)
	@DeleteMapping("/{id}")
	public Optional<LineItem> delete(@PathVariable int id) {
		Optional<LineItem> lI = lineItemRepo.findById(id);
		if (lI.isPresent()) {
			lineItemRepo.deleteById(id);
			// 2 recalculate request total
			recalculateRequestTotal(lI.get().getRequest());
			return lI;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Line not found");
		}
	}

	// method that calculates request total calculator and saves that to the request instance
	public void recalculateRequestTotal(Request r) {
		// get all lines for the definite request
		List<LineItem> lines = lineItemRepo.findAllByRequestId(r.getId());
		// loop through the lines to sum the total
		double total = 0.0;
		for (LineItem li : lines) {
			double subtotal = li.getProduct().getPrice() * li.getQuantity();
			total += subtotal;
		}
		// save the total in the request instance
		r.setTotal(total);
		requestRepo.save(r);
	}

}
