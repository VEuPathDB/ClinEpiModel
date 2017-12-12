package org.apidb.apicommon.model.datasetInjector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apidb.apicommon.datasetPresenter.ModelReference;
import org.apidb.apicommon.datasetPresenter.DatasetInjector;

public class EpidemiologyStudy extends DatasetInjector {

    private String addQuotes(String s) {
        List<String> split = Arrays.asList(s.split("\\s*,\\s*"));
        List<String> quoted = new ArrayList<>();

        for (String str : split) {
            quoted.add("'" + str + "'");
        }
        String result = String.join(",", quoted);

        return result;
    }

  @Override
  public void injectTemplates() {

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

  }

  @Override
  public void addModelReferences() {

      String categoryIri = "http://edamontology.org/topic_3305";

      String presenterId = getPropValue("presenterId");

      String observationRecordClass = presenterId + "ObservationRecordClasses." + presenterId + "ObservationRecordClass";
      String participantRecordClass = presenterId + "ParticipantRecordClasses." + presenterId + "ParticipantRecordClass";
      String householdRecordClass = presenterId + "HouseholdRecordClasses." + presenterId + "HouseholdRecordClass";

      addWdkReference(householdRecordClass, "table", "Characteristics", new String[]{"record"}, categoryIri);
      addWdkReference(householdRecordClass, "table", "HouseholdMembers", new String[]{"record"}, categoryIri);

      addWdkReference(participantRecordClass, "table", "Characteristics", new String[]{"record"}, categoryIri);
      addWdkReference(participantRecordClass, "table", "Observations", new String[]{"record"}, categoryIri);
      // TODO Samples table of participant record page

      addWdkReference(observationRecordClass, "table", "Characteristics", new String[]{"record"}, categoryIri);
      // TODO Samples table of observation record page

      // Add the Text Attributes
      String participantGraphAttributeNames = getPropValue("participantGraphAttributeNames");
      String[] participantGraphsArray = participantGraphAttributeNames.split(",");
      for (int i = 0; i < participantGraphsArray.length; i++) {
          addWdkReference(participantRecordClass, "attribute", participantGraphsArray[i], new String[]{"record","results"}, categoryIri);
      }

      // By Source Id Questions
      addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsBySourceID", new String[]{"menu","webservice"}, categoryIri); 
      addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsBySourceID", new String[]{"menu","webservice"}, categoryIri); 
      addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationssBySourceID", new String[]{"menu","webservice"}, categoryIri); 

      addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationsByParticipants", new String[]{"webservice"}, categoryIri); 
      addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsByHouseholds", new String[]{"webservice"}, categoryIri); 
      addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsByObservations", new String[]{"webservice"}, categoryIri); 
      addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsByParticipants", new String[]{"webservice"}, categoryIri); 


      /**
      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "question", "ClinicalVisitQuestions.ClinicalVisitsByAllData"); 
      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "question", "ClinicalVisitQuestions.ClinicalVisitsByRelativeVisits"); 

      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "question", "ParticipantQuestions.ParticipantsByAllData"); 
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "question", "ParticipantQuestions.ParticipantsByRelativeVisits"); 

      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "question", "ClinicalVisitQuestions.ClinicalVisitsByRelativeVisits_maled"); 
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "question", "ParticipantQuestions.ParticipantsByRelativeVisits_maled"); 

      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "table", "Visits");
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "table", "Samples");

      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "table", "Samples");


      **/
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
                                 {"participantGraphAttributes", ""},
                                 {"participantGraphAttributeNames", ""},
                                 {"householdSourceIdsIncludedInParticipantAttributes", ""},
                                 {"participantSourceIdsExcludedFromParticipantAttributes", ""},
                                 {"observationSourceIdsForParticipantsObservationsTable", ""},
                                 {"observationSourceIdsToOrderParticipantsObservationsTable", ""},
      };

    return declaration;
  }


}
