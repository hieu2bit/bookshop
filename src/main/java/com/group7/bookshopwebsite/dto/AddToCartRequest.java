package com.group7.bookshopwebsite.dto;

import lombok.*;

@Data
public class AddToCartRequest {
    private Long productId;
    private int quantity;
}
