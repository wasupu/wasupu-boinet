package io.wasupu.boinet.financial.behaviours;

import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.subjects.Behaviour;

import java.util.UUID;

public class CalculateDifferenceBetweenIncomeAndExpenses implements Behaviour {

    public CalculateDifferenceBetweenIncomeAndExpenses(Bank bank) {
        this.bank = bank;
    }

    @Override
    public void tick() {
        bank.calculateDifferenceBetweenIncomeAndExpenses();
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    private String identifier = UUID.randomUUID().toString();

    private Bank bank;
}
