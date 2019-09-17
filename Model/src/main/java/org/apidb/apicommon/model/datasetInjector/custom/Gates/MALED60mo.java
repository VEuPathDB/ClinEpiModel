package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apidb.apicommon.model.datasetInjector.BasicEpiStudy;

public class MALED60mo extends BasicEpiStudy {

    @Override
    protected String participantGraphAttributesTemplateName() {
        return("MALEDGraphAttributes");
    }

    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      Map<String,String[]> scopeMap = new LinkedHashMap<String,String[]>();
      scopeMap.put("demo_plot_allThree", new String[] {"record"});
      scopeMap.put("demo_plot_heightage", new String[] {"record"});
      scopeMap.put("demo_plot_weightheight", new String[] {"record"});
      scopeMap.put("demo_plot_weightage", new String[] {"record"});
      scopeMap.put("demo_plot_xlength", new String[] {"record"});
      scopeMap.put("demo_plot_weight", new String[] {"record"});
      scopeMap.put("demo_plot_allThree_compact", new String[] {"results"});
      scopeMap.put("demo_plot_heightage_compact", new String[] {"results"});
      scopeMap.put("demo_plot_weightheight_compact", new String[] {"results"});
      scopeMap.put("demo_plot_weightage_compact", new String[] {"results"});
      scopeMap.put("demo_plot_xlength_compact", new String[] {"results"});
      scopeMap.put("demo_plot_weight_compact", new String[] {"results"});

      return(scopeMap);
    }

}
