package org.apidb.apicommon.model.datasetInjector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apidb.apicommon.datasetPresenter.ModelReference;
import org.apidb.apicommon.datasetPresenter.DatasetInjector;

public abstract class EpidemiologyStudy extends DatasetInjector {

    protected abstract Map<String,String[]> householdQuestionTemplateNamesToScopes();
    protected abstract Map<String,String[]> participantQuestionTemplateNamesToScopes();
    protected abstract Map<String,String[]> observationQuestionTemplateNamesToScopes();

    protected abstract String participantGraphAttributesTemplateName();  
    protected abstract Map<String,String[]> participantGraphAttributesToScopes();

    protected String participantGraphAttributesString() {
        String participantGraphAttributesTemplateName = participantGraphAttributesTemplateName();
        if(participantGraphAttributesTemplateName != null && !participantGraphAttributesTemplateName.equals("")) {
            return(getTemplateInstanceText(participantGraphAttributesTemplateName));
        }
        return("");
    }


    // Subclasses may want to override this
    protected String extraHouseholdTables() {
        return "";
    }
    // Subclasses may want to override this
    protected String extraHouseholdTableQueries() {
        return "";
    }

    protected String addQuotes(String s) {
        List<String> quoted = splitStringAndAddQuotesAsList(s);
        String result = String.join(",", quoted);

        return result;
    }

    protected List<String> splitStringAndAddQuotesAsList(String s) {
        List<String> split = Arrays.asList(s.split("\\s*,\\s*"));
        List<String> quoted = new ArrayList<>();

        for (String str : split) {
            quoted.add("'" + str + "'");
        }

        return quoted;
    }

    protected void injectAttributeMetaQuery(String recordClassName, String targetName, String category){

	if(category == null || category.equals("")){
            category = CATEGORY_IRI;
	}

        setPropValue("categoryIri", category);
        setPropValue("recordClassName", recordClassName);
        setPropValue("targetName", targetName);
        injectTemplate("clinEpiAttributeMetaQuery");
    }


    protected String makeRecordClassName(String prefix) {
        String presenterId = getPropValue("presenterId");

        return(presenterId + prefix + "RecordClasses." + presenterId + prefix + "RecordClass");
    }


    protected String propertySourceIdSubquery(String s) {
        List<String> quoted = splitStringAndAddQuotesAsList(s);

        List<String> selectStatements = new ArrayList<>(); 
        
        for(int i = 0; i < quoted.size(); i++) { 
            String q = quoted.get(i);
            String stmt = "select " + q + " as property_source_id,  " + i + " as order_num from dual";
            selectStatements.add(stmt); 
        } 

        return "(" + String.join("\nUNION\n", selectStatements) + ")";
    }


    public static final String CATEGORY_IRI = "http://edamontology.org/topic_3305";
    public static final String OBSERVATION_RECORD_CLASS_PREFIX = "Observation";
    public static final String PARTICIPANT_RECORD_CLASS_PREFIX = "Participant";
    public static final String HOUSEHOLD_RECORD_CLASS_PREFIX = "Household";


