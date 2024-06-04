package com.ollogi.server.network;

import com.general.network.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 * Utility class for sending responses to clients over TCP connections.
 * This class provides methods to serialize and send response objects through a SocketChannel.
 *
 
 */
public class TCPWriter {
    private static final Logger logger = LoggerFactory.getLogger("TCPWriter");

    /**
     * Sends a response object to the client through the given socket channel.
     * The response is serialized and written to the channel in a non-blocking manner.
     *
     * @param clientSocketChannel The socket channel connected to the client.
     * @param response            The response object to be sent.
     */
    public static void sendResponse(SocketChannel clientSocketChannel, Response response) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            logger.debug("Sending response to client {}", clientSocketChannel.getRemoteAddress());
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();

            byte[] responseBytes = byteArrayOutputStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(responseBytes);

            // Write the response bytes to the channel
            while (buffer.hasRemaining()) {
                clientSocketChannel.write(buffer);
            }
        } catch (IOException e) {
            logger.error("Error sending response: {}", e.getMessage());
        }
    }
}
