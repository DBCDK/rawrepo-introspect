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
import dk.dbc.util.StopwatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
import java.nio.charset.Charset;
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
    @Produces({MediaType.TEXT_PLAIN})
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

            final RecordData recordData = rawRepoRecordServiceConnector.getRecordData(agencyId, bibliographicRecordId, params);

            res = recordDataToText(recordData, format);

            return Response.ok(res, MediaType.TEXT_PLAIN).build();
        } catch (RecordServiceConnectorException | MarcReaderException | MarcWriterException | TransformerException e) {
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
    @Produces({MediaType.TEXT_PLAIN})
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

            res = recordDataToText(recordData, format);

            return Response.ok(res, MediaType.TEXT_PLAIN).build();
        } catch (RecordServiceConnectorException | MarcReaderException | MarcWriterException | TransformerException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    private String recordDataToText(RecordData recordData, String format) throws TransformerException, MarcReaderException, MarcWriterException {
        final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(new ByteArrayInputStream(recordData.getContent()), Charset.forName("UTF-8"));
        final MarcRecord record = reader.read();

        if ("LINE".equalsIgnoreCase(format)) {
            String rawLines = new String(DANMARC_2_LINE_FORMAT_WRITER.write(record, Charset.forName("UTF-8")));

            // Replace all *<single char><value> with <space>*<single char><space><value>. E.g. *aThis is the value -> *a This is the value
            rawLines = rawLines.replaceAll("(\\*[aA0-zZ9|&])", " $1 ");

            // Replace double space with single space in front of subfield marker
            rawLines = rawLines.replaceAll(" {2}\\*", " \\*");

            // If the previous line is exactly 82 chars long it will result in an blank line with 4 spaces, so we'll remove that
            return rawLines.replaceAll(" {4}\n", "");
        } else {
            final String recordContent = new String(MARC_XCHANGE_V1_WRITER.write(record, Charset.forName("UTF-8")));
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

}
