package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.BasicEpiStudyWithLightTraps;
import java.util.HashMap;
import java.util.Map;

public class PRISM2 extends BasicEpiStudyWithLightTraps {

    @Override
    protected void setStudySpecificProperties(){
        setPropValue("visitMinDate","2017-09-01");
        setPropValue("visitMaxDate","2019-12-31");
    }  

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByMetadata_prism2", new String[] {"menu", "webservice"});

      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ObservationsByMetadata_prism2", new String[] {"menu", "webservice"});

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
