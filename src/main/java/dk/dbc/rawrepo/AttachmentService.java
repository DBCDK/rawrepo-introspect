package dk.dbc.rawrepo;

import dk.dbc.commons.jsonb.JSONBContext;
import dk.dbc.commons.jsonb.JSONBException;
import dk.dbc.rawrepo.dao.MoreInfoBean;
import dk.dbc.rawrepo.dto.AttachmentDataDTO;
import dk.dbc.rawrepo.dto.AttachmentInfoDTO;
import dk.dbc.rawrepo.exception.AttachmentException;
import dk.dbc.util.StopwatchInterceptor;
import jakarta.ejb.EJB;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;

@Interceptors(StopwatchInterceptor.class)
@Stateless
@Path("")
public class AttachmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentService.class);
    private final JSONBContext mapper = new JSONBContext();

    @EJB
    private MoreInfoBean moreInfo;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/attachment/{base}/{bibliographicRecordId}")
    public Response getAttachmentInfoDanbib(@PathParam("base") String base,
                                            @PathParam("bibliographicRecordId") String bibliographicRecordId) {
        String res = "";

        try {
            final List<AttachmentInfoDTO> attachmentInfo = moreInfo.getAttachmentInfo(base, bibliographicRecordId);

            res = mapper.marshall(attachmentInfo);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException | SQLException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        } catch (AttachmentException e) {
            LOGGER.error(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

    @GET
    @Path("v1/attachment/{base}/{bibliographicRecordId}/{agencyId}/{attachment-type}")
    public Response getAttachment(@PathParam("base") String base,
                                  @PathParam("bibliographicRecordId") String bibliographicRecordId,
                                  @PathParam("agencyId") int agencyId,
                                  @PathParam("attachment-type") String attachmentType) {
        try {
            final AttachmentDataDTO attachmentDataDTO = moreInfo.getAttachmentData(base, bibliographicRecordId, agencyId, attachmentType);

            return Response.ok(attachmentDataDTO.getData(), attachmentDataDTO.getMimetype()).build();
        } catch (SQLException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        } catch (AttachmentException e) {
            LOGGER.error(e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST.getStatusCode(), e.getMessage()).build();
        }
    }

}