  @Override
  public void injectTemplates() {

      for (Map.Entry<String, String[]> entry : participantGraphAttributesToScopes().entrySet()) {
        setPropValue(entry.getKey(), entry.getKey());
      }
   
      setPropValue("participantGraphAttributes", participantGraphAttributesString());

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
      boolean hasSamples = getPropValueAsBoolean("hasSamples");
      boolean hasMicros = getPropValueAsBoolean("hasMicros");

      setPropValue("!hasObservationRecord", Boolean.toString(!hasObservations));

      // TODO: how to handle optional tables??  probably just do these in a subclass?

      if(hasHouseholds) {
          String householdSourceIdsForHouseholdMemberTable = getPropValue("householdSourceIdsForHouseholdMemberTable");
          setPropValue("householdSourceIdsForHouseholdMemberTableSubquery", propertySourceIdSubquery(householdSourceIdsForHouseholdMemberTable));

          setPropValue("extraHouseholdTables", this.extraHouseholdTables());
          setPropValue("extraHouseholdTableQueries", this.extraHouseholdTableQueries());



          injectTemplate("householdRecord");
          injectTemplate("householdRecordAttributeQueries");
          injectTemplate("householdRecordTableQueries");
          
          // Household SourceId Question
          injectTemplate("householdSourceIdQuestion");
          injectTemplate("householdSourceIdQuery");
          injectTemplate("householdSourceIdParam");

          injectTemplate("householdResultParam");

          for(String key : householdQuestionTemplateNamesToScopes().keySet()) {
              setPropValue("householdQuestionName", key);
              setPropValue("householdQuestionFull", getTemplateInstanceText(key));
              injectTemplate("householdsByDataset");
          }
      }


      if(hasParticipants) {
          String householdSourceIdsIncludedInParticipantAttributes = getPropValue("householdSourceIdsIncludedInParticipantAttributes");
          String participantSourceIdsExcludedFromParticipantAttributes = getPropValue("participantSourceIdsExcludedFromParticipantAttributes");
          String observationSourceIdsForParticipantsObservationsTable  = getPropValue("observationSourceIdsForParticipantsObservationsTable");

          setPropValue("householdSourceIdsIncludedInParticipantAttributesQuote", addQuotes(householdSourceIdsIncludedInParticipantAttributes));
          setPropValue("participantSourceIdsExcludedFromParticipantAttributesQuote", addQuotes(participantSourceIdsExcludedFromParticipantAttributes));
          setPropValue("observationSourceIdsForParticipantsObservationsTableSubquery", propertySourceIdSubquery(observationSourceIdsForParticipantsObservationsTable));


          if(householdSourceIdsIncludedInParticipantAttributes != null && !householdSourceIdsIncludedInParticipantAttributes.equals("")) {
              householdSourceIdsIncludedInParticipantAttributes = ", " + householdSourceIdsIncludedInParticipantAttributes;
              setPropValue("householdSourceIdsIncludedInParticipantAttributes", householdSourceIdsIncludedInParticipantAttributes);
          }
 
          setPropValue("participantRecordSamplesTable", "");
          setPropValue("participantRecordSamplesMetaTableQuery", "");
          setPropValue("participantRecordSamplesTableQuery", "");
 
          if(hasSamples) {
              String sampleSourceIdsForParticipantsSamplesTable  = getPropValue("sampleSourceIdsForParticipantsSamplesTable");
              setPropValue("sampleSourceIdsForParticipantsSamplesTableSubquery", propertySourceIdSubquery(sampleSourceIdsForParticipantsSamplesTable));

          setPropValue("participantRecordSamplesTable", getTemplateInstanceText("participantRecordSamplesTable"));
          setPropValue("participantRecordSamplesMetaTableQuery", getTemplateInstanceText("participantRecordSamplesMetaTableQuery"));
          setPropValue("participantRecordSamplesTableQuery", getTemplateInstanceText("participantRecordSamplesTableQuery"));
          }


	  
	  //Micros Test results
          setPropValue("participantRecordMicrosTable", "");
          setPropValue("participantRecordMicrosTableQuery", "");
	  setPropValue("microSourceIdsForParticipantsMicrosQuote","");
	  
          if(hasMicros) {
	    
	  String microSourceIdsForParticipantsMicrosTable  = getPropValue("microSourceIdsForParticipantsMicrosTable");
          setPropValue("participantRecordMicrosTable", getTemplateInstanceText("participantRecordMicrosTable"));
	  setPropValue("microSourceIdsForParticipantsMicrosQuote", addQuotes(microSourceIdsForParticipantsMicrosTable));
	  setPropValue("participantRecordMicrosTableQuery", getTemplateInstanceText("participantRecordMicrosTableQuery"));
 

	  }

          injectTemplate("participantRecord");
          injectTemplate("participantRecordAttributeQueries");
          injectTemplate("participantRecordTableQueries");

          // Participant SourceId Question
          injectTemplate("participantSourceIdQuestion");
          injectTemplate("participantSourceIdQuery");
          injectTemplate("participantSourceIdParam");

          injectTemplate("participantResultParam");

          for(String key : participantQuestionTemplateNamesToScopes().keySet()) {
              setPropValue("participantQuestionName", key);

              setPropValue("participantQuestionFull", getTemplateInstanceText(key));
              injectTemplate("participantsByDataset");
          }



      }
 
      if(hasObservations) {


          String householdSourceIdsIncludedInObservationAttributes = getPropValue("householdSourceIdsIncludedInObservationAttributes");
          String participantSourceIdsIncludedInObservationAttributes = getPropValue("participantSourceIdsIncludedInObservationAttributes");
          String hhAndPIncludedInObservationAttributes = householdSourceIdsIncludedInObservationAttributes + " " + participantSourceIdsIncludedInObservationAttributes;
          if(participantSourceIdsIncludedInObservationAttributes != null && !participantSourceIdsIncludedInObservationAttributes.equals("") &&
          householdSourceIdsIncludedInObservationAttributes != null && !householdSourceIdsIncludedInObservationAttributes.equals("")) {
              hhAndPIncludedInObservationAttributes = householdSourceIdsIncludedInObservationAttributes + "," + participantSourceIdsIncludedInObservationAttributes;
          }
          setPropValue("participantAndHouseholdSourceIdsIncludedInObservationAttributesQuote", addQuotes(hhAndPIncludedInObservationAttributes));


          if(participantSourceIdsIncludedInObservationAttributes != null && !participantSourceIdsIncludedInObservationAttributes.equals("")) {
              participantSourceIdsIncludedInObservationAttributes = ", " + participantSourceIdsIncludedInObservationAttributes;
              setPropValue("participantSourceIdsIncludedInObservationAttributes", participantSourceIdsIncludedInObservationAttributes);
          }
          if(householdSourceIdsIncludedInObservationAttributes != null && !householdSourceIdsIncludedInObservationAttributes.equals("")) {
              householdSourceIdsIncludedInObservationAttributes = ", " + householdSourceIdsIncludedInObservationAttributes;
              setPropValue("householdSourceIdsIncludedInObservationAttributes", householdSourceIdsIncludedInObservationAttributes);
          }


	  
          setPropValue("observationRecordSamplesTable", "");
          setPropValue("observationRecordSamplesMetaTableQuery", "");
          setPropValue("observationRecordSamplesTableQuery", "");
 
          if(hasSamples) {
           
	      String sampleSourceIdsForParticipantsSamplesTable  = getPropValue("sampleSourceIdsForParticipantsSamplesTable");
              //System.err.println("sampleSourceIdsForParticipantsSamplesTable="+sampleSourceIdsForParticipantsSamplesTable);

	      setPropValue("sampleSourceIdsForParticipantsSamplesTableSubquery", propertySourceIdSubquery(sampleSourceIdsForParticipantsSamplesTable));

	      setPropValue("observationRecordSamplesTable", getTemplateInstanceText("observationRecordSamplesTable"));
	      setPropValue("observationRecordSamplesMetaTableQuery", getTemplateInstanceText("observationRecordSamplesMetaTableQuery"));
	      setPropValue("observationRecordSamplesTableQuery", getTemplateInstanceText("observationRecordSamplesTableQuery"));
          }
	  


          injectTemplate("observationRecord");
          injectTemplate("observationRecordAttributeQueries");
          injectTemplate("observationRecordTableQueries");

          // Observation: SourceId Question
          injectTemplate("observationSourceIdQuestion");
          injectTemplate("observationSourceIdQuery");
          injectTemplate("observationSourceIdParam");

          injectTemplate("observationResultParam");

          for(String key : observationQuestionTemplateNamesToScopes().keySet()) {
              setPropValue("observationQuestionName", key);
              setPropValue("observationQuestionFull", getTemplateInstanceText(key));
              injectTemplate("observationsByDataset");
          }
	  
	  


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
          int orderNumber = modelReference.getOrderNumber();

          setPropValue("modelRefOrderNumber", Integer.toString(orderNumber));
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
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdTables.HouseholdMembersColumnAttributes",null);
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdAttributes.HouseholdAttributesMeta",null);
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdTables.LightTrapColumnAttributes",null);
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantAttributes.ParticipantAttributesMeta","ParticipantNode");
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantTables.ObservationsColumnAttributes",null);
      injectAttributeMetaQuery(observationRecordClass, presenterId + "ObservationAttributes.ObservationAttributesMeta",null);
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantAttributes.HouseholdAttributesMeta","HouseholdNode");

  }

  @Override
  public void addModelReferences() {
      boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
      boolean hasSamples = getPropValueAsBoolean("hasSamples");
      boolean hasMicros = getPropValueAsBoolean("hasMicros");

      String presenterId = getPropValue("presenterId");
      
      String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
      String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
      String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);

      if(hasHouseholds) {
          addWdkReference(householdRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI, 0);
          addWdkReference(householdRecordClass, "table", "HouseholdMembers", new String[]{"record"}, CATEGORY_IRI, 0);

          addWdkReference(householdRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI, 0);

          addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI, 0); 


	  if(hasParticipants){
              addWdkReference(householdRecordClass, "table", "ParticipantsDownload", new String[]{"download"}, CATEGORY_IRI, 0);
          }


          if(hasObservations){
              addWdkReference(householdRecordClass, "table", "ObservationsDownload", new String[]{"download"}, CATEGORY_IRI,0);
          }


          if(hasSamples) {
              addWdkReference(householdRecordClass, "table", "SamplesDownload", new String[]{"download"}, CATEGORY_IRI,0);
          }



          for (Map.Entry<String, String[]> entry : householdQuestionTemplateNamesToScopes().entrySet()) {
              String questionFullName = "HouseholdQuestions." + entry.getKey();
              addWdkReference(householdRecordClass, "question", questionFullName, entry.getValue(), CATEGORY_IRI, 0);
          }
          

      }

