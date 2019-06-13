/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo.bean;

import dk.dbc.rawrepo.RecordData;
import dk.dbc.rawrepo.RecordServiceConnector;
import dk.dbc.rawrepo.RecordServiceConnectorException;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Stateless
public class RawrepoBean {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(RawrepoBean.class);

    @Inject
    private RecordServiceConnector rawRepoRecordServiceConnector;

    public List<Integer> getAllAgenciesForBibliographicRecordId(String bibliographicRecordId) throws RecordServiceConnectorException {
        Integer[] agencyArray = rawRepoRecordServiceConnector.getAllAgenciesForBibliographicRecordId(bibliographicRecordId);

        List<Integer> agencies = Arrays.asList(agencyArray);

        return agencies;
    }

    public RecordData getRecord(String bibliographicRecordId, int agencyId, RecordServiceConnector.Params.Mode mode) throws RecordServiceConnectorException {
        RecordServiceConnector.Params params = new RecordServiceConnector.Params();
        params.withMode(mode);

        RecordData recordData = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, params);

        return recordData;
    }

}
