package com.example.model_project.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor



public class TransactionDto {

    private Long id;

    private String name;
    private String location;
    private Double quantity;
    private Double price;
    private LocalDate date;
    private String type;
}
