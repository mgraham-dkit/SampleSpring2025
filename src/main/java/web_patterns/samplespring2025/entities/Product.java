package daos.entities;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Product {
    @NonNull
    @EqualsAndHashCode.Include
    private final String productCode;
    @NonNull
    private final String productName;
    @NonNull
    private final String productLine;
    @NonNull
    private final String productScale;
    @NonNull
    private final String productVendor;
    @NonNull
    private final String productDescription;
    @NonNull
    private final int quantityInStock;
    @NonNull
    private final double buyPrice;
    @NonNull
    private final double msrp;
}
