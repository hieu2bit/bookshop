package com.group7.bookshopwebsite.dto;

import lombok.*;

@Data
public class CartItemDTO {
    private Long bookId;
    private String coverImage;
    private String title;
    private Double price;
    private Integer quantity;

    public double getSubtotal() {
        return price * quantity;
    }
}
