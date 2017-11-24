package io.wasupu.boinet.population;

import com.github.javafaker.Faker;
import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.behaviours.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;
import java.util.stream.IntStream;

public class Hospital {

    public Hospital(World world) {
        this.world = world;
    }

    public Person newBorn(Integer number) {
        Person newPerson = new Person(
            createPersonUniqueIdentifier(),
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
        withCardFaults(newPerson);
        withMedicalCosts(newPerson);
        world.getPopulation().add(newPerson);
        return newPerson;
    }

    private void withCardFaults(Person newPerson) {
        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.CAR_FAULT,
            100,
            300,
            2.0)::tick);

        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.CAR_FAULT,
            300,
            800,
            1.0)::tick);

        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.CAR_FAULT,
            800,
            2000,
            0.1)::tick);

    }

    private void withMedicalCosts(Person newPerson) {
        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            50,
            100,
            0.1)::tick);

        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            100,
            500,
            0.05)::tick);

        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            3000,
            5000,
            0.001)::tick);

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
            new Random().nextInt(28),
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