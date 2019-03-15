package org.apidb.apicommon.model.datasetInjector;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class BasicEpiStudy extends EpidemiologyStudy {

    @Override
    protected void setStudySpecificProperties(){}  

    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
    Map<String,String[]> map = new HashMap<String,String[]>();
    boolean hasHouseholdQuestion = getPropValueAsBoolean("hasHouseholdQuestion");
    boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
    String studyType = getPropValue("studyType");
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
          if(studyType.equals("Longitudinal") || studyType.equals("Survey")){
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
    protected String participantGraphAttributesTemplateName() {
        return(getPropValue("participantGraphAttributesTemplate"));
    }


    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      String presenterId = getPropValue("presenterId");
      Map<String,String[]> scopeMap = new HashMap<String,String[]>();
      //Add statement here for each study with graphs
      if(presenterId.equals("DS_05ea525fd3")){
          scopeMap.put("malaria_cat_compact", new String[] {"results"});
          scopeMap.put("malaria_cat_svg", new String[] {"record"});
      }
      if(presenterId.equals("DS_3dbf92dc05")){
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
      }
      return(scopeMap);
    }

}
