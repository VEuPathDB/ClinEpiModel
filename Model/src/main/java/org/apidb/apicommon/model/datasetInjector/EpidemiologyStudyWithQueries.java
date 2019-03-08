package org.apidb.apicommon.model.datasetInjector;

import org.apidb.apicommon.model.datasetInjector.EpidemiologyStudy;
import java.util.Map;
import java.util.HashMap;

public class EpidemiologyStudyWithQueries extends EpidemiologyStudy {


    @Override
    protected Map<String,String[]> householdQuestionTemplateNamesToScopes() {
    Map<String,String[]> map = new HashMap<String,String[]>();
    map.put("HouseholdsByMetadata", new String[] {"menu", "webservice"});
    return(map);
    }

    @Override
    protected Map<String,String[]> participantQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ParticipantsByRelativeVisits", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected Map<String,String[]> observationQuestionTemplateNamesToScopes() {
      Map<String,String[]> map = new HashMap<String,String[]>();
      map.put("ObservationsByRelativeVisits", new String[] {"menu", "webservice"});
      return(map);
    }

    @Override
    protected String participantGraphAttributesTemplateName() {
        return(getPropValue("participantGraphAttributesTemplateName"));
    }


    @Override
    protected Map<String,String[]> participantGraphAttributesToScopes() {
      String presenterId = getPropValue("presenterId");
      Map<String,String[]> scopeMap = new HashMap<String,String[]>();
      if(presenterId.equals("DS_05ea525fd3")){
          scopeMap.put("malaria_cat_compact", new String[] {"results"});
          scopeMap.put("malaria_cat_svg", new String[] {"record"});
      }
      return(scopeMap);
    }

  @Override
  public void injectTemplates() {
      String tblPrefix = "D" + getPropValue("datasetDigest");
      String presenterId = getPropValue("presenterId");
      String studyType = getPropValue("studyType");
      String cardQuestions = "UNION select '" + presenterId + "' as dataset_presenter_id, 'cardQuestions' as property, '{ ";
      
      super.injectTemplates();  

      boolean hasHouseholdQuestion = getPropValueAsBoolean("hasHouseholdQuestion");
      boolean hasObservationQuestion = getPropValueAsBoolean("hasObservationQuestion");
      String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
      String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
      String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);

      //every study has participants ... injectors predicated on this as will share ontology query

      //Inject the particiant metadata query
      injectTemplate("participant" + studyType + "Query");

      //Inject the filter params 
      injectTemplate("participantFilterParams");

      //Inject the filter param queries
      setPropValue("householdMultiFilterIdsQuoted", addQuotes(getPropValue("householdMultiFilterIds")));
      setPropValue("householdFilterExcludedIdsQuoted", addQuotes(getPropValue("householdFilterExcludedIds")));
      setPropValue("participantMultiFilterIdsQuoted", addQuotes(getPropValue("participantMultiFilterIds")));
      setPropValue("participantFilterExcludedIdsQuoted", addQuotes(getPropValue("participantFilterExcludedIds")));
      setPropValue("observationMultiFilterIdsQuoted", addQuotes(getPropValue("observationMultiFilterIds")));
      setPropValue("observationFilterExcludedIdsQuoted", addQuotes(getPropValue("observationFilterExcludedIds")));
      injectTemplate("participantFilterParamQueries");

      cardQuestions = cardQuestions + " \"participants\": \"ParticipantQuestions." + presenterId + "ParticipantsByRelativeVisits\"";

      //now to do observations
      if(hasObservationQuestion){
          //Inject the metadata query
          injectTemplate("observation" + studyType + "Query");
          
          //Inject the filter params .... note these use the ontology queries from particiants filters
          injectTemplate("observationFilterParams");
          injectTemplate("observationFilterParamQueries");

          cardQuestions = cardQuestions + ", \"observations\": \"ClinicalVisitQuestions." + presenterId + "ObservationsByRelativeVisits\"";
      }

      //and households
      if(hasHouseholdQuestion){
          //Inject the metadata query
          injectTemplate("householdMetadataQuery");
          
          //Inject the filter params .... note these use the ontology queries from particiants filters
          injectTemplate("householdFilterParams");
          injectTemplate("householdFilterParamQueries");

          cardQuestions = cardQuestions + ", \"households\": \"HouseholdQuestions." + presenterId + "HouseholdsByMetadata\"";
      }
      //lastly inject the cardQuestions
      cardQuestions = cardQuestions + " }' as value from dual";
      System.err.println("cardQuestionsSql=" + cardQuestions);
      setPropValue("cardQuestionsSql",cardQuestions);
      injectTemplate("injectDatasetQuestions");
  }

  @Override
  public String[][] getPropertiesDeclaration() {

      String[][] superDeclaration = super.getPropertiesDeclaration();

      String [][] declaration = {{"householdMultiFilterIds", ""},
                                 {"householdFilterExcludedIds", ""},
                                 {"participantMultiFilterIds", ""},
                                 {"participantFilterExcludedIds", ""},
                                 {"observationMultiFilterIds", ""},
                                 {"observationFilterExcludedIds", ""},
                                 {"studyAbbreviation", ""},
                                 {"studyType", ""},
                                 {"hasHouseholdQuestion", ""},
                                 {"hasObservationQuestion", ""},
                                 {"participantGraphAttributesTemplateName", ""},
                                 {"timeSourceId", ""}
      };

      return combinePropertiesDeclarations(superDeclaration, declaration);
  }

}
