package org.apidb.apicommon.model.datasetInjector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apidb.apicommon.datasetPresenter.ModelReference;
import org.apidb.apicommon.datasetPresenter.DatasetInjector;

public abstract class EpidemiologyStudy extends DatasetInjector {
  
    protected abstract Map<String,String> participantGraphAttributeNames();

    protected abstract String participantGraphAttributes();

    protected String addQuotes(String s) {
        List<String> split = Arrays.asList(s.split("\\s*,\\s*"));
        List<String> quoted = new ArrayList<>();

        for (String str : split) {
            quoted.add("'" + str + "'");
        }
        String result = String.join(",", quoted);

        return result;
    }

    protected void injectAttributeMetaQuery(String recordClassName, String targetName) {
        setPropValue("categoryIri", CATEGORY_IRI);
        setPropValue("recordClassName", recordClassName);
        setPropValue("targetName", targetName);
        injectTemplate("clinEpiAttributeMetaQuery");
    }


    protected String makeRecordClassName(String prefix) {
        String presenterId = getPropValue("presenterId");

        return(presenterId + prefix + "RecordClasses." + presenterId + prefix + "RecordClass");
    }

    public static final String CATEGORY_IRI = "http://edamontology.org/topic_3305";
    public static final String OBSERVATION_RECORD_CLASS_PREFIX = "Observation";
    public static final String PARTICIPANT_RECORD_CLASS_PREFIX = "Participant";
    public static final String HOUSEHOLD_RECORD_CLASS_PREFIX = "Household";


    


  @Override
  public void injectTemplates() {
      Map<String,String> participantGraphAttributeNames = participantGraphAttributeNames();

      for (Map.Entry<String, String> entry : participantGraphAttributeNames.entrySet()) {
        setPropValue(entry.getKey(), entry.getValue());
        System.out.println("entry = " + entry.getKey() + " " + entry.getValue());
      }
   
      String participantGraphAttributes = participantGraphAttributes();   

      setPropValue("participantGraphAttributes", participantGraphAttributes);

      String presenterId = getPropValue("presenterId");

      addModelReferences();

      String subProjectName = getPropValue("subProjectName");
      //      String datasetName = getDatasetName();
      String includeProjects = getPropValueAsBoolean("isPublic") ? "ClinEpiDB," + subProjectName : subProjectName;
      setPropValue("includeProjects", includeProjects);


      String tblPrefix = "D" + getPropValue("datasetDigest");
      setPropValue("tblPrefix", tblPrefix);

      boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");

      // TODO: how to handle optional tables??  probably just do these in a subclass?

      if(hasHouseholds) {
          String householdSourceIdsForHouseholdMemberTable = getPropValue("householdSourceIdsForHouseholdMemberTable");
          setPropValue("householdSourceIdsForHouseholdMemberTableQuote", addQuotes(householdSourceIdsForHouseholdMemberTable));

          injectTemplate("householdRecord");
          injectTemplate("householdRecordAttributeQueries");
          injectTemplate("householdRecordTableQueries");
          
          // Household SourceId Question
          injectTemplate("householdSourceIdQuestion");
          injectTemplate("householdSourceIdQuery");
          injectTemplate("householdSourceIdParam");

          injectTemplate("householdResultParam");
      }


      if(hasParticipants) {
          String householdSourceIdsIncludedInParticipantAttributes = getPropValue("householdSourceIdsIncludedInParticipantAttributes");
          String participantSourceIdsExcludedFromParticipantAttributes = getPropValue("participantSourceIdsExcludedFromParticipantAttributes");
          String observationSourceIdsForParticipantsObservationsTable  = getPropValue("observationSourceIdsForParticipantsObservationsTable");

          setPropValue("householdSourceIdsIncludedInParticipantAttributesQuote", addQuotes(householdSourceIdsIncludedInParticipantAttributes));
          setPropValue("participantSourceIdsExcludedFromParticipantAttributesQuote", addQuotes(participantSourceIdsExcludedFromParticipantAttributes));
          setPropValue("observationSourceIdsForParticipantsObservationsTableQuote", addQuotes(observationSourceIdsForParticipantsObservationsTable));

          injectTemplate("participantRecord");
          injectTemplate("participantRecordAttributeQueries");
          injectTemplate("participantRecordTableQueries");

          // Participant SourceId Question
          injectTemplate("participantSourceIdQuestion");
          injectTemplate("participantSourceIdQuery");
          injectTemplate("participantSourceIdParam");

          injectTemplate("participantResultParam");
      }

      if(hasObservations) {


          String householdSourceIdsIncludedInObservationAttributes = getPropValue("householdSourceIdsIncludedInObservationAttributes");
          String participantSourceIdsIncludedInObservationAttributes = getPropValue("participantSourceIdsIncludedInObservationAttributes");
          String hhAndPIncludedInObservationAttributes = householdSourceIdsIncludedInObservationAttributes + "," + participantSourceIdsIncludedInObservationAttributes;
          setPropValue("participantAndHouseholdSourceIdsIncludedInObservationAttributesQuote", addQuotes(hhAndPIncludedInObservationAttributes));

          // always include the participant name here
          participantSourceIdsIncludedInObservationAttributes = "name as parent_id, " + participantSourceIdsIncludedInObservationAttributes;

          injectTemplate("observationRecord");
          injectTemplate("observationRecordAttributeQueries");
          injectTemplate("observationRecordTableQueries");

          // Observation: SourceId Question
          injectTemplate("observationSourceIdQuestion");
          injectTemplate("observationSourceIdQuery");
          injectTemplate("observationSourceIdParam");

          injectTemplate("observationResultParam");
      }

      // Household->Participant and Participant->Household
      if(hasHouseholds && hasParticipants) {
          injectTemplate("householdsByParticipantQuestion");
          injectTemplate("householdsByParticipantQuery");

          injectTemplate("participantsByHouseholdsQuestion");
          injectTemplate("participantsByHouseholdsQuery");
      }

      // Participant->Observations and Observations->Participants
      if(hasParticipants && hasObservations) {
          injectTemplate("participantsByObservationsQuestion");
          injectTemplate("participantsByObservationsQuery");

          injectTemplate("observationsByParticipantsQuestion");
          injectTemplate("observationsByParticipantsQuery");
      }


      // Add categories for all of the model references
      for (ModelReference modelReference : getModelReferences()) {
          String categoryIri = modelReference.getCategoryIri();
          String targetName = modelReference.getTargetName();
          String recordClassName = modelReference.getRecordClassName();
          String targetType = modelReference.getTargetType().equals("question") ? "search" : modelReference.getTargetType();
          String[] scopes = modelReference.getScopes();

          setPropValue("targetType", targetType);
          setPropValue("targetName", targetName);
          setPropValue("recordClassName", recordClassName);
          setPropValue("categoryIri", categoryIri);          
          setPropValue("uniqueName", recordClassName + "." + targetName);

          setPropValue("scopes", String.join("\t", scopes));

          if(scopes != null && categoryIri != null) {
              injectTemplate("clinEpiCategory");
          }
      }


      String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
      String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
      String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);

