/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2023 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.api.environment.environmentsample;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.common.DeletionReason;
import de.symeda.sormas.api.location.LocationDto;
import de.symeda.sormas.api.sample.SpecimenCondition;
import de.symeda.sormas.api.utils.PersonalData;
import de.symeda.sormas.api.utils.SensitiveData;
import de.symeda.sormas.api.utils.pseudonymization.PseudonymizableIndexDto;
import de.symeda.sormas.api.uuid.HasUuid;

public class EnvironmentSampleIndexDto extends PseudonymizableIndexDto implements HasUuid, Serializable {

	private static final long serialVersionUID = -1255140508213117874L;

	public static final String I18N_PREFIX = "EnvironmentSample";

	public static final String UUID = "uuid";
	public static final String FIELD_SAMPLE_ID = "fieldSampleId";
	public static final String SAMPLE_DATE_TIME = "sampleDateTime";
	public static final String ENVIRONMENT = "environment";
	public static final String LOCATION = "location";
	public static final String DISTRICT = "district";
	public static final String DISPATCHED = "dispatched";
	public static final String DISPATCH_DATE = "dispatchDate";
	public static final String RECEIVED = "received";
	public static final String LABORATORY = "laboratory";
	public static final String SAMPLE_MATERIAL = "sampleMaterial";
	public static final String OTHER_SAMPLE_MATERIAL = "otherSampleMaterial";
	public static final String POSITIVE_PATHOGEN_TESTS = "positivePathogenTests";
	public static final String LATEST_PATHOGEN_TEST = "latestPathogenTest";
	public static final String NUMBER_OF_TESTS = "numberOfTests";
	public static final String DELETION_REASON = "deletionReason";

	private String fieldSampleId;
	private Date sampleDateTime;
	@SensitiveData
	private String environment;
	@PersonalData
	@SensitiveData
	private String location;
	private String district;
	private boolean dispatched;
	private Date dispatchDate;
	private boolean received;
	private Date receivalDate;
	@SensitiveData
	private String laboratory;
	private SpecimenCondition specimenCondition;
	private EnvironmentSampleMaterial sampleMaterial;
	@PersonalData
	private String otherSampleMaterial;
	private long positivePathogenTests;
	private String latestPathogenTest;
	private long numberOfTests;
	private DeletionReason deletionReason;
	@SensitiveData
	private String otherDeletionReason;

	public EnvironmentSampleIndexDto(
		String uuid,
		String fieldSampleId,
		Date sampleDateTime,
		String environment,
		String street,
		String houseNumber,
		String postalCode,
		String city,
		String district,
		boolean dispatched,
		Date dispatchDate,
		boolean received,
		Date receivalDate,
		String laboratory,
		SpecimenCondition specimenCondition,
		EnvironmentSampleMaterial sampleMaterial,
		String otherSampleMaterial,
		DeletionReason deletionReason,
		String otherDeletionReason,
		boolean isInJurisdiction) {
		super(uuid);
		this.fieldSampleId = fieldSampleId;
		this.sampleDateTime = sampleDateTime;
		this.environment = environment;
		this.location = LocationDto.buildAddressCaption(street, houseNumber, postalCode, city);
		this.district = district;
		this.dispatched = dispatched;
		this.dispatchDate = dispatchDate;
		this.received = received;
		this.receivalDate = receivalDate;
		this.laboratory = laboratory;
		this.specimenCondition = specimenCondition;
		this.sampleMaterial = sampleMaterial;
		this.otherSampleMaterial = otherSampleMaterial;
		this.deletionReason = deletionReason;
		this.otherDeletionReason = otherDeletionReason;
		setInJurisdiction(isInJurisdiction);
	}

	public String getFieldSampleId() {
		return fieldSampleId;
	}

	public Date getSampleDateTime() {
		return sampleDateTime;
	}

	public String getEnvironment() {
		return environment;
	}

	public String getLocation() {
		return location;
	}

	public String getDistrict() {
		return district;
	}

	public boolean isDispatched() {
		return dispatched;
	}

	public Date getDispatchDate() {
		return dispatchDate;
	}

	public boolean isReceived() {
		return received;
	}

	public Date getReceivalDate() {
		return receivalDate;
	}

	public String getLaboratory() {
		return laboratory;
	}

	public SpecimenCondition getSpecimenCondition() {
		return specimenCondition;
	}

	public EnvironmentSampleMaterial getSampleMaterial() {
		return sampleMaterial;
	}

	public String getOtherSampleMaterial() {
		return otherSampleMaterial;
	}

	public long getPositivePathogenTests() {
		return positivePathogenTests;
	}

	public String getLatestPathogenTest() {
		return latestPathogenTest;
	}

	public long getNumberOfTests() {
		return numberOfTests;
	}

	public DeletionReason getDeletionReason() {
		return deletionReason;
	}

	public String getOtherDeletionReason() {
		return otherDeletionReason;
	}
}
