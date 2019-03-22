package org.apidb.apicommon.model.datasetInjector;

import java.util.Map;

public abstract class EpidemiologyStudyWithLightTraps extends EpidemiologyStudy {

    protected abstract Map<String,String[]> lightTrapQuestionTemplateNamesToScopes();


    @Override
    protected String extraHouseholdTables() {
        return getTemplateInstanceText("householdRecordLightTrapTables");
    }

    @Override
    protected String extraHouseholdTableQueries() {
        return getTemplateInstanceText("householdRecordLightTrapTableQueries");
    }


    @Override
    public void injectTemplates() {
        super.injectTemplates();

        String householdSourceIdsIncludedInLightTrapAttributes = getPropValue("householdSourceIdsIncludedInLightTrapAttributes");
        setPropValue("householdSourceIdsIncludedInLightTrapAttributesQuote", addQuotes(householdSourceIdsIncludedInLightTrapAttributes));
        String lightTrapAttList = getPropValue("lightTrapAttributesList");
        if(lightTrapAttList != null && !lightTrapAttList.equals("")) {
            setPropValue("lightTrapAttributesListFull","<attributesList summary=\"" + lightTrapAttList + "\" />");
        }else{
            setPropValue("lightTrapAttributesListFull","");
        }
        String lightTrapRecordAttList = getPropValue("lightTrapRecordAttributesList");
        if(lightTrapRecordAttList != null && !lightTrapRecordAttList.equals("")) {
            setPropValue("lightTrapRecordAttributesListFull","<attributesList summary=\"" + lightTrapRecordAttList + "\" />");
        }else{
            setPropValue("lightTrapRecordAttributesListFull","");
        }

        // Record
        injectTemplate("lightTrapRecord");
        injectTemplate("lightTrapRecordAttributeQueries");
        injectTemplate("lightTrapRecordTableQueries");
          
        // LightTrap SourceId Question
        injectTemplate("lightTrapSourceIdQuestion");
        injectTemplate("lightTrapSourceIdQuery");
        injectTemplate("lightTrapSourceIdParam");

        injectTemplate("lightTrapResultParam");

        boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");

        for(String key : lightTrapQuestionTemplateNamesToScopes().keySet()) {
            setPropValue("lightTrapQuestionName", key);
            setPropValue("lightTrapQuestionFull", getTemplateInstanceText(key));
            injectTemplate("lightTrapsByDataset");
        }

        // Transforms
        if(hasHouseholds) {
          injectTemplate("householdsByLightTrapsQuestion");
          injectTemplate("householdsByLightTrapsQuery");

          injectTemplate("lightTrapsByHouseholdsQuestion");
          injectTemplate("lightTrapsByHouseholdsQuery");
        }
        
        String presenterId = getPropValue("presenterId");
        injectAttributeMetaQuery(makeRecordClassName("LightTrap"), presenterId + "LightTrapAttributes.LightTrapAttributesMeta",null);
    }

    @Override
    public void injectMetadataQueries() {
        super.injectMetadataQueries();
        //String tblPrefix = "D" + getPropValue("datasetDigest");
        //String presenterId = getPropValue("presenterId");
        //String studyType = getPropValue("studyType");
        String firstWizardStep = getPropValue("firstWizardStep");
        Boolean keepRegionInHouseholdFilter = getPropValueAsBoolean("keepRegionInHouseholdFilter");
        if(firstWizardStep.equals("Household") || keepRegionInHouseholdFilter){
            setPropValue("rmRegionSqlCommentStart","/*");
            setPropValue("rmRegionSqlCommentEnd","*/");
        }else{
            setPropValue("rmRegionSqlCommentStart","");
            setPropValue("rmRegionSqlCommentEnd","");
        }
        String lightTrapMultiFilterIdsQuoted = addQuotes(getPropValue("lightTrapMultiFilterIds"));
        String lightTrapFilterExcludedIdsQuoted = addQuotes(getPropValue("lightTrapFilterExcludedIds"));
        if(lightTrapMultiFilterIdsQuoted == null || lightTrapMultiFilterIdsQuoted.equals("''")) {
            lightTrapMultiFilterIdsQuoted = "'NA'";
        }
        setPropValue("lightTrapMultiFilterIdsQuoted", lightTrapMultiFilterIdsQuoted);
        if(lightTrapFilterExcludedIdsQuoted == null || lightTrapFilterExcludedIdsQuoted.equals("''")) {
            lightTrapFilterExcludedIdsQuoted  = "'NA'";
        }
        setPropValue("lightTrapFilterExcludedIdsQuoted", lightTrapFilterExcludedIdsQuoted);
        
        //Inject the metadata query
        String queryBaseTemplate = getPropValue("queryBaseTemplate");
        if(queryBaseTemplate.equals("default")){
            setPropValue("injectedTemplateFull",getTemplateInstanceText("lightTrapMetadataQuery" + firstWizardStep));
        }else{
            setPropValue("injectedTemplateFull",getTemplateInstanceText("lightTrap" + queryBaseTemplate));
        }
        injectTemplate("lightTrapMetadataQuery");
        
        //Inject the filter params .... note these use the ontology queries from particiants filters
        boolean injectParams = getPropValueAsBoolean("injectParams");
        String filterParamBaseTemplate = getPropValue("filterParamBaseTemplate");
        if(filterParamBaseTemplate.equals("default")){
            setPropValue("injectedTemplateFull",getTemplateInstanceText("lightTrapFilterParams" + firstWizardStep));
        }else{
            setPropValue("injectedTemplateFull",getTemplateInstanceText("lightTrap" + filterParamBaseTemplate));
        }
        if(injectParams){
            injectTemplate("lightTrapFilterParams");
        }
        
        //and the filter param queries
        String filterParamQueryBaseTemplate = getPropValue("filterParamQueryBaseTemplate");
        if(filterParamQueryBaseTemplate.equals("default")){
            setPropValue("injectedTemplateFull",getTemplateInstanceText("lightTrapFilterParamQueries" + firstWizardStep));
        }else{
            setPropValue("injectedTemplateFull",getTemplateInstanceText("lightTrap" + filterParamQueryBaseTemplate));
        }
        if(injectParams){
            injectTemplate("lightTrapFilterParamQueries");
        }

    }

