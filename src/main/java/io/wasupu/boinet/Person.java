package io.wasupu.boinet;

import java.math.BigDecimal;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

public class Person {

    public Person(String identifier, World world) {
        this.identifier = identifier;
        this.world = world;

        world.listenTicks(this::tick);
    }

    public void tick() {
        contractAccount();
        initialCapital();
        contractDebitCard();
        eatEveryDay();
        publishCompanyBalance();

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

    private void eatEveryDay(){
        if (age < 2) return;

        world.findCompany().buyProduct(pan);
    }

    private void publishCompanyBalance(){
        if (age % 30 != 0) return;

        System.out.println("person:" + identifier + ",balance:" + world.getBank().getBalance(iban));
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
}
