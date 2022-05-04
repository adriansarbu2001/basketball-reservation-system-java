package basketball.server;

import basketball.model.User;
import basketball.model.Match;
import basketball.model.Ticket;
import basketball.persistence.IUserRepository;
import basketball.persistence.IMatchRepository;
import basketball.persistence.ITicketRepository;
import basketball.model.validator.ValidatorException;
import basketball.services.IBasketballObserver;
import basketball.services.IBasketballServices;
import basketball.services.ServiceException;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BasketballServicesImpl implements IBasketballServices {
    private IUserRepository accountRepository;
    private IMatchRepository matchRepository;
    private ITicketRepository ticketRepository;
    private Map<Long, IBasketballObserver> loggedClients;

    public BasketballServicesImpl(IUserRepository accountRepository, IMatchRepository matchRepository, ITicketRepository ticketRepository) {
        this.accountRepository = accountRepository;
        this.matchRepository = matchRepository;
        this.ticketRepository = ticketRepository;
        loggedClients = new ConcurrentHashMap<Long, IBasketballObserver>();
    }

    @Override
    public synchronized User login(User user, IBasketballObserver client) throws ServiceException {
        User userR = accountRepository.findBy(user.getUsername(), user.getPassword());
        if (userR != null) {
            if (loggedClients.get(userR.getId()) != null)
                throw new ServiceException("User already logged in.");
            loggedClients.put(userR.getId(), client);
            return userR;
        } else {
            throw new ServiceException("Authentication failed.");
        }
    }

    @Override
    public synchronized void logout(User user, IBasketballObserver client) throws ServiceException {
        IBasketballObserver localClient = loggedClients.remove(user.getId());
        if (localClient == null)
            throw new ServiceException("User " + user.getId() + " is not logged in.");
    }

    @Override
    public Iterable<Match> findAll() {
        return matchRepository.findAll();
    }

    @Override
    public void saveMatch(String name, float ticket_price, int no_available_seats) throws ValidatorException {
        Match match = new Match(name, ticket_price, no_available_seats);
        matchRepository.save(match);
    }

    @Override
    public void deleteMatch(Long match_id) {
        matchRepository.remove(match_id);
    }

    @Override
    public void updateMatch(Long match_id, String name, float ticket_price, int no_available_seats) throws ValidatorException {
        Match match = new Match(match_id, name, ticket_price, no_available_seats);
        matchRepository.update(match);
    }

    @Override
    public synchronized void saveTicket(String client_name, String no_seats, String match_id) throws ServiceException, ValidatorException {
        int no_seats_int = 0;
        long match_id_long = -1;
        try {
            no_seats_int = Integer.parseInt(no_seats);
            match_id_long = Long.parseLong(match_id);
        } catch (NumberFormatException e) {
            throw new ServiceException("empty text fields!");
        }
        Match match = matchRepository.findOne(match_id_long);
        if (match.getNoAvailableSeats() - no_seats_int < 0) {
            throw new ServiceException("Not enough seats available!");
        }
        Ticket ticket = new Ticket(client_name, no_seats_int, match_id_long);
        ticketRepository.save(ticket);
        match.setNoAvailableSeats(match.getNoAvailableSeats() - no_seats_int);
        matchRepository.update(match);
        notifyTicketSold();
    }

    @Override
    public Iterable<Match> availableMatchesDescending() {
        return matchRepository.availableMatchesDescending();
    }

    private final int defaultThreadsNo = 5;
    private void notifyTicketSold() {
        ExecutorService executor= Executors.newFixedThreadPool(defaultThreadsNo);
        for (Long id : this.loggedClients.keySet()){
            IBasketballObserver chatClient=loggedClients.get(id);
            if (chatClient!=null) {
                executor.execute(() -> {
                    System.out.println("Notifying [" + id + "] ticket sold.");
                    chatClient.ticketSold();
                });
            }
        }
        executor.shutdown();
    }
}
