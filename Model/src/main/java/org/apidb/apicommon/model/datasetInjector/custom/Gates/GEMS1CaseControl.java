package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;

public class GEMS1CaseControl extends EpidemiologyStudy {

    @Override
    public void addModelReferences() {
        super.addModelReferences();

        String presenterId = getPropValue("presenterId");

        String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);

        addWdkReference(participantRecordClass, "question", "ParticipantQuestions.ParticipantsByCaseControlVisits_gems", new String[]{"menu", "webservice"}, CATEGORY_IRI);
        addWdkReference(participantRecordClass, "question", "ParticipantQuestions.ParticipantsByCaseControlVisitsShort_gems", new String[]{"menu", "webservice"}, CATEGORY_IRI);
    }


}
