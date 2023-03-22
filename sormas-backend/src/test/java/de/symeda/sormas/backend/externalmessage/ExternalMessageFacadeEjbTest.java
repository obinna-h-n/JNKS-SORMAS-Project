/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.backend.externalmessage;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.common.DeletionDetails;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.customizableenum.CustomEnumNotFoundException;
import de.symeda.sormas.api.customizableenum.CustomizableEnumType;
import de.symeda.sormas.api.disease.DiseaseVariant;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageCriteria;
import de.symeda.sormas.api.externalmessage.ExternalMessageDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageIndexDto;
import de.symeda.sormas.api.externalmessage.ExternalMessageStatus;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.sample.PathogenTestResultType;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.backend.AbstractBeanTest;
import de.symeda.sormas.backend.TestDataCreator;
import de.symeda.sormas.backend.customizableenum.CustomizableEnumValue;

public class ExternalMessageFacadeEjbTest extends AbstractBeanTest {

	@Test
	public void testGetByReportIdWithCornerCaseInput() {
		String reportId = "123456789";
		creator.createExternalMessage((lm) -> lm.setReportId(reportId));

		List<ExternalMessageDto> list = getExternalMessageFacade().getByReportId(null);

		assertNotNull(list);
		assertTrue(list.isEmpty());

		list = getExternalMessageFacade().getByReportId("");

		assertNotNull(list);
		assertTrue(list.isEmpty());
	}

	@Test
	public void testGetByReportIdWithOneMessage() {

		String reportId = "123456789";
		creator.createExternalMessage((lm) -> lm.setReportId(reportId));

		// create noise
		creator.createExternalMessage(null);
		creator.createExternalMessage((lm) -> lm.setReportId("some-other-id"));

		List<ExternalMessageDto> list = getExternalMessageFacade().getByReportId(reportId);

		assertNotNull(list);
		assertFalse(list.isEmpty());
		assertEquals(1, list.size());
		assertEquals(reportId, list.get(0).getReportId());
	}

	@Test
	public void testGetByUuid() {

		ExternalMessageDto labMessage = creator.createExternalMessage(null);

		ExternalMessageDto result = getExternalMessageFacade().getByUuid(labMessage.getUuid());
		assertThat(result, equalTo(labMessage));
	}

