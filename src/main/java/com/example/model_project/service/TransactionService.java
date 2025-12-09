package com.example.model_project.service;


import com.example.model_project.dto.TransactionDto;

import java.time.LocalDate;
import java.util.List;

public interface TransactionService {

    TransactionDto save(TransactionDto dto);

    TransactionDto update(Long id, TransactionDto dto);

    List<TransactionDto> getAll();

    TransactionDto findById(Long id);

    List<TransactionDto> findByType(String type);

    void deleteById(Long id);

    boolean deleteByIdAndDate(Long id, LocalDate date);

    List<TransactionDto> findByDate(LocalDate date);

    double getAvailableQuantity();

    List<TransactionDto> search(String type, String location, LocalDate date);

    int deleteMultipleByDate(List<Long> ids, LocalDate date);

    int deleteByDateRange(LocalDate startDate, LocalDate endDate);


    int deleteMultiple(List<Long> ids);


    List<TransactionDto> findByDateRange(LocalDate startDate, LocalDate endDate);


}
