package demo;

import java.util.HashMap;
import java.util.Map;
import org.hl7.fhir.r4.model.Encounter;
import org.hl7.fhir.r4.model.Patient;

public class DBService {

  private static Map<String, Patient> patients = new HashMap<>();
  private static Map<String, Encounter> encounters = new HashMap<>();

  public static void addPatient(Patient patient) {
    patients.put(patient.getId(), patient);
  }

  public static Patient getPatient(String Id) {
    return patients.get(Id);
  }

  public static void addEncounter(Encounter encounter) {
    encounters.put(encounter.getId(), encounter);
  }

  public static Encounter getEncounter(String Id) {
    return encounters.get(Id);
  }

}
