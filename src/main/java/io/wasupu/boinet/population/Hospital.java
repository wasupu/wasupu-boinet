package io.wasupu.boinet.population;

import io.wasupu.boinet.World;
import io.wasupu.boinet.companies.ProductType;
import io.wasupu.boinet.companies.ReceiptType;
import io.wasupu.boinet.economicalSubjects.behaviours.ContractCurrentAccount;
import io.wasupu.boinet.economicalSubjects.behaviours.InitialCapital;
import io.wasupu.boinet.economicalSubjects.behaviours.RegisterInBank;
import io.wasupu.boinet.economicalSubjects.behaviours.balance.TriggeredWhenBalanceBetweenAThreshold;
import io.wasupu.boinet.economicalSubjects.behaviours.balance.WhenBalanceExceedsThreshold;
import io.wasupu.boinet.economicalSubjects.behaviours.recurrent.EveryDay;
import io.wasupu.boinet.subjects.behaviours.Monthly;
import io.wasupu.boinet.subjects.behaviours.Weekly;
import io.wasupu.boinet.subjects.behaviours.Yearly;
import io.wasupu.boinet.population.behaviours.*;

import java.math.BigDecimal;
import java.util.Random;
import java.util.UUID;

public class Hospital {

    public Hospital(World world) {
        this.world = world;
    }

    public Person newBorn() {
        var newPerson = new Person(createPersonUniqueIdentifier(), world);

        newPerson.addBehaviour(new RegisterInBank(world, newPerson));
        newPerson.addBehaviour(new ContractCurrentAccount(world, newPerson));
        newPerson.addBehaviour(new ContractDebitCard(world, newPerson));
        newPerson.addBehaviour(new InitialCapital(world, newPerson, new BigDecimal(3000)));

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

        if (withProbability(90)) {
            withOwnedHouse(newPerson);
        }

        withInternetConnection(newPerson);

        if (withProbability(80)) {
            withCarFaults(newPerson);
            withGasForCar(newPerson);
        } else {
            withPublicTransport(newPerson);
        }

        return newPerson;
    }

    private boolean withProbability(Integer percentage) {
        return getProbability(0.0, 100.0) < percentage;
    }

    private void withElectronicProductPayment(Person newPerson) {
        newPerson.addBehaviour(
            new WhenBalanceExceedsThreshold(world,
                newPerson,
                new BigDecimal("8000"),
                new Monthly(world,
                    get10to25MonthDay(),
                    new PayWithCard(world,
                        newPerson,
                        ProductType.ELECTRONIC_DEVICE,
                        300,
                        2000))));
    }

