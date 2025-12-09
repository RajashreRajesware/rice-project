package com.example.model_project.repository;

import com.example.model_project.entity.Transaction;
import jakarta.persistence.Table;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRepo extends JpaRepository<Transaction,Long> {

    List<Transaction> findByType(String type);
    List<Transaction> findByDate(LocalDate date);

    @Query("SELECT SUM(t.quantity) FROM Transaction t WHERE t.type = :type")
    Double getTotalQuantityByType(@Param("type") String type);



    @Transactional
    @Modifying
    @Query("DELETE FROM Transaction t WHERE t.date BETWEEN :startDate AND :endDate")
    int deleteByDateRange(@Param("startDate") LocalDate startDate,
                          @Param("endDate") LocalDate endDate);


    List<Transaction> findAllByDateBetween(LocalDate startDate, LocalDate endDate);

    void deleteAllByIdIn(List<Long> ids);


}
