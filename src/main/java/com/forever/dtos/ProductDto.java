package com.forever.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ProductDto {

    private UUID id;
    private String name;
    private String description;
    private BigDecimal price;
    private String brand;
    private Float rating;
    private Integer discount;
    private String slug;
    private Boolean isNewArrival;
    private String thumbnail;
    private UUID categoryId;
    private String categoryName;
    private UUID categoryTypeId;
    private String categoryTypeName;
    private List<ProductVariantDto> productVariants;
    private List<ResourceDto> resources;
}
