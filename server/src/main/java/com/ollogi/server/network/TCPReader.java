package com.ollogi.server.network;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;

/**
 * Задача, выполняемая в отдельном потоке, для чтения входящих запросов из канала сокета клиента.
 * Этот класс читает данные из канала, парсит их и передает дальнейшую обработку обработчику.
 * Обеспечивает эффективное неблокирующее чтение с использованием Java NIO.
 */
public class TCPReader implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger("TCPReader");
    private final SelectionKey key;

    /**
     * Создает TCPReader с указанным ключом выбора.
     *
     * @param key Ключ выбора, связанный с каналом сокета клиента.
     */
    public TCPReader(SelectionKey key) {
        this.key = key;
    }

    /**
     * Читает данные из канала сокета клиента и передает дальнейшую обработку обработчику.
     * Этот метод является точкой входа для выполнения задачи.
     */
    @Override
    public void run() {
        readRequest();
    }

    /**
     * Читает входящий запрос из канала сокета клиента.
     * Этот метод обрабатывает процесс чтения, обеспечивая неблокирующую работу и обработку частичных чтений.
     *
     * @return true, если чтение и парсинг запроса прошли успешно, иначе false.
     */
    private boolean readRequest() {
        SocketChannel clientSocketChannel = (SocketChannel) key.channel();
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        try {
            logger.debug("Чтение запроса от {}", clientSocketChannel.getRemoteAddress());
            int bytesRead;
            while ((bytesRead = clientSocketChannel.read(buffer)) > 0) {
                buffer.flip();
                byteArrayOutputStream.write(buffer.array(), 0, buffer.limit());
                buffer.clear();
            }
            if (bytesRead == -1) {
                // Соединение закрыто клиентом
                closeConnection(clientSocketChannel);
                return false;
            }
        } catch (IOException e) {
            logger.error("Ошибка чтения данных: {}", e.getMessage());
            closeConnection(clientSocketChannel);
            return false;
        }

        // Запускаем новый обработчик для обработки запроса
        new Handler(clientSocketChannel, byteArrayOutputStream).run();
        return true;
    }

    /**
     * Закрывает соединение с клиентом.
     * Этот метод обрабатывает закрытие канала и отмену ключа выбора.
     *
     * @param clientSocketChannel Канал сокета клиента, который нужно закрыть.
     */
    private void closeConnection(SocketChannel clientSocketChannel) {
        try {
            key.cancel();
            clientSocketChannel.close();
            logger.info("Соединение закрыто: {}", clientSocketChannel.getRemoteAddress());
        } catch (IOException e) {
            logger.error("Ошибка закрытия канала: {}", e.getMessage());
        }
    }
}
