package com.hhplus.ecommerce.balance.adapter.out.persistence;

import com.hhplus.ecommerce.balance.domain.Balance;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "balance")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceJpaEntity {

    @Id
    private Long userId;

    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal amount;

    public BalanceJpaEntity(Long userId, BigDecimal amount) {
        this.userId = userId;
        this.amount = amount;
    }

    public static BalanceJpaEntity from(Balance balance) {
        return new BalanceJpaEntity(balance.getUserId(), balance.getAmount());
    }

    public Balance toDomain() {
        return new Balance(this.userId, this.amount);
    }

}









