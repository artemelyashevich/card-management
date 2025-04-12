package com.elyashevich.card_manager.repository;

import com.elyashevich.card_manager.entity.CardLimit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardLimitRepository extends JpaRepository<CardLimit, Long> {
}
