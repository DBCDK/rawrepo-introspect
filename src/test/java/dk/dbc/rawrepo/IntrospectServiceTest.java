package dk.dbc.rawrepo;

import dk.dbc.commons.jsonb.JSONBContext;
import dk.dbc.rawrepo.dto.RecordEntryDTO;
import dk.dbc.rawrepo.dto.RecordHistoryCollectionDTO;
import dk.dbc.rawrepo.dto.RecordHistoryDTO;
import dk.dbc.rawrepo.dto.RecordIdDTO;
import dk.dbc.rawrepo.dto.RelationDTO;
import dk.dbc.rawrepo.record.RecordServiceConnector;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
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

    @Test
    void testRelationsWithoutOptInDoNotLookUpDeletedEnrichments() throws Exception {
        final String recordId = "22252851";
        stubRelations(recordId);

        final Response response = service.getRelations(recordId, 870970, false);

        assertThat(response.getStatus(), is(200));
        assertThat(JSONB.getObjectMapper().readTree((String) response.getEntity()).has("deletedEnrichments"), is(false));
        verify(service.rawRepoRecordServiceConnector, never()).getAllAgenciesForBibliographicRecordId(recordId);
    }

    @Test
    void testRelationsIncludeOnlyDeletedEnrichmentsToTheRequestedParent() throws Exception {
        final String recordId = "22252852";
        stubRelations(recordId);
        final RecordServiceConnector connector = service.rawRepoRecordServiceConnector;
        when(connector.getAllAgenciesForBibliographicRecordId(recordId))
                .thenReturn(new Integer[]{870970, 191919, 300420, 300151, 300153, 300420});
        when(connector.getRecordHistory("300420", recordId)).thenReturn(history(true));
        when(connector.getRecordHistory("300151", recordId)).thenReturn(history(true));
        when(connector.getRecordHistory("300153", recordId)).thenReturn(history(false, true));
        when(connector.getRecordSiblingsFrom(300420, recordId)).thenReturn(new RecordIdDTO[]{new RecordIdDTO(recordId, 870970)});
        when(connector.getRecordSiblingsFrom(300151, recordId)).thenReturn(new RecordIdDTO[]{
                new RecordIdDTO(recordId, 870971), new RecordIdDTO("22252853", 870970)});

        final Response response = service.getRelations(recordId, 870970, true);

        assertThat(response.getStatus(), is(200));
        final RelationDTO relations = JSONB.unmarshall((String) response.getEntity(), RelationDTO.class);
        assertThat(relations.getDeletedEnrichments(), is(List.of(new RecordIdDTO(recordId, 300420))));
        assertThat(relations.getEdges().size(), is(1));
        assertThat(relations.getEdges().get(0).getChild(), is(new RecordIdDTO(recordId, 191919)));
        verify(connector, never()).getRecordHistory("191919", recordId);
        verify(connector, never()).getRecordSiblingsFrom(300153, recordId);
    }

    private void stubRelations(String recordId) throws Exception {
        final RecordServiceConnector connector = service.rawRepoRecordServiceConnector;
        when(connector.getRecordChildren(870970, recordId)).thenReturn(new RecordIdDTO[0]);
        when(connector.getRecordParents(870970, recordId)).thenReturn(new RecordIdDTO[0]);
        when(connector.getRecordSiblingsFrom(870970, recordId)).thenReturn(new RecordIdDTO[0]);
        when(connector.getRecordSiblingsTo(870970, recordId)).thenReturn(new RecordIdDTO[]{new RecordIdDTO(recordId, 191919)});
    }

    private RecordHistoryCollectionDTO history(boolean... deletedVersions) {
        final List<RecordHistoryDTO> versions = new ArrayList<>();
        for (boolean deleted : deletedVersions) {
            final RecordHistoryDTO version = new RecordHistoryDTO();
            version.setDeleted(deleted);
            versions.add(version);
        }
        final RecordHistoryCollectionDTO history = new RecordHistoryCollectionDTO();
        history.setRecordHistoryList(versions);
        return history;
    }

}
