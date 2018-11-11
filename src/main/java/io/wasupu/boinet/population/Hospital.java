package io.wasupu.boinet.population;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.ProductType;
import io.wasupu.boinet.economicalSubjects.behaviours.ContractAccount;
import io.wasupu.boinet.economicalSubjects.behaviours.InitialCapital;
import io.wasupu.boinet.economicalSubjects.behaviours.balance.TriggeredWhenBalanceBetweenAThreshold;
import io.wasupu.boinet.economicalSubjects.behaviours.balance.WhenBalanceExceedsThreshold;
import io.wasupu.boinet.economicalSubjects.behaviours.recurrent.EveryDayBehaviour;
import io.wasupu.boinet.economicalSubjects.behaviours.recurrent.MonthlyBehaviour;
import io.wasupu.boinet.economicalSubjects.behaviours.recurrent.WeeklyBehaviour;
import io.wasupu.boinet.economicalSubjects.behaviours.recurrent.YearlyBehaviour;
import io.wasupu.boinet.population.behaviours.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public class Hospital {

    public Hospital(World world) {
        this.world = world;
    }

    public Person newBorn(Integer number) {
        var newPerson = new Person(
            createPersonUniqueIdentifier(),
            world);

        newPerson.listenTicks(new ContractAccount(world, newPerson));
        newPerson.listenTicks(new ContractDebitCard(world, newPerson));
        newPerson.listenTicks(new InitialCapital(world, newPerson, new BigDecimal(3000)));

        withJob(newPerson);
        withEating(newPerson);
        withPowerSupply(newPerson);
        withWaterSupply(newPerson);
        withCountryside(newPerson);
        withCableTV(newPerson);
        withMedicalCosts(newPerson);
        withSalaryRevision(newPerson);
        withHolidaysOnceInAYear(newPerson);
        withLuxuryProductPayment(newPerson);
        withNewCar(newPerson);
        withElectronicProductPayment(newPerson);

        if (isInPercentage(90)) {
            withMortgage(newPerson);
        }

        withInternetConnection(newPerson);

        if (isInPercentage(80)) {
            withCarFaults(newPerson);
            withGasForCar(newPerson);
        } else {
            withPublicTransport(newPerson);
        }

        world.getPopulation().add(newPerson);
        return newPerson;
    }

    private boolean isInPercentage(Integer percentage) {
        return getProbability(0.0, 100.0) < percentage;
    }

    private void withElectronicProductPayment(Person newPerson) {
        newPerson.listenTicks(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("8000"),
            new MonthlyBehaviour(world,
                newPerson,
                get10to25MonthDay(),
                new PayWithCard(world,
                    newPerson,
                    ProductType.ELECTRONIC_DEVICE,
                    300,
                    2000))));
    }

    private void withHolidaysOnceInAYear(Person newPerson) {
        newPerson.listenTicks(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("10000"),
            new YearlyBehaviour(world,
                newPerson,
                207,//If you have money in 26 of july
                new PayWithCard(world,
                    newPerson,
                    ProductType.HOLIDAYS,
                    2000,
                    4000))));
    }

    private void withLuxuryProductPayment(Person newPerson) {
        newPerson.listenTicks(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("20000"),
            new MonthlyBehaviour(world,
                newPerson,
                get10to25MonthDay(),
                new PayWithCard(world,
                    newPerson,
                    ProductType.LUXURY,
                    4000,
                    6000))));
    }

    private void withNewCar(Person newPerson) {
        newPerson.listenTicks(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("40000"),
            new YearlyBehaviour(world,
                newPerson,
                1 + new Random().nextInt(360),
                new PayWithCard(world,
                    newPerson,
                    ProductType.NEW_CAR,
                    20000,
                    30000))));
    }

    private void withJob(Person newPerson) {
        newPerson.listenTicks(new FindAJob(world, newPerson));
    }

    private void withPublicTransport(Person newPerson) {
        newPerson.listenTicks(new MonthlyBehaviour(world,
            newPerson,
            get2to10MonthDay(),
            new PayWithCard(world,
                newPerson,
                ProductType.PUBLIC_TRANSPORT,
                50,
                70)));
    }

    private void withGasForCar(Person newPerson) {
        newPerson.listenTicks(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("300"),
            new WeeklyBehaviour(world,
                newPerson,
                1 + new Random().nextInt(6),
                new PayWithCard(world,
                    newPerson,
                    ProductType.GAS,
                    60,
                    100))));
    }

    private void withCarFaults(Person newPerson) {
        newPerson.listenTicks(new PayAnImponderable(world,
            newPerson,
            ProductType.CAR_FAULT,
            100,
            300,
            getProbability(0.001, 0.1)));

        newPerson.listenTicks(new PayAnImponderable(world,
            newPerson,
            ProductType.CAR_FAULT,
            300,
            800,
            getProbability(0.001, 0.5)));

        newPerson.listenTicks(new PayAnImponderable(world,
            newPerson,
            ProductType.CAR_FAULT,
            800,
            2000,
            getProbability(0.001, 0.01)));
    }

    private void withMedicalCosts(Person newPerson) {
        newPerson.listenTicks(new PayAnImponderable(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            50,
            100,
            getProbability(0.01, 0.5)));

        newPerson.listenTicks(new PayAnImponderable(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            100,
            500,
            getProbability(0.001, 0.05)));

        newPerson.listenTicks(new PayAnImponderable(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            3000,
            5000,
            getProbability(0.00001, 0.001)));
    }

    void withEating(Person newPerson) {
        newPerson.listenTicks(new EveryDayBehaviour(world,
            newPerson,
            new PayWithCard(world, newPerson,
                ProductType.MEAL,
                10,
                25)));
    }

    void withMortgage(Person newPerson) {
        newPerson.listenTicks(new ContractMortgage(world, newPerson));

        newPerson.listenTicks(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("50"),
            new MonthlyBehaviour(world,
                newPerson,
                28,
                new PayMortgage(world,
                    newPerson,
                    generateRandomPrice.apply(500, 800)))));
    }

    void withPowerSupply(Person newPerson) {
        newPerson.listenTicks(new MonthlyBehaviour(world,
            newPerson,
            get2to10MonthDay(),
            new PayWithCard(world,
                newPerson,
                ProductType.POWER_SUPPLY,
                generateRandomPrice.apply(60, 120),
                world.findCompany())));
    }

    void withWaterSupply(Person newPerson) {
        newPerson.listenTicks(new MonthlyBehaviour(world,
            newPerson,
            get2to10MonthDay(),
            new PayWithCard(world,
                newPerson,
                ProductType.WATER_SUPPLY,
                generateRandomPrice.apply(20, 40),
                world.findCompany())));
    }

    void withCountryside(Person newPerson) {
        newPerson.listenTicks(new TriggeredWhenBalanceBetweenAThreshold(world,
            newPerson,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            new WeeklyBehaviour(world,
                newPerson,
                6,
                new PayWithCard(world,
                    newPerson, ProductType.ENTERTAINMENT,
                    100,
                    500))));
    }

    void withCableTV(Person newPerson) {
        newPerson.listenTicks(new TriggeredWhenBalanceBetweenAThreshold(world,
            newPerson,
            new BigDecimal("1000"),
            new BigDecimal("2000"),
            new MonthlyBehaviour(world,
                newPerson,
                get10to25MonthDay(),
                new PayWithCard(world,
                    newPerson,
                    ProductType.ENTERTAINMENT,
                    generateRandomPrice.apply(10, 25),
                    world.findCompany()))));
    }

    void withInternetConnection(Person newPerson) {
        newPerson.listenTicks(
            new WhenBalanceExceedsThreshold(world,
                newPerson,
                new BigDecimal("1000"),
                new MonthlyBehaviour(world,
                    newPerson,
                    get10to25MonthDay(),
                    new PayWithCard(world,
                        newPerson,
                        ProductType.INTERNET,
                        generateRandomPrice.apply(40, 100),
                        world.findCompany()))));
    }

    void withSalaryRevision(Person newPerson) {
        newPerson.listenTicks(new RequestSalaryRevisionYearly(world,
            newPerson,
            random.nextInt(365)));
    }

    private int get2to10MonthDay() {
        return 2 + new Random().nextInt(8);
    }

    private int get10to25MonthDay() {
        return 10 + new Random().nextInt(15);
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