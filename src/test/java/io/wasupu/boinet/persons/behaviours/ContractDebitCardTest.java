package io.wasupu.boinet.persons.behaviours;

import io.wasupu.boinet.Bank;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ContractDebitCardTest {

    @Test
    public void shouldContractADebitCardInFirstTick() {
        when(person.getAge()).thenReturn(0L);
        when(world.getBank()).thenReturn(bank);
        when(person.getIban()).thenReturn(IBAN);
        when(bank.contractDebitCard(IBAN)).thenReturn(PAN);

        contractDebitCard.tick();

        verify(person).setPan(PAN);
    }

    @Test
    public void shouldNotContractAgainDebitCardIfHasOne() {
        when(person.getAge()).thenReturn(1L);

        contractDebitCard.tick();
        verify(person, never()).setPan(any());
    }

    @Before
    public void setupContractAccount() {
        contractDebitCard = new ContractDebitCard(world, person);
    }

    @Mock
    private Person person;

    @Mock
    private World world;

    @Mock
    private Bank bank;

    private static final String PAN = "12312312312";

    private static final String IBAN = "2";

    private ContractDebitCard contractDebitCard;

}
