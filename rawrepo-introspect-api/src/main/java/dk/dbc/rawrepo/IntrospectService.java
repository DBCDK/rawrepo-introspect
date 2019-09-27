/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo;

import dk.dbc.holdingsitems.HoldingsItemsException;
import dk.dbc.jsonb.JSONBContext;
import dk.dbc.jsonb.JSONBException;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.reader.MarcXchangeV1Reader;
import dk.dbc.marc.writer.DanMarc2LineFormatWriter;
import dk.dbc.marc.writer.MarcWriterException;
import dk.dbc.rawrepo.dao.HoldingsItemsBean;
import dk.dbc.rawrepo.diff.DiffGeneratorException;
import dk.dbc.rawrepo.diff.ExternalToolDiffGenerator;
import dk.dbc.rawrepo.dto.ConfigDTO;
import dk.dbc.rawrepo.dto.EdgeDTO;
import dk.dbc.rawrepo.dto.HoldingsItemsDTO;
import dk.dbc.rawrepo.dto.RecordDTO;
import dk.dbc.rawrepo.dto.RecordPartDTO;
import dk.dbc.rawrepo.dto.RelationDTO;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayInputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

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

    private static final DanMarc2LineFormatWriter DANMARC_2_LINE_FORMAT_WRITER = new DanMarc2LineFormatWriter();

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
                              @DefaultValue("LINE") @QueryParam("format") String format,
                              @DefaultValue("MERGED") @QueryParam("mode") String mode) {
        String res;

        try {
            // Validate input
            if (!Arrays.asList("LINE", "XML").contains(format.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            if (!Arrays.asList("RAW", "MERGED", "EXPANDED").contains(mode.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            final RecordServiceConnector.Params params = new RecordServiceConnector.Params();
            params.withMode(RecordServiceConnector.Params.Mode.valueOf(mode.toUpperCase()));
            params.withAllowDeleted(true);

            final RecordData recordData = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, params);

            RecordDTO recordDTO = recordDataToText(recordData, format);

            res = mapper.marshall(recordDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (RecordServiceConnectorException | MarcReaderException | MarcWriterException | TransformerException | JSONBException e) {
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
                                      @DefaultValue("LINE") @QueryParam("format") String format) {
        String res;

        try {
            // Validate input
            if (!Arrays.asList("LINE", "XML").contains(format.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            final RecordData recordData = rawRepoRecordServiceConnector.getHistoricRecord(Integer.toString(agencyId), bibliographicRecordId, modifiedDate);

            RecordDTO recordDTO = recordDataToText(recordData, format);

            res = mapper.marshall(recordDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
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
                                  @DefaultValue("LINE") @QueryParam("format") String format) {
        String res;

        try {
            String[] versionList = versions.split("\\|");

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

            RecordDTO recordDTO = recordDiffToText(recordData1, recordData2, format);

            res = mapper.marshall(recordDTO);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
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

    private RecordDTO recordDataToText(RecordData recordData, String format) throws TransformerException, MarcReaderException, MarcWriterException {
        final RecordDTO recordDTO = new RecordDTO();
        final RecordPartDTO part = new RecordPartDTO();
        final List<RecordPartDTO> parts = new ArrayList<>();

        if ("LINE".equalsIgnoreCase(format)) {
            part.setContent(formatRecordDataToLine(recordData));
        } else {
            part.setContent(formatRecordDataToXML(recordData));
        }

        if (recordData.isDeleted()) {
            part.setType("right"); // 'right' translates to red text color
        } else {
            part.setType("both"); // 'both' translates to black text color
        }
        parts.add(part);
        recordDTO.setRecordParts(parts);

        return recordDTO;
    }

    private RecordDTO recordDiffToText(RecordData left, RecordData right, String format) throws DiffGeneratorException, MarcWriterException, MarcReaderException, TransformerException {
        final RecordDTO result = new RecordDTO();

        final ExternalToolDiffGenerator.Kind kind = "LINE".equalsIgnoreCase(format) ? ExternalToolDiffGenerator.Kind.PLAINTEXT : ExternalToolDiffGenerator.Kind.XML;
        final ExternalToolDiffGenerator externalToolDiffGenerator = new ExternalToolDiffGenerator();

        // First argument is "current" value and second argument is "next" value
        // The "left" record will always be the newest version while "right" is an earlier version
        // In order to match this with getDiff the order is reversed so "current" is the earlier record and "next" the is newer record

        String current, next;
        if ("LINE".equalsIgnoreCase(format)) {
            next = formatRecordDataToLine(left);
            current = formatRecordDataToLine(right);
        } else {
            next = formatRecordDataToXML(left);
            current = formatRecordDataToXML(right);
        }

        String diff = externalToolDiffGenerator.getDiff(kind, current.getBytes(), next.getBytes());

        final List<RecordPartDTO> recordParts = new ArrayList<>();

        // No diff, so just use the left record
        if ("".equals(diff)) {
            diff = new String(left.getContent());
        }

        for (String line : diff.split("\n")) {
            // Since we split on new line we must remember to add it each line again
            if (!line.startsWith("---") && !line.startsWith("+++") && !line.startsWith("@@")) {
                if (line.startsWith("-")) {
                    recordParts.add(new RecordPartDTO(line + "\n", "right"));
                } else if (line.startsWith("+")) {
                    recordParts.add(new RecordPartDTO(line + "\n", "left"));
                } else {
                    recordParts.add(new RecordPartDTO(line + "\n", "both"));
                }
            }
        }

        result.setRecordParts(recordParts);

        return result;
    }

    private String formatRecordDataToLine(RecordData recordData) throws MarcWriterException, MarcReaderException {
        final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(new ByteArrayInputStream(recordData.getContent()), StandardCharsets.UTF_8);
        final MarcRecord record = reader.read();

        String rawLines = new String(DANMARC_2_LINE_FORMAT_WRITER.write(record, StandardCharsets.UTF_8));

        // Replace all *<single char><value> with <space>*<single char><space><value>. E.g. *aThis is the value -> *a This is the value
        rawLines = rawLines.replaceAll("(\\*[aA0-zZ9|&])", " $1 ");

        // Replace double space with single space in front of subfield marker
        rawLines = rawLines.replaceAll(" {2}\\*", " \\*");

        // If the previous line is exactly 82 chars long it will result in an blank line with 4 spaces, so we'll remove that
        rawLines = rawLines.replaceAll(" {4}\n", "");

        return rawLines;
    }

    private String formatRecordDataToXML(RecordData recordData) throws TransformerException {
        final String recordContent = new String(recordData.getContent(), StandardCharsets.UTF_8);
        final Source xmlInput = new StreamSource(new StringReader(recordContent));
        final StringWriter stringWriter = new StringWriter();
        final StreamResult xmlOutput = new StreamResult(stringWriter);
        final TransformerFactory transformerFactory = TransformerFactory.newInstance();
        transformerFactory.setAttribute("indent-number", 4);
        final Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.transform(xmlInput, xmlOutput);

        return xmlOutput.getWriter().toString();
    }

}
