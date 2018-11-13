package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
import io.wasupu.boinet.companies.ReceiptType;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PayAReceiptTest {

    @Test
    public void it_should_pay_a_receipt() {
        payment.tick();
        
        verify(bank).payReceipt(anyString(), eq(RECEIPT_TYPE), eq(RECEIPT_AMOUNT), eq(person), eq(company));
    }

    @Before
    public void setupWorld() {
        when(world.getBank()).thenReturn(bank);
    }

    @Before
    public void setupMortgage() {
        payment = new PayAReceipt(world,
            person,
            company,
            RECEIPT_TYPE,
            RECEIPT_AMOUNT);
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private Company company;

    private BigDecimal RECEIPT_AMOUNT = new BigDecimal("10");

    private static ReceiptType RECEIPT_TYPE = ReceiptType.POWER_SUPPLY;

    private PayAReceipt payment;
}
