/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2018 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.app.util;

import java.util.ArrayList;
import java.util.List;

import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.app.backend.common.DatabaseHelper;
import de.symeda.sormas.app.backend.facility.Facility;
import de.symeda.sormas.app.backend.infrastructure.PointOfEntry;
import de.symeda.sormas.app.backend.region.Community;
import de.symeda.sormas.app.backend.region.District;
import de.symeda.sormas.app.backend.region.Region;
import de.symeda.sormas.app.component.Item;
import de.symeda.sormas.app.component.controls.ControlPropertyEditField;
import de.symeda.sormas.app.component.controls.ControlPropertyField;
import de.symeda.sormas.app.component.controls.ControlSpinnerField;
import de.symeda.sormas.app.component.controls.ValueChangeListener;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static de.symeda.sormas.app.util.DataUtils.toItems;

public final class InfrastructureHelper {

    public static List<Item> loadRegions() {
        return toItems(DatabaseHelper.getRegionDao().queryForAll(Region.NAME, true));
    }

    public static List<Item> loadDistricts(Region region) {
        return DataUtils.toItems(region != null
                ? DatabaseHelper.getDistrictDao().getByRegion(region)
                : new ArrayList<>(), true);
    }

    public static List<Item> loadCommunities(District district) {
        return toItems(district != null
                ? DatabaseHelper.getCommunityDao().getByDistrict(district)
                : new ArrayList<>(), true);
    }

    public static List<Item> loadFacilities(District district, Community community) {
        return toItems(community != null
                ? DatabaseHelper.getFacilityDao().getHealthFacilitiesByCommunity(community, true, true)
                : district != null
                ? DatabaseHelper.getFacilityDao().getHealthFacilitiesByDistrict(district, true, true)
                : new ArrayList<>(), true);
    }

    public static List<Item> loadPointsOfEntry(District district) {
        return toItems(district != null
                ? DatabaseHelper.getPointOfEntryDao().getByDistrict(district, true)
                : new ArrayList<>(), true);
    }

