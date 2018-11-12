package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ReceiptType;
import io.wasupu.boinet.economicalSubjects.behaviours.EconomicalSubjectBehaviour;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;

public class PayAReceipt extends EconomicalSubjectBehaviour {

    public PayAReceipt(World world,
                       Person person,
                       Company company,
                       String receiptId,
                       ReceiptType receiptType,
                       BigDecimal amount) {
        super(world, person);
        this.receiptId = receiptId;
        this.receiptType = receiptType;
        this.company = company;
        this.amount = amount;
    }

    @Override
    public void tick() {
        getWorld().getBank().payReceipt(receiptId, receiptType, amount, (Person) getEconomicalSubject(), company);
    }

    private String receiptId;

    private ReceiptType receiptType;

    private Company company;

    private BigDecimal amount;
}
