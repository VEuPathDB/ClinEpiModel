package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import org.apidb.apicommon.model.datasetInjector.BasicEpiStudy;
import java.util.Map;
import java.util.HashMap;

public class GEMS1CaseControl extends BasicEpiStudy {

    @Override
    protected void setStudySpecificProperties(){
        setPropValue("studyArmIsVisible","false");
    }  

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByCaseControlVisits_gems", new String[] {"menu", "webservice"});
      return(map);
    }

}