    private void withHolidaysOnceInAYear(Person newPerson) {
        newPerson.addBehaviour(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("10000"),
            new Yearly(world,
                207,//If you have money in 26 of july
                new PayWithCard(world,
                    newPerson,
                    ProductType.HOLIDAYS,
                    2000,
                    4000))));
    }

    private void withLuxuryProductPayment(Person newPerson) {
        newPerson.addBehaviour(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("20000"),
            new Monthly(world,
                get10to25MonthDay(),
                new PayWithCard(world,
                    newPerson,
                    ProductType.LUXURY,
                    4000,
                    6000))));
    }

    private void withNewCar(Person newPerson) {
        newPerson.addBehaviour(new WhenBalanceExceedsThreshold(world,
            newPerson,
            new BigDecimal("40000"),
            new Yearly(world,
                1 + new Random().nextInt(360),
                new PayWithCard(world,
                    newPerson,
                    ProductType.NEW_CAR,
                    20000,
                    30000))));
    }

    private void withJob(Person newPerson) {
        newPerson.addBehaviour(new FindAJob(world, newPerson));
    }

    private void withPublicTransport(Person newPerson) {
        newPerson.addBehaviour(new Monthly(world,
            get2to10MonthDay(),
            new PayWithCard(world,
                newPerson,
                ProductType.PUBLIC_TRANSPORT,
                50,
                70)));
    }

    private void withGasForCar(Person newPerson) {
        newPerson.addBehaviour(new WhenBalanceExceedsThreshold(world, newPerson,
            new BigDecimal("300"),
            new Weekly(world,
                1 + new Random().nextInt(6),
                new PayWithCard(world,
                    newPerson,
                    ProductType.GAS,
                    60,
                    100))));
    }

    private void withCarFaults(Person newPerson) {
        newPerson.addBehaviour(new PayAnImponderable(world,
            newPerson,
            ProductType.CAR_FAULT,
            100,
            300,
            getProbability(0.001, 0.1)));

        newPerson.addBehaviour(new PayAnImponderable(world,
            newPerson,
            ProductType.CAR_FAULT,
            300,
            800,
            getProbability(0.001, 0.5)));

        newPerson.addBehaviour(new PayAnImponderable(world,
            newPerson,
            ProductType.CAR_FAULT,
            800,
            2000,
            getProbability(0.001, 0.01)));
    }

    private void withMedicalCosts(Person newPerson) {
        newPerson.addBehaviour(new PayAnImponderable(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            50,
            100,
            getProbability(0.01, 0.5)));

        newPerson.addBehaviour(new PayAnImponderable(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            100,
            500,
            getProbability(0.001, 0.05)));

        newPerson.addBehaviour(new PayAnImponderable(world,
            newPerson,
            ProductType.MEDICAL_COSTS,
            3000,
            5000,
            getProbability(0.00001, 0.001)));
    }

    private void withEating(Person newPerson) {
        newPerson.addBehaviour(new EveryDay(world, newPerson,
            new PayWithCard(world, newPerson,
                ProductType.MEAL,
                10,
                25)));
    }

    private void withOwnedHouse(Person newPerson) {
        newPerson.addBehaviour(new BuyHouse(world, newPerson, world.findCompany()));
    }

    private void withPowerSupply(Person newPerson) {
        newPerson.addBehaviour(new Monthly(world,
            get2to10MonthDay(),
            new PayAReceipt(world,
                newPerson,
                world.findCompany(),
                ReceiptType.POWER_SUPPLY,
                generateRandomPrice.apply(60, 120))));
    }

    private void withWaterSupply(Person newPerson) {
        newPerson.addBehaviour(new Monthly(world,
            get2to10MonthDay(),
            new PayAReceipt(world,
                newPerson,
                world.findCompany(),
                ReceiptType.WATER_SUPPLY,
                generateRandomPrice.apply(20, 40))));
    }

    private void withCountryside(Person newPerson) {
        newPerson.addBehaviour(new TriggeredWhenBalanceBetweenAThreshold(world, newPerson,
            new BigDecimal("1000"),
            new BigDecimal("6000"),
            new Weekly(world, 6,
                new PayWithCard(world,
                    newPerson, ProductType.ENTERTAINMENT,
                    100,
                    500))));
    }

    private void withCableTV(Person newPerson) {
        newPerson.addBehaviour(new TriggeredWhenBalanceBetweenAThreshold(world, newPerson,
            new BigDecimal("1000"),
            new BigDecimal("2000"),
            new Monthly(world,
                get10to25MonthDay(),
                new PayWithCard(world,
                    newPerson,
                    ProductType.ENTERTAINMENT,
                    generateRandomPrice.apply(10, 25),
                    world.findCompany()))));
    }

    private void withInternetConnection(Person newPerson) {
        newPerson.addBehaviour(
            new WhenBalanceExceedsThreshold(world, newPerson,
                new BigDecimal("1000"),
                new Monthly(world,
                    get10to25MonthDay(),
                    new PayWithCard(world,
                        newPerson,
                        ProductType.INTERNET,
                        generateRandomPrice.apply(40, 100),
                        world.findCompany()))));
    }

    private void withSalaryRevision(Person newPerson) {
        newPerson.addBehaviour(new RequestSalaryRevisionYearly(world,
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