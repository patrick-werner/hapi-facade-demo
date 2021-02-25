package demo;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.RestfulServer;
import ca.uhn.fhir.rest.server.interceptor.ResponseHighlighterInterceptor;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;

@WebServlet("/*")
public class SimpleServer extends RestfulServer {

  @Override
  protected void initialize() throws ServletException {
    // Create a context for the appropriate version
    setFhirContext(FhirContext.forR4());

    // Register resource providers
    registerProvider(new PatientProvider());
    registerProvider(new EncounterProvider());

    // Format the responses in nice HTML
    registerInterceptor(new ResponseHighlighterInterceptor());
  }
}