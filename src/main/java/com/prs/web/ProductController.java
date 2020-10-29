package com.prs.web;

import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import com.prs.business.Product;
import com.prs.db.ProductRepo;

@CrossOrigin
@RestController
@RequestMapping("api/products")
public class ProductController {

	@Autowired
	private ProductRepo productRepo;

	// list all products
	@GetMapping("")
	public List<Product> getAll() {
		return productRepo.findAll();
	}

	// get product by primary key (id)
	@GetMapping("/{id}")
	public Optional<Product> getByPK(@PathVariable int id) {
		Optional<Product> p = productRepo.findById(id);
		if (p.isPresent()) {
			return p;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
		}
	}

	// add new product
	/*
	 * incoming JSON request must be translated into Java that's why we add
	 * the @RequestBody to the Product object
	 */
	@PostMapping("")
	public Product insert(@RequestBody Product p) {
		return productRepo.save(p);
	}

	// update a product
	/*
	 * incoming JSON request must be translated into Java that's why we add
	 * the @RequestBody to the Product object; pass productId to make sure the
	 * necessary product is updated
	 */
	@PutMapping("/{id}")
	public Product update(@RequestBody Product p, @PathVariable int id) {
		if (id == p.getId()) {
			return productRepo.save(p);
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product Id does not match");
		}
	}

	// delete product by primary key (id)
	@DeleteMapping("/{id}")
	public Optional<Product> delete(@PathVariable int id) {
		Optional<Product> p = productRepo.findById(id);
		if (p.isPresent()) {
			productRepo.deleteById(id);
			return p;
		} else {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found");
		}
	}
}
