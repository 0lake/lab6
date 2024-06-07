package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.managers.CollectionManager;
import com.general.models.base.Element;
import com.general.network.Request;
import com.general.network.Response;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;

/**
 * Команда 'show'. Выводит все элементы коллекции.
 */
public class Show<T extends Element & Comparable<T>> extends Command {
    private final CollectionManager<T> collectionManager;

    public Show(CollectionManager<T> collectionManager) {
        super("show", "вывести все элементы коллекции");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Response с результатом выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            if (request.getData() != null) {
                throw new WrongAmountOfElementsException();
            }

            if (collectionManager.getCollection().isEmpty()) {
                return new Response(true, "Коллекция пуста.");
            }

            StringBuilder result = new StringBuilder();
            List<T> collection = new ArrayList<>(collectionManager.getCollection());
            if (!collection.isEmpty()) {
                T firstElement = collection.get(0);
                String[] headers = getFieldNames(firstElement);
                int[] columnWidths = getColumnWidths(headers, collection);

                result.append(formatRow(headers, columnWidths)).append("\n");
                result.append(formatRow(getSeparator(columnWidths), columnWidths)).append("\n");

                Iterator<T> iterator = collection.iterator();
                while (iterator.hasNext()) {
                    T element = iterator.next();
                    result.append(formatRow(getFieldValues(element), columnWidths)).append("\n");
                }
            }

            return new Response(true, result.toString().trim());

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        } catch (IllegalAccessException e) {
            return new Response(false, "Ошибка доступа к полям объектов.");
        }
    }

    /**
     * Возвращает названия полей объекта
     *
     * @param element объект
     * @return массив названий полей
     */
    private String[] getFieldNames(T element) {
        Field[] fields = element.getClass().getDeclaredFields();
        String[] fieldNames = new String[fields.length + 1];
        fieldNames[0] = "id"; // Добавляем id как первое поле
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i + 1] = fields[i].getName();
        }
        return fieldNames;
    }

    /**
     * Возвращает значения полей объекта
     *
     * @param element объект
     * @return массив значений полей
     * @throws IllegalAccessException если доступ к полям невозможен
     */
    private String[] getFieldValues(T element) throws IllegalAccessException {
        Field[] fields = element.getClass().getDeclaredFields();
        String[] fieldValues = new String[fields.length + 1];
        fieldValues[0] = String.valueOf(getId(element)); // Добавляем значение id как первое поле
        for (int i = 0; i < fields.length; i++) {
            fields[i].setAccessible(true);
            fieldValues[i + 1] = String.valueOf(fields[i].get(element));
        }
        return fieldValues;
    }

    /**
     * Определяет ширину каждой колонки для форматирования таблицы
     *
     * @param headers    заголовки колонок
     * @param collection коллекция элементов
     * @return массив с ширинами колонок
     * @throws IllegalAccessException если доступ к полям невозможен
     */
    private int[] getColumnWidths(String[] headers, List<T> collection) throws IllegalAccessException {
        int[] widths = new int[headers.length];
        for (int i = 0; i < headers.length; i++) {
            widths[i] = headers[i].length();
        }

        for (T element : collection) {
            String[] values = getFieldValues(element);
            for (int i = 0; i < values.length; i++) {
                if (values[i].length() > widths[i]) {
                    widths[i] = values[i].length();
                }
            }
        }

        return widths;
    }

    /**
     * Форматирует строку с данными в виде таблицы
     *
     * @param row         массив данных строки
     * @param columnWidths ширины колонок
     * @return отформатированная строка
     */
    private String formatRow(String[] row, int[] columnWidths) {
        StringBuilder formattedRow = new StringBuilder();
        for (int i = 0; i < row.length; i++) {
            formattedRow.append(String.format("%-" + columnWidths[i] + "s", row[i])).append(" | ");
        }
        return formattedRow.toString().trim();
    }

    /**
     * Возвращает массив разделителей для таблицы
     *
     * @param columnWidths ширины колонок
     * @return массив разделителей
     */
    private String[] getSeparator(int[] columnWidths) {
        String[] separator = new String[columnWidths.length];
        for (int i = 0; i < columnWidths.length; i++) {
            separator[i] = "-".repeat(columnWidths[i]);
        }
        return separator;
    }

    /**
     * Возвращает значение поля id элемента.
     *
     * @param element элемент
     * @return значение поля id
     */
    private Object getId(T element) {
        try {
            Method getIdMethod = element.getClass().getMethod("getId");
            return getIdMethod.invoke(element);
        } catch (Exception e) {
            // В случае ошибки возвращаем неизвестный id
            return "unknown";
        }
    }
}
