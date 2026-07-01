package com.platzi.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)   // skip null fields in serialisation
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductRequest {

    private String        title;
    private Integer       price;
    private String        description;
    private Integer       categoryId;
    private List<String>  images;
}
