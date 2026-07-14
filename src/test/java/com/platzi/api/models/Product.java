package com.platzi.api.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class Product {

    private Integer       id;
    private String        title;
    private Integer       price;
    private String        description;
    private String        slug;
    private Category      category;
    private List<String>  images;
    private String        creationAt;
    private String        updatedAt;
}
