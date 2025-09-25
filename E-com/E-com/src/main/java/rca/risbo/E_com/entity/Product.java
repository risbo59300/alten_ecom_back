package rca.risbo.E_com.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    @NotBlank(message = "Le code produit est obligatoire")
    private String code;

    @Column(nullable = false)
    @NotBlank(message = "Le nom est obligatoire")
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    private String image;

    @NotBlank(message = "La catégorie est obligatoire")
    private String category;

    @Column(nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "0.0", inclusive = false, message = "Le prix doit être supérieur à 0")
    private BigDecimal price;

    @Column(nullable = false)
    @Min(value = 0, message = "La quantité ne peut pas être négative")
    private Integer quantity;

    @Column(name = "internal_reference")
    private String internalReference;

    @Column(name = "shell_id")
    private Long ShellId;

    @Column(name = "inventory_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private InventoryStatus inventoryStatus;

    @DecimalMin(value = "0.0", inclusive = false, message = "La note ne peut pas être négative")
    @DecimalMax(value = "5.0", inclusive = false, message = "La note ne peut pas dépasser 5")
    private Double rating;

    @Column(name = "created_at", nullable = false)
    private Long createdAt;

    @Column(name = "updated_at", nullable = false)
    private Long updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now().toEpochMilli();
        this.inventoryStatus = determineInventoryStatus(this.quantity);
    }

    @PrePersist
    public void prepersist(){
        if (this.createdAt == null) {
            this.createdAt = Instant.now().toEpochMilli();
        }
        this.updatedAt = this.createdAt;
        this.inventoryStatus = determineInventoryStatus(this.quantity);
    }

    private InventoryStatus determineInventoryStatus(Integer qty){
        if (qty == null || qty == 0) return InventoryStatus.OUTOFSTOCK;
        if (qty <= 10) return InventoryStatus.LOWSTOCK;
        return InventoryStatus.INSTOCK;
    }
}