package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class IndiaFS extends EpidemiologyStudy {


    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
    Map<String,String[]> map = new HashMap<String,String[]>();
    //    map.put("HouseholdsByCharacteristicsCS_india", new String[] {"menu", "webservice"});
    return(map);
    }

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsCrossSectional_indiaFS", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      //      map.put("ObservationsXSectional_india", new String[] {"menu", "webservice"});
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
