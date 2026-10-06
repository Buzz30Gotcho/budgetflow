package com.budgetflow.transaction;

import com.budgetflow.category.Category;
import com.budgetflow.category.CategoryRepository;
import com.budgetflow.common.ApiException;
import com.budgetflow.transaction.dto.TransactionRequest;
import com.budgetflow.transaction.dto.TransactionResponse;
import com.budgetflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<TransactionResponse> list(User user, LocalDate from, LocalDate to) {
        List<Transaction> transactions = (from != null && to != null)
                ? transactionRepository.findByUserIdAndDateBetweenOrderByDateDescIdDesc(user.getId(), from, to)
                : transactionRepository.findByUserIdOrderByDateDescIdDesc(user.getId());
        return transactions.stream().map(TransactionResponse::from).toList();
    }

    @Transactional
    public TransactionResponse create(User user, TransactionRequest request) {
        Transaction transaction = Transaction.builder()
                .type(request.type())
                .amount(request.amount())
                .description(request.description())
                .date(request.date())
                .category(resolveCategory(user, request.categoryId()))
                .user(user)
                .build();
        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional
    public TransactionResponse update(User user, Long id, TransactionRequest request) {
        Transaction transaction = getOwned(user, id);
        transaction.setType(request.type());
        transaction.setAmount(request.amount());
        transaction.setDescription(request.description());
        transaction.setDate(request.date());
        transaction.setCategory(resolveCategory(user, request.categoryId()));
        return TransactionResponse.from(transactionRepository.save(transaction));
    }

    @Transactional
    public void delete(User user, Long id) {
        transactionRepository.delete(getOwned(user, id));
    }

    private Transaction getOwned(User user, Long id) {
        return transactionRepository.findByIdAndUserId(id, user.getId())
                .orElseThrow(() -> ApiException.notFound("Transaction introuvable"));
    }

    private Category resolveCategory(User user, Long categoryId) {
        if (categoryId == null) {
            return null;
        }
        return categoryRepository.findByIdAndUserId(categoryId, user.getId())
                .orElseThrow(() -> ApiException.notFound("Catégorie introuvable"));
    }
}
