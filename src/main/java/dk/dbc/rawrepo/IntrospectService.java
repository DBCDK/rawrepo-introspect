package dk.dbc.rawrepo;

import dk.dbc.commons.jsonb.JSONBContext;
import dk.dbc.commons.jsonb.JSONBException;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.writer.MarcWriterException;
import dk.dbc.rawrepo.dto.ConfigDTO;
import dk.dbc.rawrepo.dto.EdgeDTO;
import dk.dbc.rawrepo.dto.RecordDTO;
import dk.dbc.rawrepo.dto.RecordHistoryCollectionDTO;
import dk.dbc.rawrepo.dto.RecordHistoryDTO;
import dk.dbc.rawrepo.dto.RecordIdDTO;
import dk.dbc.rawrepo.dto.RecordPartsDTO;
import dk.dbc.rawrepo.dto.RelationDTO;
import dk.dbc.rawrepo.record.RecordServiceConnector;
import dk.dbc.rawrepo.record.RecordServiceConnectorException;
import dk.dbc.rawrepo.utils.RecordDataTransformer;
import dk.dbc.util.StopwatchInterceptor;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.transform.TransformerException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_LINE;
import static dk.dbc.rawrepo.utils.RecordDataTransformer.FORMAT_STDHENTDM2;
import static dk.dbc.rawrepo.utils.RecordDataTransformer.SUPPORTED_FORMATS;

