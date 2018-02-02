package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class GEMS1CaseControl extends EpidemiologyStudy {

    @Override
    protected String participantGraphAttributes(){
      return("");
    }

    @Override
    protected Map<String,String> participantGraphAttributeNames(){
      return(new HashMap<String,String>());
    }

    @Override
    protected Map<String,String[]> participantGraphAttributeScopes() {
      return(new HashMap<String,String[]>());
    }

    @Override
    public void addModelReferences() {
        super.addModelReferences();

        String presenterId = getPropValue("presenterId");

        String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);

        addWdkReference(participantRecordClass, "question", "ParticipantQuestions.ParticipantsByCaseControlVisits_gems", new String[]{"menu", "webservice"}, CATEGORY_IRI);
    }


}
