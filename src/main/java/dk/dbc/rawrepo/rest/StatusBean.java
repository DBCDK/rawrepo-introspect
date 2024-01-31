package dk.dbc.rawrepo.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.Produces;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

@ApplicationScoped
public class StatusBean {


    @Produces
    @Readiness
    public HealthCheck status() {
        return () -> HealthCheckResponse.named("status")
                .status(true)
                .build();
    }

}
