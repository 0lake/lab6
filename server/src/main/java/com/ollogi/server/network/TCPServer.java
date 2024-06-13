package com.ollogi.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * TCP-сервер, прослушивающий входящие соединения и обрабатывающий их асинхронно.
 * Сервер разработан для неблокирующей работы, используя Java NIO и селектор для управления несколькими соединениями.
 * Он использует пул потоков для асинхронной обработки операций чтения.
 */
public class TCPServer {
    private static final Logger logger = LoggerFactory.getLogger("TCPServer");
    private final int port;
    private Selector selector;
    private ServerSocketChannel serverSocketChannel;

    /**
     * Создает TCP-сервер с указанным портом.
     *
     * @param port Порт, на котором сервер будет прослушивать входящие соединения.
     */
    public TCPServer(int port) {
        this.port = port;
    }

    /**
     * Запускает TCP-сервер, инициализируя канал серверного сокета и обрабатывая входящие соединения.
     * Этот метод содержит основной цикл сервера, который непрерывно ожидает событий на зарегистрированных каналах.
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
     * Ожидает событий на зарегистрированных каналах.
     * Этот метод блокируется, пока не произойдут события или пока поток не будет прерван.
     */
    private void select() {
        try {
            selector.select();
        } catch (Exception e) {
            logger.error("Ошибка выбора потока: {}", e.getMessage());
        }
    }

    /**
     * Инициализирует канал серверного сокета и регистрирует его в селекторе.
     * Этот метод настраивает сервер для принятия входящих соединений на указанном порту.
     */
    private void initServerSocketChannel() {
        try {
            selector = Selector.open();
            serverSocketChannel = ServerSocketChannel.open();
            serverSocketChannel.configureBlocking(false);
            serverSocketChannel.socket().bind(new InetSocketAddress(port));
            serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);
            logger.info("Сервер запущен на порту {}", port);
        } catch (ClosedChannelException e) {
            logger.error("Канал закрыт: {}", e.getMessage());
        } catch (IOException e) {
            logger.error("Ошибка открытия серверного сокета: {}", e.getMessage());
        }
    }

    /**
     * Обрабатывает запрос на входящее соединение.
     * Этот метод принимает соединение, настраивает его как неблокирующее и регистрирует его в селекторе для событий чтения.
     */
    private void handleAccept() {
        try {
            SocketChannel client = serverSocketChannel.accept();
            if (client != null) {
                client.configureBlocking(false);
                client.register(selector, SelectionKey.OP_READ);
                logger.info("Новое соединение: {}", client.getRemoteAddress());
            }
        } catch (IOException e) {
            logger.error("Ошибка приема соединения: {}", e.getMessage());
        }
    }
}
