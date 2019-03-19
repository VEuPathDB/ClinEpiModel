package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import org.apidb.apicommon.model.datasetInjector.BasicEpiStudy;
import java.util.Map;
import java.util.HashMap;

public class GEMS1aCaseControl extends BasicEpiStudy {

    @Override
    protected void setStudySpecificProperties(){
        setPropValue("injectParams","false");
    }  

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByCaseControlVisits_gems1a", new String[] {"menu", "webservice"});

      return(map);
    }

}
