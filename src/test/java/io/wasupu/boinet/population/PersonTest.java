package io.wasupu.boinet.population;

import com.github.javafaker.Faker;
import com.github.javafaker.Name;
import com.github.javafaker.PhoneNumber;
import com.google.common.testing.EqualsTester;
import io.wasupu.boinet.*;
import org.apache.commons.lang3.tuple.Pair;
import org.hamcrest.Matchers;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Spy;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;
import java.util.stream.IntStream;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.hamcrest.MockitoHamcrest.argThat;


@RunWith(PowerMockRunner.class)
@PrepareForTest(Person.class)
public class PersonTest {

    @Test
    public void testEquals() {
        new EqualsTester().
            addEqualityGroup(new Person("person1",
                    world),
                new Person("person1",
                    world)).

            addEqualityGroup(new Person("person2",
                    world),
                new Person("person2",
                    world)).
            testEquals();
    }

    @Test
    public void shouldNotBeUnemployedAfterYouAreHired() {
        person.youAreHired(company);

        assertNotNull("Must be not null", person.isUnemployed());
        assertFalse("The person must be hired", person.isUnemployed());
    }

    @Test
    public void shouldPublishPersonStatusOnFirstTick() {
        person.tick();

        verify(eventPublisher).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("person", "personId")));
        verify(eventPublisher).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("name", FULL_NAME)));
        verify(eventPublisher).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("cellPhone", CELL_PHONE)));
        verify(eventPublisher).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("balance", new BigDecimal("12"))));
        verify(eventPublisher).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("currency", "EUR")));
        verify(eventPublisher).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("date", CURRENT_DATE)));
    }

    @Test
    public void shouldPublishPersonAddressOnFirstTick() {
        person.tick();

        verify(eventPublisher).publish(eq("personEventStream"), (Map<String, Object>) argThat(hasKey("address")));
    }

    @Test
    public void shouldPublishPersonPanOnFirstTick() {
        person.tick();

        verify(eventPublisher).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("pan", PAN)));
    }

    @Test
    public void shouldPublishPersonStatusAt30Ticks() {
        IntStream.range(0, 31).forEach(i -> person.tick());

        verify(eventPublisher, times(2)).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("person", "personId")));
        verify(eventPublisher, times(2)).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("name", FULL_NAME)));
        verify(eventPublisher, times(2)).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("cellPhone", CELL_PHONE)));
        verify(eventPublisher, times(2)).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("balance", new BigDecimal("12"))));
        verify(eventPublisher, times(2)).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("currency", "EUR")));
        verify(eventPublisher, times(2)).publish(eq("personEventStream"), (Map<String, Object>) argThat(Matchers.<String, Object>hasEntry("date", CURRENT_DATE)));
    }

    @Before
    public void setupEventPublisher() {
        when(world.getEventPublisher()).thenReturn(eventPublisher);
    }

    @Before
    public void setupPerson() {
        when(faker.name()).thenReturn(name);
        when(name.fullName()).thenReturn(FULL_NAME);

        when(faker.phoneNumber()).thenReturn(phoneNumber);
        when(phoneNumber.cellPhone()).thenReturn(CELL_PHONE);

        when(world.getGPS()).thenReturn(gps);
        when(gps.coordinates()).thenReturn(Pair.of(40.2, -3.7));

        person = new Person(IDENTIFIER, world);
        Person.setFaker(faker);
        person.setIban(IBAN);
        person.setPan(PAN);
    }

    @Before
    public void setupAccount() {
        when(world.getBank()).thenReturn(bank);
        when(world.getCurrentDateTime()).thenReturn(new DateTime(CURRENT_DATE));
        when(bank.getBalance(IBAN)).thenReturn(new BigDecimal(12));
    }

    @Mock
    private World world;

    @Mock
    private Bank bank;

    @Mock
    private EventPublisher eventPublisher;

    private static final String IDENTIFIER = "personId";

    private Person person;

    private static final String IBAN = "2";

    private static final Date CURRENT_DATE = new Date();

    private static final String FULL_NAME = "fullName";
    private static final String CELL_PHONE = "686338292";

    private static final String PAN = "mipan";

    @Spy
    private Faker faker = new Faker();

    @Mock
    private Name name;

    @Mock
    private PhoneNumber phoneNumber;

    @Mock
    private GPS gps;

    @Mock
    private Company company;

}