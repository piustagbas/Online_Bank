package com.example.bankApp.ServiceImp;

import com.example.bankApp.Enums.AccountStatus;
import com.example.bankApp.Model.BankAccount;
import com.example.bankApp.Repository.BankAccountRepository;
import com.example.bankApp.Service.BankAccountService;
import com.example.bankApp.dto.CreateAccountReq;
import com.example.bankApp.dto.Transaction;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.util.*;

@Service
@RequiredArgsConstructor
public class BankAccountServiceImp implements BankAccountService {
    @Autowired
    private BankAccountRepository accountRepository;

    @Override
        public BankAccount createBankAccount(CreateAccountReq req) {
            BankAccount bankAccount = new BankAccount();
            bankAccount.setAccountNumber(generateUniqueAccountNumber());
            bankAccount.setAccountHolderName(req.getAccountHolderName());
            bankAccount.setAccountType(req.getAccountType());
            bankAccount.setBalance(req.getInitialBalance());
            bankAccount.setCreatedDate(new Date());
            bankAccount.setLastTransactionDate(new Date());
            bankAccount.setInterestRate(0.0);
            bankAccount.setAccountStatus(AccountStatus.ACTIVE);

            return accountRepository.save(bankAccount);
        }

    private String generateUniqueAccountNumber() {
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000; // Generates an 8-digit random number

        String accountNumber = "0039" + randomNumber;
        return accountNumber;
    }
    @Override
    public BankAccount checkBalance(Long accountId) throws AccountNotFoundException {
        Optional<BankAccount> balance = accountRepository.findById(accountId);
        if (balance.isPresent()){
            return balance.get();
        }else {
            throw new AccountNotFoundException("Account with ID " + accountId + " not found");
        }
    }
    @Override
    public BankAccount deposit(Transaction dep, Long accountId) throws AccountNotFoundException {
        Optional<BankAccount> accountDept = accountRepository.findById(accountId);

        if (accountDept.isPresent()) {
            BankAccount account = accountDept.get();
            double newBalance = account.getBalance() + dep.getAmount();
            account.setBalance(newBalance);
            account.setLastTransactionDate(new Date());
            String accountNumber = account.getAccountNumber();
            String accountHolderName = account.getAccountHolderName();

            // Save the updated account
            account = accountRepository.save(account);

            // Return the updated account with additional information
            account.setAccountNumber(accountNumber);
            account.setAccountHolderName(accountHolderName);

            return account;        } else {
            throw new AccountNotFoundException("Account with ID " + accountId + " not found");
        }
    }
    @Override
    public BankAccount withdraw(Transaction withdrawal, Long accountId) throws AccountNotFoundException{
        Optional<BankAccount> optionalAccount = accountRepository.findById(accountId);

        if (optionalAccount.isPresent()) {
            BankAccount account = optionalAccount.get();
            double newBalance = account.getBalance() - withdrawal.getAmount();

            if (newBalance < 0) {
                throw new RuntimeException("Insufficient funds for withdrawal from account with ID " + accountId);
            }

            account.setBalance(newBalance);
            account.setLastTransactionDate(new Date());

            String accountNumber = account.getAccountNumber();
            String accountHolderName = account.getAccountHolderName();

            // Save the updated account
            account = accountRepository.save(account);

            // Return the updated account with additional information
            account.setAccountNumber(accountNumber);
            account.setAccountHolderName(accountHolderName);

            return account;
        } else {
            throw new AccountNotFoundException("Account with ID " + accountId + " not found");
        }
    }

}

