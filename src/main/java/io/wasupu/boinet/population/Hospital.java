package io.wasupu.boinet.population;

import com.github.javafaker.Faker;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.behaviours.ContractAccount;
import io.wasupu.boinet.population.behaviours.ContractDebitCard;
import io.wasupu.boinet.population.behaviours.EveryDayRecurrentPayment;
import io.wasupu.boinet.population.behaviours.GenerateRandomPrice;
import io.wasupu.boinet.population.behaviours.InitialCapital;
import io.wasupu.boinet.population.behaviours.MonthlyRecurrentPayment;
import io.wasupu.boinet.population.behaviours.TriggeredByBalanceThreshold;
import io.wasupu.boinet.population.behaviours.WeekendRecurrentPayment;

import java.math.BigDecimal;
import java.util.UUID;

public class Hospital {

    public Hospital(World world) {
        this.world = world;
    }

    public Person newSettler(Integer number) {
        Person newPerson = new Person(
            createPersonUniqueIdentifier(),
            faker.name().fullName(),
            faker.phoneNumber().cellPhone(),
            world);

        newPerson.listenTicks(new ContractAccount(world, newPerson)::tick);
        newPerson.listenTicks(new ContractDebitCard(world, newPerson)::tick);
        newPerson.listenTicks(new InitialCapital(world, newPerson)::tick);

        withEating(newPerson);
        withPowerSupply(newPerson);
        withCountryside(newPerson);
        withCableTV(newPerson);
        withMortgage(newPerson);
        withInternetConnection(newPerson);

        world.getPopulation().add(newPerson);
        return newPerson;
    }

    void withEating(Person newPerson) {
        newPerson.listenTicks(new EveryDayRecurrentPayment(world,
            newPerson,
            ProductType.MEAL,
            10,
            20)::tick);
    }

    void withMortgage(Person newPerson) {
        newPerson.listenTicks(new MonthlyRecurrentPayment(world,
            newPerson,
            3,
            ProductType.MORTGAGE,
            generateRandomPrice.apply(300, 500),
            world.findCompany())::tick);
    }

    void withPowerSupply(Person newPerson) {
        newPerson.listenTicks(new MonthlyRecurrentPayment(world,
            newPerson,
            25,
            ProductType.ELECTRICITY,
            60,
            120,
            world.findCompany())::tick);
    }

    void withCountryside(Person newPerson) {
        newPerson.listenTicks(new TriggeredByBalanceThreshold(world,
            newPerson,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            new WeekendRecurrentPayment(world,
                newPerson,
                ProductType.ENTERTAINMENT,
                100,
                500))::tick);
    }

    void withCableTV(Person newPerson) {
        newPerson.listenTicks(new TriggeredByBalanceThreshold(world,
            newPerson,
            new BigDecimal("1000"),
            new BigDecimal("2000"),
            new MonthlyRecurrentPayment(world,
                newPerson,
                5,
                ProductType.ENTERTAINMENT,
                generateRandomPrice.apply(10, 25),
                world.findCompany()))::tick);
    }

    void withInternetConnection(Person newPerson) {
        newPerson.listenTicks(
            new TriggeredByBalanceThreshold(world,
                newPerson,
                new BigDecimal("1000"),
                new BigDecimal("1000"),
                new MonthlyRecurrentPayment(world,
                    newPerson,
                    5,
                    ProductType.INTERNET,
                    generateRandomPrice.apply(40, 100),
                    world.findCompany()))::tick);
    }

    private String createPersonUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private Faker faker = new Faker();

    private GenerateRandomPrice generateRandomPrice = new GenerateRandomPrice();

    private World world;
}