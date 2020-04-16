package org.apidb.apicommon.model.datasetInjector.custom.Gates;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apidb.apicommon.model.datasetInjector.BasicEpiStudy;

public class COVID19hcqnrct extends BasicEpiStudy {

    @Override
    protected String participantGraphAttributesTemplateName() {
        return("COVID19hcqnrctGraphAttributes");
    }

    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      Map<String,String[]> scopeMap = new LinkedHashMap<String,String[]>();
      scopeMap.put("demo_plot_ct_value_compact", new String[] {"results"});
      scopeMap.put("demo_plot_ct_value", new String[] {"record"});

      return(scopeMap);
    }

}
