package dk.dbc.rawrepo;

import dk.dbc.jsonb.JSONBContext;
import dk.dbc.jsonb.JSONBException;
import dk.dbc.rawrepo.dao.MoreInfoBean;
import dk.dbc.rawrepo.dto.AttachmentInfoDTO;
import dk.dbc.util.StopwatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.SQLException;
import java.util.List;

@Interceptors(StopwatchInterceptor.class)
@Stateless
@Path("")
public class AttachmentService {
    private static final Logger LOGGER = LoggerFactory.getLogger(IntrospectService.class);
    private final JSONBContext mapper = new JSONBContext();

    @EJB
    private MoreInfoBean moreInfo;

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/attachment/{bibliographicRecordId}/danbib")
    public Response getAttachmentInfoDanbib(@PathParam("bibliographicRecordId") String bibliographicRecordId) {
        String res = "";

        try {
            final List<AttachmentInfoDTO> attachmentInfo = moreInfo.getAttachmentInfoDanbib(bibliographicRecordId);

            res = mapper.marshall(attachmentInfo);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException | SQLException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/attachment/{bibliographicRecordId}/update")
    public Response getAttachmentInfoUpdate(@PathParam("bibliographicRecordId") String bibliographicRecordId) {
        String res = "";

        try {
            final List<AttachmentInfoDTO> attachmentInfo = moreInfo.getAttachmentInfoUpdate(bibliographicRecordId);

            res = mapper.marshall(attachmentInfo);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException | SQLException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        }
    }

    @GET
    @Produces({MediaType.APPLICATION_JSON})
    @Path("v1/attachment/{bibliographicRecordId}/basis")
    public Response getAttachmentInfoBasis(@PathParam("bibliographicRecordId") String bibliographicRecordId) {
        String res = "";

        try {
            final List<AttachmentInfoDTO> attachmentInfo = moreInfo.getAttachmentInfo24(bibliographicRecordId);

            res = mapper.marshall(attachmentInfo);

            return Response.ok(res, MediaType.APPLICATION_JSON).build();
        } catch (JSONBException | SQLException e) {
            LOGGER.error(e.getMessage());
            return Response.serverError().build();
        }
    }




}
