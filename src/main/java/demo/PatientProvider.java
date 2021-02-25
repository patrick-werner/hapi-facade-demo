package demo;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import java.util.Date;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Enumerations.AdministrativeGender;
import org.hl7.fhir.r4.model.HumanName.NameUse;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Identifier;
import org.hl7.fhir.r4.model.Patient;

public class PatientProvider implements IResourceProvider {

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Patient.class;
  }

  public PatientProvider() {
    Patient patient = new Patient();
    patient.setId("testPatient");
    patient.getMeta().addProfile("https://gematik.de/fhir/IsiK/StructureDefinition/IsiKPatient");
    Identifier identifier = patient.addIdentifier();
    identifier.getType().addCoding().setSystem("http://terminology.hl7.org/CodeSystem/v2-0203")
        .setCode("MR");
    identifier.setSystem("https://fhir.krankenhaus.anderesBeispiel/identifiers/PID")
        .setValue("1234");
    patient.addName().addGiven("Petra").setFamily("Musterfrau").setUse(NameUse.OFFICIAL);
    patient.setGender(AdministrativeGender.FEMALE);
    patient.setBirthDate(new Date(1980, 10, 12));
    DBService.addPatient(patient);
  }

  /**
   * Simple implementation of the "read" method
   */
  @Read()
  public Patient read(@IdParam IdType theId) {
    Patient patient = DBService.getPatient(theId.getIdPart());
    if (patient == null) {
      throw new ResourceNotFoundException(theId);
    }
    return patient;
  }

  @Create()
  public MethodOutcome createPatient(@ResourceParam Patient thePatient) {
//Identifier vorhanden?
    if (thePatient.getIdentifierFirstRep().isEmpty()) {
      throw new UnprocessableEntityException("No identifier supplied");
    }

    thePatient.setId(IdType.newRandomUuid());
    DBService.addPatient(thePatient);
    MethodOutcome retVal = new MethodOutcome();
    retVal.setId(new IdType("Patient", thePatient.getId(), "1"));
    return retVal;
  }
}


