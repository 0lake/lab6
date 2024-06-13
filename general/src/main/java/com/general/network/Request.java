package com.general.network;

import java.util.Objects;

/**
 * Класс {@code Request} представляет запрос, отправленный по сети.
 * Он инкапсулирует информацию о команде, которую нужно выполнить, а также необязательные данные, связанные с командой.
 * Запросы могут быть созданы с различными комбинациями параметров для передачи различных типов информации.
 */
public class Request extends Sendable {
    private static final long serialVersionUID = 1L;

    /**
     * Создает запрос с указанным статусом успешности, именем команды и данными.
     *
     * @param success успешность выполнения запроса
     * @param name    имя команды, связанной с запросом
     * @param data    данные, связанные с запросом
     */
    public Request(boolean success, String name, Object data) {
        super(success, name, data);
    }

    /**
     * Создает запрос с указанным именем команды и данными, считая выполнение успешным.
     *
     * @param name имя команды, связанной с запросом
     * @param data данные, связанные с запросом
     */
    public Request(String name, Object data) {
        this(true, name, data);
    }

    /**
     * Возвращает имя команды, связанной с запросом.
     *
     * @return имя команды
     */
    public String getCommand() {
        return getMessage();
    }

    /**
     * Определяет, равен ли этот объект другому объекту.
     *
     * @param o объект, с которым сравнивается текущий объект
     * @return true, если этот объект равен переданному объекту, иначе false
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Request request = (Request) o;
        return Objects.equals(getCommand(), request.getCommand());
    }

    /**
     * Возвращает хэш-код для объекта.
     *
     * @return хэш-код для этого объекта
     */
    @Override
    public int hashCode() {
        return Objects.hash(getCommand());
    }

    /**
     * Возвращает строковое представление запроса.
     * Если данные присутствуют, они добавляются к строковому представлению.
     *
     * @return строковое представление запроса
     */
    @Override
    public String toString() {
        return "Request{" +
                (isSuccess() ? "" : "Ошибка при выполнении команды") +
                "command='" + getCommand() + '\'' +
                (getData() != null ? "data=" + getData() : "") +
                '}';
    }
}
