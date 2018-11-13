package io.wasupu.boinet.population.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.Company;
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
public class BuyHouseTest {

    @Test
    public void it_should_contract_a_mortgage_in_first_tick() {
        when(person.getAge()).thenReturn(0L);
        when(world.getBank()).thenReturn(bank);

        when(person.getIban()).thenReturn(IBAN);
        when(person.getIdentifier()).thenReturn(IDENTIFIER);
        when(bank.contractMortgage(IDENTIFIER, IBAN, AMOUNT)).thenReturn(MORTGAGE_IDENTIFIER);

        buyHouse.tick();

        verify(person).setMortgageIdentifier(MORTGAGE_IDENTIFIER);
    }

    @Test
    public void it_should_buy_house_with_mortgage_amount() {
        when(person.getAge()).thenReturn(0L);
        when(world.getBank()).thenReturn(bank);

        when(person.getIban()).thenReturn(IBAN);
        when(person.getIdentifier()).thenReturn(IDENTIFIER);
        when(bank.contractMortgage(IDENTIFIER, IBAN, AMOUNT)).thenReturn(MORTGAGE_IDENTIFIER);

        buyHouse.tick();

        verify(company).buyHouse(IBAN, AMOUNT);
    }

    @Test
    public void it_should_not_contract_again_a_mortgage_if_has_one() {
        when(person.getAge()).thenReturn(1L);

        buyHouse.tick();

        verify(person, never()).setMortgageIdentifier(any());
    }

    @Before
    public void setupContractDebitCard() {
        buyHouse = new BuyHouse(world, person, company);
    }

    @Mock
    private Company company;

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    private BuyHouse buyHouse;

    private static final String MORTGAGE_IDENTIFIER = "12312312312";

    private static final String IBAN = "2";

    private static final String IDENTIFIER = "234214234";

    private static final BigDecimal AMOUNT = new BigDecimal(240000);

}
