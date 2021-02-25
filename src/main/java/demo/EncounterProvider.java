package demo;

import ca.uhn.fhir.rest.annotation.Create;
import ca.uhn.fhir.rest.annotation.IdParam;
import ca.uhn.fhir.rest.annotation.Read;
import ca.uhn.fhir.rest.annotation.ResourceParam;
import ca.uhn.fhir.rest.api.MethodOutcome;
import ca.uhn.fhir.rest.server.IResourceProvider;
import ca.uhn.fhir.rest.server.exceptions.ResourceNotFoundException;
import ca.uhn.fhir.rest.server.exceptions.UnprocessableEntityException;
import org.hl7.fhir.instance.model.api.IBaseResource;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.IdType;

public class EncounterProvider implements IResourceProvider {

  @Override
  public Class<? extends IBaseResource> getResourceType() {
    return Encounter.class;
  }

  /**
   * Simple implementation of the "read" method
   */
  @Read()
  public Encounter read(@IdParam IdType theId) {
    Encounter encounter = DBService.getEncounter(theId.getIdPart());
    if (encounter == null) {
      throw new ResourceNotFoundException(theId);
    }
    return encounter;
  }

  @Create()
  public MethodOutcome createEncounter(@ResourceParam Encounter encounter) {
    if (encounter.getIdentifierFirstRep().isEmpty()) {
      /* It is also possible to pass an OperationOutcome resource
       * to the UnprocessableEntityException if you want to return
       * a custom populated OperationOutcome. Otherwise, a simple one
       * is created using the string supplied below.
       */
      throw new UnprocessableEntityException("No identifier supplied");
    }
    encounter.setId(IdType.newRandomUuid());
    DBService.addEncounter(encounter);
    MethodOutcome retVal = new MethodOutcome();
    retVal.setId(new IdType("Encounter", encounter.getId(), "1"));
    return retVal;
  }
}


