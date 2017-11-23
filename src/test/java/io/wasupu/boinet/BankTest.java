package io.wasupu.boinet;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bank.class)
public class BankTest {

    public static final String DETAILS = "meal";

    @Test
    public void shouldContractANewAccount() throws Exception {
        whenNew(Account.class).withArguments(IBAN).thenReturn(firstAccount);
        String iban = bank.contractAccount();

        assertEquals("The iban must be 0", IBAN, iban);
        assertTrue("The bank must have the expected firstAccount", bank.existAccount(IBAN));
    }

    @Test
    public void shouldContractDebitCardInTheBank() throws Exception {
        whenNew(Account.class).withArguments(IBAN).thenReturn(firstAccount);
        bank.contractAccount();

        String pan = bank.contractDebitCard(IBAN);

        assertEquals("The pan must be 0", PAN, pan);
        assertEquals("The iban must for pan 0 must be 0", "0", bank.getIbanByPan(PAN));
    }

    @Test
    public void shouldDepositMoneyInTheBank() throws Exception {
        whenNew(Account.class).withArguments(IBAN).thenReturn(firstAccount);
        bank.contractAccount();

        bank.deposit(IBAN, new BigDecimal(10));

        verify(firstAccount).deposit(new BigDecimal(10));
    }

    @Test
    public void shouldTransferMoneyBetweenToAccounts() throws Exception {
        whenNew(Account.class)
            .withArguments(IBAN)
            .thenReturn(firstAccount);

        whenNew(Account.class)
            .withArguments(SECOND_IBAN)
            .thenReturn(secondAccount);

        String firstIban = bank.contractAccount();
        String secondIban = bank.contractAccount();

        BigDecimal amount = new BigDecimal(10);
        bank.transfer(firstIban, secondIban, amount);

        verify(firstAccount).withdraw(amount);
        verify(secondAccount).deposit(amount);
    }

    @Test
    public void shouldProcessAPayment() throws Exception {
        whenNew(Account.class)
            .withArguments(IBAN)
            .thenReturn(firstAccount);

        whenNew(Account.class)
            .withArguments(SECOND_IBAN)
            .thenReturn(secondAccount);

        String firstIban = bank.contractAccount();
        String secondIban = bank.contractAccount();
        String pan = bank.contractDebitCard(firstIban);

        BigDecimal amount = new BigDecimal(10);

        bank.processPayment(amount, pan, secondIban, COMPANY, DETAILS);

        verify(secondAccount).deposit(amount);
    }

    @Before
    public void setupEventPublisher() {
        when(world.getEventPublisher()).thenReturn(eventPublisher);
    }

    private static final String IBAN = "0";

    private static final String SECOND_IBAN = "1";

    private static final String PAN = "0";

    private static final String COMPANY = "12";

    @Before
    public void setupBank() {
        bank = new Bank(world);
        when(world.getCurrentDateTime()).thenReturn(new DateTime(new GregorianCalendar(2017, 10, 10).getTime()));
    }

    private Bank bank;

    @Mock
    private Account firstAccount;

    @Mock
    private Account secondAccount;

    @Mock
    private World world;

    @Mock
    private EventPublisher eventPublisher;

}
