package io.wasupu.boinet.persons;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.persons.behaviours.*;

import java.util.Collection;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Person {

    public Person(String identifier,
                  String name,
                  String cellPhone,
                  World world) {
        this.identifier = identifier;
        this.name = name;
        this.world = world;
        this.cellPhone = cellPhone;

        tickConsumers = ImmutableList.of(
            new ContractAccount(world,this)::tick,
            new ContractDebitCard(world,this)::tick,
            new InitialCapital(world,this)::tick,
            new EatEveryDay(world,this)::tick,
            new MonthlyRecurrentPayment(world,
                this,
                25,
                ProductType.ELECTRICITY,
                60,
                120)::tick,
            new GoToCountryside(world,this)::tick
            );

        world.listenTicks(this::tick);
    }

    public void tick() {
        tickConsumers.forEach(Runnable::run);

        publishPersonBalance();

        age++;
    }

    public Boolean isUnemployed() {
        return !employed;
    }

    public void youAreHired() {
        employed = TRUE;
    }

    public String getIban() {
        return iban;
    }

    public String getPan() {
        return pan;
    }

    public Long getAge() {
        return age;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }


    public void setPan(String pan) {
        this.pan = pan;
    }

    private void publishPersonBalance() {
        if (age % 30 != 0) return;

        world.getEventPublisher().publish(STREAM_ID, ImmutableMap
            .<String, Object>builder()
            .put("person", identifier)
            .put("name", name)
            .put("cellPhone", cellPhone)
            .put("balance", world.getBank().getBalance(iban))
            .put("currency", "EUR")
            .put("date", world.getCurrentDateTime().toDate())
            .build());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Person person = (Person) o;

        return identifier.equals(person.identifier);
    }

    @Override
    public int hashCode() {
        return identifier.hashCode();
    }

    private String iban;

    private String identifier;

    private String pan;

    private World world;

    private Long age = 0L;

    private Boolean employed = FALSE;

    private String name;

    private final String cellPhone;

    private static final String STREAM_ID = "personEventStream";

    private Collection<Runnable> tickConsumers;
}
