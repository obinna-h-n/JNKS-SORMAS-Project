package de.symeda.sormas.api.caze;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.user.UserRole;

public class CaseCriteria implements Serializable {

	private static final long serialVersionUID = 5114202107622217837L;

	private UserRole reportingUserRole;
	private Disease disease;
	private CaseOutcome outcome;
	private DistrictReferenceDto district;
	private Date newCaseDateFrom;
	private Date newCaseDateTo;

	public CaseCriteria reportingUserHasRole(UserRole reportingUserRole) {
		this.reportingUserRole = reportingUserRole;
		return this;
	}

	public CaseCriteria outcomeEquals(CaseOutcome outcome) {
		this.outcome = outcome;
		return this;
	}
	
	public CaseCriteria diseaseEquals(Disease disease) {
		this.disease = disease;
		return this;
	}
	
	public CaseCriteria districtEquals(DistrictReferenceDto district) {
		this.district = district;
		return this;
	}
	
	public CaseCriteria newCaseDateBetween(Date newCaseDateFrom, Date newCaseDateTo) {
		this.newCaseDateFrom = newCaseDateFrom;
		this.newCaseDateTo = newCaseDateTo;
		return this;
	}
	
	public UserRole getReportingUserRole() {
		return reportingUserRole;
	}

	public CaseOutcome getOutcome( ){
		return outcome;
	}
	
	public Disease getDisease() {
		return disease;
	}
	
	public DistrictReferenceDto getDistrict() {
		return district;
	}

	public Date getNewCaseDateFrom() {
		return newCaseDateFrom;
	}

	public Date getNewCaseDateTo() {
		return newCaseDateTo;
	}
	
}