      if(hasParticipants) {
          addWdkReference(participantRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI, 0);
          addWdkReference(participantRecordClass, "table", "Observations", new String[]{"record"}, CATEGORY_IRI, 0);
          //addWdkReference(participantRecordClass, "table", "ObservationsDownload", new String[]{"download"}, CATEGORY_IRI, 0);
	 		    
          // TODO Samples table of participant record page

          addWdkReference(participantRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI, 0);

          // Add the Text Attributes

          int attributeCount = 1;
          for (Map.Entry<String, String[]> entry : participantGraphAttributesToScopes().entrySet()) {
              addWdkReference(participantRecordClass, "attribute", entry.getKey(), entry.getValue(), CATEGORY_IRI, attributeCount);
              attributeCount++;
          }

          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI, 0); 


          for (Map.Entry<String, String[]> entry : participantQuestionTemplateNamesToScopes().entrySet()) {
              String questionFullName = "ParticipantQuestions." + entry.getKey();
              addWdkReference(participantRecordClass, "question", questionFullName, entry.getValue(), CATEGORY_IRI, 0);
          }

	  if(hasObservations){
              addWdkReference(participantRecordClass, "table", "ObservationsDownload", new String[]{"download"}, CATEGORY_IRI,0);
          }


          if(hasSamples) {
              addWdkReference(participantRecordClass, "table", "Samples", new String[]{"record"}, CATEGORY_IRI, 0);
	      addWdkReference(participantRecordClass, "table", "SamplesDownload", new String[]{"download"}, CATEGORY_IRI,0);
          }
	  
