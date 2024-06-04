package com.general.models;

import com.general.models.base.Element;
import com.general.models.base.Validatable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * Класс, представляющий сущность "Квартира".
 */
@EqualsAndHashCode(callSuper = false)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Flat extends Element implements Validatable, Comparable<Flat> {
    private String name;
    private Coordinates coordinates;
    private LocalDate creationDate = LocalDate.now();
    private Integer area;
    private int numberOfRooms;
    private int height;
    private Float kitchenArea;
    private View view;
    private House house;
    private Integer userId;

    /**
     * Создает новый объект Flat.
     *
     * @param name          имя квартиры
     * @param coordinates   координаты квартиры
     * @param area          площадь квартиры
     * @param numberOfRooms количество комнат в квартире
     * @param height        высота потолков в квартире
     * @param kitchenArea   площадь кухни в квартире
     * @param view          вид из окна квартиры
     * @param house         дом, в котором находится квартира
     */
    public Flat(long id, String name, Coordinates coordinates, int area, int numberOfRooms, int height, Float kitchenArea, View view, House house) {
        setId(id);
        this.name = name;
        this.coordinates = coordinates;
        this.area = area;
        this.numberOfRooms = numberOfRooms;
        this.height = height;
        this.kitchenArea = kitchenArea;
        this.house = house;
        this.view = view;
    }

    /**
     * Валидирует правильность полей.
     *
     * @return true, если все верно, иначе false
     */
    @Override
    public boolean validate() {
        if (name == null || name.isEmpty()) return false;
        if (coordinates == null || !coordinates.validate()) return false;
        if (creationDate == null) return false;
        if (area != null && area <= 0) return false;
        if (numberOfRooms <= 0) return false;
        if (height <= 0) return false;
        if (kitchenArea != null && kitchenArea <= 0) return false;
        if (view == null) return false;
        if (house == null) return false;
        return true;
    }

    @Override
    public int compareTo(Flat flat) {
        int comparison = Integer.compare(this.numberOfRooms, flat.numberOfRooms);
        if (comparison != 0) {
            return comparison;
        }
        return Integer.compare(this.height, flat.height);
    }
}
