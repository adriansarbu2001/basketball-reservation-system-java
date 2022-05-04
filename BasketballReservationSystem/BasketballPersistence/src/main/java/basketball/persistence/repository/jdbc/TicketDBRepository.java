package basketball.persistence.repository.jdbc;

import basketball.model.Ticket;
import basketball.persistence.ITicketRepository;
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

public class TicketDBRepository implements ITicketRepository {
    private final JdbcUtils dbUtils;
    private final IValidator<Ticket> validator;

    private static final Logger logger= LogManager.getLogger();

    public TicketDBRepository(Properties props, IValidator<Ticket> validator) {
        logger.info("Initializing TicketDBRepository with properties: {} ", props);
        this.dbUtils = new JdbcUtils(props);
        this.validator = validator;
    }

    @Override
    public void save(Ticket ticket) throws ValidatorException {
        validator.validate(ticket);
        logger.traceEntry("saving ticket {} ", ticket);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("insert into Tickets (client_name, no_seats, match_id) values (?, ?, ?)")) {
            ps.setString(1, ticket.getClientName());
            ps.setInt(2, ticket.getNoSeats());
            ps.setLong(3, ticket.getMatchId());
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
        logger.traceEntry("Removing ticket {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("delete from Tickets where ticket_id = ?")) {
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
        try (PreparedStatement ps = con.prepareStatement("select count(*) as len from Tickets")) {
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
    public Ticket findOne(Long id) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("select * from Tickets where ticket_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    Long ticket_id = result.getLong("ticket_id");
                    String client_name = result.getString("client_name");
                    int no_seats = result.getInt("no_seats");
                    Long match_id = result.getLong("match_id");
                    Ticket ticket = new Ticket(client_name, no_seats, match_id);
                    ticket.setId(ticket_id);
                    return ticket;
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
    public Iterable<Ticket> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<Ticket> tickets = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("select * from Tickets")) {
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    Long ticket_id = result.getLong("ticket_id");
                    String client_name = result.getString("client_name");
                    int no_seats = result.getInt("no_seats");
                    Long match_id = result.getLong("match_id");
                    Ticket ticket = new Ticket(client_name, no_seats, match_id);
                    ticket.setId(ticket_id);
                    tickets.add(ticket);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return tickets;
    }

    @Override
    public void update(Ticket ticket) throws ValidatorException {
        validator.validate(ticket);
        logger.traceEntry("Updating ticket {} ", ticket);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("update Tickets set client_name = ?, no_seats = ?, match_id = ? where ticket_id = ?")) {
            ps.setString(1, ticket.getClientName());
            ps.setInt(2, ticket.getNoSeats());
            ps.setLong(3, ticket.getMatchId());
            ps.setLong(4, ticket.getId());
            int result = ps.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }
}
