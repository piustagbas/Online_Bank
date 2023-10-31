package com.example.bankApp.dto;

import com.example.bankApp.Enums.AccountType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateAccountReq {
   @NotNull(message = "Account name cannot be null")
   private String accountHolderName;
   @NotNull(message = "Account type cannot be null")
   private AccountType accountType;
   @NotNull(message = "Account initial balance cannot be null")
   private double initialBalance;

}
