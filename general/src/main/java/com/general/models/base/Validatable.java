package com.general.models.base;

/**
 * интерфейс для проверки валидности объекта класса(Поле не может быть null, cтрока не может быть пустой, x>0 и т.д.)
 */

public interface Validatable {
    boolean validate();
}