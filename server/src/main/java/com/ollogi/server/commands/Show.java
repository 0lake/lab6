package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.managers.CollectionManager;
import com.general.models.base.Element;
import com.general.network.Request;
import com.general.network.Response;

import java.util.Iterator;

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
            Iterator<T> iterator = collectionManager.getCollection().iterator();
            while (iterator.hasNext()) {
                T element = iterator.next();
                result.append(element.toString()).append("\n");
            }

            return new Response(true, result.toString().trim());

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        }
    }
}
