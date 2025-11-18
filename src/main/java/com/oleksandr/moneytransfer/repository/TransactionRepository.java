package com.oleksandr.moneytransfer.repository;

import com.oleksandr.moneytransfer.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
}
