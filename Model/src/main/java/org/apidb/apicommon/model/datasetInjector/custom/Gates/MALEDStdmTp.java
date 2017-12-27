package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class MALEDStdmTp extends EpidemiologyStudy {

    @Override
    protected String participantGraphAttributes() {
      return(getTemplateInstanceText("MALEDGraphAttributes"));
    }

    @Override
    protected Map<String,String> participantGraphAttributeNames() {
      Map<String,String> map = new HashMap<String,String>();
      map.put("allCompact", "demo_plot_allThree_compact");
      map.put("weightHeight", "demo_plot_weightheight");
      map.put("weightAge", "demo_plot_weightage");
      map.put("heightAge", "demo_plot_heightage");
      map.put("allThree", "demo_plot_allThree");
      map.put("length", "demo_plot_xlength");
      map.put("weight", "demo_plot_weight");

      return(map);
    }

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
