package io.wasupu.boinet;

import com.google.common.collect.ImmutableMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.util.Random;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static net.logstash.logback.marker.Markers.appendEntries;

public class Person {



    public Person(String identifier,
                  String name,
                  World world) {
        this.identifier = identifier;
        this.name = name;
        this.world = world;

        world.listenTicks(this::tick);
    }

    public void tick() {
        contractAccount();
        initialCapital();
        contractDebitCard();
        eatEveryDay();
        publishPersonBalance();

        age++;
    }

    public Boolean isUnemployed() {
        return !employed;
    }

    public void youAreHired() {
        employed = TRUE;
    }

    String getIban() {
        return iban;
    }

    String getPan() {
        return pan;
    }

    private void contractDebitCard() {
        if (age != 0) return;

        pan = world.getBank().contractDebitCard(iban);
    }

    private void contractAccount() {
        if (age != 0) return;

        iban = world.getBank().contractAccount();
    }

    private void initialCapital() {
        if (age != 0) return;

        world.getBank().deposit(iban, INITIAL_CAPITAL);
    }

    private void eatEveryDay() {
        if (age < 2) return;

        world.findCompany().buyProduct(pan, generateRandomPrice(10,20));
    }

    private BigDecimal generateRandomPrice(Integer startPrice, Integer endPrice){
        Random random = new Random();
        double randomValue = startPrice + (endPrice - startPrice) * random.nextDouble();
        return new BigDecimal(randomValue)
            .setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private void publishPersonBalance() {
        if (age % 30 != 0) return;

        logger.info(appendEntries(ImmutableMap
                .builder()
                .put("person", identifier)
                .put("name", name)
                .put("balance", world.getBank().getBalance(iban))
                .put("currency", "EUR")
                .put("date", world.getCurrentDate())
                .build()),
            "Person balance");
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

    static final BigDecimal INITIAL_CAPITAL = new BigDecimal(1000);

    private static Logger logger = LoggerFactory.getLogger(Person.class);


    private String name;
}
