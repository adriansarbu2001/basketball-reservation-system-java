package basketball.services;

import basketball.model.User;
import basketball.model.Match;
import basketball.persistence.repository.RepositoryException;
import basketball.model.validator.ValidatorException;

public interface IBasketballServices {
    User login(User user, IBasketballObserver client) throws ServiceException;

    void logout(User user, IBasketballObserver client) throws ServiceException;

    Iterable<Match> findAll() throws ServiceException;

    void saveMatch(String name, float ticket_price, int no_available_seats) throws ValidatorException;

    void deleteMatch(Long match_id) throws RepositoryException;

    void updateMatch(Long match_id, String name, float ticket_price, int no_available_seats) throws ValidatorException;

    void saveTicket(String client_name, String no_seats, String match_id) throws ServiceException, ValidatorException;

    Iterable<Match> availableMatchesDescending() throws ServiceException;
}