    @Override
    public String getCardQuestionString(){
        String presenterId = getPropValue("presenterId");
        //boolean hasHouseholdQuestion = getPropValueAsBoolean("hasHouseholdQuestion");
        boolean hasParticipantQuestion = getPropValueAsBoolean("hasParticipantQuestion");
        //boolean hasObservationQuestion = getPropValueAsBoolean("hasObservationQuestion");
        //boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");
        boolean hasParticipants = getPropValueAsBoolean("hasParticipantRecord");
        //boolean hasObservations = getPropValueAsBoolean("hasObservationRecord");
        String cardQuestions = super.getCardQuestionString();
        if(hasParticipantQuestion && hasParticipants){
            for (Map.Entry<String, String[]> entry : lightTrapQuestionTemplateNamesToScopes().entrySet()) {
                String questionName = entry.getKey();
                if(questionName.startsWith("LightTrapsByMetadata")){
                    cardQuestions = cardQuestions + ", \"lighttraps\": \"LighttrapQuestions." + presenterId + "LighttrapsByMetadata\"";
                }else{                  
                    cardQuestions = cardQuestions + ", \"lighttraps\": \"LighttrapQuestions." + questionName + "\"";
                }
            }
        }
        return cardQuestions;
    }

    public static final String LIGHT_TRAP_RECORD_CLASS_PREFIX = "LightTrap";

    @Override
    public void addModelReferences() {
        super.addModelReferences();

        String presenterId = getPropValue("presenterId");

        boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");

        String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
        //String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
        //String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);
        String lightTrapRecordClass = makeRecordClassName(LIGHT_TRAP_RECORD_CLASS_PREFIX);

        addWdkReference(lightTrapRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI, 0);
        addWdkReference(lightTrapRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI, 0);

        addWdkReference(lightTrapRecordClass, "question", "LighttrapQuestions." + presenterId + "CollectionsBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI, 0); 

        for (Map.Entry<String, String[]> entry : lightTrapQuestionTemplateNamesToScopes().entrySet()) {
            String questionFullName = "LighttrapQuestions." + entry.getKey();
            addWdkReference(lightTrapRecordClass, "question", questionFullName, entry.getValue(), CATEGORY_IRI, 0);
        }

        if(hasHouseholds) {
            String lightTrapSourceIdsForHouseholdsLightTrapTable = getPropValue("lightTrapSourceIdsForHouseholdsLightTrapTable");
            setPropValue("lightTrapSourceIdsForHouseholdsLightTrapTableSubquery", propertySourceIdSubquery(lightTrapSourceIdsForHouseholdsLightTrapTable));

            addWdkReference(lightTrapRecordClass, "question", "LighttrapQuestions." + presenterId + "CollectionsByHouseholdId", new String[]{"webservice"}, CATEGORY_IRI, 0); 
            addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsByLightTrapId", new String[]{"webservice"}, CATEGORY_IRI, 0); 

            addWdkReference(householdRecordClass, "table", "LightTraps", new String[]{"record"}, CATEGORY_IRI, 0);
        }

    }



  @Override
  public String[][] getPropertiesDeclaration() {
        String[][] exprDeclaration = super.getPropertiesDeclaration();
        
        String [][] declaration = {{"householdSourceIdsIncludedInLightTrapAttributes", ""},
                                   {"lightTrapRecordOverview", ""},
                                   {"lightTrapAttributesList", ""},
                                   {"lightTrapRecordAttributesList", ""},
                                   {"lightTrapSourceIdsForHouseholdsLightTrapTable", ""},
                                   {"lightTrapSourceIdsToOrderHouseholdsLightTrapTable", ""},
                                   {"lightTrapMultiFilterIds", ""},
                                   {"lightTrapFilterExcludedIds", ""},
        };

        return combinePropertiesDeclarations(exprDeclaration, declaration);
  }


}
