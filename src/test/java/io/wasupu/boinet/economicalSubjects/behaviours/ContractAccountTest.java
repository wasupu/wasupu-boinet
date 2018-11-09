package io.wasupu.boinet.economicalSubjects.behaviours;

import io.wasupu.boinet.World;
import io.wasupu.boinet.financial.Bank;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContractAccountTest {

    @Test
    public void shouldContractAnAccountInFirstTick() {
        when(world.getBank()).thenReturn(bank);
        when(person.getAge()).thenReturn(0L);
        when(bank.contractAccount()).thenReturn(IBAN);

        contractAccount.tick();

        verify(person).setIban(IBAN);
    }

    @Test
    public void shouldNotContractAgainAnAccountIfHasOneInOtherTick() {
        when(person.getAge()).thenReturn(1L);

        contractAccount.tick();

        verify(person, never()).setIban(any());
    }

    @Before
    public void setupContractAccount() {
        contractAccount = new ContractAccount(world, person);
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    private static final String IBAN = "2";

    private ContractAccount contractAccount;
}
