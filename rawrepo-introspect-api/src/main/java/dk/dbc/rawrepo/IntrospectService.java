package dk.dbc.rawrepo;

import dk.dbc.util.StopwatchInterceptor;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Interceptors(StopwatchInterceptor.class)
@Stateless
@Path("api")
public class IntrospectService {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(IntrospectService.class);

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/hello")
    public Response getRecordsSummary() {
        LOGGER.entry();
        String res = "";

        try {
            res = "Hello world";

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } finally {
            LOGGER.exit(res);
        }
    }
}
