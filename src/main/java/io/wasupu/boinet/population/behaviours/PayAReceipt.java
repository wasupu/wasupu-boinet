package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ReceiptType;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;
import java.util.UUID;

public class PayAReceipt extends EconomicalSubjectBehaviour {

    public PayAReceipt(World world,
                       Person person,
                       Company company,
                       ReceiptType receiptType,
                       BigDecimal amount) {
        super(world, person);
        this.receiptType = receiptType;
        this.company = company;
        this.amount = amount;
    }

    @Override
    public void tick() {
        getWorld().getBank().payReceipt(receiptId, receiptType, amount, (Person) getEconomicalSubject(), company);
    }

    private String receiptId = UUID.randomUUID().toString();

    private ReceiptType receiptType;

    private Company company;

    private BigDecimal amount;
}
