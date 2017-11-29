package org.apidb.apicommon.model.datasetInjector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

      //String projectName = getPropValue("projectName");
      //String datasetName = getDatasetName();
      //String includeProjects = getPropValueAsBoolean("isPublic") ? "ClinEpiDB," + projectName : projectName;

      boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");

      if(hasHouseholds) {

          String householdSourceIdsForHouseholdMemberTable = getPropValue("householdSourceIdsForHouseholdMemberTable");
          setPropValue("householdSourceIdsForHouseholdMemberTableQuote", addQuotes(householdSourceIdsForHouseholdMemberTable));

          injectTemplate("householdRecord");
          injectTemplate("householdRecordAttributeQueries");
          injectTemplate("householdRecordTableQueries");

          //TODO inject metadata filter question

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

          // TODO:  model ref for visit table and sample table if has those things
          //TODO inject metadata filter question
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

          // model ref for visit table and sample table if has those things
          // TODO inject metadata filter question
      }


      if(hasHouseholds && hasParticipants) {
          // inject transform question
          // inject answer param
      }
      if(hasHouseholds && hasObservations) {
          // inject transform question
          // inject answer param
      }
      if(hasParticipants && hasObservations) {
          // inject transform question
          // inject answer param
      }


  }

  @Override
  public void addModelReferences() {
      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "question", "ClinicalVisitQuestions.ClinicalVisitsByAllData"); 
      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "question", "ClinicalVisitQuestions.ClinicalVisitBySourceID"); 
      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "question", "ClinicalVisitQuestions.ClinicalVisitsByRelativeVisits"); 
      addWdkReference("DwellingRecordClasses.DwellingRecordClass", "question", "DwellingQuestions.DwellingsBySourceID"); 
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "question", "ParticipantQuestions.ParticipantsByAllData"); 
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "question", "ParticipantQuestions.ParticipantsByRelativeVisits"); 
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "question", "ParticipantQuestions.ParticipantsByClinicalVisit"); 
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "question", "ParticipantQuestions.ParticipantsBySourceID"); 
      /* MAL-ED */
      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "question", "ClinicalVisitQuestions.ClinicalVisitsByRelativeVisits_maled"); 
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "question", "ParticipantQuestions.ParticipantsByRelativeVisits_maled"); 

      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "table", "Visits");
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "table", "Samples");
      addWdkReference("ParticipantRecordClasses.ParticipantRecordClass", "table", "Characteristics");
      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "table", "Samples");
      addWdkReference("ClinicalVisitRecordClasses.ClinicalVisitRecordClass", "table", "Characteristics");
      addWdkReference("DwellingRecordClasses.DwellingRecordClass", "table", "Characteristics");
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
