package com.ollogi.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A TCP server that listens for incoming connections and handles them asynchronously.
 * The server is designed to be non-blocking, utilizing Java NIO and a selector to manage multiple connections.
 * It uses a thread pool to handle read operations asynchronously.
*/
public class TCPServer {
    private static final Logger logger = LoggerFactory.getLogger("TCPServer");
    private final int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    /**
     * Constructs a TCP server with the specified port.
     *
     * @param port The port on which the server will listen for incoming connections.
     */
    public TCPServer(int port) {
        this.port = port;
    }

    /**
     * Starts the TCP server, initializing the server socket channel and handling incoming connections.
     * This method contains the main server loop, which continuously waits for events on the registered channels.
     */
    public void start() {
        initServerSocketChannel();
        while (!Thread.currentThread().isInterrupted()) {
            select();
            for (SelectionKey key : selector.selectedKeys()) {
                if (key.isAcceptable()) {
                    handleAccept();
                } else if (key.isReadable()) {
                    new TCPReader(key).run();
                }
            }
            selector.selectedKeys().clear();
        }
    }

    /**
     * Waits for events on the registered channels.
     * This method blocks until events occur or until the thread is interrupted.
     */
    private void select() {
        try {
            selector.select();
        } catch (Exception e) {
            logger.error("Error selecting thread: {}", e.getMessage());
        }
    }

    /**
     * Initializes the server socket channel and registers it with the selector.
     * This method sets up the server to accept incoming connections on the specified port.
     */
    private void initServerSocketChannel() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Server started on port {}", port);
        } catch (ClosedChannelException e) {
            logger.error("Channel closed: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Error opening server socket: {}", e.getMessage());
        }
    }

    /**
     * Handles an incoming connection request.
     * This method accepts the connection, configures it as non-blocking, and registers it with the selector for read events.
     */
    private void handleAccept() {
        try {
            SocketChannel client = serverSocketChannel.accept();
            if (client != null) {
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ);
                logger.info("New connection: {}", client.getRemoteAddress());
            }
        } catch (IOException e) {
            logger.error("Error accepting connection: {}", e.getMessage());
        }
    }
}
