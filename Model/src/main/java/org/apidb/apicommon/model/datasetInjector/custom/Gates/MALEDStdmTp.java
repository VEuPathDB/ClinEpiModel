package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;

public class MALEDStdmTp extends EpidemiologyStudy {

    @Override
    public void addModelReferences() {
        super.addModelReferences();

        String presenterId = getPropValue("presenterId");

        String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
        String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);

        addWdkReference(participantRecordClass, "question", "ParticipantQuestions.ParticipantsByRelativeVisits_maled", new String[]{"menu", "webservice"}, CATEGORY_IRI);
        addWdkReference(observationRecordClass, "question", "ClinicalVisitQuestions.ClinicalVisitsByRelativeVisits_maled", new String[]{"menu", "webservice"}, CATEGORY_IRI);

    }


}
