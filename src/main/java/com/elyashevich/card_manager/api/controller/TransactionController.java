package com.elyashevich.card_manager.api.controller;

import com.elyashevich.card_manager.api.dto.transaction.TransferRequest;
import com.elyashevich.card_manager.api.dto.transaction.WithdrawRequest;
import com.elyashevich.card_manager.entity.Transaction;
import com.elyashevich.card_manager.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    @GetMapping("/{id}")
    public ResponseEntity<Transaction> findTransactionById(final @PathVariable("id") Long id) {
        var transaction = this.transactionService.findById(id);
        return ResponseEntity.ok(transaction);
    }

    @PostMapping("/transfer")
    public ResponseEntity<Transaction> transfer(
        final @RequestBody TransferRequest transferRequest,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        var transaction = this.transactionService.transferBetweenCards(
            transferRequest.fromCardId(),
            transferRequest.toCardId(),
            transferRequest.amount()
        );
        return ResponseEntity.created(
            uriComponentsBuilder.replacePath("/api/v1/transactions/{id}")
                .build(Map.of("id", transaction.getId()))
        ).body(transaction);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<Transaction> withdrawFromCard(
        final @RequestBody WithdrawRequest request,
        final UriComponentsBuilder uriComponentsBuilder
    ) {
        var transaction = this.transactionService.withdrawFromCard(request.cardId(), request.amount());
        return ResponseEntity.created(
            uriComponentsBuilder.replacePath("/api/v1/transactions/{id}")
                .build(Map.of("id", transaction.getId()))
        ).body(transaction);
    }
}