	@Test
	public void testExistsForwardedLabMessageWith() {

		String reportId = "1234";

		// create noise
		creator.createExternalMessage((lm) -> lm.setStatus(ExternalMessageStatus.FORWARDED));

		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));
		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(null));

		creator.createExternalMessage((lm) -> lm.setReportId(reportId));

		assertFalse(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));

		ExternalMessageDto forwardedMessage = creator.createExternalMessage((lm) -> {
			lm.setReportId(reportId);
			lm.setStatus(ExternalMessageStatus.FORWARDED);
		});

		assertTrue(getExternalMessageFacade().existsForwardedExternalMessageWith(reportId));
	}

	@Test
	public void testExistsLabMessageForEntityCase() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		CaseDataDto caze = creator.createCase(user.toReference(), person.toReference(), rdcf);

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		// create noise
		CaseDataDto noiseCaze = creator.createCase(user.toReference(), person.toReference(), rdcf);
		creator.createSample(noiseCaze.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample.toReference());
		SampleDto sample2 = creator.createSample(caze.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caze.toReference(), sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(caze.toReference()));
	}

	@Test
	public void testExistsLabMessageForEntityContact() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		ContactDto contact = creator.createContact(user.toReference(), person.toReference());

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		// create noise
		ContactDto noiseContact = creator.createContact(user.toReference(), person.toReference());
		creator.createSample(noiseContact.toReference(), user.toReference(), rdcf.facility, null);

		SampleDto sample = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(contact.toReference(), user.toReference(), rdcf.facility, null);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(contact.toReference()));
	}

	@Test
	public void testExistsLabMessageForEntityEventParticipant() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();
		EventDto event = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());

		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		// create noise
		EventParticipantDto noiseEventParticipant = creator.createEventParticipant(event.toReference(), person, user.toReference());
		creator.createSample(noiseEventParticipant.toReference(), user.toReference(), rdcf.facility);

		SampleDto sample = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		assertFalse(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		creator.createLabMessageWithTestReport(sample.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));

		// create additional matches
		creator.createLabMessageWithTestReport(sample.toReference());
		SampleDto sample2 = creator.createSample(eventParticipant.toReference(), user.toReference(), rdcf.facility);
		creator.createLabMessageWithTestReport(sample2.toReference());
		assertTrue(getExternalMessageFacade().existsExternalMessageForEntity(eventParticipant.toReference()));
	}

	@Test
	public void testCountAndIndexListDoesNotReturnMessagesLinkedToDeletedEntities() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();
		PersonDto person = creator.createPerson();

		CaseDataDto externalMessageCase = creator.createCase(user.toReference(), person.toReference(), rdcf);
		SampleDto externalMessageSample = creator.createSample(
			creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf).toReference(),
			user.toReference(),
			rdcf.facility);

		CaseDataDto caseWithSample = creator.createCase(user.toReference(), person.toReference(), rdcf);
		SampleDto caseSample = creator.createSample(caseWithSample.toReference(), user.toReference(), rdcf.facility);

		ContactDto contactWithSample = creator.createContact(rdcf, user.toReference(), creator.createPerson().toReference());
		SampleDto contactSample = creator.createSample(contactWithSample.toReference(), user.toReference(), rdcf.facility, null);

		EventParticipantDto eventParticipantWithSample =
			creator.createEventParticipant(creator.createEvent(user.toReference()).toReference(), creator.createPerson(), user.toReference());
		SampleDto eventParticipantSample = creator.createSample(eventParticipantWithSample.toReference(), user.toReference(), rdcf.facility);

		EventDto eventToDelete = creator.createEvent(user.toReference());
		EventParticipantDto eventParticipantWithDeletedEvent =
			creator.createEventParticipant(eventToDelete.toReference(), creator.createPerson(), user.toReference());
		SampleDto eventParticipantSampleForDeletedEvent =
			creator.createSample(eventParticipantWithDeletedEvent.toReference(), user.toReference(), rdcf.facility);

		ExternalMessageDto messageWithCaseSample = creator.createLabMessageWithTestReport(externalMessageSample.toReference());
		ExternalMessageDto messageWithSurveillanceReport =
			creator.createLabMessageWithSurveillanceReport(user.toReference(), externalMessageCase.toReference());
		ExternalMessageDto labMessageWithSurveillanceReportAndSample =
			creator.createLabMessageWithTestReportAndSurveillanceReport(user.toReference(), caseWithSample.toReference(), caseSample.toReference());
		ExternalMessageDto messageWithContactSample = creator.createLabMessageWithTestReport(contactSample.toReference());
		ExternalMessageDto messageWithEventParticipantSample = creator.createLabMessageWithTestReport(eventParticipantSample.toReference());
		ExternalMessageDto messageWithEvent = creator.createLabMessageWithTestReport(eventParticipantSampleForDeletedEvent.toReference());

		assertThat(getExternalMessageFacade().count(new ExternalMessageCriteria()), is(6L));
		List<ExternalMessageIndexDto> indexList = getExternalMessageFacade().getIndexList(new ExternalMessageCriteria(), null, null, null);
		assertThat(indexList, hasSize(6));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithCaseSample)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithSurveillanceReport)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, labMessageWithSurveillanceReportAndSample)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithContactSample)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithEventParticipantSample)).count(), is(1L));
		assertThat(indexList.stream().filter(m -> DataHelper.isSame(m, messageWithEvent)).count(), is(1L));

		getCaseFacade().delete(externalMessageCase.getUuid(), new DeletionDetails());
		getSampleFacade().deleteSample(externalMessageSample.toReference(), new DeletionDetails());
		getCaseFacade().delete(caseWithSample.getUuid(), new DeletionDetails());
		getContactFacade().delete(contactWithSample.getUuid(), new DeletionDetails());
		getEventParticipantFacade().delete(eventParticipantWithSample.getUuid(), new DeletionDetails());
		getEventFacade().delete(eventToDelete.getUuid(), new DeletionDetails());

		assertThat(getExternalMessageFacade().count(new ExternalMessageCriteria()), is(0L));
		indexList = getExternalMessageFacade().getIndexList(new ExternalMessageCriteria(), null, null, null);
		assertThat(indexList, hasSize(0));

	}

	@Test
	public void testDiseaseVariantDeterminationOnSave() {
		TestDataCreator.RDCF rdcf = creator.createRDCF();
		UserDto user = creator.createNationalUser();

		CustomizableEnumValue entry = new CustomizableEnumValue();
		entry.setDataType(CustomizableEnumType.DISEASE_VARIANT);
		entry.setValue("BF.1.2");
		entry.setDiseases(Arrays.asList(Disease.CORONAVIRUS));
		entry.setCaption("BF.1.2 variant");
		getCustomizableEnumValueService().ensurePersisted(entry);
		DiseaseVariant diseaseVariant = null;
		try {
			diseaseVariant = getCustomizableEnumFacade().getEnumValue(CustomizableEnumType.DISEASE_VARIANT, "BF.1.2", Disease.CORONAVIRUS);
		} catch (CustomEnumNotFoundException e) {
			throw new RuntimeException(e);
		}
		SampleDto labMessageSample = creator.createSample(
			creator.createCase(user.toReference(), creator.createPerson().toReference(), rdcf).toReference(),
			user.toReference(),
			rdcf.facility);
		ExternalMessageDto labMessage = creator.createLabMessageWithTestReport(labMessageSample.toReference());
		labMessage.setDisease(Disease.CORONAVIRUS);
		labMessage = getExternalMessageFacade().save(labMessage);
		labMessage.getSampleReports().stream().flatMap(sampleReportDto -> sampleReportDto.getTestReports().stream()).forEach(testReportDto -> {
			testReportDto.setTestedDiseaseVariant("BF.1.2");
			testReportDto.setTestResult(PathogenTestResultType.POSITIVE);
			getTestReportFacade().saveTestReport(testReportDto);
		});
		getExternalMessageFacade().save(labMessage);

		assertEquals(diseaseVariant, labMessage.getDiseaseVariant());
	}

	//	This test currently does not work because the bean tests used don't support @TransactionAttribute tags.
