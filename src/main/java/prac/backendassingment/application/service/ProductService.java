package prac.backendassingment.application.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import prac.backendassingment.application.dto.ProductAddRequest;
import prac.backendassingment.application.dto.ProductStockRequest;
import prac.backendassingment.domain.model.Product;
import prac.backendassingment.domain.repository.ProductRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;

    @Transactional
    public Product addNewProduct(ProductAddRequest request){
        Product product = new Product(
                request.getName(),
                request.getPrice(),
                request.getTotalAmount()
        );

        return productRepository.addNewProduct(product);
    }

    @Transactional
    public void removeProduct(Long id){
        productRepository.removeProduct(id);
    }

    public Product findProductById(Long id){
        Optional<Product> optional = productRepository.findById(id);

        return optional.orElseThrow(()-> new IllegalArgumentException("그런 상품은 존재하지 않음."));
    }

    @Transactional
    public Product decreaseStock(ProductStockRequest request){
        Product target = productRepository.findByIdWithLock(request.getId()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 상품입니다."));
        target.decreaseAmount(request.getQuantity());
        return productRepository.updateProduct(target);
    }

    @Transactional
    public Product increaseStock(ProductStockRequest request){
        Product target = productRepository.findByIdWithLock(request.getId()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 상품입니다."));
        target.increaseAmount(request.getQuantity());
        return productRepository.updateProduct(target);
    }


}
