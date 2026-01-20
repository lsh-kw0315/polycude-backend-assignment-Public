package prac.backendassingment.infra.persistence;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import prac.backendassingment.domain.model.Product;
import prac.backendassingment.domain.repository.ProductRepository;
import prac.backendassingment.infra.persistence.entity.ProductEntity;
import prac.backendassingment.infra.persistence.repository.ProductJpaRepository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepository {
    private final ProductJpaRepository productJpaRepository;

    @Override
    public Optional<Product> findById(Long id) {
        Optional<ProductEntity> optional = productJpaRepository.findById(id);
        if(optional.isEmpty()) return Optional.empty();

        ProductEntity productEntity = optional.get();
        return Optional.of(toDomain(productEntity));
    }

    @Override
    public Optional<Product> findByIdWithLock(Long id) {
        Optional<ProductEntity> optional = productJpaRepository.findByIdWithLock(id);
        if(optional.isEmpty()) return Optional.empty();

        ProductEntity productEntity = optional.get();
        return Optional.of(toDomain(productEntity));
    }

    @Override
    public Product addNewProduct(Product product) {
        ProductEntity productEntity = toEntity(product);

        ProductEntity saved = productJpaRepository.save(productEntity);
        return toDomain(saved);
    }

    @Override
    public void removeProduct(Long id) {
        productJpaRepository.deleteById(id);
    }

    @Override
    public Product updateProduct(Product product) {
        ProductEntity target = productJpaRepository.findById(product.getId()).orElseThrow(()->new IllegalArgumentException("존재하지 않는 상품입니다."));
        target.update(product.getTotalAmount(), product.getName(), product.getPrice());
        return toDomain(target);
    }

    private Product toDomain(ProductEntity productEntity){
        return new Product(
                productEntity.getId(),
                productEntity.getName(),
                productEntity.getPrice(),
                productEntity.getTotalAmount()
        );
    }

    private ProductEntity toEntity(Product product){
        return new ProductEntity(
          product.getId(),
          product.getName(),
          product.getPrice(),
          product.getTotalAmount()
        );
    }
}