	  if(hasMicros) {
              addWdkReference(participantRecordClass, "table", "Micros", new String[]{"record"}, CATEGORY_IRI, 0);
          }


      }

      if(hasObservations) {
          addWdkReference(observationRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI, 0);
          // TODO Samples table of observation record page

          addWdkReference(observationRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI, 0);

          addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationssBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI, 0); 

          for (Map.Entry<String, String[]> entry : observationQuestionTemplateNamesToScopes().entrySet()) {
              String questionFullName = "ClinicalVisitQuestions." + entry.getKey();
              addWdkReference(observationRecordClass, "question", questionFullName, entry.getValue(), CATEGORY_IRI, 0);
          }

	  if(hasSamples) {
              addWdkReference(observationRecordClass, "table", "Samples", new String[]{"record"}, CATEGORY_IRI, 0);
	      addWdkReference(observationRecordClass, "table", "SamplesDownload", new String[]{"download"}, CATEGORY_IRI,0);
          }
      }

      if(hasHouseholds && hasParticipants) {
          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsByHouseholds", new String[]{"webservice"}, CATEGORY_IRI, 0); 
          addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsByParticipants", new String[]{"webservice"}, CATEGORY_IRI, 0); 
      }

      if(hasParticipants && hasObservations) {
          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsByObservations", new String[]{"webservice"}, CATEGORY_IRI, 0); 
          addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationsByParticipants", new String[]{"webservice"}, CATEGORY_IRI, 0); 
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
                                 //attributes to be added to the record for the studies.jason replacement
                                 {"studyCategories", ""},
                                 {"projectAvailability", ""},
                                 {"studyAccess", ""},
                                 {"policyUrl", ""},
                                 {"cardHeadline", ""},
                                 {"cardPoints", ""},
                                 {"cardQuestions", ""},
                                 {"cardQuestions", ""},
                                 {"requestProtectionLevel", ""},
                                 {"requestAccessFields", ""},
                                 {"requestEmail", ""},
                                 {"requestEmailBody", ""},
                                 {"requestNeedsApproval", ""}
      };

    return declaration;
  }

}
