package org.apidb.apicommon.model.datasetInjector.custom.ICEMR;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudyWithLightTraps;
import java.util.HashMap;
import java.util.Map;

public class PRISMCohort extends EpidemiologyStudyWithLightTraps {

    @Override
    protected void setStudySpecificProperties(){
        setPropValue("visitMinDate","2011-07-01");
        setPropValue("visitMaxDate","2017-07-31");
    }  

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
    protected Map<String,String[]> sampleQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      boolean hasSampleQuestion = getPropValueAsBoolean("hasSampleQuestion");
      boolean hasSamples = getPropValueAsBoolean("hasSampleRecord");
      String studyType = getPropValue("studyType");
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
