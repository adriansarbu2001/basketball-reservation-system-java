import basketball.model.User;
import basketball.model.Match;
import basketball.model.Ticket;
import basketball.network.utils.AbstractServer;
import basketball.network.utils.BasketballRpcConcurrentServer;
import basketball.network.utils.ServerException;
import basketball.persistence.IUserRepository;
import basketball.persistence.IMatchRepository;
import basketball.persistence.ITicketRepository;
import basketball.persistence.repository.jdbc.UserDBRepository;
import basketball.persistence.repository.jdbc.MatchDBRepository;
import basketball.persistence.repository.jdbc.TicketDBRepository;
import basketball.model.validator.AccountValidator;
import basketball.model.validator.IValidator;
import basketball.model.validator.MatchValidator;
import basketball.model.validator.TicketValidator;
import basketball.server.BasketballServicesImpl;
import basketball.services.IBasketballServices;

import java.io.IOException;
import java.util.Properties;

public class StartRpcServer {
    private static int defaultPort = 55555;

    public static void main(String[] args) {
        Properties serverProps = new Properties();

        try {
            serverProps.load(StartRpcServer.class.getResourceAsStream("/basketballserver.properties"));
            System.out.println("Server properties set. ");
            serverProps.list(System.out);
        } catch (IOException e) {
            System.err.println("Cannot find basketballserver.properties " + e);
            return;
        }

        IValidator<User> accountValidator = new AccountValidator();
        IValidator<Match> matchValidator = new MatchValidator();
        IValidator<Ticket> ticketValidator = new TicketValidator();
        IUserRepository accountRepository = new UserDBRepository(serverProps, accountValidator);
        IMatchRepository matchRepository = new MatchDBRepository(serverProps, matchValidator);
        ITicketRepository ticketRepository = new TicketDBRepository(serverProps, ticketValidator);
        IBasketballServices basketballServerImpl = new BasketballServicesImpl(accountRepository, matchRepository, ticketRepository);

        int basketballServerPort = defaultPort;
        try {
            basketballServerPort = Integer.parseInt(serverProps.getProperty("basketball.server.port"));
        } catch (NumberFormatException nef) {
            System.err.println("Wrong Port Number " + nef.getMessage());
            System.err.println("Using default port " + defaultPort);
        }
        System.out.println("Starting server on port: " + basketballServerPort);
        AbstractServer server = new BasketballRpcConcurrentServer(basketballServerPort, basketballServerImpl);
        try {
            server.start();
        } catch (ServerException e) {
            System.err.println("Error starting the server" + e.getMessage());
        } finally {
            try {
                server.stop();
            } catch (ServerException e) {
                System.err.println("Error stopping server " + e.getMessage());
            }
        }
    }
}
