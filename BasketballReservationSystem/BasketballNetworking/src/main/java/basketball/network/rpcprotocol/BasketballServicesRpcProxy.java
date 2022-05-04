package basketball.network.rpcprotocol;

import basketball.model.User;
import basketball.model.Match;
import basketball.model.Ticket;
import basketball.persistence.repository.RepositoryException;
import basketball.model.validator.ValidatorException;
import basketball.services.IBasketballObserver;
import basketball.services.IBasketballServices;
import basketball.services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class BasketballServicesRpcProxy implements IBasketballServices {
    private String host;
    private int port;

    private IBasketballObserver client;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private Socket connection;

    private BlockingQueue<Response> qresponses;
    private volatile boolean finished;

    public BasketballServicesRpcProxy(String host, int port) {
        this.host = host;
        this.port = port;
        qresponses = new LinkedBlockingQueue<Response>();
    }

    @Override
    public User login(User user, IBasketballObserver client) throws ServiceException {
        initializeConnection();
        Request req = new Request.Builder().type(RequestType.LOGIN).data(user).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            this.client = client;
            return (User) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            closeConnection();
            throw new ServiceException(err);
        }
        return null;
    }

    @Override
    public void logout(User user, IBasketballObserver client) throws ServiceException {
        Request req = new Request.Builder().type(RequestType.LOGOUT).data(user).build();
        sendRequest(req);
        Response response = readResponse();
        closeConnection();
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
    }

    @Override
    public Iterable<Match> findAll() throws ServiceException {
        Request req = new Request.Builder().type(RequestType.FIND_ALL_MATCH).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            return (Iterable<Match>) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
        return null;
    }

    @Override
    public void saveMatch(String name, float ticket_price, int no_available_seats) throws ValidatorException {

    }

    @Override
    public void deleteMatch(Long match_id) throws RepositoryException {

    }

    @Override
    public void updateMatch(Long match_id, String name, float ticket_price, int no_available_seats) throws ValidatorException {

    }

    @Override
    public void saveTicket(String client_name, String no_seats, String match_id) throws ServiceException, ValidatorException {
        try {
            Ticket ticket = new Ticket(client_name, Integer.parseInt(no_seats), Long.parseLong(match_id));
            Request req = new Request.Builder().type(RequestType.SAVE_TICKET).data(ticket).build();
            sendRequest(req);
            Response response = readResponse();
            if (response.type() == ResponseType.ERROR) {
                String err = response.data().toString();
                throw new ServiceException(err);
            }
        } catch (NumberFormatException e) {
            throw new ValidatorException("Invalid input!");
        }
    }

    @Override
    public Iterable<Match> availableMatchesDescending() throws ServiceException {
        Request req = new Request.Builder().type(RequestType.AVAILABLE_MATCHES_DESCENDING).build();
        sendRequest(req);
        Response response = readResponse();
        if (response.type() == ResponseType.OK) {
            return (Iterable<Match>) response.data();
        }
        if (response.type() == ResponseType.ERROR) {
            String err = response.data().toString();
            throw new ServiceException(err);
        }
        return null;
    }

    private void closeConnection() {
        finished = true;
        try {
            input.close();
            output.close();
            connection.close();
            client = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendRequest(Request request) throws ServiceException {
        try {
            output.writeObject(request);
            output.flush();
        } catch (IOException e) {
            throw new ServiceException("Error sending object " + e);
        }

    }

    private Response readResponse() throws ServiceException {
        Response response = null;
        try {
            response = qresponses.take();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return response;
    }

    private void initializeConnection() throws ServiceException {
        try {
            connection = new Socket(host, port);
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            finished = false;
            startReader();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void startReader() {
        Thread tw = new Thread(new ReaderThread());
        tw.start();
    }

    private void handleUpdate(Response response) {
        if (response.type() == ResponseType.TICKET_SOLD) {
            System.out.println("Ticket sold");
            client.ticketSold();
        }
    }

    private boolean isUpdate(Response response) {
        return response.type() == ResponseType.TICKET_SOLD;
    }

    private class ReaderThread implements Runnable {
        public void run() {
            while (!finished) {
                try {
                    Object response = input.readObject();
                    System.out.println("response received " + response);
                    if (isUpdate((Response) response)) {
                        handleUpdate((Response) response);
                    } else {
                        try {
                            qresponses.put((Response) response);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    System.out.println("Reading error " + e);
                }
            }
        }
    }
}
