package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class Template extends EpidemiologyStudy {


    @Override
    protected void setStudySpecificProperties(){}  

    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
    Map<String,String[]> map = new HashMap<String,String[]>();
    //    map.put("HouseholdsByCharacteristics_template", new String[] {"menu", "webservice"});
    return(map);
    }

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByRelativeVisits_template", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      //      map.put("ObservationsByRelativeVisits_template", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> sampleQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      boolean hasSampleQuestion = getPropValueAsBoolean("hasSampleQuestion");
      boolean hasSamples = getPropValueAsBoolean("hasSampleRecord");
      //String studyType = getPropValue("studyType");
      if(hasSampleQuestion && hasSamples){
          //          if(studyType.equals("Longitudinal")){
          //              map.put("SamplesByMetadata" + studyType, new String[] {"menu", "webservice"});
          //          }else{
          map.put("SamplesByMetadata", new String[] {"menu", "webservice"});
          //          }
      }
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
