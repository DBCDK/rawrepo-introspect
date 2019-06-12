/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo.bean;

import dk.dbc.rawrepo.RecordServiceConnector;
import dk.dbc.rawrepo.RecordServiceConnectorException;
import dk.dbc.rawrepo.RecordServiceConnectorFactory;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import java.util.Arrays;
import java.util.List;

@Stateless
public class RawrepoBean {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(RawrepoBean.class);

    private RecordServiceConnector rawRepoRecordServiceConnector;

    @PostConstruct
    public void init() {
        final String rawrepoRecordServiceURL = System.getenv().getOrDefault("RAWREPO_RECORD_SERVICE_URL", "RAWREPO_RECORD_SERVICE_URL is missing");

        this.rawRepoRecordServiceConnector = RecordServiceConnectorFactory.create(rawrepoRecordServiceURL);
    }

    public List<Integer> getAllAgenciesForBibliographicRecordId(String bibliographicRecordId) throws RecordServiceConnectorException {
        Integer[] agencyArray = rawRepoRecordServiceConnector.getAllAgenciesForBibliographicRecordId(bibliographicRecordId);

        List<Integer> agencies = Arrays.asList(agencyArray);

        return agencies;
    }

}
