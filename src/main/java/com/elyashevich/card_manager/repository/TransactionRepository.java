package com.elyashevich.card_manager.repository;

import com.elyashevich.card_manager.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
}
