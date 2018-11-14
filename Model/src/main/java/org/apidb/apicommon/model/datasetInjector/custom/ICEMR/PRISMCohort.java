package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudyWithLightTraps;
import java.util.HashMap;
import java.util.Map;

public class PRISMCohort extends EpidemiologyStudyWithLightTraps {


    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("HouseholdsByCharacteristics_prism", new String[] {"menu", "webservice"});
      return(map);
    }
    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      //      map.put("ParticipantsByAllData", new String[] {"menu", "webservice"});
      map.put("ParticipantsByRelativeVisits_prism", new String[] {"menu", "webservice"});

      return(map);
    }
    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ClinicalVisitsByRelativeVisits_prism", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> lightTrapQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("CollectionsByCharacteristics_prism", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected String participantGraphAttributesTemplateName() {
      return("PRISMCaseControlGraphAttributes");
    }

    @Override 
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      Map<String,String[]> scopeMap = new HashMap<String,String[]>();
      scopeMap.put("malaria_cat_compact", new String[] {"results"});
      scopeMap.put("malaria_cat_svg", new String[] {"record"});

      return(scopeMap);
    }


}
