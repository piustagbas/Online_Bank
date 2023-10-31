package com.example.bankApp.Service;

import com.example.bankApp.Model.BankAccount;
import com.example.bankApp.dto.CreateAccountReq;
import com.example.bankApp.dto.Transaction;

import javax.security.auth.login.AccountNotFoundException;

public interface BankAccountService {
    BankAccount createBankAccount(CreateAccountReq createAccountReq);

    BankAccount checkBalance(Long accountId) throws AccountNotFoundException;

    BankAccount deposit(Transaction dep, Long id) throws AccountNotFoundException;

    BankAccount withdraw(Transaction withdrawal, Long accountId) throws AccountNotFoundException;
}
