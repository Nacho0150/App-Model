package com.laula.demo.module;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class Product {

    private long code;
    private String description;
    private int stock;
    private int price;
}