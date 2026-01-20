package prac.backendassingment.domain.repository;

import prac.backendassingment.domain.model.Product;

import java.util.Optional;

public interface ProductRepository {
    Optional<Product> findById(Long id);
    Product addNewProduct(Product product);

    Product updateProduct(Product product);

    Optional<Product> findByIdWithLock(Long id);

    void removeProduct(Long id);

}
