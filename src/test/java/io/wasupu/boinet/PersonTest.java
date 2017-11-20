package io.wasupu.boinet;

import com.google.common.testing.EqualsTester;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PersonTest {

    @Test
    public void testEquals() {
        new EqualsTester().
            addEqualityGroup(new Person("person1", world), new Person("person1", world)).
            addEqualityGroup(new Person("person2", world), new Person("person2", world)).
            testEquals();
    }

    @Test
    public void shouldContractAnAccountInFirstTick() {
        person.tick();

        assertNotNull("After first tick must have an account", person.getIban());
        assertEquals("After first tick must have the expected iban", IBAN, person.getIban());
    }

    @Test
    public void shouldContractADebitCardInFirstTick() {
        when(bank.contractDebitCard(IBAN)).thenReturn(PAN);

        person.tick();

        assertNotNull("After first tick must have a debit card", person.getIban());
        assertEquals("After first tick must have the expected debit card", PAN, person.getPan());
    }

    @Test
    public void shouldNotContractAgainAnAccountIfHasOneInOtherTick() {
        when(bank.contractAccount()).thenReturn(IBAN)
            .thenReturn(OTHER_IBAN);

        person.tick();
        person.tick();

        assertNotNull("After second tick must have an account", person.getIban());
        assertEquals("After second tick must the same account that have in first tick",
            IBAN, person.getIban());
    }

    @Test
    public void shouldNotContractAgainDebitCardIfHasOne() {
        when(bank.contractDebitCard(IBAN))
            .thenReturn(PAN,OTHER_PAN);

        person.tick();
        person.tick();

        assertNotNull("After second tick must have a debit card", person.getIban());
        assertEquals("After second tick must have the same debit card that have in first tick",
            PAN, person.getPan());
    }

    @Test
    public void shouldNotBeUnemployedAfterYouAreHired() {
        person.youAreHired();

        assertNotNull("Must be not null", person.isUnemployed());
        assertFalse("The person must be hired", person.isUnemployed());
    }

    @Test
    public void shouldDepositASocialSalaryInTheFirstTick() {
        person.tick();

        verify(bank).deposit(IBAN, Person.INITIAL_CAPITAL);
    }

    @Test
    public void shouldNotDepositAnySalaryInOtherTick() {
        person.tick();
        person.tick();

        verify(bank).deposit(IBAN, Person.INITIAL_CAPITAL);
    }

    @Test
    public void shouldBuyAProductEveryTickAfterTwoTicks() {
        when(bank.contractDebitCard(IBAN)).thenReturn(PAN);

        when(world.findCompany()).thenReturn(company);

        person.tick();
        person.tick();
        person.tick();

        verify(company).buyProduct(PAN);
    }

    @Test
    public void shouldPublishPersonInfoAt0Ticks() {
        ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        person.tick();

        assertEquals("The balance string is not the expected",
            BALANCE_JSON  + "\n", out.toString());
    }

    @Test
    public void shouldPublishPersonInfoAt30Ticks() {
        when(world.getBank()).thenReturn(bank);
        when(bank.contractAccount()).thenReturn(IBAN);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
        when(world.findCompany()).thenReturn(company);

        ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IntStream.range(0,31).forEach(i -> person.tick());

        assertEquals("The balance string is not the expected",
             BALANCE_JSON + "\n" + BALANCE_JSON + "\n", out.toString());
    }

    @Before
    public void setupPerson() {
        person = new Person(IDENTIFIER, world);
    }

    @Before
    public void setupAccount(){
        when(world.getBank()).thenReturn(bank);
        when(world.getCurrentDate()).thenReturn(CURRENT_DATE);
        when(bank.contractAccount()).thenReturn(IBAN);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
  }

    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private Company company;

    private static final String IDENTIFIER = "personId";

    private Person person;

    private static final String IBAN = "2";

    private static final String OTHER_IBAN = "6";

    private static final String OTHER_PAN = "212312316";

    private static final String PAN = "12312312312";

    private static final String CURRENT_DATE = "2017-10-05T14:48:00.000Z";

    private static final String BALANCE_JSON =
        "{" +
            "\"person\":\"personId\"," +
            "\"balance\":12," +
            "\"currency\":\"EUR\"," +
            "\"date\":\""+ CURRENT_DATE + "\"" +
            "}";

}