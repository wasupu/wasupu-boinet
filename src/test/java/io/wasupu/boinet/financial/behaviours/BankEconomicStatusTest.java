package io.wasupu.boinet.financial.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.eventPublisher.EventPublisher;
import io.wasupu.boinet.financial.Account;
import io.wasupu.boinet.financial.Bank;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class BankEconomicStatusTest {

    @Test
    public void it_should_publish_the_bank_balance_with_event_type_balance() {
        when(bank.getTreasuryAccount()).thenReturn(treasuryAccount);

        var balance = new BigDecimal("10000000");
        when(treasuryAccount.getBalance()).thenReturn(balance);

        Map<String, Object> value = executeBehaviour();

        assertEquals("Must have the balance event type", "bankBalance", value.get("eventType"));
    }

    @Test
    public void it_should_publish_the_bank_balance_the_treasury_account_balance() {
        when(bank.getTreasuryAccount()).thenReturn(treasuryAccount);

        var balance = new BigDecimal("10000000");
        when(treasuryAccount.getBalance()).thenReturn(balance);

        Map<String, Object> value = executeBehaviour();

        assertEquals("Must have the balance of the treasury", balance, value.get("treasuryAccount"));
    }

    private Map<String, Object> executeBehaviour() {
        bankEconomicStatus.tick();

        verify(eventPublisher).publish(captor.capture());

        return captor.getValue();
    }

    @Before
    public void setupBankEconomicStatus() {
        bankEconomicStatus = new BankEconomicStatus(world, bank);

        when(world.getEventPublisher()).thenReturn(eventPublisher);
        when(bank.getTreasuryAccount()).thenReturn(treasuryAccount);
    }

    private BankEconomicStatus bankEconomicStatus;

    @Mock
    private EventPublisher eventPublisher;

    @Mock
    private Bank bank;

    @Mock
    private World world;

    @Captor
    private ArgumentCaptor<Map<String, Object>> captor;

    @Mock
    private Account treasuryAccount;
}
