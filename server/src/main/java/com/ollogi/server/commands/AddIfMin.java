package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.managers.CollectionManager;
import com.general.models.base.Element;
import com.general.network.Request;
import com.general.network.Response;

import java.util.Optional;

/**
 * Команда 'add_if_min'. Добавляет новый элемент в коллекцию, если его значение меньше минимального.
 */
public class AddIfMin<T extends Element & Comparable<T>> extends Command {
    private final CollectionManager<T> collectionManager;

    public AddIfMin(CollectionManager<T> collectionManager) {
        super("add_if_min {element}", "добавить новый элемент в коллекцию, если его значение меньше минимального значения этой коллекции");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return Response с результатом выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            if (request.getData() == null) throw new WrongAmountOfElementsException();
            T element = (T) request.getData();

            Optional<T> minValue = minValue();
            if (minValue.isPresent() && element.compareTo(minValue.get()) < 0) {
                Long newId = collectionManager.addToCollection(element);
                return new Response(true, "Квартира успешно добавлена!", newId);
            } else {
                return new Response(false, "Квартира не добавлена, значение не минимальное");
            }

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        } catch (Exception unknownException) {
            return new Response(false, unknownException.getMessage());
        }
    }

    private Optional<T> minValue() {
        if (collectionManager.getCollection() == null || collectionManager.getCollection().isEmpty()) {
            return Optional.empty();
        }

        // Получаем минимальный объект T из коллекции, используя сортировку класса T
        return collectionManager.getCollection().stream()
                .min(T::compareTo);
    }
}
