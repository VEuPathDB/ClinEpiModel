package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class India extends EpidemiologyStudy {


    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("HouseholdsByCharacteristics_india", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByRelativeVisits_india", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ObservationsByRelativeVisits_india", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected String participantGraphAttributesTemplateName() {
      return("");
    }


    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      return(new HashMap<String,String[]>());
    }


}
