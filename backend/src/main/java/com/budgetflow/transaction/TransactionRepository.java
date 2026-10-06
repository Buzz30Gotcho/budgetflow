package com.budgetflow.transaction;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserIdOrderByDateDescIdDesc(Long userId);

    Optional<Transaction> findByIdAndUserId(Long id, Long userId);

    List<Transaction> findByUserIdAndDateBetweenOrderByDateDescIdDesc(
            Long userId, LocalDate from, LocalDate to);

    @Query("""
            select coalesce(sum(t.amount), 0) from Transaction t
            where t.user.id = :userId and t.type = :type
              and t.date between :from and :to
            """)
    BigDecimal sumByTypeAndPeriod(@Param("userId") Long userId,
                                  @Param("type") TransactionType type,
                                  @Param("from") LocalDate from,
                                  @Param("to") LocalDate to);

    @Query("""
            select coalesce(sum(t.amount), 0) from Transaction t
            where t.user.id = :userId and t.category.id = :categoryId and t.type = :type
              and t.date between :from and :to
            """)
    BigDecimal sumByCategoryAndPeriod(@Param("userId") Long userId,
                                      @Param("categoryId") Long categoryId,
                                      @Param("type") TransactionType type,
                                      @Param("from") LocalDate from,
                                      @Param("to") LocalDate to);

    @Query("""
            select c.name as name, c.color as color, coalesce(sum(t.amount), 0) as total
            from Transaction t join t.category c
            where t.user.id = :userId and t.type = :type
              and t.date between :from and :to
            group by c.id, c.name, c.color
            order by total desc
            """)
    List<CategoryTotalProjection> sumByCategory(@Param("userId") Long userId,
                                                @Param("type") TransactionType type,
                                                @Param("from") LocalDate from,
                                                @Param("to") LocalDate to);

    interface CategoryTotalProjection {
        String getName();
        String getColor();
        BigDecimal getTotal();
    }
}
