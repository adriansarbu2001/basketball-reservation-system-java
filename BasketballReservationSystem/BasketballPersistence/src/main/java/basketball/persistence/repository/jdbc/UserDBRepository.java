package basketball.persistence.repository.jdbc;

import basketball.model.User;
import basketball.model.validator.IValidator;
import basketball.model.validator.ValidatorException;
import basketball.persistence.IUserRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class UserDBRepository implements IUserRepository {
    private final JdbcUtils dbUtils;
    private final IValidator<User> validator;

    private static final Logger logger= LogManager.getLogger();

    public UserDBRepository(Properties props, IValidator<User> validator) {
        logger.info("Initializing AccountDBRepository with properties: {} ", props);
        this.dbUtils = new JdbcUtils(props);
        this.validator = validator;
    }

    @Override
    public void save(User user) throws ValidatorException {
        validator.validate(user);
        logger.traceEntry("saving account {} ", user);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("insert into Users (username, password) values (?, ?)")) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
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
        logger.traceEntry("Removing account {} ", id);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("delete from Users where account_id = ?")) {
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
        try (PreparedStatement ps = con.prepareStatement("select count(*) as len from Users")) {
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
    public User findOne(Long id) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("select * from Users where account_id = ?")) {
            ps.setLong(1, id);
            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    Long account_id = result.getLong("account_id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    User user = new User(username, password);
                    user.setId(account_id);
                    return user;
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
    public Iterable<User> findAll() {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        List<User> users = new ArrayList<>();
        try (PreparedStatement ps = con.prepareStatement("select * from Users")) {
            try (ResultSet result = ps.executeQuery()) {
                while (result.next()) {
                    Long account_id = result.getLong("account_id");
                    String username = result.getString("username");
                    String password = result.getString("password");
                    User user = new User(username, password);
                    user.setId(account_id);
                    users.add(user);
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return users;
    }

    @Override
    public void update(User user) throws ValidatorException {
        validator.validate(user);
        logger.traceEntry("Updating account {} ", user);
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("update Users set username = ?, password = ? where account_id = ?")) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword());
            ps.setLong(3, user.getId());
            int result = ps.executeUpdate();
            logger.trace("Updated {} instances", result);
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
    }

    @Override
    public User findBy(String username, String password) {
        logger.traceEntry();
        Connection con = dbUtils.getConnection();
        try (PreparedStatement ps = con.prepareStatement("select * from Users where username = ? and password = ?")) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet result = ps.executeQuery()) {
                if (result.next()) {
                    Long account_id = result.getLong("account_id");
                    User user = new User(username, password);
                    user.setId(account_id);
                    return user;
                }
            }
        } catch (SQLException ex) {
            logger.error(ex);
            System.err.println("Error DB " + ex);
        }
        logger.traceExit();
        return null;
    }
}
