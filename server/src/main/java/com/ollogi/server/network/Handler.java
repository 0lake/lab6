package com.ollogi.server.network;

import com.general.managers.CommandManager;
import com.general.network.Request;
import com.general.network.Response;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.nio.channels.SocketChannel;

/**
 * Handles incoming requests from clients on a separate thread.
 * This class reads the request, processes it, and sends back a response.
 *
 */
public class Handler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger("Handler");
    private static final Request SAVE_REQUEST = new Request("save", null);

    @Setter
    private static CommandManager commandManager;

    private final SocketChannel clientSocketChannel;
    private final ByteArrayOutputStream byteArrayOutputStream;

    /**
     * Constructs a new Handler object.
     *
     * @param clientSocketChannel   The socket channel connected to the client.
     * @param byteArrayOutputStream The output stream containing the client's request.=
     */
    public Handler(SocketChannel clientSocketChannel, ByteArrayOutputStream byteArrayOutputStream) {
        this.clientSocketChannel = clientSocketChannel;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    /**
     * Entry point for the Handler's execution.
     * Reads the request, processes it, and sends the appropriate response.
     */
    @Override
    public void run() {
        try {
            byte[] requestBytes = byteArrayOutputStream.toByteArray();
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(requestBytes))) {
                Request request = (Request) objectInputStream.readObject();
                if ("exit".equals(request.getCommand())) {
                    logger.info("Client {} terminated", clientSocketChannel.getRemoteAddress());
                    clientSocketChannel.close();
                    if(commandManager.handle(SAVE_REQUEST).isSuccess())
                        logger.info("Collection saved");
                    return;
                }
                handleRequest(request);
            }
        } catch (Exception e) {
            logger.error("Error processing request: {}", e.getMessage());
            sendErrorResponse(clientSocketChannel);
        }
    }

    /**
     * Handles the incoming request by delegating it to the CommandManager.
     * Sends the processed response back to the client.
     *
     * @param request The request object received from the client.
     */
    private void handleRequest(Request request) {
        Response response = commandManager.handle(request);
        TCPWriter.sendResponse(clientSocketChannel, response);
    }

    /**
     * Sends an error response to the client, indicating that the request was invalid.
     *
     * @param channel The socket channel to send the response to.
     */
    private void sendErrorResponse(SocketChannel channel) {
        Response response = new Response(false, "Invalid request");
        TCPWriter.sendResponse(channel, response);
    }
}
