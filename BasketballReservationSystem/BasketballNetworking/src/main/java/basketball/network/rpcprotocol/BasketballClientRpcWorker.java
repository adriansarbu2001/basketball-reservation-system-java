package basketball.network.rpcprotocol;

import basketball.model.User;
import basketball.model.Ticket;
import basketball.model.validator.ValidatorException;
import basketball.services.IBasketballObserver;
import basketball.services.IBasketballServices;
import basketball.services.ServiceException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class BasketballClientRpcWorker implements Runnable, IBasketballObserver {
    private IBasketballServices server;
    private Socket connection;

    private ObjectInputStream input;
    private ObjectOutputStream output;
    private volatile boolean connected;

    public BasketballClientRpcWorker(IBasketballServices server, Socket connection) {
        this.server = server;
        this.connection = connection;
        try {
            output = new ObjectOutputStream(connection.getOutputStream());
            output.flush();
            input = new ObjectInputStream(connection.getInputStream());
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while (connected) {
            try {
                Object request = input.readObject();
                Response response = handleRequest((Request) request);
                if (response != null) {
                    sendResponse(response);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        try {
            input.close();
            output.close();
            connection.close();
        } catch (IOException e) {
            System.out.println("Error " + e);
        }
    }

    @Override
    public void ticketSold() {
        Response resp = new Response.Builder().type(ResponseType.TICKET_SOLD).build();
        System.out.println("Ticket sold");
        try {
            sendResponse(resp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Response handleRequest(Request request) {
        if (request.type() == RequestType.LOGIN) {
            System.out.println("Login request ..." + request.type());
            User user = (User) request.data();
            try {
                User userR = server.login(user, this);
                return new Response.Builder().type(ResponseType.OK).data(userR).build();
            } catch (ServiceException e) {
                connected = false;
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.LOGOUT) {
            System.out.println("Logout request");
            User user = (User) request.data();
            try {
                server.logout(user, this);
                connected = false;
                return new Response.Builder().type(ResponseType.OK).build();

            } catch (ServiceException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.SAVE_TICKET) {
            System.out.println("Saving ticket request ..." + request.type());
            Ticket ticket = (Ticket) request.data();
            try {
                server.saveTicket(ticket.getClientName(), String.valueOf(ticket.getNoSeats()), String.valueOf(ticket.getMatchId()));
                return new Response.Builder().type(ResponseType.OK).build();
            } catch (ServiceException | ValidatorException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.FIND_ALL_MATCH) {
            System.out.println("Find all match request ..." + request.type());
            try {
                return new Response.Builder().type(ResponseType.OK).data(server.findAll()).build();
            } catch (ServiceException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        if (request.type() == RequestType.AVAILABLE_MATCHES_DESCENDING) {
            System.out.println("Availabel matches descending request ..." + request.type());
            try {
                return new Response.Builder().type(ResponseType.OK).data(server.availableMatchesDescending()).build();
            } catch (ServiceException e) {
                return new Response.Builder().type(ResponseType.ERROR).data(e.getMessage()).build();
            }
        }
        return null;
    }

    private void sendResponse(Response response) throws IOException {
        System.out.println("sending response " + response);
        output.writeObject(response);
        output.flush();
    }
}