@Interceptors(StopwatchInterceptor.class)
@Stateless
@Path("")
public class IntrospectService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntrospectService.class);
    private final JSONBContext mapper = new JSONBContext();

    @Inject
    RecordServiceConnector rawRepoRecordServiceConnector;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/agencies-for/{bibliographicRecordId}")
    public Response getAllAgenciesForBibliographicRecordId(@PathParam("bibliographicRecordId") String bibliographicRecordId) {
        String res;

        try {
            final List<Integer> agencies = Arrays.asList(rawRepoRecordServiceConnector.getAllAgenciesForBibliographicRecordId(bibliographicRecordId));

            res = mapper.marshall(agencies);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException | RecordServiceConnectorException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/record/{bibliographicRecordId}/{agencyId}")
    public Response getRecord(@PathParam("bibliographicRecordId") String bibliographicRecordId,
                              @PathParam("agencyId") int agencyId,
                              @DefaultValue(FORMAT_LINE) @QueryParam("format") String format,
                              @DefaultValue("MERGED") @QueryParam("mode") String mode,
                              @DefaultValue("false") @QueryParam("diffEnrichment") boolean diffEnrichment) {
        String res;

        try {
            // Validate input
            if (!SUPPORTED_FORMATS.contains(format.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            if (!Arrays.asList("RAW", "MERGED", "EXPANDED").contains(mode.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            final RecordServiceConnector.Params params = new RecordServiceConnector.Params();
            params.withMode(RecordServiceConnector.Params.Mode.valueOf(mode.toUpperCase()));
            params.withAllowDeleted(true);

            RecordPartsDTO recordPartsDTO;
            final RecordDTO recordData = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, params);
            final Charset charset = FORMAT_STDHENTDM2.equalsIgnoreCase(format) ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;

            if (Arrays.asList("MERGED", "EXPANDED").contains(mode.toUpperCase()) && diffEnrichment) {
                final RecordServiceConnector.Params enrichmentParams = new RecordServiceConnector.Params();
                enrichmentParams.withMode(RecordServiceConnector.Params.Mode.RAW);
                enrichmentParams.withAllowDeleted(true);

                final RecordDTO enrichmentData = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, enrichmentParams);
                recordPartsDTO = RecordDataTransformer.recordDiffToDTO(recordData, enrichmentData, format, charset);
            } else {
                recordPartsDTO = RecordDataTransformer.recordDataToDTO(recordData, format, charset);
            }

            res = mapper.marshall(recordPartsDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).encoding(charset.name()).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/record/{bibliographicRecordId}/{agencyId}/history")
    public Response getRecordHistory(@PathParam("bibliographicRecordId") String bibliographicRecordId,
                                     @PathParam("agencyId") int agencyId) {
        String res;

        try {
            final RecordHistoryCollectionDTO recordHistoryCollection = rawRepoRecordServiceConnector.getRecordHistory(Integer.toString(agencyId), bibliographicRecordId);
            final List<RecordHistoryDTO> recordHistoryList = recordHistoryCollection.getRecordHistoryList();

            res = mapper.marshall(recordHistoryList);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (RecordServiceConnectorException | JSONBException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/record/{bibliographicRecordId}/{agencyId}/{modifiedDate}")
    public Response getHistoricRecord(@PathParam("bibliographicRecordId") String bibliographicRecordId,
                                      @PathParam("agencyId") int agencyId,
                                      @PathParam("modifiedDate") String modifiedDate,
                                      @DefaultValue(FORMAT_LINE) @QueryParam("format") String format) {
        String res;

        try {
            // Validate input
            if (!SUPPORTED_FORMATS.contains(format.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            final RecordDTO recordData = rawRepoRecordServiceConnector.getHistoricRecord(Integer.toString(agencyId), bibliographicRecordId, modifiedDate);
            final Charset charset = FORMAT_STDHENTDM2.equalsIgnoreCase(format) ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;

            RecordPartsDTO recordPartsDTO = RecordDataTransformer.recordDataToDTO(recordData, format, charset);

            res = mapper.marshall(recordPartsDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).encoding(charset.name()).build();
        } catch (RecordServiceConnectorException | MarcReaderException | MarcWriterException | TransformerException |
                 JSONBException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/record/{bibliographicRecordId}/{agencyId}/diff/{versions}")
    public Response getRecordDiff(@PathParam("bibliographicRecordId") String bibliographicRecordId,
                                  @PathParam("agencyId") int agencyId,
                                  @PathParam("versions") String versions,
                                  @DefaultValue(FORMAT_LINE) @QueryParam("format") String format) {
        String res;

        try {
            String[] versionList = versions.split("\\|");

            if (!SUPPORTED_FORMATS.contains(format.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            if (versionList.length != 2) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            String version1 = versionList[0];
            String version2 = versionList[1];

            RecordDTO recordData1;
            RecordDTO recordData2;

            final RecordServiceConnector.Params params = new RecordServiceConnector.Params();
            params.withMode(RecordServiceConnector.Params.Mode.RAW);
            params.withAllowDeleted(true);

            if ("current".equals(version1)) {
                recordData1 = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, params);
            } else {
                recordData1 = rawRepoRecordServiceConnector.getHistoricRecord(Integer.toString(agencyId), bibliographicRecordId, version1);
            }

            if ("current".equals(version2)) {
                recordData2 = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, params);
            } else {
                recordData2 = rawRepoRecordServiceConnector.getHistoricRecord(Integer.toString(agencyId), bibliographicRecordId, version2);
            }

            final Charset charset = FORMAT_STDHENTDM2.equalsIgnoreCase(format) ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;

            RecordPartsDTO recordPartsDTO = RecordDataTransformer.recordDiffToDTO(recordData1, recordData2, format, charset);

            res = mapper.marshall(recordPartsDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).encoding(charset.name()).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/record/{bibliographicRecordId}/{agencyId}/relations")
    public Response getRelations(@PathParam("bibliographicRecordId") String bibliographicRecordId,
                                 @PathParam("agencyId") int agencyId) {
        String res;

        try {
            RelationDTO relationDTO = new RelationDTO();
            relationDTO.setNodes(Collections.<RecordIdDTO>emptyList());
            relationDTO.setEdges(Collections.<EdgeDTO>emptyList());

            RecordIdDTO currentNode = new RecordIdDTO(bibliographicRecordId, agencyId);
            relationDTO.getNodes().add(currentNode);

            // Children
            RecordIdDTO[] recordIds = rawRepoRecordServiceConnector.getRecordChildren(agencyId, bibliographicRecordId);
            for (RecordIdDTO recordId : recordIds) {
                relationDTO.getNodes().add(recordId);

                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setParent(currentNode);
                edgeDTO.setChild(recordId);
                relationDTO.getEdges().add(edgeDTO);
            }

            // Parents
            recordIds = rawRepoRecordServiceConnector.getRecordParents(agencyId, bibliographicRecordId);
            for (RecordIdDTO recordId : recordIds) {
                relationDTO.getNodes().add(recordId);

                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setParent(recordId);
                edgeDTO.setChild(currentNode);
                relationDTO.getEdges().add(edgeDTO);
            }

            // Siblings from this record
            recordIds = rawRepoRecordServiceConnector.getRecordSiblingsFrom(agencyId, bibliographicRecordId);
            for (RecordIdDTO recordId : recordIds) {
                relationDTO.getNodes().add(recordId);

                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setParent(recordId);
                edgeDTO.setChild(currentNode);
                relationDTO.getEdges().add(edgeDTO);
            }

            // Siblings to this record
            recordIds = rawRepoRecordServiceConnector.getRecordSiblingsTo(agencyId, bibliographicRecordId);
            for (RecordIdDTO recordId : recordIds) {
                relationDTO.getNodes().add(recordId);

                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setParent(currentNode);
                edgeDTO.setChild(recordId);
                relationDTO.getEdges().add(edgeDTO);
            }

            res = mapper.marshall(relationDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (RecordServiceConnectorException | JSONBException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }

}
