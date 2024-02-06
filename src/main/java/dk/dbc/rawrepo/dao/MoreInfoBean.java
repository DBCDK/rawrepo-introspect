package dk.dbc.rawrepo.dao;

import dk.dbc.rawrepo.dto.AttachmentDataDTO;
import dk.dbc.rawrepo.dto.AttachmentInfoDTO;
import dk.dbc.rawrepo.exception.AttachmentException;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class MoreInfoBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(MoreInfoBean.class);

    private static final String ATTACHMENT_INFO_DANBIB_QUERY = "SELECT attachment_type, ajourdato, opretdato, source_id, octet_length(data) AS size FROM attachment WHERE lokalid = ? ORDER BY source_id, attachment_type desc";
    private static final String ATTACHMENT_INFO_UPDATE_QUERY = "SELECT attachment_type, ajourdato, opretdato, source_id, octet_length(data) AS size FROM extern_attachment WHERE lokalid = ? ORDER BY source_id, attachment_type desc";
    private static final String ATTACHMENT_INFO_BASIS_QUERY = "SELECT attachment_type, ajourdato, opretdato, source_id, octet_length(data) AS size FROM attachment WHERE lokalid = ? ORDER BY source_id, attachment_type desc";

    private static final String ATTACHMENT_DATA_DANBIB_QUERY = "SELECT t.mimetype as mimetype, a.data as data from attachment a, attachment_types t where lokalid=? and source_id=? and attachment_type=? and t.type = a.attachment_type";
    private static final String ATTACHMENT_DATA_UPDATE_QUERY = "SELECT t.mimetype as mimetype, a.data as data from extern_attachment a, attachment_types t where lokalid=? and source_id=? and attachment_type=? and t.type = a.attachment_type";
    private static final String ATTACHMENT_DATA_BASIS_QUERY = "SELECT t.mimetype as mimetype, a.data as data from attachment a, attachment_types t where lokalid=? and source_id=? and attachment_type=? and t.type = a.attachment_type";

    @Resource(lookup = "jdbc/moreinfoDanbib")
    private DataSource moreinfoDanbib;

    @Resource(lookup = "jdbc/moreinfoUpdate")
    private DataSource moreinfoUpdate;

    @Resource(lookup = "jdbc/moreinfoBasis")
    private DataSource moreinfoBasis;

    public List<AttachmentInfoDTO> getAttachmentInfo(String base, String bibliographicRecordId) throws AttachmentException, SQLException {
        switch (base) {
            case "danbib":
                return getAttachmentInfoDTOS(bibliographicRecordId, moreinfoDanbib, ATTACHMENT_INFO_DANBIB_QUERY);
            case "update":
                return getAttachmentInfoDTOS(bibliographicRecordId, moreinfoUpdate, ATTACHMENT_INFO_UPDATE_QUERY);
            case "basis":
                return getAttachmentInfoDTOS(bibliographicRecordId, moreinfoBasis, ATTACHMENT_INFO_BASIS_QUERY);
            default:
                throw new AttachmentException("The base '" + base + "' is not a valid moreinfo base");
        }
    }

    private List<AttachmentInfoDTO> getAttachmentInfoDTOS(String bibliographicRecordId, DataSource moreinfoUpdate, String attachmentInfoUpdateQuery) throws SQLException {
        final List<AttachmentInfoDTO> result = new ArrayList<>();

        try (Connection connection = moreinfoUpdate.getConnection();
             PreparedStatement stmt = connection.prepareStatement(attachmentInfoUpdateQuery)) {
            stmt.setString(1, bibliographicRecordId);
            try (ResultSet resultSet = stmt.executeQuery()) {
                while (resultSet.next()) {
                    final AttachmentInfoDTO dto = new AttachmentInfoDTO(
                            resultSet.getString("attachment_type"),
                            resultSet.getInt("source_id"),
                            resultSet.getString("ajourdato"),
                            resultSet.getString("opretdato"),
                            resultSet.getInt("size")
                    );
                    result.add(dto);
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }

        return result;
    }

    public AttachmentDataDTO getAttachmentData(String base, String bibliographicRecordId, int sourceId, String attachmentType) throws SQLException, AttachmentException {
        switch (base) {
            case "danbib":
                return getAttachmentDataDTO(bibliographicRecordId, sourceId, attachmentType, moreinfoDanbib, ATTACHMENT_DATA_DANBIB_QUERY, "danbib");
            case "update":
                return getAttachmentDataDTO(bibliographicRecordId, sourceId, attachmentType, moreinfoUpdate, ATTACHMENT_DATA_UPDATE_QUERY, "update");
            case "basis":
                return getAttachmentDataDTO(bibliographicRecordId, sourceId, attachmentType, moreinfoBasis, ATTACHMENT_DATA_BASIS_QUERY, "basis");
            default:
                throw new AttachmentException("The base '" + base + "' is not a valid moreinfo base");
        }
    }

    private AttachmentDataDTO getAttachmentDataDTO(String bibliographicRecordId, int sourceId, String attachmentType, DataSource moreinfoUpdate, String attachmentDataUpdateQuery, String base) throws AttachmentException, SQLException {
        try (Connection connection = moreinfoUpdate.getConnection();
             PreparedStatement stmt = connection.prepareStatement(attachmentDataUpdateQuery)) {
            stmt.setString(1, bibliographicRecordId);
            stmt.setInt(2, sourceId);
            stmt.setString(3, attachmentType);
            try (ResultSet resultSet = stmt.executeQuery()) {
                if (resultSet.next()) {
                    return new AttachmentDataDTO(
                            resultSet.getBytes("data"),
                            resultSet.getString("mimetype")
                    );
                } else {
                    throw new AttachmentException(String.format("Attachment type '%s' for %s:%s does not exist in moreinfo %s", attachmentType, bibliographicRecordId, sourceId, base));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }
}
