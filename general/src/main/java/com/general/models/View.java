package com.general.models;

import java.io.Serializable;

/**
 * Перечисление типов вида.
 */
public enum View implements Serializable {
    STREET,
    YARD,
    BAD,
    GOOD;

    /**
     * Возвращает все элементы enum через запятую (строку).
     *
     * @return строка с именами всех элементов перечисления, разделенными запятыми
     */
    public static String names() {
        return String.join(", ", valuesAsStringArray());
    }

    /**
     * Преобразует элементы перечисления в массив строк.
     *
     * @return массив строк с именами всех элементов перечисления
     */
    private static String[] valuesAsStringArray() {
        return java.util.Arrays.stream(values())
                .map(View::name)
                .toArray(String[]::new);
    }
}
