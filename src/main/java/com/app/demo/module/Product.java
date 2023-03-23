package com.app.demo.module;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Product {

    private String code;
    private String description;
    private int stock;
    private String price;
}