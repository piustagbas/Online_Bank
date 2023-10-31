package com.example.bankApp.Model;

import com.example.bankApp.Enums.AccountStatus;
import com.example.bankApp.Enums.AccountType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Entity
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String accountNumber;
    @Column(nullable = false, unique = true)
    @NotBlank
    private String accountHolderName;
    private AccountType accountType;
    @Column(nullable = false)
    private double balance;
    private Date createdDate;
    private Date lastTransactionDate;
    private double interestRate;
    private AccountStatus accountStatus;

}

