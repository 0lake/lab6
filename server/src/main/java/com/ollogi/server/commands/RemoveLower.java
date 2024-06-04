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
 * Команда 'remove_lower {element}'. Удаляет из коллекции все элементы, меньшие, чем заданный.
 */
public class RemoveLower<T extends Element & Comparable<T>> extends Command {
    private final CollectionManager<T> collectionManager;

    public RemoveLower(CollectionManager<T> collectionManager) {
        super("remove_lower {element}", "удалить из коллекции все элементы, меньшие, чем заданный");
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

            int removedFlatsCount = removeLower(element);
            return new Response(true, "Удалено " + removedFlatsCount + " элементов, меньших, чем заданный.");

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        } catch (Exception e){
            return new Response(false, e.getMessage());
        }
    }

    private int removeLower(T element) {
        int count = 0;
        var collection = collectionManager.getCollection();

        // Проверка на null и пустоту коллекции
        if (collection != null && !collection.isEmpty()) {
            List<T> flatsToRemove = new ArrayList<>();

            for (T f : collection) {
                if (f.compareTo(element) < 0) {
                    flatsToRemove.add(f);
                    count++;
                }
            }

            // Удаление элементов из коллекции
            for (T flatToRemove : flatsToRemove) {
                collectionManager.removeFromCollection(flatToRemove);
            }
        }

        return count;
    }
}
