package dk.dbc.rawrepo.rest;

import dk.dbc.rawrepo.AttachmentService;
import dk.dbc.rawrepo.IntrospectService;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@ApplicationPath("/api")
public class IntrospectApplication extends Application {
    private static final Set<Class<?>> classes = new HashSet<>(Arrays.asList(IntrospectService.class, AttachmentService.class, StatusBean.class));

    @Override
    public Set<Class<?>> getClasses() {
        return classes;
    }

}
