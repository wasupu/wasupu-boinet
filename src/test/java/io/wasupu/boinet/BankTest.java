package io.wasupu.boinet;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
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
    public void should(){
        
    }

    @Before
    public void setupBank(){
        bank = new Bank();
    }

    private Bank bank;

    @Mock
    private Account account;
}
