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

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PayMortgageTest {

    @Test
    public void it_should_pay_a_mortgage() {
        when(bank.isMortgageAmortized(MORTGAGE_IDENTIFIER)).thenReturn(false);

        payment.tick();

        verify(bank).payMortgage(MORTGAGE_IDENTIFIER, new BigDecimal(60));
    }

    @Test
    public void it_should_cancel_the_mortgage_if_is_pay() {
        when(bank.isMortgageAmortized(MORTGAGE_IDENTIFIER)).thenReturn(true);

        payment.tick();

        verify(bank).cancelMortgage(MORTGAGE_IDENTIFIER);
    }

    @Test
    public void it_should_unregister_the_behaviour() {
        when(bank.isMortgageAmortized(MORTGAGE_IDENTIFIER)).thenReturn(true);

        payment.tick();

        verify(person).removeBehaviour(payment);
    }

    @Before
    public void setupPerson() {
        when(person.getMortgageIdentifier()).thenReturn(MORTGAGE_IDENTIFIER);
    }

    @Before
    public void setupBank() {
        when(world.getBank()).thenReturn(bank);
    }

    @Before
    public void setupMortgage(){
        payment = new PayMortgage(world, person, new BigDecimal(60));
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    private PayMortgage payment;

    private static String MORTGAGE_IDENTIFIER = "0";
}
