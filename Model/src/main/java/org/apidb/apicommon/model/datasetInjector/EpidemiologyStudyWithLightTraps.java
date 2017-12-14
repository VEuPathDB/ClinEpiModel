package org.apidb.apicommon.model.datasetInjector;


public class EpidemiologyStudyWithLightTraps extends EpidemiologyStudy {

    @Override
    public void injectTemplates() {
        super.injectTemplates();

        String householdSourceIdsIncludedInLightTrapAttributes = getPropValue("householdSourceIdsIncludedInLightTrapAttributes");
        setPropValue("householdSourceIdsIncludedInLightTrapAttributesQuote", addQuotes(householdSourceIdsIncludedInLightTrapAttributes));

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

        // Transforms
        if(hasHouseholds) {
          injectTemplate("householdsByLightTrapsQuestion");
          injectTemplate("householdsByLightTrapsQuery");

          injectTemplate("lightTrapsByHouseholdsQuestion");
          injectTemplate("lightTrapsByHouseholdsQuery");
        }
        
        String presenterId = getPropValue("presenterId");
        injectAttributeMetaQuery(makeRecordClassName("LightTrap"), presenterId + "LightTrapAttributes.LightTrapAttributesMeta");
    }


    public static final String LIGHT_TRAP_RECORD_CLASS_PREFIX = "LightTrap";

    @Override
    public void addModelReferences() {
        super.addModelReferences();

        String presenterId = getPropValue("presenterId");

        boolean hasHouseholds = getPropValueAsBoolean("hasHouseholdRecord");

        String householdRecordClass = makeRecordClassName(HOUSEHOLD_RECORD_CLASS_PREFIX);
        String participantRecordClass = makeRecordClassName(PARTICIPANT_RECORD_CLASS_PREFIX);
        String observationRecordClass = makeRecordClassName(OBSERVATION_RECORD_CLASS_PREFIX);
        String lightTrapRecordClass = makeRecordClassName(LIGHT_TRAP_RECORD_CLASS_PREFIX);

        addWdkReference(lightTrapRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, CATEGORY_IRI);
        addWdkReference(lightTrapRecordClass, "table", "Characteristics", new String[]{"record"}, CATEGORY_IRI);

        addWdkReference(lightTrapRecordClass, "question", "LighttrapQuestions." + presenterId + "CollectionsBySourceID", new String[]{"menu","webservice"}, CATEGORY_IRI); 

        if(hasHouseholds) {
            addWdkReference(lightTrapRecordClass, "question", "LighttrapQuestions." + presenterId + "CollectionsByHouseholdId", new String[]{"webservice"}, CATEGORY_IRI); 
            addWdkReference(householdRecordClass, "question", "HouseholdQuestions." + presenterId + "HouseholdsByLightTrapId", new String[]{"webservice"}, CATEGORY_IRI); 
        }

    }


}