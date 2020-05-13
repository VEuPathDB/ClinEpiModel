package org.apidb.apicommon.model.datasetInjector;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class BasicEpiStudy extends EpidemiologyStudy {

    @Override
    protected void setStudySpecificProperties(){
    }  

    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
    Map<String,String[]> map = new HashMap<String,String[]>();
    boolean hasHouseholdQuestion = getPropValueAsBoolean("hasHouseholdQuestion");
    boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
    //String studyType = getPropValue("studyType");
    if(hasHouseholdQuestion && hasHouseholds){
        map.put("HouseholdsByMetadata", new String[] {"menu", "webservice"});
    }
    return(map);
    }

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasParticipantQuestion = getPropValueAsBoolean("hasParticipantQuestion");
      String studyType = getPropValue("studyType");
      if(hasParticipantQuestion && hasParticipants){
          if(studyType.equals("Longitudinal") || studyType.equals("Survey") || studyType.equals("CaseControl")){
              map.put("ParticipantsByMetadata" + studyType, new String[] {"menu", "webservice"});
          }else{
              map.put("ParticipantsByMetadata", new String[] {"menu", "webservice"});
          }
      }
      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      boolean hasObservationQuestion = getPropValueAsBoolean("hasObservationQuestion");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
      String studyType = getPropValue("studyType");
      if(hasObservationQuestion && hasObservations){
          if(studyType.equals("Longitudinal")){
              map.put("ObservationsByMetadata" + studyType, new String[] {"menu", "webservice"});
          }else{
              map.put("ObservationsByMetadata", new String[] {"menu", "webservice"});
          }
      }
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
        return("");
    }


    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      Map<String,String[]> scopeMap = new HashMap<String,String[]>();
      return(scopeMap);
    }

}
