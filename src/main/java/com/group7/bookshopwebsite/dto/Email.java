package com.group7.bookshopwebsite.dto;

import lombok.*;

@Data
public class Email {
    private String to;
    private String subject;
    private String message;
}
