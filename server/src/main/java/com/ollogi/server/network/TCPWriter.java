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
 * Утилитарный класс для отправки ответов клиентам по TCP-соединениям.
 * Этот класс предоставляет методы для сериализации и отправки объектов ответов через SocketChannel.
 */
public class TCPWriter {
    private static final Logger logger = LoggerFactory.getLogger("TCPWriter");

    /**
     * Отправляет объект ответа клиенту через указанный сокет-канал.
     * Ответ сериализуется и записывается в канал неблокирующим способом.
     *
     * @param clientSocketChannel Сокет-канал, подключенный к клиенту.
     * @param response            Объект ответа для отправки.
     */
    public static void sendResponse(SocketChannel clientSocketChannel, Response response) {
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream)) {

            logger.debug("Отправка ответа клиенту {}", clientSocketChannel.getRemoteAddress());
            objectOutputStream.writeObject(response);
            objectOutputStream.flush();

            byte[] responseBytes = byteArrayOutputStream.toByteArray();
            ByteBuffer buffer = ByteBuffer.wrap(responseBytes);

            // Запись байтов ответа в канал
            while (buffer.hasRemaining()) {
                clientSocketChannel.write(buffer);
            }
        } catch (IOException e) {
            logger.error("Ошибка отправки ответа: {}", e.getMessage());
        }
    }
}
