package io.wasupu.boinet.financial;

import io.wasupu.boinet.financial.Account;
import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;

public class AccountTest {

    @Test
    public void shouldDepositMoneyIntoAccount(){
        account.deposit(new BigDecimal(10));

        assertEquals("The balance of the account is not the expected",
            new BigDecimal(10),
            account.getBalance());
    }

    @Test
    public void shouldWithdrawMoneyFromAccount(){
        account.deposit(new BigDecimal(10));
        account.withdraw(new BigDecimal(3));
        assertEquals("The balance of the account is not the expected",
            new BigDecimal(7),
            account.getBalance());
    }


    private static final String ACCOUNT_NUMBER = "12";

    private Account account = new Account(ACCOUNT_NUMBER);
}


