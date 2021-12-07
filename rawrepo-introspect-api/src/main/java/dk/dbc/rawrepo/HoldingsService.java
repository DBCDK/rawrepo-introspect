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
import dk.dbc.rawrepo.dao.HoldingsItemsBean;
import dk.dbc.rawrepo.dto.HoldingsItemsDTO;
import dk.dbc.rawrepo.dto.HoldingsItemsListDTO;
import dk.dbc.rawrepo.dto.RecordDTO;
import dk.dbc.rawrepo.record.RecordServiceConnector;
import dk.dbc.rawrepo.record.RecordServiceConnectorException;
import dk.dbc.rawrepo.utils.RecordDataTransformer;
import dk.dbc.util.StopwatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

@Interceptors(StopwatchInterceptor.class)
@Stateless
@Path("")
public class HoldingsService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntrospectService.class);
    private final JSONBContext mapper = new JSONBContext();

    @Inject
    RecordServiceConnector rawRepoRecordServiceConnector;

    @EJB
    private HoldingsItemsBean holdingsItemsBean;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/holdingsitems/{bibliographicRecordId}")
    public Response getAgenciesWithHoldings(@PathParam("bibliographicRecordId") String bibliographicRecordId) {
        String res;
        final List<HoldingsItemsListDTO> holdingsItemsDTOList = new ArrayList<>();

        try {
            final Set<Integer> holdingsItems = holdingsItemsBean.getAgenciesWithHoldings(bibliographicRecordId);

            final HoldingsItemsListDTO holdingsForThisRecord = new HoldingsItemsListDTO();
            holdingsForThisRecord.setBibliographicRecordId(bibliographicRecordId);
            for (int holdingsItem : holdingsItems) {
                holdingsForThisRecord.getHoldingsAgencies().add(new HoldingsItemsDTO(bibliographicRecordId, holdingsItem));
            }
            holdingsItemsDTOList.add(holdingsForThisRecord);

            // Next get holdings of all previous bibliographic records
            final List<Integer> agencies = Arrays.asList(rawRepoRecordServiceConnector.getAllAgenciesForBibliographicRecordId(bibliographicRecordId));
            if (agencies.contains(870970)) {
                final RecordDTO recordData = rawRepoRecordServiceConnector.getRecordData(870970, bibliographicRecordId);
                final MarcRecord marcRecord = RecordDataTransformer.recordDataToMarcRecord(recordData);
                final List<String> previousBibliographicRecordIds = marcRecord.getSubFieldValues("002", 'a');

                for (String previousBibliographicRecordId : previousBibliographicRecordIds) {
                    final Set<Integer> previousHoldingsItems = holdingsItemsBean.getAgenciesWithHoldings(previousBibliographicRecordId);

                    final HoldingsItemsListDTO holdingsForPreviousRecord = new HoldingsItemsListDTO();
                    holdingsForPreviousRecord.setBibliographicRecordId(previousBibliographicRecordId);
                    for (int previousHoldingsItem : previousHoldingsItems) {
                        holdingsForPreviousRecord.getHoldingsAgencies().add(new HoldingsItemsDTO(previousBibliographicRecordId, previousHoldingsItem));
                    }
                    holdingsItemsDTOList.add(holdingsForPreviousRecord);
                }
            }

            res = mapper.marshall(holdingsItemsDTOList);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException | SQLException | HoldingsItemsException | RecordServiceConnectorException | MarcReaderException e) {
            LOGGER.error(e.getMessage(), e);
            return Response.serverError().build();
        }
    }
}
