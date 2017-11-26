package io.wasupu.boinet.population;

import io.wasupu.boinet.ProductType;
import io.wasupu.boinet.World;
import io.wasupu.boinet.population.behaviours.*;
import io.wasupu.boinet.population.behaviours.balance.WhenBalanceExceedsThreshold;
import io.wasupu.boinet.population.behaviours.balance.TriggeredWhenBalanceBetweenAThreshold;
import io.wasupu.boinet.population.behaviours.recurrent.EveryDayBehaviour;
import io.wasupu.boinet.population.behaviours.recurrent.MonthlyBehaviour;
import io.wasupu.boinet.population.behaviours.Payment;
import io.wasupu.boinet.population.behaviours.recurrent.WeeklyBehaviour;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

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

        withJob(newPerson);
        withEating(newPerson);
        withPowerSupply(newPerson);
        withWaterSupply(newPerson);
        withCountryside(newPerson);
        withCableTV(newPerson);
        withMedicalCosts(newPerson);
        withSalaryRevision(newPerson);

        if (getProbability(0.0, 100.0) < 90) {
            withMortgage(newPerson);
        }

        withInternetConnection(newPerson);

        if (getProbability(0.0, 100.0) < 80) {
            withCarFaults(newPerson);
            withGasForCar(newPerson);
        } else {
            withPublicTransport(newPerson);
        }

        world.getPopulation().add(newPerson);
        return newPerson;
    }

    private void withJob(Person newPerson) {
        newPerson.listenTicks(new FindAJob(world, newPerson)::tick);
    }

    private void withPublicTransport(Person newPerson) {
        newPerson.listenTicks(new MonthlyBehaviour(world,
            newPerson,
            aDayOfMonth(),
            new Payment(world,
                newPerson,
                ProductType.PUBLIC_TRANSPORT,
                generateRandomPrice.apply(50, 70),
                world.findCompany()))::tick);
    }

    private void withGasForCar(Person newPerson) {
        newPerson.listenTicks(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("300"),
            new WeeklyBehaviour(world,
                newPerson,
                1 + new Random().nextInt(6),
                new Payment(world,
                    newPerson,
                    ProductType.GAS,
                    60,
                    100)))::tick);
    }

    private void withCarFaults(Person newPerson) {
        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.CAR_FAULT,
            100,
            300,
            getProbability(0.001, 0.1))::tick);

        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.CAR_FAULT,
            300,
            800,
            getProbability(0.001, 0.5))::tick);

        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.CAR_FAULT,
            800,
            2000,
            getProbability(0.001, 0.01))::tick);
    }

    private void withMedicalCosts(Person newPerson) {
        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            50,
            100,
            getProbability(0.001, 0.1))::tick);

        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            100,
            500,
            getProbability(0.001, 0.05))::tick);

        newPerson.listenTicks(new ImponderablePaymentBehaviour(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            3000,
            5000,
            getProbability(0.00001, 0.001))::tick);

    }

    void withEating(Person newPerson) {
        newPerson.listenTicks(new EveryDayBehaviour(world,
            newPerson,
            new Payment(world, newPerson,
                ProductType.MEAL,
                10,
                20))::tick);
    }

    void withMortgage(Person newPerson) {
        newPerson.listenTicks(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("50"),
            new MonthlyBehaviour(world,
                newPerson,
                aDayOfMonth(),
                new Payment(world,
                    newPerson,
                    ProductType.MORTGAGE,
                    generateRandomPrice.apply(300, 500),
                    world.findCompany())))::tick);
    }

    void withPowerSupply(Person newPerson) {
        newPerson.listenTicks(new MonthlyBehaviour(world,
            newPerson,
            25,
            new Payment(world,
                newPerson,
                ProductType.POWER_SUPPLY,
                generateRandomPrice.apply(60, 120),
                world.findCompany()))::tick);
    }

    void withWaterSupply(Person newPerson) {
        newPerson.listenTicks(new MonthlyBehaviour(world,
            newPerson,
            aDayOfMonth(),
            new Payment(world,
                newPerson,
                ProductType.WATER_SUPPLY,
                generateRandomPrice.apply(20, 40),
                world.findCompany()))::tick);
    }

    void withCountryside(Person newPerson) {
        newPerson.listenTicks(new TriggeredWhenBalanceBetweenAThreshold(world,
            newPerson,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            new WeeklyBehaviour(world,
                newPerson,
                6,
                new Payment(world,
                    newPerson, ProductType.ENTERTAINMENT,
                    100,
                    500)))::tick);
    }

    void withCableTV(Person newPerson) {
        newPerson.listenTicks(new TriggeredWhenBalanceBetweenAThreshold(world,
            newPerson,
            new BigDecimal("1000"),
            new BigDecimal("2000"),
            new MonthlyBehaviour(world,
                newPerson,
                5,
                new Payment(world,
                    newPerson,
                    ProductType.ENTERTAINMENT,
                    generateRandomPrice.apply(10, 25),
                    world.findCompany())))::tick);
    }

    void withInternetConnection(Person newPerson) {
        newPerson.listenTicks(
            new WhenBalanceExceedsThreshold(world,
                newPerson,
                new BigDecimal("1000"),
                new MonthlyBehaviour(world,
                    newPerson,
                    5,
                    new Payment(world,
                        newPerson,
                        ProductType.INTERNET,
                        generateRandomPrice.apply(40, 100),
                        world.findCompany())))::tick);
    }

    void withSalaryRevision(Person newPerson) {
        newPerson.listenTicks(new RequestSalaryRevisionYearly(world,
            newPerson,
            random.nextInt(365))::tick);

    }

    private Integer aDayOfMonth() {
        return new Random().nextInt(28);
    }

    private double getProbability(Double minRange, Double maxRange) {
        return minRange + random.nextDouble() * (maxRange - minRange);
    }

    private String createPersonUniqueIdentifier() {
        return UUID.randomUUID().toString();
    }

    private GenerateRandomPrice generateRandomPrice = new GenerateRandomPrice();

    private World world;

    private Random random = new Random();
}