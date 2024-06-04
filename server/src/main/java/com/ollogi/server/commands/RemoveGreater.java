package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.managers.CollectionManager;
import com.general.models.base.Element;
import com.general.network.Request;
import com.general.network.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * Команда 'remove_greater {element}'. Удаляет из коллекции все элементы, превышающие заданный.
 */
public class RemoveGreater<T extends Element & Comparable<T>> extends Command {
    private final CollectionManager<T> collectionManager;

    public RemoveGreater(CollectionManager<T> collectionManager) {
        super("remove_greater {element}", "удалить из коллекции все элементы, превышающие заданный");
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

            int removedFlatsCount = removeGreater(element);
            return new Response(true, "Удалено " + removedFlatsCount + " элементов, превышающих заданный.");

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        } catch (Exception e){
            return new Response(false, e.getMessage());
        }
    }

    private int removeGreater(T element) {
        int count = 0;
        List<T> flatsToRemove = new ArrayList<>();

        // Проверка на пустоту коллекции
        if (collectionManager.getCollection().isEmpty()) {
            return 0;
        }

        for (T flatElement : collectionManager.getCollection()) {
            if (flatElement.compareTo(element) > 0) {
                flatsToRemove.add(flatElement);
                count++;
            }
        }

        // Удаление элементов из коллекции
        for (T flatToRemove : flatsToRemove) {
            collectionManager.removeFromCollection(flatToRemove);
        }

        return count;
    }
}
