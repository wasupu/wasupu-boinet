package io.wasupu.boinet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest(Bank.class)
public class BankTest {

    @Test
    public void shouldContractANewAccount() throws Exception {
        whenNew(Account.class).withArguments("0").thenReturn(account);
        bank.contractAccount();

        assertTrue("The bank must have the expected account", bank.existAccount("0"));
    }

    @Test
    public void shouldDepositMoneyInTheBank() throws Exception {
        whenNew(Account.class).withArguments("0").thenReturn(account);
        bank.contractAccount();

        bank.deposit("0",new BigDecimal(10));

        verify(account).deposit(new BigDecimal(10));
    }

    @Before
    public void setupBank(){
        bank = new Bank();
    }

    private Bank bank;

    @Mock
    private Account account;
}
