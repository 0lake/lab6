package com.ollogi.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * A runnable task for reading incoming requests from a client's socket channel.
 * It reads data from the channel, parses it, and delegates further processing to a handler.
 * This class ensures efficient non-blocking reading using Java NIO.
 *
 */
public class TCPReader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger("TCPReader");
    private final SelectionKey key;

    /**
     * Constructs a TCPReader with the given selection key.
     *
     * @param key The selection key associated with the client's socket channel.
     */
    public TCPReader(SelectionKey key) {
        this.key = key;
    }

    /**
     * Reads data from the client's socket channel and delegates further processing to a handler.
     * This method is the entry point for the task execution.
     */
    @Override
    public void run() {
        readRequest();
    }

    /**
     * Reads the incoming request from the client's socket channel.
     * This method handles the reading process, ensuring non-blocking operation and handling partial reads.
     *
     * @return true if the reading and parsing of the request is successful, false otherwise.
     */
    private boolean readRequest() {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            logger.debug("Reading request from {}", clientSocketChannel.getRemoteAddress());
            int bytesRead;
            while ((bytesRead = clientSocketChannel.read(buffer)) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
            if (bytesRead == -1) {
                // Connection closed by client
                closeConnection(clientSocketChannel);
                return false;
            }
        } catch (IOException e) {
            logger.error("Error reading data: {}", e.getMessage());
            closeConnection(clientSocketChannel);
            return false;
        }

        // Start a new handler to process the request
        new Handler(clientSocketChannel, byteArrayOutputStream).run();
        return true;
    }

    /**
     * Closes the connection to the client.
     * This method handles closing the channel and canceling the selection key.
     *
     * @param clientSocketChannel The client's socket channel to be closed.
     */
    private void closeConnection(SocketChannel clientSocketChannel) {
        try {
            key.cancel();
            clientSocketChannel.close();
            logger.info("Connection closed: {}", clientSocketChannel.getRemoteAddress());
        } catch (IOException e) {
            logger.error("Error closing channel: {}", e.getMessage());
        }
    }
}
