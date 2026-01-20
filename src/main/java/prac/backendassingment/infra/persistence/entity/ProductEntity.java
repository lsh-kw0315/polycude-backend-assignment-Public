package prac.backendassingment.infra.persistence.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "product")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@AllArgsConstructor
public class ProductEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Long price;

    private Long totalAmount;


    public ProductEntity(String name, Long price, Long totalAmount){
        this(null, name, price, totalAmount);
    }

    public void update(Long stock, String name, Long price){
        totalAmount = stock;
        this.name = name;
        this.price = price;
    }

}
