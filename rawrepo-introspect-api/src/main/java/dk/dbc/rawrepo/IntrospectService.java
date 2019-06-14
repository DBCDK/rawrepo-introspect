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
import dk.dbc.rawrepo.bean.RawrepoBean;
import dk.dbc.util.StopwatchInterceptor;
import org.slf4j.ext.XLogger;
import org.slf4j.ext.XLoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayInputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Interceptors(StopwatchInterceptor.class)
@Stateless
@Path("")
public class IntrospectService {
    private static final XLogger LOGGER = XLoggerFactory.getXLogger(IntrospectService.class);
    private final JSONBContext mapper = new JSONBContext();

    @EJB
    RawrepoBean rawrepoBean;

    private static final DanMarc2LineFormatWriter DANMARC_2_LINE_FORMAT_WRITER = new DanMarc2LineFormatWriter();
    private static final MarcXchangeV1Writer MARC_XCHANGE_V1_WRITER = new MarcXchangeV1Writer();

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/agencies-for/{bibliographicRecordId}")
    public Response getAllAgenciesForBibliographicRecordId(@PathParam("bibliographicRecordId") String bibliographicRecordId) {
        LOGGER.entry();
        String res = "";

        try {
            final List<Integer> agencies = rawrepoBean.getAllAgenciesForBibliographicRecordId(bibliographicRecordId);

            res = mapper.marshall(agencies);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException | RecordServiceConnectorException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        } finally {
            LOGGER.exit(res);
        }
    }

    @GET
    @Produces({MediaType.TEXT_PLAIN})
    @Path("v1/record/{bibliographicRecordId}/{agencyId}")
    public Response getRecord(@PathParam("bibliographicRecordId") String bibliographicRecordId,
                              @PathParam("agencyId") int agencyId,
                              @DefaultValue("LINE") @QueryParam("format") String format,
                              @DefaultValue("MERGED") @QueryParam("mode") String mode) {
        LOGGER.entry();
        String res = "";

        try {
            // Validate input
            if (!Arrays.asList("LINE", "XML").contains(format.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            if (!Arrays.asList("RAW", "MERGED", "EXPANDED").contains(mode.toUpperCase())) {
                return Response.status(Response.Status.BAD_REQUEST).build();
            }

            final RecordData recordData = rawrepoBean.getRecord(bibliographicRecordId, agencyId, RecordServiceConnector.Params.Mode.valueOf(mode.toUpperCase()));

            final MarcXchangeV1Reader reader = new MarcXchangeV1Reader(new ByteArrayInputStream(recordData.getContent()), Charset.forName("UTF-8"));
            final MarcRecord record = reader.read();

            if ("LINE".equalsIgnoreCase(format)) {
                res = new String(DANMARC_2_LINE_FORMAT_WRITER.write(record, Charset.forName("UTF-8")));
            } else {
                res = new String(MARC_XCHANGE_V1_WRITER.write(record, Charset.forName("UTF-8")));
                // TODO implement XML beautification
            }

            return Response.ok(res, MediaType.TEXT_PLAIN).build();
        } catch (RecordServiceConnectorException | MarcReaderException | MarcWriterException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        } finally {
            LOGGER.exit(res);
        }
    }

}
