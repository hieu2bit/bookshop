package com.group7.bookshopwebsite.dto;

import lombok.AllArgsConstructor;
import lombok.*;

@Data
@AllArgsConstructor
public class BookSearchDTO {
    private Long categoryId;
    private String keyword;

}

