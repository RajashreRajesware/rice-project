package com.example.model_project.service.Impl;

import com.example.model_project.dto.TransactionDto;
import com.example.model_project.entity.Transaction;
import com.example.model_project.repository.TransactionRepo;
import com.example.model_project.service.TransactionService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepo transactionRepo;
    private final ModelMapper modelMapper;

    public TransactionServiceImpl(TransactionRepo transactionRepo, ModelMapper modelMapper) {
        this.transactionRepo = transactionRepo;
        this.modelMapper = modelMapper;
    }

    @Override
    public TransactionDto save(TransactionDto dto) {

        dto.setDate(LocalDate.now());

        if (dto.getType().equalsIgnoreCase("Sold")) {
            double available = getAvailableQuantity();

            if (available <= 0) {
                throw new IllegalStateException("No stock available. You cannot sell.");
            }

            if (dto.getQuantity() > available) {
                throw new IllegalArgumentException(
                        "You cannot sell more than available stock. Available: " + available
                );
            }
        }

        Transaction tx = modelMapper.map(dto, Transaction.class);
        Transaction saved = transactionRepo.save(tx);
        return modelMapper.map(saved, TransactionDto.class);
    }

    @Override
    public TransactionDto update(Long id, TransactionDto dto) {
        Transaction existing = transactionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));

        double oldQty = existing.getQuantity();
        String oldType = existing.getType();
        double available = getAvailableQuantity();

        if (dto.getType().equalsIgnoreCase("Sold")) {
            double maxAllowed = available + (oldType.equalsIgnoreCase("Sold") ? oldQty : 0);
            if (dto.getQuantity() > maxAllowed) {
                throw new IllegalArgumentException(
                        "Cannot sell more than available stock. Available: " + maxAllowed
                );
            }
        }

        existing.setName(dto.getName());
        existing.setType(dto.getType());
        existing.setPrice(dto.getPrice());
        existing.setQuantity(dto.getQuantity());
        existing.setLocation(dto.getLocation());
        existing.setDate(existing.getDate());

        Transaction updated = transactionRepo.save(existing);
        return modelMapper.map(updated, TransactionDto.class);
    }

    @Override
    public List<TransactionDto> getAll() {
        return transactionRepo.findAll().stream()
                .map(t -> modelMapper.map(t, TransactionDto.class))
                .toList();
    }

    @Override
    public TransactionDto findById(Long id) {
        Transaction t = transactionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));
        return modelMapper.map(t, TransactionDto.class);
    }

    @Override
    public List<TransactionDto> findByType(String type) {
        List<Transaction> transactions = transactionRepo.findByType(type);

        return transactions
                .stream()
                .map(t -> modelMapper.map(t, TransactionDto.class))
                .toList();
    }

    @Override
    public void deleteById(Long id) {
        Transaction transaction = transactionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Transaction not found: " + id));

        if (transaction.getType().equalsIgnoreCase("Bought")) {
           double availableAfterDelete = getAvailableQuantity() - transaction.getQuantity();
            if (availableAfterDelete < 0) {
                throw new IllegalStateException(
                        "Cannot delete! This purchase is required for existing sales."
                );
            }
        }

        transactionRepo.deleteById(id);
    }


    @Override
    public boolean deleteByIdAndDate(Long id, LocalDate date) {
        Optional<Transaction> transactionOpt = transactionRepo.findById(id);

        if (transactionOpt.isPresent() && transactionOpt.get().getDate().equals(date)) {
            transactionRepo.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<TransactionDto> findByDate(LocalDate date) {
        return transactionRepo.findByDate(date)
                .stream()
                .map(t -> new TransactionDto(
                        t.getId(),
                        t.getName(),
                        t.getType(),
                        t.getQuantity(),

                        t.getPrice(),
                        t.getDate(),
                        t.getLocation()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public double getAvailableQuantity() {
        List<Transaction> boughtList = transactionRepo.findByType("Bought");


        List<Transaction> soldList = transactionRepo.findByType("Sold");

        double totalBought = boughtList.stream()
                .mapToDouble(Transaction::getQuantity)
                .sum();

        double totalSold = soldList.stream()
                .mapToDouble(Transaction::getQuantity)
                .sum();

        double available = totalBought - totalSold;
        return Math.max(available, 0.0);
    }


    @Override
    public List<TransactionDto> search(String type, String location, LocalDate date) {

        return transactionRepo.findAll()
                .stream()
                .filter(t -> (type == null || type.isEmpty() || t.getType().equalsIgnoreCase(type)))
                .filter(t -> (location == null || location.isEmpty() ||
                        t.getLocation().equalsIgnoreCase(location)))
                .filter(t -> (date == null || t.getDate().equals(date)))
                .map(t -> modelMapper.map(t, TransactionDto.class))
                .toList();
    }

    public int deleteMultipleByDate(List<Long> ids, LocalDate date) {
        int count = 0;
        for (Long id : ids) {
            if (deleteByIdAndDate(id, date)) {
                count++;
            }
        }
        return count;
    }


}
