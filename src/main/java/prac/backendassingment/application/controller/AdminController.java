package prac.backendassingment.application.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import prac.backendassingment.application.dto.ProductAddRequest;
import prac.backendassingment.application.service.ProductService;
import prac.backendassingment.domain.model.Post;
import prac.backendassingment.domain.model.Product;

@RestController
@RequiredArgsConstructor
public class AdminController {
    private final ProductService productService;

    @PostMapping("/products")
    public Product addProduct(@RequestBody ProductAddRequest request){
        return productService.addNewProduct(request);
    }

    @GetMapping("/products/{id}")
    public Product getProducts(@PathVariable("id")Long id){
        return productService.findProductById(id);
    }
}
