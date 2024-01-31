package dk.dbc.rawrepo.dao;

import dk.dbc.rawrepo.dto.AttachmentDataDTO;
import dk.dbc.rawrepo.dto.AttachmentInfoDTO;
import dk.dbc.rawrepo.exception.AttachmentException;
import dk.dbc.util.StopwatchInterceptor;
import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.interceptor.Interceptors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Interceptors(StopwatchInterceptor.class)
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
                return getAttachmentInfoDanbib(bibliographicRecordId);
            case "update":
                return getAttachmentInfoUpdate(bibliographicRecordId);
            case "basis":
                return getAttachmentInfoBasis(bibliographicRecordId);
            default:
                throw new AttachmentException("The base '" + base + "' is not a valid moreinfo base");
        }
    }

    private List<AttachmentInfoDTO> getAttachmentInfoDanbib(String bibliographicRecordId) throws SQLException {
        final List<AttachmentInfoDTO> result = new ArrayList<>();

        try (Connection connection = moreinfoDanbib.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ATTACHMENT_INFO_DANBIB_QUERY)) {
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

    private List<AttachmentInfoDTO> getAttachmentInfoUpdate(String bibliographicRecordId) throws SQLException {
        final List<AttachmentInfoDTO> result = new ArrayList<>();

        try (Connection connection = moreinfoUpdate.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ATTACHMENT_INFO_UPDATE_QUERY)) {
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

    private List<AttachmentInfoDTO> getAttachmentInfoBasis(String bibliographicRecordId) throws SQLException {
        final List<AttachmentInfoDTO> result = new ArrayList<>();

        try (Connection connection = moreinfoBasis.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ATTACHMENT_INFO_BASIS_QUERY)) {
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
                return getAttachmentDataDanbib(bibliographicRecordId, sourceId, attachmentType);
            case "update":
                return getAttachmentDataUpdate(bibliographicRecordId, sourceId, attachmentType);
            case "basis":
                return getAttachmentDataBasis(bibliographicRecordId, sourceId, attachmentType);
            default:
                throw new AttachmentException("The base '" + base + "' is not a valid moreinfo base");
        }
    }

    private AttachmentDataDTO getAttachmentDataDanbib(String bibliographicRecordId, int sourceId, String attachmentType) throws SQLException, AttachmentException {
        try (Connection connection = moreinfoDanbib.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ATTACHMENT_DATA_DANBIB_QUERY)) {
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
                    throw new AttachmentException(String.format("Attachment type '%s' for %s:%s does not exist in moreinfo danbib", attachmentType, bibliographicRecordId, sourceId));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private AttachmentDataDTO getAttachmentDataUpdate(String bibliographicRecordId, int sourceId, String attachmentType) throws SQLException, AttachmentException {
        try (Connection connection = moreinfoUpdate.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ATTACHMENT_DATA_UPDATE_QUERY)) {
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
                    throw new AttachmentException(String.format("Attachment type '%s' for %s:%s does not exist in moreinfo update", attachmentType, bibliographicRecordId, sourceId));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

    private AttachmentDataDTO getAttachmentDataBasis(String bibliographicRecordId, int sourceId, String attachmentType) throws SQLException, AttachmentException {
        try (Connection connection = moreinfoBasis.getConnection();
             PreparedStatement stmt = connection.prepareStatement(ATTACHMENT_DATA_BASIS_QUERY)) {
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
                    throw new AttachmentException(String.format("Attachment type '%s' for %s:%s does not exist in moreinfo basis", attachmentType, bibliographicRecordId, sourceId));
                }
            }
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

}
