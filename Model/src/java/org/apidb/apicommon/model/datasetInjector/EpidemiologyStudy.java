package org.apidb.apicommon.model.datasetInjector;


import org.apidb.apicommon.datasetPresenter.DatasetInjector;

public class EpidemiologyStudy extends DatasetInjector {


  @Override
  public void injectTemplates() {}

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
    String[][] propertiesDeclaration = {};
    return propertiesDeclaration;
  }


}
