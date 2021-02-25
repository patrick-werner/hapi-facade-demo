package demo;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import java.util.HashMap;
import java.util.Map;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.IdType;
import org.hl7.fhir.r4.model.Patient;

public class PatientProvider implements IResourceProvider {

  private Map<String, Patient> myPatients = new HashMap<String, Patient>();

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Patient.class;
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


