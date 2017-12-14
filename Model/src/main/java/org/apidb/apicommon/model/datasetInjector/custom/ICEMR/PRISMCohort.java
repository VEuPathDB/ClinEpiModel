package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudyWithLightTraps;

public class PRISMCohort extends EpidemiologyStudyWithLightTraps {

    @Override
    public void addModelReferences() {
        super.addModelReferences();

        String presenterId = getPropValue("presenterId");

        String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
        String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
        String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);
        String lightTrapRecordClass = makeRecordClassName(LIGHT_TRAP_RECORD_CLASS_PREFIX);

        //        addWdkReference(participantRecordClass, "question", "ParticipantQuestions.ParticipantsByAllData", new String[]{"menu", "webservice"}, CATEGORY_IRI);
        addWdkReference(participantRecordClass, "question", "ParticipantQuestions.ParticipantsByRelativeVisits_prism", new String[]{"menu", "webservice"}, CATEGORY_IRI);
        addWdkReference(lightTrapRecordClass, "question", "LighttrapQuestions.CollectionsByCharacteristics_prism", new String[]{"menu", "webservice"}, CATEGORY_IRI);
        addWdkReference(householdRecordClass, "question", "HouseholdQuestions.HouseholdsByCharacteristics_prism", new String[]{"menu", "webservice"}, CATEGORY_IRI);
        addWdkReference(observationRecordClass, "question", "ClinicalVisitQuestions.ClinicalVisitsByRelativeVisits_prism", new String[]{"menu", "webservice"}, CATEGORY_IRI);

    }


}
