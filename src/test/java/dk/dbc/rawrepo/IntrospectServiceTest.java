package dk.dbc.rawrepo;

import dk.dbc.commons.jsonb.JSONBContext;
import dk.dbc.rawrepo.dto.RecordEntryDTO;
import dk.dbc.rawrepo.dto.RecordHistoryCollectionDTO;
import dk.dbc.rawrepo.dto.RecordHistoryDTO;
import dk.dbc.rawrepo.dto.RecordIdDTO;
import dk.dbc.rawrepo.record.RecordServiceConnector;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.stream.Collectors;

import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_LINE;
import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_STDHENTDM2;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class IntrospectServiceTest {

    private static final JSONBContext JSONB = new JSONBContext();

    private String loadFileContent(String filename) throws Exception {
        final File file = new File("src/test/resources/" + filename);
        final FileInputStream fstream = new FileInputStream(file);

        return new BufferedReader(new InputStreamReader(fstream)).lines().collect(Collectors.joining("\n"));
    }

    private static IntrospectService service;

    @BeforeAll
    public static void before() {
        service = new IntrospectService();
        service.rawRepoRecordServiceConnector = mock(RecordServiceConnector.class);
    }

    @Test
    void testGetAllAgenciesFor() throws Exception {
        when(service.rawRepoRecordServiceConnector.getAllAgenciesForBibliographicRecordId("12345678")).thenReturn(new Integer[]{191919, 870970});

        final Response response = service.getAllAgenciesForBibliographicRecordId("12345678");

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is("[191919,870970]"));
    }

    @Test
    void testGetRecord() throws Exception {
        final String reply = loadFileContent("record-data-from-record-service.json");
        final RecordEntryDTO recordEntryDTO = JSONB.unmarshall(reply, RecordEntryDTO.class);

        when(service.rawRepoRecordServiceConnector.getRecordData(anyInt(), anyString(), any(RecordServiceConnector.Params.class))).thenReturn(recordEntryDTO);

        final Response response = service.getRecord("22058037", 191919, FORMAT_LINE, "EXPANDED", false);

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is(loadFileContent("get-record-output.json")));
    }

    @Test
    void testGetRecord_STDHENTDM2() throws Exception {
        final String reply = loadFileContent("record-data-from-record-service.json");
        final RecordEntryDTO recordEntryDTO = JSONB.unmarshall(reply, RecordEntryDTO.class);

        when(service.rawRepoRecordServiceConnector.getRecordData(anyInt(), anyString(), any(RecordServiceConnector.Params.class))).thenReturn(recordEntryDTO);

        final Response response = service.getRecord("22058037", 191919, FORMAT_STDHENTDM2, "EXPANDED", false);

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is(loadFileContent("get-record-output-stdhentdm2.json")));
    }

    @Test
    void testGetHistory() throws Exception {
        final RecordHistoryCollectionDTO recordHistoryCollection = new RecordHistoryCollectionDTO();

        final RecordHistoryDTO recordHistory1 = new RecordHistoryDTO();
        recordHistory1.setId(new RecordIdDTO("44783851", 870970));
        recordHistory1.setCreated("2010-01-27T23:00:00Z");
        recordHistory1.setDeleted(false);
        recordHistory1.setMimeType("text/marcxchange");
        recordHistory1.setModified("2016-06-15T08:58:06.640Z");
        recordHistory1.setTrackingId("");

        final RecordHistoryDTO recordHistory2 = new RecordHistoryDTO();
        recordHistory2.setId(new RecordIdDTO("44783851", 870970));
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
    void testGetHistoricRecord() throws Exception {
        final String reply = loadFileContent("historic-record-data-from-record-service.json");
        final RecordEntryDTO recordEntryDTO = JSONB.unmarshall(reply, RecordEntryDTO.class);

        when(service.rawRepoRecordServiceConnector.getHistoricRecord("870970", "44783851", "2015-03-16T23:35:30.467032Z")).thenReturn(recordEntryDTO);

        final Response response = service.getHistoricRecord("44783851", 870970, "2015-03-16T23:35:30.467032Z", FORMAT_LINE);

        assertThat(response, is(notNullValue()));
        assertThat(response.getStatus(), is(200));
        assertThat(response.hasEntity(), is(true));
        assertThat(response.getEntity(), is(loadFileContent("get-historic-record-output-line.json")));
    }

}
