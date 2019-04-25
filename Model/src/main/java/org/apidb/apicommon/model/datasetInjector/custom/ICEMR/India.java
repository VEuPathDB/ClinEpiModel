package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.BasicEpiStudy;
import java.util.Map;
import java.util.HashMap;

public class India extends BasicEpiStudy {

    @Override
    protected String participantGraphAttributesTemplateName() {
      return("IndiaICEMRGraphAttributes");
    }

    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      Map<String,String[]> scopeMap = new HashMap<String,String[]>();
      scopeMap.put("malaria_cat_compact", new String[] {"results"});
      scopeMap.put("malaria_cat_svg", new String[] {"record"});
      return(scopeMap);
    }


}
