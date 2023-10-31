package com.example.bankApp.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
@Data
public class Transaction {
        @NotNull(message = "Please enter the amount")
        private double amount;
}
