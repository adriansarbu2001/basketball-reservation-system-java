package basketball.persistence.repository.jdbc;

import basketball.model.Match;
import basketball.persistence.IMatchRepository;
import basketball.model.validator.IValidator;
import basketball.model.validator.ValidatorException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MatchDBRepository implements IMatchRepository {
    private final JdbcUtils dbUtils;
    private final IValidator<Match> validator;

    private static final Logger logger= LogManager.getLogger();

    public MatchDBRepository(Properties props, IValidator<Match> validator) {
        logger.info("Initializing MatchDBRepository with properties: {} ", props);
        this.dbUtils = new JdbcUtils(props);
        this.validator = validator;
    }

    @Override
    public void save(Match match) throws ValidatorException {
        validator.validate(match);
        logger.traceEntry("saving match {} ", match);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("insert into Matches (name, ticket_price, no_available_seats) values (?, ?, ?)")) {
            ps.setString(1, match.getName());
            ps.setFloat(2, match.getTicketPrice());
            ps.setInt(3, match.getNoAvailableSeats());
            int result = ps.executeUpdate();
            logger.trace("Saved {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public void remove(Long id) {
        logger.traceEntry("Removing match {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("delete from Matches where match_id = ?")) {
            ps.setLong(1, id);
            int result = ps.executeUpdate();
            logger.trace("Removed {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public Long size() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        long len = -1L;
        try (PreparedStatement ps = con.prepareStatement("select count(*) as len from Matches")) {
            try (ResultSet result = ps.executeQuery()) {
                len = result.getLong("len");
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return len;
    }

    @Override
    public Match findOne(Long id) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("select * from Matches where match_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    Long match_id = result.getLong("match_id");
                    String name = result.getString("name");
                    float ticket_price = result.getFloat("ticket_price");
                    int no_available_seats = result.getInt("no_available_seats");
                    Match match = new Match(name, ticket_price, no_available_seats);
                    match.setId(match_id);
                    return match;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return null;
    }

    @Override
    public Iterable<Match> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Match> matches = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("select * from Matches")) {
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    Long match_id = result.getLong("match_id");
                    String name = result.getString("name");
                    float ticket_price = result.getFloat("ticket_price");
                    int no_available_seats = result.getInt("no_available_seats");
                    Match match = new Match(name, ticket_price, no_available_seats);
                    match.setId(match_id);
                    matches.add(match);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return matches;
    }

    @Override
    public void update(Match match) throws ValidatorException {
        validator.validate(match);
        logger.traceEntry("Updating match {} ", match);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("update Matches set name = ?, ticket_price = ?, no_available_seats = ? where match_id = ?")) {
            ps.setString(1, match.getName());
            ps.setFloat(2, match.getTicketPrice());
            ps.setInt(3, match.getNoAvailableSeats());
            ps.setLong(4, match.getId());
            int result = ps.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public List<Match> availableMatchesDescending() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Match> matches = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("select * from Matches where no_available_seats > 0 order by no_available_seats desc")) {
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    Long match_id = result.getLong("match_id");
                    String name = result.getString("name");
                    float ticket_price = result.getFloat("ticket_price");
                    int no_available_seats = result.getInt("no_available_seats");
                    Match match = new Match(name, ticket_price, no_available_seats);
                    match.setId(match_id);
                    matches.add(match);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return matches;
    }
}
