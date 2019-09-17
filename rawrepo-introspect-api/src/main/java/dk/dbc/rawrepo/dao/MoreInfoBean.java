package dk.dbc.rawrepo.dao;

import dk.dbc.rawrepo.dto.AttachmentInfoDTO;
import dk.dbc.util.StopwatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
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

    @Resource(lookup = "jdbc/moreinfoDanbib")
    private DataSource moreinfoDanbib;

    @Resource(lookup = "jdbc/moreinfoUpdate")
    private DataSource moreinfoUpdate;

    @Resource(lookup = "jdbc/moreinfoBasis")
    private DataSource moreinfoBasis;

    public List<AttachmentInfoDTO> getAttachmentInfoDanbib(String bibliographicRecordId) throws SQLException {
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

    public List<AttachmentInfoDTO> getAttachmentInfoUpdate(String bibliographicRecordId) throws SQLException {
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

    public List<AttachmentInfoDTO> getAttachmentInfo24(String bibliographicRecordId) throws SQLException {
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

}
