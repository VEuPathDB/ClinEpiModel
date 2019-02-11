package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class SAfrica extends EpidemiologyStudy {


    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
    Map<String,String[]> map = new HashMap<String,String[]>();
    //    map.put("HouseholdsByCharacteristics_safrica", new String[] {"menu", "webservice"});
    return(map);
    }

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByRelativeVisits_safrica", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      //      map.put("ObservationsByRelativeVisits_safrica", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected String participantGraphAttributesTemplateName() {
      return("");
    }


    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      Map<String,String[]> scopeMap = new HashMap<String,String[]>();
      //      scopeMap.put("malaria_cat_compact", new String[] {"results"});
      //      scopeMap.put("malaria_cat_svg", new String[] {"record"});

      return(scopeMap);
    }


}
