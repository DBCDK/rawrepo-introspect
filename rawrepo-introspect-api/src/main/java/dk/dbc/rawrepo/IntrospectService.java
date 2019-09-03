/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo;

import dk.dbc.jsonb.JSONBContext;
import dk.dbc.jsonb.JSONBException;
import dk.dbc.marc.binding.MarcRecord;
import dk.dbc.marc.reader.MarcReaderException;
import dk.dbc.marc.reader.MarcXchangeV1Reader;
import dk.dbc.marc.writer.DanMarc2LineFormatWriter;
import dk.dbc.marc.writer.MarcWriterException;
import dk.dbc.marc.writer.MarcXchangeV1Writer;
import dk.dbc.rawrepo.dto.EdgeDTO;
import dk.dbc.rawrepo.dto.RecordDTO;
import dk.dbc.rawrepo.dto.RecordPartDTO;
import dk.dbc.rawrepo.dto.RelationDTO;
import dk.dbc.util.StopwatchInterceptor;
import dk.dbc.xmldiff.XmlDiff;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

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
import javax.xml.xpath.XPathExpressionException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private static final DanMarc2LineFormatWriter DANMARC_2_LINE_FORMAT_WRITER = new DanMarc2LineFormatWriter();
    private static final MarcXchangeV1Writer MARC_XCHANGE_V1_WRITER = new MarcXchangeV1Writer();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/agencies-for/{bibliographicRecordId}")
    public Response getAllAgenciesForBibliographicRecordId(@PathParam("bibliographicRecordId") String bibliographicRecordId) {
        String res = "";

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
        String res = "";

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
        String res = "";

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
        String res = "";

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
        String res = "";

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
        String res = "";

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
    @Produces({MediaType.TEXT_PLAIN})
    @Path("v1/instance")
    public Response getInstance() {
        return Response.ok(INSTANCE, MediaType.TEXT_PLAIN).build();
    }

    private RecordDTO recordDataToText(RecordData recordData, String format) throws TransformerException, MarcReaderException, MarcWriterException {
        RecordDTO recordDTO = new RecordDTO();
        List<RecordPartDTO> parts = new ArrayList<>();

        if ("LINE".equalsIgnoreCase(format)) {
            final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(new ByteArrayInputStream(recordData.getContent()), Charset.forName("UTF-8"));
            final MarcRecord record = reader.read();

            String rawLines = new String(DANMARC_2_LINE_FORMAT_WRITER.write(record, Charset.forName("UTF-8")));

            // Replace all *<single char><value> with <space>*<single char><space><value>. E.g. *aThis is the value -> *a This is the value
            rawLines = rawLines.replaceAll("(\\*[aA0-zZ9|&])", " $1 ");

            // Replace double space with single space in front of subfield marker
            rawLines = rawLines.replaceAll(" {2}\\*", " \\*");

            // If the previous line is exactly 82 chars long it will result in an blank line with 4 spaces, so we'll remove that

            rawLines = rawLines.replaceAll(" {4}\n", "");
            RecordPartDTO part = new RecordPartDTO();
            part.setType("both");
            part.setContent(rawLines);
            parts.add(part);
        } else {
            final String recordContent = new String(recordData.getContent(), Charset.forName("UTF-8"));
            final Source xmlInput = new StreamSource(new StringReader(recordContent));
            final StringWriter stringWriter = new StringWriter();
            final StreamResult xmlOutput = new StreamResult(stringWriter);
            final TransformerFactory transformerFactory = TransformerFactory.newInstance();
            transformerFactory.setAttribute("indent-number", 4);
            final Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.transform(xmlInput, xmlOutput);

            RecordPartDTO part = new RecordPartDTO();
            part.setType("both");
            part.setContent(xmlOutput.getWriter().toString());
            parts.add(part);
        }

        recordDTO.setRecordParts(parts);

        return recordDTO;
    }

    private RecordDTO recordDiffToText(RecordData recordData1, RecordData recordData2, String format) throws MarcReaderException, XPathExpressionException, SAXException, IOException {
        RecordDTO result = new RecordDTO();

        if ("LINE".equalsIgnoreCase(format)) {
            final MarcXchangeV1Reader reader1 = new MarcXchangeV1Reader(new ByteArrayInputStream(recordData1.getContent()), Charset.forName("UTF-8"));
            final MarcRecord record1 = reader1.read();

            final MarcXchangeV1Reader reader2 = new MarcXchangeV1Reader(new ByteArrayInputStream(recordData2.getContent()), Charset.forName("UTF-8"));
            final MarcRecord record2 = reader2.read();

            // TODO implement
            //ArrayList<Object> response = new ArrayList<>();
            //response.add("Not implemented");
            //result.setRecordParts();
        } else {
            ByteArrayInputStream leftStream = new ByteArrayInputStream(recordData1.getContent());
            ByteArrayInputStream rightStream = new ByteArrayInputStream(recordData2.getContent());
            XMLDiffHelper writer = new XMLDiffHelper();
            XmlDiff.builder().indent(4).normalize(true).strip(true).trim(true).build()
                    .compare(leftStream, rightStream, writer);
            result.setRecordParts(writer.getData());
        }

        return result;
    }

}
