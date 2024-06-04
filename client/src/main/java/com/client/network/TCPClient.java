package com.client.network;

import com.general.io.Console;
import com.general.network.Request;
import com.general.network.Response;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.TimeoutException;

public class TCPClient {
    private final Console output;
    private final String serverAddress;
    private final int serverPort;
    private SocketChannel socketChannel;

    public TCPClient(String serverAddress, int serverPort, Console output) {
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
        this.output = output;
    }

    /**
     * Пытается подключиться к серверу в течение указанного времени.
     *
     * @return true, если подключение успешно, иначе false
     * @throws TimeoutException если время подключения истекло
     */
    public boolean connect() throws TimeoutException {
        Selector selector = null;
        boolean connectFlag = false;
        try {
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);
            InetSocketAddress address = new InetSocketAddress(serverAddress, serverPort);
            socketChannel.connect(address);

            selector = Selector.open();
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            long startTime = System.currentTimeMillis();
            while (System.currentTimeMillis() - startTime < 10000) {
                if (selector.select(1000) == 0) {
                    continue;
                }

                Iterator<SelectionKey> keys = selector.selectedKeys().iterator();
                while (keys.hasNext()) {
                    SelectionKey key = keys.next();
                    keys.remove();
                    if (key.isConnectable()) {
                        try {
                            connectFlag = socketChannel.finishConnect();
                        } catch (IOException ignored) {
                        }
                        if (connectFlag) {
                            output.println("Подключено к серверу: " + serverAddress + ":" + serverPort);
                            return true;
                        }
                    }
                }
            }
            throw new TimeoutException("Не удалось подключиться в течение 10 секунд");
        } catch (IOException e) {
            output.println("Ошибка при подключении к серверу: " + e.getMessage());
            return false;
        } catch (UnresolvedAddressException badAddress) {
            output.printError("Ошибка в указании адреса");
            return false;
        } finally {
            if (!connectFlag) {
                closeResources(selector, socketChannel);
            }
        }
    }

    /**
     * Проверяет, есть ли подключение к серверу, и при необходимости пытается переподключиться.
     *
     * @return true, если подключение обеспечено, иначе false
     */
    public boolean ensureConnection() {
        if (!isConnected()) {
            output.println("Нет подключения к серверу.");
            try {
                output.println("Попытка повторного подключения к серверу...");
                connect();
            } catch (TimeoutException e) {
                output.printError("Ошибка переподключения: " + e.getMessage());
                return false;
            }
        }
        return true;
    }

    /**
     * Отключается от сервера.
     *
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public void disconnect() throws IOException {
        if (socketChannel != null) {
            socketChannel.close();
        }
    }

    /**
     * Отправляет запрос на сервер.
     *
     * @param request запрос для отправки
     * @throws IOException если произошла ошибка ввода-вывода
     */
    public void sendRequest(Request request) throws IOException {
        if (!ensureConnection()) throw new IOException("Не удалось обеспечить подключение");
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(request);
        objectOutputStream.flush();
        byte[] requestBytes = byteArrayOutputStream.toByteArray();
        ByteBuffer buffer = ByteBuffer.wrap(requestBytes);
        socketChannel.write(buffer);
    }

    /**
     * Получает ответ от сервера.
     *
     * @return ответ от сервера
     * @throws IOException если произошла ошибка ввода-вывода
     * @throws ClassNotFoundException если класс ответа не найден
     */
    public Response receiveResponse() throws IOException, ClassNotFoundException {
        ensureConnection();
        Selector selector = Selector.open();
        socketChannel.configureBlocking(false);
        socketChannel.register(selector, SelectionKey.OP_READ);
        ByteBuffer buffer = ByteBuffer.allocate(16384);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        long startTime = System.currentTimeMillis();

        while (System.currentTimeMillis() - startTime < 10000) { // Ожидание ответа до 10 секунд
            int readyChannels = selector.select(10000); // Ожидание событий до 10 секунд
            if (readyChannels == 0) {
                continue;
            }

            Set<SelectionKey> selectedKeys = selector.selectedKeys();
            Iterator<SelectionKey> keyIterator = selectedKeys.iterator();

            while (keyIterator.hasNext()) {
                SelectionKey key = keyIterator.next();
                if (key.isReadable()) {
                    int bytesRead;
                    while ((bytesRead = socketChannel.read(buffer)) > 0) {
                        buffer.flip();
                        byteArrayOutputStream.write(buffer.array(), 0, bytesRead);
                        buffer.clear();
                    }
                    if (bytesRead == -1) {
                        // Закрытие канала
                        socketChannel.close();
                    }
                }
                keyIterator.remove();
            }

            byte[] responseBytes = byteArrayOutputStream.toByteArray();
            if (responseBytes.length > 0) {
                try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(responseBytes))) {
                    return (Response) objectInputStream.readObject();
                } catch (Exception ignore) {
                }
            }
        }

        // Если за 10 секунд не получили ответ, генерируем исключение
        throw new IOException("Нет ответа от сервера в течение указанного времени ожидания");
    }

    /**
     * Отправляет команду на сервер и ждет ответа.
     *
     * @param request запрос для отправки
     * @return ответ от сервера
     */
    public Response sendCommand(Request request) {
        try {
            sendRequest(request);
            return receiveResponse();
        } catch (IOException | ClassNotFoundException e) {
            output.printError(e.getMessage());
        }
        output.printError("Запрос не отправлен. Повторите попытку позже.");
        try {
            disconnect();
        } catch (IOException e) {
            output.printError("Не удалось закрыть соединение");
        }
        return new Response(false, "Команда не выполнена!", null);
    }

    /**
     * Проверяет, подключен ли клиент к серверу.
     *
     * @return true, если подключен, иначе false
     */
    public boolean isConnected() {
        return socketChannel != null && socketChannel.isConnected();
    }

    /**
     * Закрывает указанные ресурсы.
     *
     * @param selector селектор для закрытия
     * @param socketChannel канал для закрытия
     */
    private void closeResources(Selector selector, SocketChannel socketChannel) {
        try {
            if (socketChannel != null) {
                socketChannel.close();
            }
            if (selector != null) {
                selector.close();
            }
        } catch (IOException e) {
            output.println("Ошибка при закрытии ресурсов: " + e.getMessage());
        }
    }
}
