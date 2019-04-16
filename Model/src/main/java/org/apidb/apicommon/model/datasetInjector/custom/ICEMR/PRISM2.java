package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.BasicEpiStudy;
import java.util.HashMap;
import java.util.Map;

public class PRISM2 extends BasicEpiStudy {

    @Override
    protected void setStudySpecificProperties(){
        setPropValue("visitMinDate","2017-09-01");
        setPropValue("visitMaxDate","2019-12-31");
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
