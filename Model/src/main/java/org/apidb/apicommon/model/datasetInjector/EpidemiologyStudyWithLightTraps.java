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
          injectTemplate("lightTrapssByHouseholdsQuery");
        }


    }

    @Override
    public void addModelReferences() {
        super.addModelReferences();

        String categoryIri = "http://edamontology.org/topic_3305";

        String presenterId = getPropValue("presenterId");

        String lightTrapRecordClass = presenterId + "LightTrapRecordClasses." + presenterId + "LightTrapRecordClass";

        addWdkReference(lightTrapRecordClass, "attribute", "record_overview", new String[]{"record-internal"}, categoryIri);
        addWdkReference(lightTrapRecordClass, "table", "Characteristics", new String[]{"record"}, categoryIri);
    }


}
