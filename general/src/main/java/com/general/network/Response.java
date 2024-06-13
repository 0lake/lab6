package com.general.network;

/**
 * Класс {@code Response} представляет ответ, отправленный по сети.
 * Он инкапсулирует информацию о успешности или неуспешности операции, а также необязательное сообщение и данные.
 * Ответы могут быть созданы с различными комбинациями параметров для передачи различных типов информации.
 */
public class Response extends Sendable {

    /**
     * Создает ответ с указанным статусом успешности, сообщением и данными.
     *
     * @param success успешность выполнения операции
     * @param message сообщение, связанное с ответом
     * @param data    данные, связанные с ответом
     */
    public Response(boolean success, String message, Object data) {
        super(success, message, data);
    }

    /**
     * Создает ответ с указанным статусом успешности и сообщением.
     *
     * @param success успешность выполнения операции
     * @param message сообщение, связанное с ответом
     */
    public Response(boolean success, String message) {
        super(success, message, null);
    }

    /**
     * Создает ответ с указанным статусом успешности.
     *
     * @param success успешность выполнения операции
     */
    public Response(boolean success) {
        super(success, null, null);
    }

    /**
     * Возвращает строковое представление ответа.
     * Если сообщение и данные присутствуют, они добавляются к строковому представлению.
     *
     * @return строковое представление ответа
     */
    @Override
    public String toString() {
        return ((message != null) ? message : "") + (data != null ? ((message != null) ? '\n' + data.toString() : data.toString()) : "");
    }
}
