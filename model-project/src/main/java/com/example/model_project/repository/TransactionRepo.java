package com.example.model_project.repository;

import com.example.model_project.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    List<Transaction> findByType(String type);
    List<Transaction> findByDate(LocalDate date);

    @Query("SELECT SUM(t.quantity) FROM Transaction t WHERE t.type = :type")
    Double getTotalQuantityByType(@Param("type") String type);

}
