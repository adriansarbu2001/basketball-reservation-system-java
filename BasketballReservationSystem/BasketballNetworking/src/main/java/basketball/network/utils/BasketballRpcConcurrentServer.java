package basketball.network.utils;

import basketball.network.rpcprotocol.BasketballClientRpcWorker;
import basketball.services.IBasketballServices;

import java.net.Socket;

public class BasketballRpcConcurrentServer extends AbstractConcurrentServer {
    private IBasketballServices chatServer;

    public BasketballRpcConcurrentServer(int port, IBasketballServices chatServer) {
        super(port);
        this.chatServer = chatServer;
        System.out.println("Chat- ChatRpcConcurrentServer");
    }

    @Override
    protected Thread createWorker(Socket client) {
        BasketballClientRpcWorker worker = new BasketballClientRpcWorker(chatServer, client);

        return new Thread(worker);
    }

    @Override
    public void stop() {
        System.out.println("Stopping services ...");
    }
}
