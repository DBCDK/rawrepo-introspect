package dk.dbc.rawrepo.dao;

import dk.dbc.holdingsitems.HoldingsItemsDAO;
import dk.dbc.holdingsitems.HoldingsItemsException;
import dk.dbc.util.StopwatchInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.interceptor.Interceptors;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Set;

@Interceptors(StopwatchInterceptor.class)
@Stateless
public class HoldingsItemsBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(HoldingsItemsBean.class);

    @Resource(lookup = "jdbc/holdingsItems")
    private DataSource holdingsItems;

    public Set<Integer> getAgenciesWithHoldings(String bibliographicRecordId) throws SQLException, HoldingsItemsException {
        try (Connection connection = holdingsItems.getConnection()){
              final HoldingsItemsDAO dao = HoldingsItemsDAO.newInstance(connection);

              return dao.getAgenciesThatHasHoldingsFor(bibliographicRecordId);
        } catch (SQLException ex) {
            LOGGER.error(ex.getMessage(), ex);
            throw ex;
        }
    }

}
