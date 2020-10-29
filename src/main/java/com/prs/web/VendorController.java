package com.prs.web;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.prs.business.Vendor;
import com.prs.db.VendorRepo;

@CrossOrigin
@RestController
@RequestMapping("api/vendors")
public class VendorController {

	@Autowired
	private VendorRepo vendorRepo;

	// list all vendors
	@GetMapping("")
	public List<Vendor> getAll() {
		return vendorRepo.findAll();
	}

	// get vendor by primary key (id)
	@GetMapping("/{id}")
	public Optional<Vendor> getByPk(@PathVariable int id) {
		Optional<Vendor> v = vendorRepo.findById(id);
		if (v.isPresent()) {
			return v;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found");
		}
	}

	// add a vendor
	/*
	 * incoming JSON request must be translated into Java that's why we add
	 * the @RequstedBody to the Vendor object
	 */
	@PostMapping("")
	public Vendor insert(@RequestBody Vendor v) {
		return vendorRepo.save(v);
	}

	// update vendor
	/*
	 * incoming JSON request must be translated into Java that's why we add
	 * the @RequstedBody to the Vendor object; pass vendorId to make sure the
	 * necessary vendor is updated
	 */
	@PutMapping("/{id}")
	public Vendor update(@RequestBody Vendor v, @PathVariable int id) {
		if (id == v.getId()) {
			return vendorRepo.save(v);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor Id does not match");
		}
	}

	// delete vendor by primary key (id)
	@DeleteMapping("/{id}")
	public Optional<Vendor> delete(@PathVariable int id) {
		Optional<Vendor> v = vendorRepo.findById(id);
		if (v.isPresent()) {
			vendorRepo.deleteById(id);
			return v;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Vendor not found");
		}
	}
}