    public static void initializeRegionFields(final ControlSpinnerField regionField, List<Item> initialRegions,
                                              final ControlSpinnerField districtField, List<Item> initialDistricts,
                                              final ControlSpinnerField communityField, List<Item> initialCommunities) {
        regionField.initializeSpinner(initialRegions, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                Region selectedRegion = (Region) field.getValue();
                if (selectedRegion != null) {
                    districtField.setSpinnerData(toItems(DatabaseHelper.getDistrictDao().getByRegion(selectedRegion)), districtField.getValue());
                } else {
                    districtField.setSpinnerData(null);
                }
            }
        });

        districtField.initializeSpinner(initialDistricts, new ValueChangeListener() {
            @Override
            public void onChange(ControlPropertyField field) {
                District selectedDistrict = (District) field.getValue();
                if (selectedDistrict != null) {
                    communityField.setSpinnerData(toItems(DatabaseHelper.getCommunityDao().getByDistrict(selectedDistrict)), communityField.getValue());
                } else {
                    communityField.setSpinnerData(null);
                }
            }
        });

        communityField.initializeSpinner(initialCommunities);
    }

    public static void initializeFacilityFields(final ControlSpinnerField regionField, List<Item> regions,
                                                final ControlSpinnerField districtField, List<Item> districts,
                                                final ControlSpinnerField communityField, List<Item> communities,
                                                final ControlSpinnerField facilityField, List<Item> facilities) {
        initializeFacilityFields(regionField, regions, districtField, districts, communityField, communities, facilityField, facilities, null, null);
    }

    public static void initializeFacilityFields(final ControlSpinnerField regionField, List<Item> regions,
                                                final ControlSpinnerField districtField, List<Item> districts,
                                                final ControlSpinnerField communityField, List<Item> communities,
                                                final ControlSpinnerField facilityField, List<Item> facilities,
                                                final ControlSpinnerField pointOfEntryField, List<Item> pointsOfEntry) {

        regionField.initializeSpinner(regions, field -> {
            Region selectedRegion = (Region) field.getValue();
            if (selectedRegion != null) {
                districtField.setSpinnerData(loadDistricts(selectedRegion), districtField.getValue());
            } else {
                districtField.setSpinnerData(null);
            }
        });

        districtField.initializeSpinner(districts, field -> {
            District selectedDistrict = (District) field.getValue();
            if (selectedDistrict != null) {
                communityField.setSpinnerData(loadCommunities(selectedDistrict), communityField.getValue());
                facilityField.setSpinnerData(loadFacilities(selectedDistrict, null), facilityField.getValue());
                if (pointOfEntryField != null) {
                    pointOfEntryField.setSpinnerData(loadPointsOfEntry(selectedDistrict), pointOfEntryField.getValue());
                }
            } else {
                communityField.setSpinnerData(null);
                facilityField.setSpinnerData(null);
                if (pointOfEntryField != null) {
                    pointOfEntryField.setSpinnerData(null);
                }
            }
        });

        communityField.initializeSpinner(communities, field -> {
            Community selectedCommunity = (Community) field.getValue();
            if (selectedCommunity != null) {
                facilityField.setSpinnerData(loadFacilities(null, selectedCommunity));
            } else if (districtField.getValue() != null) {
                facilityField.setSpinnerData(loadFacilities((District) districtField.getValue(), null));
            } else {
                facilityField.setSpinnerData(null);
            }
        });

        facilityField.initializeSpinner(facilities);

        if (pointOfEntryField != null) {
            pointOfEntryField.initializeSpinner(pointsOfEntry);
        }
    }

    /**
     * Hide facilityDetails when no static health facility is selected and adjust the caption based on
     * the selected static health facility.
     */
    public static void initializeHealthFacilityDetailsFieldVisibility(final ControlPropertyField healthFacilityField, final ControlPropertyField healthFacilityDetailsField) {
        setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField);
        healthFacilityField.addValueChangedListener(field -> setHealthFacilityDetailsFieldVisibility(healthFacilityField, healthFacilityDetailsField));
    }

    public static void setHealthFacilityDetailsFieldVisibility(ControlPropertyField healthFacilityField, ControlPropertyField healthFacilityDetailsField) {
        Facility selectedFacility = (Facility) healthFacilityField.getValue();

        if (selectedFacility != null) {
            boolean otherHealthFacility = selectedFacility.getUuid().equals(FacilityDto.OTHER_FACILITY_UUID);
            boolean noneHealthFacility = selectedFacility.getUuid().equals(FacilityDto.NONE_FACILITY_UUID);

            if (otherHealthFacility) {
                healthFacilityDetailsField.setVisibility(VISIBLE);
                String caption = I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.HEALTH_FACILITY_DETAILS);
                healthFacilityDetailsField.setCaption(caption);
                if (healthFacilityDetailsField instanceof ControlPropertyEditField) {
                    ((ControlPropertyEditField) healthFacilityDetailsField).setHint(caption);
                }
            } else if (noneHealthFacility) {
                healthFacilityDetailsField.setVisibility(VISIBLE);
                String caption = I18nProperties.getPrefixCaption(CaseDataDto.I18N_PREFIX, CaseDataDto.NONE_HEALTH_FACILITY_DETAILS);
                healthFacilityDetailsField.setCaption(caption);
                if (healthFacilityDetailsField instanceof ControlPropertyEditField) {
                    ((ControlPropertyEditField) healthFacilityDetailsField).setHint(caption);
                }
            } else {
                healthFacilityDetailsField.setVisibility(GONE);
            }
        } else {
            healthFacilityDetailsField.setVisibility(GONE);
        }
    }

    public static void initializePointOfEntryDetailsFieldVisibility(final ControlPropertyField pointOfEntryField, final ControlPropertyField pointOfEntryDetailsField) {
        setPointOfEntryDetailsFieldVisibility(pointOfEntryField, pointOfEntryDetailsField);
        pointOfEntryField.addValueChangedListener(e -> setPointOfEntryDetailsFieldVisibility(pointOfEntryField, pointOfEntryDetailsField));
    }

    public static void setPointOfEntryDetailsFieldVisibility(final ControlPropertyField pointOfEntryField, final ControlPropertyField pointOfEntryDetailsField) {
        PointOfEntry selectedPointOfEntry = (PointOfEntry) pointOfEntryField.getValue();
        if (selectedPointOfEntry != null) {
            pointOfEntryDetailsField.setVisibility(selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_AIRPORT_UUID) || selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_SEAPORT_UUID) ||
                    selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_GROUND_CROSSING_UUID) || selectedPointOfEntry.getUuid().equals(PointOfEntryDto.OTHER_POE_UUID) ? VISIBLE : GONE);
        } else {
            pointOfEntryDetailsField.setVisibility(GONE);
        }
    }

}