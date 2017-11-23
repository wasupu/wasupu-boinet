package io.wasupu.boinet;

import com.google.common.collect.ImmutableList;
import io.wasupu.boinet.population.Person;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class EmploymentOfficeTest {

    /**
     * 2 Companies with capital 78% and 22%
     * 2 People: the two are unemployed
     * <p>
     * Company with 78% requests its candidates the office return the two unemployed candidates
     */
    @Test
    public void shouldReturnTheTwoUnemployedCandidatesToTheLargestCompany() {
        when(world.getCompanies()).thenReturn(ImmutableList.of(firstCompany, secondCompany));
        when(world.getPopulation()).thenReturn(ImmutableList.of(firstPerson, secondPerson));

        when(firstPerson.isUnemployed()).thenReturn(Boolean.TRUE);
        when(secondPerson.isUnemployed()).thenReturn(Boolean.TRUE);

        Collection<Person> returnedCandidates = employmentOffice.getCandidates(firstCompanyCapital);

        assertThat(returnedCandidates)
            .isNotEmpty()
            .containsExactlyInAnyOrder(firstPerson, secondPerson);
    }

    /**
     * 2 Companies with capital 78% and 22%
     * 2 People: 2 employed
     * <p>
     * Company with 22% requests its candidates, should be create a new person
     */
    @Test
    public void shouldCreateANewPersonIfThereAreNotUnemployed() {
        when(world.getCompanies()).thenReturn(ImmutableList.of(firstCompany, secondCompany));
        when(world.getPopulation()).thenReturn(ImmutableList.of(firstPerson, secondPerson));

        when(firstPerson.isUnemployed()).thenReturn(Boolean.FALSE);
        when(secondPerson.isUnemployed()).thenReturn(Boolean.FALSE);

        when(world.newSettler()).thenReturn(thirdPerson);

        Collection<Person> returnedCandidates = employmentOffice.getCandidates(secondCompanyCapital);

        assertThat(returnedCandidates)
            .isNotEmpty()
            .containsExactly(thirdPerson);
    }

    /**
     * 3 Companies with capital 70%, 20% and 10%
     * 4 People: 3 employed and 1 unemployed
     * <p>
     * Company with 20% requests its candidates, should be the unemployed one
     */
    @Test
    public void shouldGetTheFirstUnemployedPerson() {
        when(world.getCompanies()).thenReturn(ImmutableList.of(firstCompany, secondCompany, thirdCompany));
        when(world.getPopulation()).thenReturn(ImmutableList.of(firstPerson, secondPerson, thirdPerson, fourthPerson));

        when(firstPerson.isUnemployed()).thenReturn(Boolean.FALSE);
        when(secondPerson.isUnemployed()).thenReturn(Boolean.FALSE);
        when(thirdPerson.isUnemployed()).thenReturn(Boolean.FALSE);
        when(fourthPerson.isUnemployed()).thenReturn(Boolean.TRUE);

        Collection<Person> returnedCandidates = employmentOffice.getCandidates(secondCompanyCapital);

        assertThat(returnedCandidates)
            .isNotEmpty()
            .containsExactly(fourthPerson);
    }

    /**
     * 3 Companies with capital 70%, 20% and 10%
     * 3 People: all of them are employed
     * <p>
     * Company with 10% requests its candidates, should be a newly created person
     */
    @Test
    public void shouldCreateANewPersonForCandidate() {
        when(world.getCompanies()).thenReturn(ImmutableList.of(firstCompany, secondCompany, thirdCompany));
        when(world.getPopulation()).thenReturn(ImmutableList.of(firstPerson, secondPerson, thirdPerson));
        when(world.newSettler()).thenReturn(fourthPerson);

        when(firstPerson.isUnemployed()).thenReturn(Boolean.FALSE);
        when(secondPerson.isUnemployed()).thenReturn(Boolean.FALSE);
        when(thirdPerson.isUnemployed()).thenReturn(Boolean.FALSE);

        Collection<Person> returnedCandidates = employmentOffice.getCandidates(thirdCompanyCapital);

        assertThat(returnedCandidates)
            .isNotEmpty()
            .containsExactly(fourthPerson);
    }

    /**
     * 3 Companies with capital 10%, 20% and 70%
     * 3 People: 2 are employed and 1 unemployed
     * <p>
     * Company with 70% requests its candidates, should be the third person
     */
    @Test
    public void shouldGiveTheRemainingPersonToBiggestCompany() {
        when(world.getCompanies()).thenReturn(ImmutableList.of(thirdCompany, secondCompany, firstCompany));
        when(world.getPopulation()).thenReturn(ImmutableList.of(firstPerson, secondPerson, thirdPerson));


        when(firstPerson.isUnemployed()).thenReturn(Boolean.FALSE);
        when(secondPerson.isUnemployed()).thenReturn(Boolean.FALSE);
        when(thirdPerson.isUnemployed()).thenReturn(Boolean.TRUE);

        Collection<Person> returnedCandidates = employmentOffice.getCandidates(thirdCompanyCapital);

        assertThat(returnedCandidates)
            .isNotEmpty()
            .containsExactly(thirdPerson);
    }

    @Before
    public void setupEmploymentOffice() throws Exception {
        employmentOffice = new EmploymentOffice(world);
    }

    @Before
    public void setupWorld() throws Exception {
        when(firstCompany.getMyBalance()).thenReturn(firstCompanyCapital);
        when(secondCompany.getMyBalance()).thenReturn(secondCompanyCapital);
        when(thirdCompany.getMyBalance()).thenReturn(thirdCompanyCapital);
    }

    @Mock(name = "firstPerson")
    private Person firstPerson;

    @Mock(name = "secondPerson")
    private Person secondPerson;

    @Mock(name = "thirdPerson")
    private Person thirdPerson;

    @Mock(name = "fourthPerson")
    private Person fourthPerson;

    @Mock(name = "firstCompany")
    private Company firstCompany;

    @Mock(name = "secondCompany")
    private Company secondCompany;

    @Mock(name = "thirdCompany")
    private Company thirdCompany;

    private BigDecimal firstCompanyCapital = new BigDecimal(70);
    private BigDecimal secondCompanyCapital = new BigDecimal(20);
    private BigDecimal thirdCompanyCapital = new BigDecimal(10);

    @Mock
    private World world;

    private EmploymentOffice employmentOffice;

}