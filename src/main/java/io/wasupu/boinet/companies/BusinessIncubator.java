package io.wasupu.boinet.companies;

import io.wasupu.boinet.World;
import io.wasupu.boinet.economicalSubjects.behaviours.ContractAccount;
import io.wasupu.boinet.economicalSubjects.behaviours.InitialCapital;
import io.wasupu.boinet.financial.Bank;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static java.util.stream.Collectors.toList;

public class BusinessIncubator {

    public Company findCompany(List<Company> companies) {
        var random = new Random();

        var randomNumber = random.nextInt(companies.size());
        return companies.get(randomNumber);
    }

    public Company findBestCompanyToWork(List<Company> companies, Bank bank) {
        return companies
            .stream()
            .sorted(Comparator
                .comparingDouble(company -> getCompanyRatio((Company) company, bank))
                .reversed())
            .collect(toList())
            .get(0);
    }

    public Company newCompany(World world) {
        var company = new Company(createCompanyUniqueIdentifier(), world);

        company.listenTicks(new ContractAccount(world, company));
        company.listenTicks(new InitialCapital(world, company, new BigDecimal(60000)));

        return company;
    }

    private String createCompanyUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private double getCompanyRatio(Company company, Bank bank) {
        return bank.getBalance(company.getIban()).doubleValue() / (company.getNumberOfEmployees() + 1);
    }
}
