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
 * Обрабатывает входящие запросы от клиентов в отдельном потоке.
 * Этот класс читает запрос, обрабатывает его и отправляет ответ.
 */
public class Handler implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger("Handler");
    private static final Request SAVE_REQUEST = new Request("save", null);

    @Setter
    private static CommandManager commandManager;

    private final SocketChannel clientSocketChannel;
    private final ByteArrayOutputStream byteArrayOutputStream;

    /**
     * Конструктор для создания нового объекта Handler.
     *
     * @param clientSocketChannel   Канал сокета, подключенный к клиенту.
     * @param byteArrayOutputStream Поток вывода, содержащий запрос клиента.
     */
    public Handler(SocketChannel clientSocketChannel, ByteArrayOutputStream byteArrayOutputStream) {
        this.clientSocketChannel = clientSocketChannel;
        this.byteArrayOutputStream = byteArrayOutputStream;
    }

    /**
     * Точка входа для выполнения Handler.
     * Читает запрос, обрабатывает его и отправляет соответствующий ответ.
     */
    @Override
    public void run() {
        try {
            byte[] requestBytes = byteArrayOutputStream.toByteArray();
            try (ObjectInputStream objectInputStream = new ObjectInputStream(new ByteArrayInputStream(requestBytes))) {
                Request request = (Request) objectInputStream.readObject();
                if ("exit".equals(request.getCommand())) {
                    logger.info("Клиент {} завершил работу", clientSocketChannel.getRemoteAddress());
                    clientSocketChannel.close();
                    if(commandManager.handle(SAVE_REQUEST).isSuccess())
                        logger.info("Коллекция сохранена");
                    return;
                }
                handleRequest(request);
            }
        } catch (Exception e) {
            logger.error("Ошибка обработки запроса: {}", e.getMessage());
            sendErrorResponse(clientSocketChannel);
        }
    }

    /**
     * Обрабатывает входящий запрос, передавая его в CommandManager.
     * Отправляет обработанный ответ обратно клиенту.
     *
     * @param request Объект запроса, полученный от клиента.
     */
    private void handleRequest(Request request) {
        Response response = commandManager.handle(request);
        TCPWriter.sendResponse(clientSocketChannel, response);
    }

    /**
     * Отправляет клиенту ответ об ошибке, указывая на то, что запрос был недействительным.
     *
     * @param channel Канал сокета, в который отправляется ответ.
     */
    private void sendErrorResponse(SocketChannel channel) {
        Response response = new Response(false, "Недействительный запрос");
        TCPWriter.sendResponse(channel, response);
    }
}
