package io.wasupu.boinet.financial.behaviours;

import io.wasupu.boinet.financial.Bank;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class CalculateDifferenceBetweenIncomeAndExpensesTest {

    @Test
    public void it_should_calculate_the_differences_between_income_and_expenses(){
        behaviour.tick();

        verify(bank).calculateDifferenceBetweenIncomeAndExpenses();
    }

    @Before
    public void setupBehaviour(){
        behaviour = new CalculateDifferenceBetweenIncomeAndExpenses(bank);
    }

    @Mock
    private Bank bank;

    private CalculateDifferenceBetweenIncomeAndExpenses behaviour;

}
