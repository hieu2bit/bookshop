package com.group7.bookshopwebsite.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderPerson {
    private String fullName;
    private String phoneNumber;
    private String email;
    private String address;
}
