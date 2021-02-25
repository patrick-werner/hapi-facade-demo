package demo;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.support.DefaultProfileValidationSupport;
import ca.uhn.fhir.validation.FhirValidator;
import ca.uhn.fhir.validation.ValidationResult;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.hl7.fhir.common.hapi.validation.support.CachingValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.CommonCodeSystemsTerminologyService;
import org.hl7.fhir.common.hapi.validation.support.InMemoryTerminologyServerValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.PrePopulatedValidationSupport;
import org.hl7.fhir.common.hapi.validation.support.ValidationSupportChain;
import org.hl7.fhir.common.hapi.validation.validator.FhirInstanceValidator;
import org.hl7.fhir.instance.model.api.IBaseResource;

public class PrimitiveValidationDemo {

  public static void main(String[] args) throws IOException {
    FhirContext ctx = FhirContext.forR4();

// Create a chain that will hold our modules
    ValidationSupportChain supportChain = new ValidationSupportChain();

// DefaultProfileValidationSupport supplies base FHIR definitions. This is generally required
// even if you are using custom profiles, since those profiles will derive from the base
// definitions.
    DefaultProfileValidationSupport defaultSupport = new DefaultProfileValidationSupport(ctx);
    supportChain.addValidationSupport(defaultSupport);

// This module supplies several code systems that are commonly used in validation
    supportChain.addValidationSupport(new CommonCodeSystemsTerminologyService(ctx));

// This module implements terminology services for in-memory code validation
    supportChain.addValidationSupport(new InMemoryTerminologyServerValidationSupport(ctx));

// Create a PrePopulatedValidationSupport which can be used to load custom definitions.
// In this example we're loading two things, but in a real scenario we might
// load many StructureDefinitions, ValueSets, CodeSystems, etc.
    PrePopulatedValidationSupport prePopulatedSupport = new PrePopulatedValidationSupport(ctx);

    Path path = Paths.get("patientProfile.json");
    String input = Files.readString(path);
    IBaseResource resource = ctx.newJsonParser().parseResource(input);

    prePopulatedSupport.addStructureDefinition(resource);
//    prePopulatedSupport.addValueSet(someValueSet);

// Add the custom definitions to the chain
    supportChain.addValidationSupport(prePopulatedSupport);

// Wrap the chain in a cache to improve performance
    CachingValidationSupport cache = new CachingValidationSupport(supportChain);

// Create a validator using the FhirInstanceValidator module. We can use this
// validator to perform validation
    FhirInstanceValidator validatorModule = new FhirInstanceValidator(cache);
    FhirValidator validator = ctx.newValidator().registerValidatorModule(validatorModule);

    path = Paths.get("testInstance.json");
    input = Files.readString(path);

    ValidationResult result = validator.validateWithResult(input);
    System.out.println("validated? : " + result.isSuccessful());
    result.getMessages().forEach(m -> System.out.println(m.getSeverity() + " -+- " + m.getMessage()));
  }

}
