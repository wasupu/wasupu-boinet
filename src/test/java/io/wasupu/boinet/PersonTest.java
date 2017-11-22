package io.wasupu.boinet;

import com.google.common.testing.EqualsTester;
import org.assertj.core.api.Condition;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PersonTest {

    public static final String FULL_NAME = "fullName";
    public static final String CELL_PHONE = "686338292";

    @Test
    public void testEquals() {
        new EqualsTester().
            addEqualityGroup(new Person("person1",
                    "fullName1",
                    CELL_PHONE,
                    world),
                new Person("person1",
                    "fullName1",
                    CELL_PHONE,
                    world)).

            addEqualityGroup(new Person("person2",
                    "fullName1",
                    CELL_PHONE,
                    world),
                new Person("person2",
                    "fullName1",
                    CELL_PHONE,
                    world)).
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
            .thenReturn(PAN, OTHER_PAN);

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
    public void shouldPayElectricityAtDay25() {
        when(bank.contractDebitCard(IBAN)).thenReturn(PAN);

        when(world.findCompany()).thenReturn(company);

        IntStream.range(0, 26).forEach(i -> person.tick());

        verify(company, atLeastOnce()).buyProduct(eq(PAN), pricesCaptor.capture());
        BigDecimal price = pricesCaptor.getAllValues().get(pricesCaptor.getAllValues().size() - 1);

        assertTrue("The 25 tick must pay electricity",
            priceBetween(price, new BigDecimal(60), new BigDecimal(120)));
    }

    @Test
    public void shouldPayElectricityEveryMonthAtDay25() {
        when(bank.contractDebitCard(IBAN)).thenReturn(PAN);

        when(world.findCompany()).thenReturn(company);

        IntStream.range(0, 56).forEach(i -> person.tick());

        verify(company, atLeastOnce()).buyProduct(eq(PAN), pricesCaptor.capture());
        BigDecimal price = pricesCaptor.getAllValues().get(pricesCaptor.getAllValues().size() - 1);

        assertTrue("Every month at tick 25 must pay electricity",
            priceBetween(price, new BigDecimal(60), new BigDecimal(120)));
    }

    @Test
    public void shouldEatEveryTickAfterTwoTicks() {
        when(bank.contractDebitCard(IBAN)).thenReturn(PAN);

        when(world.findCompany()).thenReturn(company);

        person.tick();
        person.tick();
        person.tick();
        person.tick();
        person.tick();

        verify(company, times(3)).buyProduct(eq(PAN), pricesCaptor.capture());

        assertThat(pricesCaptor.getAllValues())
            .as("There must be 3 random values between 10 and 20 euros")
            .isNotEmpty()
            .hasSize(3)
            .are(new Condition<>(bigDecimal -> priceBetween(bigDecimal,
                new BigDecimal(10),
                new BigDecimal(20)), "More than ten, less than twenty"));
    }

    private boolean priceBetween(BigDecimal bigDecimal, BigDecimal begin, BigDecimal end) {
        return bigDecimal.compareTo(begin) >= 0 && bigDecimal.compareTo(end) <= 0;
    }

    @Test
    public void shouldPublishPersonInfoAt0Ticks() {
        ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        person.tick();

        assertEquals("The balance string is not the expected",
            BALANCE_JSON + "\n", out.toString());
    }

    @Test
    public void shouldPublishPersonInfoAt30Ticks() {
        when(world.getBank()).thenReturn(bank);
        when(bank.contractAccount()).thenReturn(IBAN);
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
        when(world.findCompany()).thenReturn(company);

        ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        System.setOut(new java.io.PrintStream(out));

        IntStream.range(0, 31).forEach(i -> person.tick());

        assertEquals("The balance string is not the expected",
            BALANCE_JSON + "\n" + BALANCE_JSON + "\n", out.toString());
    }

    @Before
    public void setupPerson() {
        person = new Person(IDENTIFIER, FULL_NAME, CELL_PHONE, world);
    }

    @Before
    public void setupAccount() {
        when(world.getBank()).thenReturn(bank);

        GregorianCalendar gregorianCalendar = new GregorianCalendar(2017, 9, 5);
        gregorianCalendar.setTimeZone(TimeZone.getTimeZone("UTC"));

        when(world.getCurrentDate()).thenReturn(gregorianCalendar.getTime());
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

    private static final String CURRENT_DATE = "2017-10-05T00:00:00Z";

    private static final String BALANCE_JSON =
        "{" +
            "\"person\":\"personId\"," +
            "\"name\":\"" + FULL_NAME + "\"," +
            "\"cellPhone\":\"" + CELL_PHONE + "\"," +
            "\"balance\":12," +
            "\"currency\":\"EUR\"," +
            "\"date\":\"" + CURRENT_DATE + "\"" +
            "}";

    @Captor
    private ArgumentCaptor<BigDecimal> pricesCaptor;
}