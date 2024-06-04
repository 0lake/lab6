package com.general.models;

import com.general.models.base.Validatable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Класс дома
 */
@Data
@NoArgsConstructor
public class House implements Validatable, Serializable {
    private static final transient Map<Integer, House> houses = new HashMap<>();

    @NonNull
    private String name; // Поле не может быть null
    private Integer year; // Значение поля должно быть больше 0
    private Long numberOfFlatsOnFloor; // Значение поля должно быть больше 0
    private long numberOfLifts; // Значение поля должно быть больше 0

    public House(@NonNull String name, Integer year, Long numberOfFlatsOnFloor, long numberOfLifts) {
        this.name = name;
        this.year = year;
        this.numberOfFlatsOnFloor = numberOfFlatsOnFloor;
        this.numberOfLifts = numberOfLifts;
    }

    /**
     * Валидирует правильность полей.
     * @return true, если все верно, иначе false
     */
    @Override
    public boolean validate() {
        return name != null && !name.isEmpty() &&
                year != null && year > 0 &&
                numberOfFlatsOnFloor != null && numberOfFlatsOnFloor > 0 &&
                numberOfLifts > 0;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        House house = (House) o;
        return numberOfLifts == house.numberOfLifts &&
                Objects.equals(name, house.name) &&
                Objects.equals(year, house.year) &&
                Objects.equals(numberOfFlatsOnFloor, house.numberOfFlatsOnFloor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, year, numberOfFlatsOnFloor, numberOfLifts);
    }

    @Override
    public String toString() {
        return "House{" +
                "name='" + name + '\'' +
                ", year=" + year +
                ", numberOfFlatsOnFloor=" + numberOfFlatsOnFloor +
                ", numberOfLifts=" + numberOfLifts +
                '}';
    }
}
