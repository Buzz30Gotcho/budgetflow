package com.budgetflow.transaction;

import com.budgetflow.category.Category;
import com.budgetflow.category.CategoryRepository;
import com.budgetflow.common.ApiException;
import com.budgetflow.transaction.dto.ImportResult;
import com.budgetflow.transaction.dto.TransactionRequest;
import com.budgetflow.transaction.dto.TransactionResponse;
import com.budgetflow.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private static final String[] PALETTE = {
        "#6366F1", "#22C55E", "#EF4444", "#F59E0B", "#3B82F6",
        "#EC4899", "#14B8A6", "#8B5CF6", "#F97316", "#10B981",
    };

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

    // Import d'un CSV au format : date,description,montant,type,categorie
    @Transactional
    public ImportResult importCsv(User user, MultipartFile file) {
        int imported = 0;
        List<String> errors = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;
                if (lineNumber == 1 && line.toLowerCase().startsWith("date")) continue;
                try {
                    importLine(user, line);
                    imported++;
                } catch (Exception e) {
                    errors.add("Ligne " + lineNumber + " ignorée (format invalide)");
                }
            }
        } catch (IOException e) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Impossible de lire le fichier");
        }

        return new ImportResult(imported, errors);
    }

    private void importLine(User user, String line) {
        String[] cols = line.split(",", -1);
        LocalDate date = LocalDate.parse(cols[0].trim());
        String description = cols[1].trim();
        BigDecimal amount = new BigDecimal(cols[2].trim().replace(",", "."));
        TransactionType type = TransactionType.valueOf(cols[3].trim().toUpperCase());
        Category category = (cols.length > 4 && !cols[4].isBlank())
                ? resolveOrCreateCategory(user, cols[4].trim())
                : null;

        Transaction transaction = Transaction.builder()
                .type(type)
                .amount(amount)
                .description(description.isBlank() ? null : description)
                .date(date)
                .category(category)
                .user(user)
                .build();
        transactionRepository.save(transaction);
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

    private Category resolveOrCreateCategory(User user, String name) {
        return categoryRepository.findByNameIgnoreCaseAndUserId(name, user.getId())
                .orElseGet(() -> {
                    int count = categoryRepository.findByUserIdOrderByNameAsc(user.getId()).size();
                    Category category = Category.builder()
                            .name(name)
                            .color(PALETTE[count % PALETTE.length])
                            .user(user)
                            .build();
                    return categoryRepository.save(category);
                });
    }
}
