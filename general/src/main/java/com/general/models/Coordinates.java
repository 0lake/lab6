package com.general.models;

import com.general.models.base.Validatable;
import lombok.Data;
import lombok.NonNull;

import java.io.Serializable;

/**
 * Класс координат.
 */
@Data
public class Coordinates implements Validatable, Serializable {
    @NonNull
    private Long x; // Поле не может быть null

    @NonNull
    private Float y; // Значение поля должно быть больше -519, Поле не может быть null

    public Coordinates(@NonNull Long x, @NonNull Float y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Валидирует правильность полей.
     * @return true, если все верно, иначе false
     */
    @Override
    public boolean validate() {
        return y > -519;
    }
}
