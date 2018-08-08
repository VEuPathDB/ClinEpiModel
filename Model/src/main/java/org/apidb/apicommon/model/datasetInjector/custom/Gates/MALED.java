package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.HashMap;

public class MALED extends EpidemiologyStudy {


    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
      return(new HashMap<String,String[]>());
    }

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByRelativeVisits_maled", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ClinicalVisitsByRelativeVisits_maled", new String[] {"menu", "webservice"});
      return(map);
    }


    @Override
    protected String participantGraphAttributesTemplateName() {
        return("MALEDGraphAttributes");
    }

    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      Map<String,String[]> scopeMap = new LinkedHashMap<String,String[]>();
      scopeMap.put("demo_plot_allThree_compact", new String[] {"results"});
      scopeMap.put("demo_plot_allThree", new String[] {"record"});
      scopeMap.put("demo_plot_weightheight", new String[] {"record"});
      scopeMap.put("demo_plot_weightage", new String[] {"record"});
      scopeMap.put("demo_plot_heightage", new String[] {"record"});
      scopeMap.put("demo_plot_xlength", new String[] {"record"});
      scopeMap.put("demo_plot_weight", new String[] {"record"});

      return(scopeMap);
    }

}
