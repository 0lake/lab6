package com.general.network;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Класс {@code Sendable} представляет объект, который может быть отправлен по сети.
 * Он инкапсулирует информацию о статусе успешности, сообщении, данных и, по желанию, учетных данных пользователя.
 * Этот класс служит абстрактным базовым классом для конкретных типов объектов, которые могут быть отправлены.
 */
@Getter
@Setter
public abstract class Sendable implements Serializable {
    /**
     * Указывает, была ли успешной операция, связанная с этим объектом.
     */
    protected final boolean success;

    /**
     * Дополнительное сообщение, связанное с операцией, обычно используется для сообщений об ошибках.
     */
    protected final String message;

    /**
     * Данные, связанные с объектом, которые могут варьироваться в зависимости от конкретного подкласса.
     */
    protected final Object data;

    /**
     * Логин пользователя, связанный с запросом, если применимо.
     */
    protected String login;

    /**
     * Пароль пользователя, связанный с запросом, если применимо.
     */
    protected String password;

    /**
     * Идентификатор пользователя, связанный с запросом, если применимо.
     */
    protected Integer userId;

    /**
     * Создает объект, который может быть отправлен с указанным статусом успешности, сообщением и данными.
     *
     * @param success указывает, была ли успешной операция, связанная с этим объектом
     * @param message дополнительное сообщение, связанное с операцией
     * @param data    данные, связанные с объектом
     */
    public Sendable(final boolean success, String message, final Object data) {
        this.success = success;
        this.message = message;
        this.data = data;
    }
}