      // Add meta attribute queries to categories / individuals
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdTables.HouseholdMembersColumnAttributes");
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdAttributes.HouseholdAttributesMeta");
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdTables.LightTrapColumnAttributes");
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantAttributes.ParticipantAttributesMeta");
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantTables.ObservationsColumnAttributes ");
      injectAttributeMetaQuery(observationRecordClass, presenterId + "ObservationAttributes.ObservationAttributesMeta");
  }

  @Override
  public void addModelReferences() {
      Map<String,String> participantGraphAttributeNames = participantGraphAttributeNames();

      boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");

      String presenterId = getPropValue("presenterId");
      
      String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
      String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
      String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);

      if(hasHouseholds) {
          addWdkReference(householdRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI);
          addWdkReference(householdRecordClass, "table", "HouseholdMembers", new String[]{"record"}, CATEGORY_IRI);

          addWdkReference(householdRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI);

          addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI); 
      }

      if(hasParticipants) {
          addWdkReference(participantRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI);
          addWdkReference(participantRecordClass, "table", "Observations", new String[]{"record"}, CATEGORY_IRI);
          // TODO Samples table of participant record page

          addWdkReference(participantRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI);

          // Add the Text Attributes
          for (Map.Entry<String, String> entry : participantGraphAttributeNames.entrySet()) {
              addWdkReference(participantRecordClass, "attribute", entry.getValue(), new String[]{"record","results"}, CATEGORY_IRI);
          }

          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI); 
      }

      if(hasObservations) {
          addWdkReference(observationRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI);
          // TODO Samples table of observation record page

          addWdkReference(observationRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI);

          addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationssBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI); 
      }

      if(hasHouseholds && hasParticipants) {
          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsByHouseholds", new String[]{"webservice"}, CATEGORY_IRI); 
          addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsByParticipants", new String[]{"webservice"}, CATEGORY_IRI); 
      }

      if(hasParticipants && hasObservations) {
          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsByObservations", new String[]{"webservice"}, CATEGORY_IRI); 
          addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationsByParticipants", new String[]{"webservice"}, CATEGORY_IRI); 
      }
  }


  @Override
  public String[][] getPropertiesDeclaration() {

      String [][] declaration = {
                                 {"isPublic", ""},
                                 {"hasHouseholdRecord", ""},
                                 {"hasObservationRecord", ""},
                                 {"hasParticipantRecord", ""},

                                 {"householdAttributesList", ""},
                                 {"householdSourceIdsForHouseholdMemberTable", ""},
                                 {"householdRecordOverview", ""},

                                 {"observationAttributesList", ""},
                                 {"observationRecordOverview", ""},
                                 {"householdSourceIdsIncludedInObservationAttributes", ""},
                                 {"participantSourceIdsIncludedInObservationAttributes", ""},

                                 {"participantAttributesList", ""},
                                 {"participantRecordOverview", ""},
                                 {"householdSourceIdsIncludedInParticipantAttributes", ""},
                                 {"participantSourceIdsExcludedFromParticipantAttributes", ""},
                                 {"observationSourceIdsForParticipantsObservationsTable", ""},
                                 {"observationSourceIdsToOrderParticipantsObservationsTable", ""},
      };

    return declaration;
  }


}
