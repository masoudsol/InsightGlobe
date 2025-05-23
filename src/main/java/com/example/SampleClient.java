package com.example;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.context.PerformanceOptionsEnum;
import ca.uhn.fhir.rest.client.api.IGenericClient;
import ca.uhn.fhir.rest.client.api.ServerValidationModeEnum;
import ca.uhn.fhir.rest.client.interceptor.LoggingInterceptor;

import org.hl7.fhir.r4.model.Bundle;
import org.hl7.fhir.r4.model.Patient;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

public class SampleClient {

    public static void main(String[] args) throws IOException {

        List<String> lastNames = Files.readAllLines(Paths.get("/Users/masoudsoleimani/Develop/demo/target/classes/lastnames.txt"));

        // Loop 1 and 2: With caching enabled
        for (int i = 1; i <= 2; i++) {
            System.out.println("=== Loop " + i + " (Caching Enabled) ===");
            runSearchLoop(lastNames, true);
        }

        // Loop 3: With caching disabled
        System.out.println("=== Loop 3 (Caching Disabled) ===");
        runSearchLoop(lastNames, false);
    }

    private static void runSearchLoop(List<String> lastNames, boolean useCaching) {

        FhirContext fhirContext = FhirContext.forR4();
        fhirContext.getRestfulClientFactory().setSocketTimeout(20_000); // 20s timeout

        if (!useCaching) {
            fhirContext.getRestfulClientFactory().setServerValidationMode(ServerValidationModeEnum.NEVER);
            fhirContext.setPerformanceOptions(PerformanceOptionsEnum.DEFERRED_MODEL_SCANNING);
        }

        IGenericClient client = fhirContext.newRestfulGenericClient("http://hapi.fhir.org/baseR4");

        client.registerInterceptor(new LoggingInterceptor(false));
        ResponseTimeInterceptor timingInterceptor = new ResponseTimeInterceptor();
        client.registerInterceptor(timingInterceptor);
        for (String lastName : lastNames) {
            
            Bundle bundle = client
                    .search()
                    .forResource("Patient")
                    .where(Patient.FAMILY.matches().value(lastName))
                    .returnBundle(Bundle.class)
                    .execute();
            // Optional: Print result count
            System.out.println("LastName: " + lastName + " - Results: " + bundle.getEntry().size());
        }

        double avg = timingInterceptor.getAverageResponseTime();
        System.out.println("Average Response Time: " + avg + " ms\n");
    }
}
