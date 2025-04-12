package com.elyashevich.card_manager.repository;

import com.elyashevich.card_manager.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CardRepository extends JpaRepository<Card, Long> {
}
