package com.example.bankApp.Controller;

import com.example.bankApp.Model.BankAccount;
import com.example.bankApp.ServiceImp.BankAccountServiceImp;
import com.example.bankApp.dto.CreateAccountReq;
import com.example.bankApp.dto.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/account")
@RequiredArgsConstructor
public class BankAccountController {
    @Autowired
    private final BankAccountServiceImp bankAccountService;

    @GetMapping("/{accountId}/balance")
    public ResponseEntity<?> getAccount(@PathVariable Long accountId) throws AccountNotFoundException {
        BankAccount account = bankAccountService.checkBalance(accountId);
        return ResponseEntity.ok(account);
    }

    @PostMapping("/deposit/{accountId}")
    public ResponseEntity<?> deposit(@PathVariable Long accountId, @RequestBody Transaction dep) throws AccountNotFoundException {
        if (dep.getAmount() <= 99.0) {
            return ResponseEntity.badRequest().body("You can not deposit any money less than 100 naira, please enter a valid amount.");
        } else if (dep.getAmount() >= 10000000.0) {
            return ResponseEntity.badRequest().body("you can not make a deposit that is more than 99999999");
        }
        BankAccount newBalance = bankAccountService.deposit(dep, accountId);
        return ResponseEntity.ok(newBalance);
    }

    @PostMapping("/withdraw/{accountId}")
    public ResponseEntity<?> withdraw(@PathVariable Long accountId, @RequestBody Transaction amount) throws AccountNotFoundException {
        if (amount.getAmount() <= 49.0){
            return ResponseEntity.badRequest().body("you cannot withdraw any money less than 50 naira");
        } else if (amount.getAmount() >= 1000001.0) {
            return ResponseEntity.badRequest().body("You cannot withdraw any money greater than 1000000");
        }
        BankAccount newBalance = bankAccountService.withdraw(amount, accountId);
        return ResponseEntity.ok(newBalance);
    }

    @PostMapping("/create")
    public ResponseEntity<BankAccount> createBankAccount(@RequestBody CreateAccountReq req) {
        BankAccount bankAccount = bankAccountService.createBankAccount(req);
        return ResponseEntity.ok(bankAccount);
    }
}

