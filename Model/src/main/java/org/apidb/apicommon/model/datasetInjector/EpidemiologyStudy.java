package org.apidb.apicommon.model.datasetInjector;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apidb.apicommon.datasetPresenter.DatasetInjector;
import org.apidb.apicommon.datasetPresenter.ModelReference;

public abstract class EpidemiologyStudy extends DatasetInjector {

    protected abstract Map<String,String[]> householdQuestionTemplateNamesToScopes();
    protected abstract Map<String,String[]> participantQuestionTemplateNamesToScopes();
    protected abstract Map<String,String[]> observationQuestionTemplateNamesToScopes();
    protected abstract Map<String,String[]> sampleQuestionTemplateNamesToScopes();

    protected abstract String participantGraphAttributesTemplateName();  
    protected abstract Map<String,String[]> participantGraphAttributesToScopes();
    protected abstract void setStudySpecificProperties();  

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
    public static final String SAMPLE_RECORD_CLASS_PREFIX = "Sample";

  @Override
  public void injectTemplates() {

      boolean injectStudy = getPropValueAsBoolean("injectStudy");
      if(!injectStudy){
          return;
      }

      //default property values
      setPropValue("injectParams","true");  //ONLY for studies that are using hard cooded param names ... for all but one study
      //microbiome defaults ... no linkage. NOTE: ONLY implemented for participantCaseControlRegion
      setPropValue("hasMicrobiomeData","false");
      setPropValue("MicrobiomeDatasetName","none");
      
      setStudySpecificProperties();  

      for (Map.Entry<String, String[]> entry : participantGraphAttributesToScopes().entrySet()) {
        setPropValue(entry.getKey(), entry.getKey());
      }
   
      setPropValue("participantGraphAttributes", participantGraphAttributesString());

      String presenterId = getPropValue("presenterId");

      addModelReferences();

      String subProjectName = getPropValue("subProjectName");
      //      String datasetName = getDatasetName();
      String includeProjects = getPropValueAsBoolean("isPublic") ? "AllClinEpiDB,ClinEpiDB," + subProjectName : "AllClinEpiDB," + subProjectName;
      setPropValue("includeProjects", includeProjects);


      String tblPrefix = "D" + getPropValue("datasetDigest");
      setPropValue("tblPrefix", tblPrefix);
      // want to have optional param suffixes so if that prop is not set ... non-null .. then make tblPrefix
      String paramSuffix = getPropValue("paramSuffix");
      if (paramSuffix == null) {
          setPropValue("paramSuffix", tblPrefix);
      }
          

      String projectId = getPropValue("projectName");
      String gusHome = System.getenv("GUS_HOME");
      String modelPropPath = gusHome + "/config/" + projectId + "/model.prop";
      Properties modelProp = new Properties();
      InputStream input = null;
      try {
        input = new FileInputStream(modelPropPath);
        modelProp.load(input);
      } catch (IOException e) {
          //        e.printStackTrace();
      } finally {
        if (input != null) {
	  try {
	    input.close();
	  } catch (IOException ex) {
	    ex.printStackTrace();
	  }
	}	
      }

      //multiFilter ids from legacy studies where not part of ontology
      String householdMultiFilterIdsQuoted = addQuotes(getPropValue("householdMultiFilterIds"));
      if(householdMultiFilterIdsQuoted == null || householdMultiFilterIdsQuoted.equals("''")) {
          householdMultiFilterIdsQuoted = "'NA'";
      }
      setPropValue("householdMultiFilterIdsQuoted", householdMultiFilterIdsQuoted);

      String participantMultiFilterIdsQuoted = addQuotes(getPropValue("participantMultiFilterIds"));
      if(participantMultiFilterIdsQuoted == null || participantMultiFilterIdsQuoted.equals("''")) {
          participantMultiFilterIdsQuoted  = "'NA'";
      }
      setPropValue("participantMultiFilterIdsQuoted", participantMultiFilterIdsQuoted);

      String observationMultiFilterIdsQuoted = addQuotes(getPropValue("observationMultiFilterIds"));
      if(observationMultiFilterIdsQuoted == null || observationMultiFilterIdsQuoted.equals("''")) {
          observationMultiFilterIdsQuoted  = "'NA'";
      }
      setPropValue("observationMultiFilterIdsQuoted", observationMultiFilterIdsQuoted);
      
      String sampleMultiFilterIdsQuoted = addQuotes(getPropValue("sampleMultiFilterIds"));
      if(sampleMultiFilterIdsQuoted == null || sampleMultiFilterIdsQuoted.equals("''")) {
          sampleMultiFilterIdsQuoted  = "'NA'";
      }
      setPropValue("sampleMultiFilterIdsQuoted", sampleMultiFilterIdsQuoted);


      String localhost = modelProp.getProperty("LOCALHOST") + modelProp.getProperty("LEGACY_WEBAPP_BASE_URL");

      //inject shiny data loading
      String datasetName = getPropValue("datasetName");
      String shinyDataURL = localhost + "/service/shiny/data/" + datasetName;
      setPropValue("dataURL", shinyDataURL);
      String shinyOntologyURL = localhost + "/service/shiny/ontology/" + datasetName;
      setPropValue("ontologyURL", shinyOntologyURL);
      injectTemplate("shinyDataLoad");
      injectTemplate("shinyDatasetConfig");

      // boolean hasHouseholdDataCollection = getPropValueAsBoolean("hasHouseholdDataCollection");
      boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
      boolean hasSamples = getPropValueAsBoolean("hasSampleRecord");
      boolean hasSampleDataCollected = getPropValueAsBoolean("hasSampleDataCollected");
      //boolean hasObserTableInSQL = getPropValueAsBoolean("hasObserTableInSQL");
      boolean hasMicrosTableInPartiPage = getPropValueAsBoolean("hasMicrosTableInPartiPage");
      boolean hasMicrosTableInObserPage = getPropValueAsBoolean("hasMicrosTableInObserPage");
      //boolean hasHouseholdObservations = getPropValueAsBoolean("hasHouseholdObservations");

      setPropValue("!hasObservationRecord", Boolean.toString(!hasObservations));
      setPropValue("!hasSampleRecord", Boolean.toString(!hasSamples));

      // TODO: how to handle optional tables??  probably just do these in a subclass?

      if(hasHouseholds) {
	  // String householdSourceIdsForHouseholdMemberTable = getPropValue("householdSourceIdsForHouseholdMemberTable");
          //setPropValue("householdSourceIdsForHouseholdMemberTableSubquery", propertySourceIdSubquery(householdSourceIdsForHouseholdMemberTable));

	  String participantRecordAttributesList = getPropValue("participantRecordAttributesList");
          setPropValue("participantRecordAttributesListSubquery", propertySourceIdSubquery(participantRecordAttributesList));

          setPropValue("extraHouseholdTables", this.extraHouseholdTables());
          setPropValue("extraHouseholdTableQueries", this.extraHouseholdTableQueries());

          String householdAttList = getPropValue("householdAttributesList");
          if(householdAttList != null && !householdAttList.equals("")) {
              setPropValue("householdAttributesListFull","<attributesList summary=\"" + householdAttList + "\" />");
          }else{
              setPropValue("householdAttributesListFull","");
          }
          String householdRecordAttList = getPropValue("householdRecordAttributesList");
          if(householdRecordAttList != null && !householdRecordAttList.equals("")) {
              setPropValue("householdRecordAttributesListFull","<attributesList summary=\"" + householdRecordAttList + "\" />");
          }else{
              setPropValue("householdRecordAttributesListFull","");
          }


          injectTemplate("householdRecord");
          injectTemplate("householdRecordAttributeQueries");
          injectTemplate("householdRecordTableQueries");
          
          // Household SourceId Question
          //          injectTemplate("householdSourceIdQuestion");
          //          injectTemplate("householdSourceIdQuery");
          //          injectTemplate("householdSourceIdParam");

          injectTemplate("householdResultParam");

          for(String key : householdQuestionTemplateNamesToScopes().keySet()) {
              setPropValue("householdQuestionName", key);
              setPropValue("householdQuestionFull", getTemplateInstanceText(key));
              injectTemplate("householdsByDataset");
          }
      }


      if(hasParticipants) {
	   String observationSourceIdsForParticipantsObservationsTable  = getPropValue("observationSourceIdsForParticipantsObservationsTable");
          String participantSourceIdsExcludedFromParticipantAttributes = getPropValue("participantSourceIdsExcludedFromParticipantAttributes");

          if(participantSourceIdsExcludedFromParticipantAttributes == null || participantSourceIdsExcludedFromParticipantAttributes.equals("") ){
            setPropValue("participantSourceIdsExcludedFromParticipantAttributesQuote", addQuotes("none"));          
          } else {
            setPropValue("participantSourceIdsExcludedFromParticipantAttributesQuote", addQuotes(participantSourceIdsExcludedFromParticipantAttributes));
          }
          //setPropValue("observationSourceIdsForParticipantsObservationsTableSubquery", propertySourceIdSubquery(observationSourceIdsForParticipantsObservationsTable));



          //directly set the boolean
	  Boolean hasObserTableInSQL  = true;
	  if(observationSourceIdsForParticipantsObservationsTable == null || observationSourceIdsForParticipantsObservationsTable.equals("") ){
              hasObserTableInSQL = false;
	  }
	  

	  //ObservationTable results
          setPropValue("participantRecordObservationsTable", "");
          setPropValue("participantRecordObservationsMetaTableQuery", "");
          setPropValue("participantRecordObservationsTableQuery", "");
 
          if(hasObserTableInSQL) {
              setPropValue("observationSourceIdsForParticipantsObservationsTableSubquery", propertySourceIdSubquery(observationSourceIdsForParticipantsObservationsTable));

          setPropValue("participantRecordObservationsTable", getTemplateInstanceText("participantRecordObservationsTable"));
          setPropValue("participantRecordObservationsMetaTableQuery", getTemplateInstanceText("participantRecordObservationsMetaTableQuery"));
          setPropValue("participantRecordObservationsTableQuery", getTemplateInstanceText("participantRecordObservationsTableQuery"));
          }


	  //Samples Test results
          setPropValue("participantRecordSamplesTable", "");
          setPropValue("participantRecordSamplesMetaTableQuery", "");
          setPropValue("participantRecordSamplesTableQuery", "");
 
          if(hasSampleDataCollected) {
              String sampleSourceIdsForSampleTable  = getPropValue("sampleSourceIdsForSampleTable");
              setPropValue("sampleSourceIdsForSampleTableSubquery", propertySourceIdSubquery(sampleSourceIdsForSampleTable));

          setPropValue("participantRecordSamplesTable", getTemplateInstanceText("participantRecordSamplesTable"));
          setPropValue("participantRecordSamplesMetaTableQuery", getTemplateInstanceText("participantRecordSamplesMetaTableQuery"));
          setPropValue("participantRecordSamplesTableQuery", getTemplateInstanceText("participantRecordSamplesTableQuery"));
          }


	  
	  //Micros Test results
          setPropValue("participantRecordMicrosTable", "");
          setPropValue("participantRecordMicrosTableQuery", "");
	  setPropValue("microSourceIdsForParticipantsMicrosQuote","");
	  
          if(hasMicrosTableInPartiPage) {
	    
	  String microSourceIdsForMicroTable  = getPropValue("microSourceIdsForMicroTable");
          setPropValue("participantRecordMicrosTable", getTemplateInstanceText("participantRecordMicrosTable"));
	  setPropValue("microSourceIdsForParticipantsMicrosQuote", addQuotes(microSourceIdsForMicroTable));
	  setPropValue("participantRecordMicrosTableQuery", getTemplateInstanceText("participantRecordMicrosTableQuery"));
 	  }


          //before injecting any templates need to determine and set the attributes list ... can't be null
          String participantAttList = getPropValue("participantAttributesList");
          if(participantAttList != null && !participantAttList.equals("")) {
              setPropValue("participantAttributesListFull","<attributesList summary=\"" + participantAttList + "\" />");
          }else{
              setPropValue("participantAttributesListFull","");
          }
          String participantRecordAttList = getPropValue("participantRecordAttributesList");
          if(participantRecordAttList != null && !participantRecordAttList.equals("")) {
              setPropValue("participantRecordAttributesListFull","<attributesList summary=\"" + participantRecordAttList + "\" />");
          }else{
              setPropValue("participantRecordAttributesListFull","");
          }

          injectTemplate("participantRecord");
          injectTemplate("participantRecordAttributeQueries");
          injectTemplate("participantRecordTableQueries");

          // Participant SourceId Question
          //injectTemplate("participantSourceIdQuestion");
          //injectTemplate("participantSourceIdQuery");
          //injectTemplate("participantSourceIdParam");

          injectTemplate("participantResultParam");

          // Deal with microbiomedb linkage.
          boolean hasMbioData = getPropValueAsBoolean("hasMicrobiomeData");
          if(hasMbioData){
              setPropValue("microbiomeOntologyQueryFull",getTemplateInstanceText("microbiomeOntologyQuery"));
              setPropValue("participantMicrobiomeMetadataQueryFull",getTemplateInstanceText("participantMicrobiomeMetadataQuery"));
              setPropValue("participantMicrobiomeMetadataBkgQueryFull",getTemplateInstanceText("participantMicrobiomeMetadataBkgQuery"));
          }else{  //need to setProps to empty string
              setPropValue("microbiomeOntologyQueryFull","");
              setPropValue("participantMicrobiomeMetadataQueryFull","");
              setPropValue("participantMicrobiomeMetadataBkgQueryFull","");
          }

          for(String key : participantQuestionTemplateNamesToScopes().keySet()) {
              setPropValue("participantQuestionName", key);

              setPropValue("participantQuestionFull", getTemplateInstanceText(key));
              injectTemplate("participantsByDataset");
          }



      }
 
      if(hasObservations) {

          setPropValue("observationRecordSamplesTable", "");
          setPropValue("observationRecordSamplesMetaTableQuery", "");
          setPropValue("observationRecordSamplesTableQuery", "");
 
          if(hasSampleDataCollected) {
           
	      String sampleSourceIdsForSampleTable  = getPropValue("sampleSourceIdsForSampleTable");
              //System.err.println("sampleSourceIdsForSampleTable="+sampleSourceIdsForSampleTable);

	      setPropValue("sampleSourceIdsForSampleTableSubquery", propertySourceIdSubquery(sampleSourceIdsForSampleTable));

	      setPropValue("observationRecordSamplesTable", getTemplateInstanceText("observationRecordSamplesTable"));
	      setPropValue("observationRecordSamplesMetaTableQuery", getTemplateInstanceText("observationRecordSamplesMetaTableQuery"));
	      setPropValue("observationRecordSamplesTableQuery", getTemplateInstanceText("observationRecordSamplesTableQuery"));
          }



	  	  
	  //Micros Test results
          setPropValue("observationRecordMicrosTable", "");
          setPropValue("observationRecordMicrosTableQuery", "");
	  setPropValue("microSourceIdsForObservationsMicrosQuote","");
	  
          if(hasMicrosTableInObserPage) {
	    
	  String microSourceIdsForMicroTable  = getPropValue("microSourceIdsForMicroTable");
          setPropValue("observationRecordMicrosTable", getTemplateInstanceText("observationRecordMicrosTable"));
	  setPropValue("microSourceIdsForObservationsMicrosQuote", addQuotes(microSourceIdsForMicroTable));
	  setPropValue("observationRecordMicrosTableQuery", getTemplateInstanceText("observationRecordMicrosTableQuery"));
 
	  }

          String observationAttList = getPropValue("observationAttributesList");
          if(observationAttList != null && !observationAttList.equals("")) {
              setPropValue("observationAttributesListFull","<attributesList summary=\"" + observationAttList + "\" />");
          }else{
              setPropValue("observationAttributesListFull","");
          }
          String observationRecordAttList = getPropValue("observationRecordAttributesList");
          if(observationRecordAttList != null && !observationRecordAttList.equals("")) {
              setPropValue("observationRecordAttributesListFull","<attributesList summary=\"" + observationRecordAttList + "\" />");
          }else{
              setPropValue("observationRecordAttributesListFull","");
          }

          injectTemplate("observationRecord");
          injectTemplate("observationRecordAttributeQueries");
          injectTemplate("observationRecordTableQueries");

          // Observation: SourceId Question
          //injectTemplate("observationSourceIdQuestion");
          //injectTemplate("observationSourceIdQuery");
          //injectTemplate("observationSourceIdParam");

          injectTemplate("observationResultParam");

          for(String key : observationQuestionTemplateNamesToScopes().keySet()) {
              setPropValue("observationQuestionName", key);
              setPropValue("observationQuestionFull", getTemplateInstanceText(key));
              injectTemplate("observationsByDataset");
          }
	  
	  


      }



      if(hasSamples){
	  
	  String sampleAttList = getPropValue("sampleAttributesList");
          String sampleSourceIdsExcludedFromSampleAttributes = getPropValue("sampleSourceIdsExcludedFromSampleAttributes");
          String participantSourceIdsExcludedFromParticipantAttributes = getPropValue("participantSourceIdsExcludedFromParticipantAttributes");


          if(sampleAttList != null && !sampleAttList.equals("")) {
	      setPropValue("sampleAttributesListFull","<attributesList summary=\"" + sampleAttList + "\" />");
	  }else{
	      setPropValue("sampleAttributesListFull","");
	  }
          String sampleRecordAttList = getPropValue("sampleRecordAttributesList");
          if(sampleRecordAttList != null && !sampleRecordAttList.equals("")) {
	      setPropValue("sampleRecordAttributesListFull","<attributesList summary=\"" + sampleRecordAttList + "\" />");
	  }else{
	      setPropValue("sampleRecordAttributesListFull","");
	  }
	  

          if(sampleSourceIdsExcludedFromSampleAttributes == null || sampleSourceIdsExcludedFromSampleAttributes.equals("") ){
            setPropValue("sampleSourceIdsExcludedFromSampleAttributesQuote", addQuotes("none"));          
          } else {
            setPropValue("sampleSourceIdsExcludedFromSampleAttributesQuote", addQuotes(sampleSourceIdsExcludedFromSampleAttributes));
          }

          if(participantSourceIdsExcludedFromParticipantAttributes == null || participantSourceIdsExcludedFromParticipantAttributes.equals("") ){
            setPropValue("participantSourceIdsExcludedFromParticipantAttributesQuote", addQuotes("none"));          
          } else {
            setPropValue("participantSourceIdsExcludedFromParticipantAttributesQuote", addQuotes(participantSourceIdsExcludedFromParticipantAttributes));
          }


	  
	  injectTemplate("sampleRecord");
          injectTemplate("sampleRecordAttributeQueries");
          injectTemplate("sampleRecordTableQueries");
	  
          injectTemplate("sampleResultParam");

          for(String key : sampleQuestionTemplateNamesToScopes().keySet()) {
              setPropValue("sampleQuestionName", key);
              setPropValue("sampleQuestionFull", getTemplateInstanceText(key));
              injectTemplate("samplesByDataset");
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

      // Samples->Observations and Observations->Samples
      if(hasObservations && hasSamples) {
         injectTemplate("samplesByObservationsQuestion");
         injectTemplate("samplesByObservationsQuery");
         injectTemplate("observationsBySamplesQuestion");
         injectTemplate("observationsBySamplesQuery");
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
      String sampleRecordClass = makeRecordClassName(SAMPLE_RECORD_CLASS_PREFIX);

      // Add meta attribute queries to categories / individuals
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdTables.HouseholdMembersColumnAttributes",null);
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdAttributes.HouseholdAttributesMeta","HouseholdNode");
      injectAttributeMetaQuery(householdRecordClass, presenterId + "HouseholdTables.LightTrapColumnAttributes",null);
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantAttributes.ParticipantAttributesMeta","ParticipantNode");
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantTables.ObservationsColumnAttributes",null);
      injectAttributeMetaQuery(observationRecordClass, presenterId + "ObservationAttributes.ObservationAttributesMeta","ObservationNode");
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantAttributes.HouseholdAttributesMeta","HouseholdNode");
      injectAttributeMetaQuery(participantRecordClass, presenterId + "ParticipantAttributes.ObservationAttributesMeta","ObservationNode");
      injectAttributeMetaQuery(observationRecordClass, presenterId + "ObservationAttributes.ParticipantAttributesMeta","ParticipantNode");
      injectAttributeMetaQuery(observationRecordClass, presenterId + "ObservationAttributes.HouseholdAttributesMeta","HouseholdNode");


      injectAttributeMetaQuery(sampleRecordClass, presenterId + "SampleAttributes.ParticipantAttributesMeta","ParticipantNode");
      injectAttributeMetaQuery(sampleRecordClass, presenterId + "SampleAttributes.HouseholdAttributesMeta","HouseholdNode");
      injectAttributeMetaQuery(sampleRecordClass, presenterId + "SampleAttributes.ObservationAttributesMeta","ObservationNode");
      injectAttributeMetaQuery(sampleRecordClass, presenterId + "SampleAttributes.SampleAttributesMeta","SampleNode");


      //inject the metadata queries and params
      injectMetadataQueries();
  }

    public void injectMetadataQueries() {
      String studyType = getPropValue("studyType");
      String firstWizardStep = getPropValue("firstWizardStep");
      Boolean keepRegionInHouseholdFilter = getPropValueAsBoolean("keepRegionInHouseholdFilter");
      if(firstWizardStep.equals("Household") || keepRegionInHouseholdFilter){
          setPropValue("rmRegionSqlCommentStart","/*");
          setPropValue("rmRegionSqlCommentEnd","*/");
      }else{
          setPropValue("rmRegionSqlCommentStart","");
          setPropValue("rmRegionSqlCommentEnd","");
      }
      Boolean hideRegionVariablesKeepDataInHousehold = getPropValueAsBoolean("hideRegionVariablesKeepDataInHousehold");
      if(firstWizardStep.equals("Household") || !hideRegionVariablesKeepDataInHousehold){
          setPropValue("hideRegionVarsSqlCommentStart","/*");
          setPropValue("hideRegionVarsSqlCommentEnd","*/");
      }else{
          setPropValue("hideRegionVarsSqlCommentStart","");
          setPropValue("hideRegionVarsSqlCommentEnd","");
      }
      
      boolean hasHouseholdQuestion = getPropValueAsBoolean("hasHouseholdQuestion");
      boolean hasParticipantQuestion = getPropValueAsBoolean("hasParticipantQuestion");
      boolean hasObservationQuestion = getPropValueAsBoolean("hasObservationQuestion");
      boolean hasSampleQuestion = getPropValueAsBoolean("hasSampleQuestion");
      boolean hasStudyDetailsStep = getPropValueAsBoolean("hasStudyDetailsStep");
      // boolean hasHouseholdDataCollection = getPropValueAsBoolean("hasHouseholdDataCollection");
      boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
      boolean hasSamples = getPropValueAsBoolean("hasSampleRecord");
      String regionFilterExcludedIdsQuoted = addQuotes(getPropValue("regionFilterExcludedIds"));
      String householdFilterExcludedIdsQuoted = addQuotes(getPropValue("householdFilterExcludedIds"));
      String participantFilterExcludedIdsQuoted = addQuotes(getPropValue("participantFilterExcludedIds"));
      String observationFilterExcludedIdsQuoted = addQuotes(getPropValue("observationFilterExcludedIds"));
      String studyDetailsOntologyIdsQuoted = addQuotes(getPropValue("studyDetailsOntologyIds"));

      //set properties needed for householdObservations
      boolean hasHouseholdObservations = getPropValueAsBoolean("hasHouseholdObservations");
      String tmpHouseholdStepName = getPropValue("householdStepName");
      String householdStepName = tmpHouseholdStepName.contains("Region") ? "Participants" : tmpHouseholdStepName + "s";
      if(hasHouseholdObservations){
          setPropValue("householdOrObservationIsVisible", "true");
          setPropValue("householdFilterDataTypeDisplayName", getPropValue("householdStepName")+" Observations");
      }else{
          setPropValue("householdOrObservationIsVisible", "false");
          setPropValue("householdFilterDataTypeDisplayName", householdStepName);
      }

      //set property for name of observation and region filters
      setPropValue("observationFilterDataTypeDisplayName", getPropValue("observationStepName")+"s");
      setPropValue("regionFilterDataTypeDisplayName", householdStepName);

      //Inject the filter param queries for participants if any questions generated
      if(hasParticipantQuestion || hasHouseholdQuestion || hasObservationQuestion){
          boolean injectParams = getPropValueAsBoolean("injectParams");
          
          if(regionFilterExcludedIdsQuoted == null || regionFilterExcludedIdsQuoted.equals("''")) {
              regionFilterExcludedIdsQuoted  = "'NA'";
          }
          setPropValue("regionFilterExcludedIdsQuoted", regionFilterExcludedIdsQuoted);
          if(householdFilterExcludedIdsQuoted == null || householdFilterExcludedIdsQuoted.equals("''")) {
              householdFilterExcludedIdsQuoted  = "'NA'";
          }
          setPropValue("householdFilterExcludedIdsQuoted", householdFilterExcludedIdsQuoted);
          if(participantFilterExcludedIdsQuoted == null || participantFilterExcludedIdsQuoted.equals("''")) {
              participantFilterExcludedIdsQuoted  = "'NA'";
          }
          setPropValue("participantFilterExcludedIdsQuoted", participantFilterExcludedIdsQuoted);
          if(observationFilterExcludedIdsQuoted == null || observationFilterExcludedIdsQuoted.equals("''")) {
              observationFilterExcludedIdsQuoted  = "'NA'";
          }
          setPropValue("observationFilterExcludedIdsQuoted", observationFilterExcludedIdsQuoted);
          if(studyDetailsOntologyIdsQuoted == null || studyDetailsOntologyIdsQuoted.equals("''")) {
              studyDetailsOntologyIdsQuoted = "'NA'";
          }
          setPropValue("studyDetailsOntologyIdsQuoted", studyDetailsOntologyIdsQuoted);
          //determine if a special template is used
          String filterParamQueryBaseTemplate = getPropValue("filterParamQueryBaseTemplate");
          if(filterParamQueryBaseTemplate.equals("default")){
              if(studyType.equals("CaseControl")){
                  setPropValue("injectedTemplateFull",getTemplateInstanceText("participantFilterParamQueries" + studyType + firstWizardStep));
              }else{
                  setPropValue("injectedTemplateFull",getTemplateInstanceText("participantFilterParamQueries" + firstWizardStep + (hasStudyDetailsStep ? "SD" : "")));
              }
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("participant" + filterParamQueryBaseTemplate));
          }
          if(injectParams){
              injectTemplate("participantFilterParamQueries");
              //Inject the household param group ... note all studies injected uniformly based on presenter properties
              injectTemplate("participantParamGroups");
          }
      }


      if(hasParticipantQuestion && hasParticipants){
          //first the params 
          boolean injectParams = getPropValueAsBoolean("injectParams");
          String filterParamBaseTemplate = getPropValue("filterParamBaseTemplate");
          if(filterParamBaseTemplate.equals("default")){
              if(studyType.equals("CaseControl")){
                  setPropValue("injectedTemplateFull",getTemplateInstanceText("participantFilterParams" + studyType + firstWizardStep));
              }else{
                  setPropValue("injectedTemplateFull",getTemplateInstanceText("participantFilterParams" + firstWizardStep + (hasStudyDetailsStep ? "SD" : "")));
              }
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("participant" + filterParamBaseTemplate));
          }
          if(injectParams){
              injectTemplate("participantFilterParams");
          }

          //Inject the particiant metadata query
          String queryBaseTemplate = getPropValue("queryBaseTemplate");
          if(queryBaseTemplate.equals("default")){
              setPropValue("injectedTemplateFull",getTemplateInstanceText("participant" + studyType + "Query" + firstWizardStep + (hasStudyDetailsStep ? "SD" : "")));
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("participant" + queryBaseTemplate));
          }
          injectTemplate("participantMetadataQuery");

      }

      //now to do observations
      if(hasObservationQuestion && hasObservations){
          //Inject the metadata query
          String queryBaseTemplate = getPropValue("queryBaseTemplate");
          if(queryBaseTemplate.equals("default")){
              setPropValue("injectedTemplateFull",getTemplateInstanceText("observation" + studyType + "Query" + firstWizardStep));
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("observation" + queryBaseTemplate));
          }
          injectTemplate("observationMetadataQuery");
          
          //Inject the filter params .... note these use the ontology queries from particiants filters
          boolean injectParams = getPropValueAsBoolean("injectParams");
          String filterParamBaseTemplate = getPropValue("filterParamBaseTemplate");
          if(filterParamBaseTemplate.equals("default")){
              setPropValue("injectedTemplateFull",getTemplateInstanceText("observationFilterParams" + firstWizardStep));
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("observation" + filterParamBaseTemplate));
          }
          if(injectParams){
              injectTemplate("observationFilterParams");
          }

