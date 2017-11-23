package io.wasupu.boinet;

import com.google.common.collect.ImmutableSet;
import io.wasupu.boinet.population.Person;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static java.math.RoundingMode.CEILING;

public class EmploymentOffice {

    public EmploymentOffice(World world) {
        this.world = world;
    }

    public Collection<Person> getCandidates(BigDecimal companyCapital) {
        Integer estimatedCandidates = estimateCandidates(companyCapital);
        return selectAtMostFromUnemployed(estimatedCandidates);
    }

    private Collection<Person> selectAtMostFromUnemployed(Integer estimatedCandidates) {
        Set<Person> candidates = world.getPopulation()
            .stream()
            .filter(Person::isUnemployed)
            .limit(estimatedCandidates)
            .collect(Collectors.toSet());

        return (!candidates.isEmpty()) ? candidates : ImmutableSet.of(world.newSettler());
    }

    private Integer estimateCandidates(BigDecimal companyCapital) {
        BigDecimal populationSize = new BigDecimal(world.getPopulation().size());
        BigDecimal minimumNumberOfCandidates = populationSize
            .multiply(companyCapital)
            .divide(allCompanyCapital(), CEILING);

        return minimumNumberOfCandidates.equals(new BigDecimal(0)) ? 1 : minimumNumberOfCandidates.intValue();
    }

    private BigDecimal allCompanyCapital() {
        return world.getCompanies()
            .stream()
            .map(Company::getMyBalance)
            .reduce(new BigDecimal(0), BigDecimal::add);
    }

    private World world;

}
