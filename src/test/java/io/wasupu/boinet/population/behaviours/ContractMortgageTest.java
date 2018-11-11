package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContractMortgageTest {

    @Test
    public void it_should_contract_a_mortgage_in_first_tick() {
        when(person.getAge()).thenReturn(0L);
        when(world.getBank()).thenReturn(bank);

        when(person.getIban()).thenReturn(IBAN);
        when(person.getIdentifier()).thenReturn(IDENTIFIER);
        when(bank.contractMortgage(IDENTIFIER, IBAN, amount)).thenReturn(MORTGAGE_IDENTIFIER);

        contractMortgage.tick();

        verify(person).setMortgageIdentifier(MORTGAGE_IDENTIFIER);
    }

    @Test
    public void it_should_not_contract_again_a_mortgage_if_has_one() {
        when(person.getAge()).thenReturn(1L);

        contractMortgage.tick();

        verify(person, never()).setMortgageIdentifier(any());
    }

    @Before
    public void setupContractDebitCard() {
        contractMortgage = new ContractMortgage(world, person);
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    private static final String MORTGAGE_IDENTIFIER = "12312312312";

    private static final String IBAN = "2";

    private static final String IDENTIFIER = "234214234";

    private ContractMortgage contractMortgage;

    private BigDecimal amount = new BigDecimal(300);
}