//	This test should be enabled once there is a new test framework in use.
//	@Test
//	public void testSaveWithFallback() {
//
//		// valid message
//		ExternalMessageDto validMessage = ExternalMessageDto.build();
//		validMessage.setReportId("reportId");
//		validMessage.setStatus(ExternalMessageStatus.FORWARDED);
//		validMessage.setTestReports(Collections.singletonList(TestReportDto.build()));
//		validMessage.setPersonFirstName("Dude");
//		validMessage.setExternalMessageDetails("Details");
//		getLabMessageFacade().saveWithFallback(validMessage);
//		ExternalMessageDto savedMessage = getLabMessageFacade().getByUuid(validMessage.getUuid());
//		assertEquals(validMessage, savedMessage);
//
//		// Invalid message
//		ExternalMessageDto invalidMessage = ExternalMessageDto.build();
//		invalidMessage.setExternalMessageDetails("Details");
//		invalidMessage.setPersonFirstName(String.join("", Collections.nCopies(50, "MaliciousDude")));
//		getLabMessageFacade().saveWithFallback(invalidMessage);
//		savedMessage = getLabMessageFacade().getByUuid(invalidMessage.getUuid());
//		assertEquals(invalidMessage.getUuid(), savedMessage.getUuid());
//		assertEquals(invalidMessage.getStatus(), savedMessage.getStatus());
//		assertEquals(invalidMessage.getExternalMessageDetails(), savedMessage.getExternalMessageDetails());
//		assertNull(savedMessage.getPersonFirstName());
//
//		// make sure that valid message still exists
//		savedMessage = getLabMessageFacade().getByUuid(validMessage.getUuid());
//		assertEquals(validMessage, savedMessage);
//
//	}
}
