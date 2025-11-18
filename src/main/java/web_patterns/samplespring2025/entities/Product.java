package web_patterns.samplespring2025.entities;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter
@Builder
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @NonNull
    @NotBlank(message="Product code is required")
    @EqualsAndHashCode.Include
    private final String productCode;

    @NotBlank(message="Product name is required")
    @NonNull
    private final String productName;

    @NotBlank(message="Product line is required")
    @NonNull
    private final String productLine;

    @NotBlank(message="Product scale is required")
    @NonNull
    private final String productScale;

    @NotBlank(message="Product vendor is required")
    @NonNull
    private final String productVendor;

    @NotBlank(message="Product description is required")
    @NonNull
    private final String productDescription;

    @PositiveOrZero(message = "Quantity in stock must be >= 0")
    @NonNull
    private final int quantityInStock;

    @Positive(message = "Buy price must be positive")
    @NonNull
    private final double buyPrice;

    @Positive(message = "msrp must be positive")
    @NonNull
    private final double msrp;
}
