/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo;

import dk.dbc.holdingsitems.HoldingsItemsException;
import dk.dbc.jsonb.JSONBContext;
import dk.dbc.jsonb.JSONBException;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.writer.MarcWriterException;
import dk.dbc.rawrepo.dao.HoldingsItemsBean;
import dk.dbc.rawrepo.dto.ConfigDTO;
import dk.dbc.rawrepo.dto.EdgeDTO;
import dk.dbc.rawrepo.dto.HoldingsItemsDTO;
import dk.dbc.rawrepo.dto.RecordDTO;
import dk.dbc.rawrepo.dto.RelationDTO;
import dk.dbc.rawrepo.utils.RecordDataTransformer;
import dk.dbc.util.StopwatchInterceptor;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.transform.TransformerException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    @Inject
    @ConfigProperty(name = "INSTANCE", defaultValue = "")
    private String INSTANCE;

    @Inject
    @ConfigProperty(name = "HOLDINGS_ITEMS_INTROSPECT_URL", defaultValue = "")
    private String HOLDINGS_ITEMS_INTROSPECT_URL;

    @EJB
    private HoldingsItemsBean holdingsItemsBean;

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
            LOGGER.error(e.getMessage());
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

            RecordDTO recordDTO;
            final RecordData recordData = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, params);
            final Charset charset = FORMAT_STDHENTDM2.equalsIgnoreCase(format) ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;

            if (Arrays.asList("MERGED", "EXPANDED").contains(mode.toUpperCase()) && diffEnrichment) {
                final RecordServiceConnector.Params enrichmentParams = new RecordServiceConnector.Params();
                enrichmentParams.withMode(RecordServiceConnector.Params.Mode.RAW);
                enrichmentParams.withAllowDeleted(true);

                final RecordData enrichmentData = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, enrichmentParams);
                recordDTO = RecordDataTransformer.recordDiffToDTO(recordData, enrichmentData, format, charset);
            } else {
                recordDTO = RecordDataTransformer.recordDataToDTO(recordData, format, charset);
            }

            res = mapper.marshall(recordDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).encoding(charset.name()).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
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
            final RecordHistoryCollection recordHistoryCollection = rawRepoRecordServiceConnector.getRecordHistory(Integer.toString(agencyId), bibliographicRecordId);
            final List<RecordHistory> recordHistoryList = recordHistoryCollection.getRecordHistoryList();

            res = mapper.marshall(recordHistoryList);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (RecordServiceConnectorException | JSONBException e) {
            LOGGER.error(e.getMessage());
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

            final RecordData recordData = rawRepoRecordServiceConnector.getHistoricRecord(Integer.toString(agencyId), bibliographicRecordId, modifiedDate);
            final Charset charset = FORMAT_STDHENTDM2.equalsIgnoreCase(format) ? StandardCharsets.ISO_8859_1 : StandardCharsets.UTF_8;

            RecordDTO recordDTO = RecordDataTransformer.recordDataToDTO(recordData, format, charset);

            res = mapper.marshall(recordDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).encoding(charset.name()).build();
        } catch (RecordServiceConnectorException | MarcReaderException | MarcWriterException | TransformerException | JSONBException e) {
            LOGGER.error(e.getMessage());
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

            RecordData recordData1;
            RecordData recordData2;

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

            RecordDTO recordDTO = RecordDataTransformer.recordDiffToDTO(recordData1, recordData2, format, charset);

            res = mapper.marshall(recordDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).encoding(charset.name()).build();
        } catch (Exception e) {
            LOGGER.error(e.getMessage());
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
            relationDTO.setNodes(new ArrayList<>());
            relationDTO.setEdges(new ArrayList<>());

            RecordId currentNode = new RecordId(bibliographicRecordId, agencyId);
            relationDTO.getNodes().add(currentNode);

            // Children
            RecordId[] recordIds = rawRepoRecordServiceConnector.getRecordChildren(agencyId, bibliographicRecordId);
            for (RecordId recordId : recordIds) {
                relationDTO.getNodes().add(recordId);

                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setParent(currentNode);
                edgeDTO.setChild(recordId);
                relationDTO.getEdges().add(edgeDTO);
            }

            // Parents
            recordIds = rawRepoRecordServiceConnector.getRecordParents(agencyId, bibliographicRecordId);
            for (RecordId recordId : recordIds) {
                relationDTO.getNodes().add(recordId);

                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setParent(recordId);
                edgeDTO.setChild(currentNode);
                relationDTO.getEdges().add(edgeDTO);
            }

            // Siblings from this record
            recordIds = rawRepoRecordServiceConnector.getRecordSiblingsFrom(agencyId, bibliographicRecordId);
            for (RecordId recordId : recordIds) {
                relationDTO.getNodes().add(recordId);

                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setParent(recordId);
                edgeDTO.setChild(currentNode);
                relationDTO.getEdges().add(edgeDTO);
            }

            // Siblings to this record
            recordIds = rawRepoRecordServiceConnector.getRecordSiblingsTo(agencyId, bibliographicRecordId);
            for (RecordId recordId : recordIds) {
                relationDTO.getNodes().add(recordId);

                EdgeDTO edgeDTO = new EdgeDTO();
                edgeDTO.setParent(currentNode);
                edgeDTO.setChild(recordId);
                relationDTO.getEdges().add(edgeDTO);
            }

            res = mapper.marshall(relationDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (RecordServiceConnectorException | JSONBException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/holdingsitems/{bibliographicRecordId}")
    public Response getAgenciesWithHoldings(@PathParam("bibliographicRecordId") String bibliographicRecordId) {
        String res;
        final List<HoldingsItemsDTO> holdingsItemsDTOList = new ArrayList<>();

        try {
            final Set<Integer> holdingsItems = holdingsItemsBean.getAgenciesWithHoldings(bibliographicRecordId);

            for (Integer holdingsItem : holdingsItems) {
                final HoldingsItemsDTO dto = new HoldingsItemsDTO();
                dto.setAgencyId(holdingsItem);
                holdingsItemsDTOList.add(dto);
            }

            res = mapper.marshall(holdingsItemsDTOList);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException | SQLException | HoldingsItemsException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/config")
    public Response getConfig() {
        String res;

        try {
            final ConfigDTO config = new ConfigDTO();
            config.setInstance(INSTANCE);
            config.setHoldingsItemsIntrospectUrl(HOLDINGS_ITEMS_INTROSPECT_URL);

            res = mapper.marshall(config);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        }
    }


}
