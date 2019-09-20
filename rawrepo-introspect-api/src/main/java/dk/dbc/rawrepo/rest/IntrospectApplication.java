/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo.rest;

import dk.dbc.rawrepo.AttachmentService;
import dk.dbc.rawrepo.IntrospectService;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
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
