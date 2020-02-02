package ru.otus.homework.frontend;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.frontend.handlers.GetAllUsersResponseHandler;
import ru.otus.homework.ms_client.Message;
import ru.otus.homework.ms_client.MessageType;
import ru.otus.homework.ms_client.MsClient;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class FrontendServer {
    private static Logger logger = LoggerFactory.getLogger(FrontendServer.class);
    private final int serverPort;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final MsClient msClient;
    private final Gson gson = new Gson();

    public FrontendServer(int serverPort, MsClient msClient) {
        this.serverPort = serverPort;
        this.msClient = msClient;
    }

    public void start() {
        Message regServerMsg = msClient.produceMessage(null, serverPort, MessageType.REGISTER_MESSAGE_CONSUMER);
        msClient.sendMessage(regServerMsg);

        //executor.submit() added before ServerSocket created as Spring blocks further initialization on serverSocket.accept()
        executor.submit(() -> {
            try (ServerSocket serverSocket = new ServerSocket(serverPort)) {
                while (!Thread.currentThread().isInterrupted()) {
                    logger.info("waiting for client connection");
                    Socket socket = serverSocket.accept();
                    clientHandler(socket);
                }
            } catch (Exception ex) {
                logger.error("error", ex);
            }
        });
        executor.shutdown();
    }

    //gets input data from the connected socket and sends to clientSocket
    private void clientHandler(Socket socket) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {

            while (true) {
                String input = in.readLine();
                if (input != null) {
                    try {
                        Message message = gson.fromJson(input, Message.class);
                        logger.info("Got message from queue: {}", message);
                        msClient.handle(message);
                    } catch (Exception ex) {
                        logger.error("error", ex);
                    }
                }
            }
        } catch (Exception ex) {
            logger.error("error", ex);
        }
    }
}