          //and the filter param queries
          String filterParamQueryBaseTemplate = getPropValue("filterParamQueryBaseTemplate");
          if(filterParamQueryBaseTemplate.equals("default")){
              if(studyType.equals("CaseControl")){
                  setPropValue("injectedTemplateFull",getTemplateInstanceText("observationFilterParamQueries" + studyType + firstWizardStep));
              }else{
                  setPropValue("injectedTemplateFull",getTemplateInstanceText("observationFilterParamQueries" + firstWizardStep));
              }
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("observation" + filterParamQueryBaseTemplate));
          }
          if(injectParams){
              injectTemplate("observationFilterParamQueries");
          }

      }

      //Samples
      if(hasSampleQuestion && hasSamples){
          //Inject the metadata query
          String queryBaseTemplate = getPropValue("queryBaseTemplate");
          // always use default template for samples
          setPropValue("injectedTemplateFull",getTemplateInstanceText("sampleQuery" + firstWizardStep + (hasStudyDetailsStep ? "SD" : "")));
          injectTemplate("sampleMetadataQuery");
          
          //Inject the filter params .... note these use the ontology queries from particiants filters
          boolean injectParams = getPropValueAsBoolean("injectParams");
          String filterParamBaseTemplate = getPropValue("filterParamBaseTemplate");
          //always use default
          setPropValue("injectedTemplateFull",getTemplateInstanceText("sampleFilterParams" + firstWizardStep + (hasStudyDetailsStep ? "SD" : "")));
          if(injectParams){
              injectTemplate("sampleFilterParams");
          }

          //and the filter param queries
          String filterParamQueryBaseTemplate = getPropValue("filterParamQueryBaseTemplate");
          //only default
          setPropValue("injectedTemplateFull",getTemplateInstanceText("sampleFilterParamQueries" + firstWizardStep + (hasStudyDetailsStep ? "SD" : "")));
          if(injectParams){
              injectTemplate("sampleFilterParamQueries");
          }

      }

      //Households
      if(hasHouseholdQuestion && hasHouseholds){
          //Inject the metadata query
          String queryBaseTemplate = getPropValue("queryBaseTemplate");
          if(queryBaseTemplate.equals("default")){
              setPropValue("injectedTemplateFull",getTemplateInstanceText("householdMetadataQuery" + firstWizardStep));
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("household" + queryBaseTemplate));
          }
          injectTemplate("householdMetadataQuery");
          
          //Inject the filter params .... note these use the ontology queries from particiants filters
          String filterParamBaseTemplate = getPropValue("filterParamBaseTemplate");
          boolean injectParams = getPropValueAsBoolean("injectParams");
          if(filterParamBaseTemplate.equals("default")){
              setPropValue("injectedTemplateFull",getTemplateInstanceText("householdFilterParams" + firstWizardStep));
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("household" + filterParamBaseTemplate));
          }
          if(injectParams){
              injectTemplate("householdFilterParams");
          }

          //and the filter param queries
          String filterParamQueryBaseTemplate = getPropValue("filterParamQueryBaseTemplate");
          if(filterParamQueryBaseTemplate.equals("default")){
              if(studyType.equals("CaseControl")){
                  setPropValue("injectedTemplateFull",getTemplateInstanceText("householdFilterParamQueries" + studyType + firstWizardStep));
              }else{
                  setPropValue("injectedTemplateFull",getTemplateInstanceText("householdFilterParamQueries" + firstWizardStep));
              }
          }else{
              setPropValue("injectedTemplateFull",getTemplateInstanceText("household" + filterParamQueryBaseTemplate));
          }
          if(injectParams){
              injectTemplate("householdFilterParamQueries");
          }

      }
      //and inject the cardQuestions and projectAvailability to drive home page
      injectCardQuestions();
      injectProjectAvailability();
      
    }

    public String getCardQuestionString(){
        String presenterId = getPropValue("presenterId");
        boolean hasHouseholdQuestion = getPropValueAsBoolean("hasHouseholdQuestion");
        boolean hasParticipantQuestion = getPropValueAsBoolean("hasParticipantQuestion");
        boolean hasObservationQuestion = getPropValueAsBoolean("hasObservationQuestion");
        boolean hasSampleQuestion = getPropValueAsBoolean("hasSampleQuestion");
	//boolean hasHouseholdDataCollection = getPropValueAsBoolean("hasHouseholdDataCollection");
        boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
        boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
        boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
        boolean hasSamples = getPropValueAsBoolean("hasSampleRecord");
        String cardQuestions = "";
        if(hasParticipantQuestion && hasParticipants){
            for (Map.Entry<String, String[]> entry : participantQuestionTemplateNamesToScopes().entrySet()) {
                String questionName = entry.getKey();
                if(questionName.startsWith("ParticipantsByMetadata")){
                    cardQuestions = cardQuestions + " \"participants\": \"ParticipantQuestions." + presenterId + "ParticipantsByMetadata\"";
                }else{                  
                    cardQuestions = cardQuestions + " \"participants\": \"ParticipantQuestions." + questionName + "\"";
                }
            }
        }
        if(hasObservationQuestion && hasObservations){
            for (Map.Entry<String, String[]> entry : observationQuestionTemplateNamesToScopes().entrySet()) {
                String questionName = entry.getKey();
                if(questionName.startsWith("ObservationsByMetadata")){
                    cardQuestions = cardQuestions + (hasParticipantQuestion ? ", " : "") + "\"observations\": \"ClinicalVisitQuestions." + presenterId + "ObservationsByMetadata\"";
                }else{
                        cardQuestions = cardQuestions + (hasParticipantQuestion ? ", " : "") + "\"observations\": \"ClinicalVisitQuestions." + questionName + "\"";
                }
            }
        }
        if(hasHouseholdQuestion && hasHouseholds){
            for (Map.Entry<String, String[]> entry : householdQuestionTemplateNamesToScopes().entrySet()) {
                String questionName = entry.getKey();
                if(questionName.startsWith("HouseholdsByMetadata")){
                    cardQuestions = cardQuestions + (hasParticipantQuestion || hasObservationQuestion ? ", " : "") + "\"households\": \"HouseholdQuestions." + presenterId + "HouseholdsByMetadata\"";
                }else{
                    cardQuestions = cardQuestions + (hasParticipantQuestion || hasObservationQuestion ? ", " : "") + "\"households\": \"HouseholdQuestions." + questionName + "\"";
                }
            }
        }
        if(hasSampleQuestion && hasSamples){
            for (Map.Entry<String, String[]> entry : sampleQuestionTemplateNamesToScopes().entrySet()) {
                String questionName = entry.getKey();
                if(questionName.startsWith("SamplesByMetadata")){
                    cardQuestions = cardQuestions + (hasParticipantQuestion || hasObservationQuestion || hasHouseholdQuestion ? ", " : "") + "\"samples\": \"SampleQuestions." + presenterId + "SamplesByMetadata\"";
                }else{
                        cardQuestions = cardQuestions + (hasParticipantQuestion || hasObservationQuestion || hasHouseholdQuestion ? ", " : "") + "\"observations\": \"ClinicalVisitQuestions." + questionName + "\"";
                }
            }
        }
        return cardQuestions;
    }

    private void injectCardQuestions() {
        String presenterId = getPropValue("presenterId");
        String cardQuestions = "UNION select '" + presenterId + "' as dataset_presenter_id, 'cardQuestions' as property, '{ ";
        cardQuestions = cardQuestions + getCardQuestionString();
        cardQuestions = cardQuestions + " }' as value from dual";
        //System.err.println("cardQuestionsSql=" + cardQuestions);
        setPropValue("cardQuestionsSql",cardQuestions);
        injectTemplate("injectDatasetQuestions");
    }

    private void injectProjectAvailability() {
        String presenterId = getPropValue("presenterId");
        String subProjectName = getPropValue("subProjectName");
        String projectAvailability = "UNION select '" + presenterId + "' as dataset_presenter_id, 'projectAvailability' as property, '[\"" + subProjectName +  "\",\"AllClinEpiDB\"" + (getPropValueAsBoolean("isPublic") ? ",\"ClinEpiDB\"" : "") + "]' as value from dual";
        setPropValue("projectAvailabilitySql",projectAvailability);
        injectTemplate("injectProjectAvailability");
    }

  @Override
  public void addModelReferences() {
      //boolean hasHouseholdDataCollection = getPropValueAsBoolean("hasHouseholdDataCollection");
      boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
      boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
      boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
      boolean hasSampleDataCollected = getPropValueAsBoolean("hasSampleDataCollected");
      boolean hasSamples = getPropValueAsBoolean("hasSampleRecord");
      //    boolean hasObserTableInSQL = getPropValueAsBoolean("ObserTableInSQL");
      boolean hasMicrosTableInPartiPage = getPropValueAsBoolean("hasMicrosTableInPartiPage");
      boolean hasMicrosTableInObserPage = getPropValueAsBoolean("hasMicrosTableInObserPage");
      boolean hasHouseholdObservations = getPropValueAsBoolean("hasHouseholdObservations");
      //boolean hasTreatments = getPropValueAsBoolean("hasTreatments");

      String presenterId = getPropValue("presenterId");
      
      String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
      String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
      String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);
      String sampleRecordClass = makeRecordClassName(SAMPLE_RECORD_CLASS_PREFIX);
      
      if(hasHouseholds) {
          addWdkReference(householdRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI, 0);
          addWdkReference(householdRecordClass, "table", "HouseholdMembers", new String[]{"record"}, CATEGORY_IRI, 0);

          addWdkReference(householdRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI, 0);

          addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI, 0); 


	  if(hasHouseholdObservations){
	      addWdkReference(householdRecordClass, "table", "HouseholdObservationsDownload", new String[]{"download"}, CATEGORY_IRI, 0);
	  }
	  addWdkReference(householdRecordClass, "table", "ParticipantsDownload", new String[]{"download"}, CATEGORY_IRI, 0);
          
	  addWdkReference(householdRecordClass, "table", "ObservationsDownload", new String[]{"download"}, CATEGORY_IRI,0);

	  addWdkReference(householdRecordClass, "table", "SamplesDownload", new String[]{"download"}, CATEGORY_IRI,0);


          for (Map.Entry<String, String[]> entry : householdQuestionTemplateNamesToScopes().entrySet()) {
              String questionName = entry.getKey();
              String questionFullName = "";
              if(questionName.equals("HouseholdsByMetadata")){
                  questionFullName = "HouseholdQuestions." + presenterId + "HouseholdsByMetadata";
                  //System.err.println("Household questionFullName="+questionFullName);
              }else{                  
                  questionFullName = "HouseholdQuestions." + entry.getKey();
              }
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
              String questionName = entry.getKey();
              String questionFullName = "";
              if(questionName.startsWith("ParticipantsByMetadata")){
                  questionFullName = "ParticipantQuestions." + presenterId + "ParticipantsByMetadata";
                  //System.err.println("Participant questionFullName="+questionFullName);
              }else{                  
                  questionFullName = "ParticipantQuestions." + entry.getKey();
              }
              addWdkReference(participantRecordClass, "question", questionFullName, entry.getValue(), CATEGORY_IRI, 0);
          }

	  addWdkReference(participantRecordClass, "table", "ObservationsDownload", new String[]{"download"}, CATEGORY_IRI,0);
	  addWdkReference(participantRecordClass, "table", "SamplesDownload", new String[]{"download"}, CATEGORY_IRI,0); 


	  if(hasHouseholdObservations){
	  addWdkReference(participantRecordClass, "table", "HouseholdsDownload", new String[]{"download"}, CATEGORY_IRI,0); 
	  }

          if(hasSampleDataCollected) {
              addWdkReference(participantRecordClass, "table", "Samples", new String[]{"record"}, CATEGORY_IRI, 0);
          }
	  
	  if(hasMicrosTableInPartiPage) {
              addWdkReference(participantRecordClass, "table", "Micros", new String[]{"record"}, CATEGORY_IRI, 0);
          }


	  //if(hasTreatments){
	  //addWdkReference(participantRecordClass, "table", "TreatmentsDownload", new String[]{"download"}, CATEGORY_IRI,0); 
	  //}


      }

      if(hasObservations) {
          addWdkReference(observationRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI, 0);
          // TODO Samples table of observation record page

          addWdkReference(observationRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI, 0);
	  addWdkReference(observationRecordClass, "table", "SamplesDownload", new String[]{"download"}, CATEGORY_IRI,0);

	  //addWdkReference(observationRecordClass, "table", "HouseholdsDownload", new String[]{"download"}, CATEGORY_IRI,0);

          addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationssBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI, 0); 

          for (Map.Entry<String, String[]> entry : observationQuestionTemplateNamesToScopes().entrySet()) {
              String questionName = entry.getKey();
              String questionFullName = "";
              if(questionName.startsWith("ObservationsByMetadata")){
                  questionFullName = "ClinicalVisitQuestions." + presenterId + "ObservationsByMetadata";
                  //System.err.println("Observations questionFullName="+questionFullName);
              }else{                  
                  questionFullName = "ClinicalVisitQuestions." + entry.getKey();
              }
              addWdkReference(observationRecordClass, "question", questionFullName, entry.getValue(), CATEGORY_IRI, 0);
          }

	  if(hasHouseholdObservations) {
	      addWdkReference(observationRecordClass, "table", "HouseholdsDownload", new String[]{"download"}, CATEGORY_IRI,0);
          }
	  
	  if(hasSampleDataCollected) {
              addWdkReference(observationRecordClass, "table", "Samples", new String[]{"record"}, CATEGORY_IRI, 0);
          }
	  
          if(hasMicrosTableInObserPage) {
              addWdkReference(observationRecordClass, "table", "MicrosInObser", new String[]{"record"}, CATEGORY_IRI, 0);
          }
      }



      if(hasSamples){
	  
	  addWdkReference(sampleRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI, 0);
	  addWdkReference(sampleRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI, 0);
	  
	  //addWdkReference(sampleRecordClass, "question", "SampleQuestions." + presenterId + "SamplesBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI, 0); 
	  
	  //addWdkReference(sampleRecordClass, "table", "ParticipantsDownload", new String[]{"download"}, CATEGORY_IRI, 0);
	  //addWdkReference(sampleRecordClass, "table", "ObservationsDownload", new String[]{"download"}, CATEGORY_IRI,0);
	  //addWdkReference(sampleRecordClass, "table", "HouseholdObservationDownload", new String[]{"download"}, CATEGORY_IRI,0);
	  
	  
      }
      

      if(hasHouseholdObservations) {
	  addWdkReference(sampleRecordClass, "table", "HouseholdObservationDownload", new String[]{"download"}, CATEGORY_IRI,0);
      }


      if(hasHouseholds && hasParticipants) {
          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsByHouseholds", new String[]{"webservice"}, CATEGORY_IRI, 0); 
          addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsByParticipants", new String[]{"webservice"}, CATEGORY_IRI, 0); 
      }

      if(hasParticipants && hasObservations) {
          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsByObservations", new String[]{"webservice"}, CATEGORY_IRI, 0); 
          addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationsByParticipants", new String[]{"webservice"}, CATEGORY_IRI, 0); 
      }

      if(hasSamples && hasObservations) {
          addWdkReference(sampleRecordClass, "question", "SampleQuestions." + presenterId + "SamplesByObservations", new String[]{"webservice"}, CATEGORY_IRI, 0); 
          addWdkReference(observationRecordClass, "question", "ObservationQuestions." + presenterId + "ObservationsBySamples", new String[]{"webservice"}, CATEGORY_IRI, 0); 
      }

      if(hasParticipants && hasSamples) {
          addWdkReference(participantRecordClass, "question", "ParticipantQuestions." + presenterId + "ParticipantsBySamples", new String[]{"webservice"}, CATEGORY_IRI, 0); 
          addWdkReference(observationRecordClass, "question", "SampleQuestions." + presenterId + "SamplesByParticipants", new String[]{"webservice"}, CATEGORY_IRI, 0); 
      }
  }


  @Override
  public String[][] getPropertiesDeclaration() {

      String [][] declaration = {
                                 {"injectStudy", ""},  
                                 {"isPublic", ""},
				 {"hasHouseholdRecord", ""},
                                 {"hasObservationRecord", ""},
                                 {"hasParticipantRecord", ""},
                                 {"hasSampleRecord", ""},

				 {"householdAttributesList", ""},
                                 {"householdRecordAttributesList", ""},
                                 //{"householdSourceIdsForHouseholdMemberTable", ""},
                                 {"householdRecordOverview", ""},

                                 {"observationAttributesList", ""},
                                 {"observationRecordAttributesList", ""},
                                 {"observationRecordOverview", ""},

                                 {"sampleAttributesList", ""},
                                 {"sampleRecordAttributesList", ""},
                                 {"sampleRecordOverview", ""},
                                 {"sampleSourceIdsExcludedFromSampleAttributes", ""},

                                 {"participantAttributesList", ""},
                                 {"participantRecordAttributesList", ""},
                                 {"participantRecordOverview", ""},
                                 {"participantSourceIdsExcludedFromParticipantAttributes", ""},
                                 {"observationSourceIdsForParticipantsObservationsTable", ""},
                                 {"observationSourceIdsToOrderParticipantsObservationsTable", ""},
                                 //attributes to be added to the record for the studies.jason replacement
                                 {"studyCategories", ""},
                                 {"studyAccess", ""},
                                 {"policyUrl", ""},
                                 {"cardHeadline", ""},
                                 {"cardPoints", ""},
                                 {"requestProtectionLevel", ""},
                                 {"requestAccessFields", ""},
                                 {"requestEmail", ""},
                                 {"requestEmailBody", ""},
                                 {"requestNeedsApproval", ""},
                                 //properties for injecting metadata queries
                                 {"regionFilterExcludedIds", ""},
                                 {"householdMultiFilterIds", ""},
                                 {"householdFilterExcludedIds", ""},
                                 {"participantMultiFilterIds", ""},
                                 {"participantFilterExcludedIds", ""},
                                 {"observationMultiFilterIds", ""},
                                 {"observationFilterExcludedIds", ""},
                                 {"sampleMultiFilterIds", ""},
                                 {"studyAbbreviation", ""},
                                 {"studyType", ""},
                                 {"hasParticipantQuestion", ""},
                                 {"hasHouseholdQuestion", ""},
                                 {"hasObservationQuestion", ""},
                                 {"hasSampleQuestion", ""},
                                 {"firstWizardStep", ""},
                                 {"hasStudyArmParameter", ""},
                                 {"hasHouseholdObservations", ""},
                                 {"keepRegionInHouseholdFilter", ""},
                                 {"hideRegionVariablesKeepDataInHousehold", ""},
                                 //control of whether to trim the ontology when single leaf or sub-node
                                 {"trimHouseholdMetadataTerms", ""},
                                 {"trimParticipantMetadataTerms", ""},
                                 {"trimObservationMetadataTerms", ""},
                                 {"filterParamBaseTemplate", ""},      //Note these BaseTemplates will have participant etc. 
                                 {"filterParamQueryBaseTemplate", ""}, //prepended as determined by hasxxxQuestion properties
                                 {"queryBaseTemplate", ""},            //also need set of templates for each record with question
                                 {"householdStepName", ""},
                                 {"householdStepDescription", ""},
                                 {"observationStepName", ""},
                                 {"observationStepDescription", ""},
                                 {"hasStudyDetailsStep", ""},
                                 {"studyDetailsStepName", ""},
                                 {"studyDetailsStepDescription", ""},
                                 {"studyDetailsOntologyIds", ""},
                                 {"timepointUnits", ""},
                                 {"timeColumnName", ""},
                                 {"timeSourceId", ""}
      };

    return declaration;
  }

}
