package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.managers.CollectionManager;
import com.general.models.base.Element;
import com.general.network.Request;
import com.general.network.Response;

/**
 * Команда 'add'. Добавляет новый элемент в коллекцию.
 */

public class Add<T extends Element & Comparable<T>> extends Command {
    private final CollectionManager<T> collectionManager;

    public Add(CollectionManager<T> collectionManager) {
        super("add {element}", "добавить новый элемент в коллекцию");
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
            Long newId = collectionManager.addToCollection(element);
            return new Response(true, "Элемент успешно добавлен!", newId);

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов!");
        } catch (Exception unknownException) {
            return new Response(false, unknownException.getMessage());
        }
    }
}
