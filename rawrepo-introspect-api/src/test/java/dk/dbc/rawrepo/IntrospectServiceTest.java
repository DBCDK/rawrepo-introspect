package dk.dbc.rawrepo;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.core.Response;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_LINE;
import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_STDHENTDM2;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntrospectServiceTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(IntrospectServiceTest.class);

    private String loadFileContent(String filename) throws Exception {
        final File file = new File("src/test/resources/" + filename);
        final FileInputStream fstream = new FileInputStream(file);

        return new BufferedReader(new InputStreamReader(fstream)).lines().collect(Collectors.joining("\n"));
    }

    private IntrospectService service;

    @Before
    public void before() {
        service = new IntrospectService();
        service.rawRepoRecordServiceConnector = mock(RecordServiceConnector.class);
    }

    @Test
    public void testGetAllAgenciesFor() throws Exception {
        when(service.rawRepoRecordServiceConnector.getAllAgenciesForBibliographicRecordId("12345678")).thenReturn(new Integer[]{191919, 870970});

        final Response response = service.getAllAgenciesForBibliographicRecordId("12345678");

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is("[191919,870970]"));
    }

    @Test
    public void testGetRecord() throws Exception {
        final RecordData recordData = new RecordData();
        recordData.setContent(loadFileContent("record-data-from-record-service.xml").getBytes());

        when(service.rawRepoRecordServiceConnector.getRecordData(anyInt(), anyString(), any(RecordServiceConnector.Params.class))).thenReturn(recordData);

        final Response response = service.getRecord("22058037", 191919, FORMAT_LINE, "EXPANDED", false);

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is(loadFileContent("get-record-output.json")));
    }

    @Test
    public void testGetRecord_STDHENTDM2() throws Exception {
        final RecordData recordData = new RecordData();
        recordData.setContent(loadFileContent("record-data-from-record-service.xml").getBytes());

        when(service.rawRepoRecordServiceConnector.getRecordData(anyInt(), anyString(), any(RecordServiceConnector.Params.class))).thenReturn(recordData);

        final Response response = service.getRecord("22058037", 191919, FORMAT_STDHENTDM2, "EXPANDED", false);

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is(loadFileContent("get-record-output-stdhentdm2.json")));
    }

    @Test
    public void testGetHistory() throws Exception {
        final RecordHistoryCollection recordHistoryCollection = new RecordHistoryCollection();

        final RecordHistory recordHistory1 = new RecordHistory();
        recordHistory1.setId(new RecordId("44783851", 870970));
        recordHistory1.setCreated("2010-01-27T23:00:00Z");
        recordHistory1.setDeleted(false);
        recordHistory1.setMimeType("text/marcxchange");
        recordHistory1.setModified("2016-06-15T08:58:06.640Z");
        recordHistory1.setTrackingId("");

        final RecordHistory recordHistory2 = new RecordHistory();
        recordHistory2.setId(new RecordId("44783851", 870970));
        recordHistory2.setCreated("2010-01-27T23:00:00Z");
        recordHistory2.setDeleted(false);
        recordHistory2.setMimeType("text/enrichment+marcxchange");
        recordHistory2.setModified("2015-03-16T23:35:30.467032Z");
        recordHistory2.setTrackingId("");

        recordHistoryCollection.setRecordHistoryList(Arrays.asList(recordHistory1, recordHistory2));

        when(service.rawRepoRecordServiceConnector.getRecordHistory("870970", "44783851")).thenReturn(recordHistoryCollection);

        final Response response = service.getRecordHistory("44783851", 870970);

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is(loadFileContent("get-record-history-expected.json")));
    }

    @Test
    public void testGetHistoricRecord() throws Exception {
        final RecordData recordData = new RecordData();
        recordData.setContent(loadFileContent("historic-record-data-from-record-service.xml").getBytes());

        when(service.rawRepoRecordServiceConnector.getHistoricRecord("870970", "44783851", "2015-03-16T23:35:30.467032Z")).thenReturn(recordData);

        final Response response = service.getHistoricRecord("44783851",870970, "2015-03-16T23:35:30.467032Z", FORMAT_LINE);

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is(loadFileContent("get-historic-record-output.json")));
    }

}
