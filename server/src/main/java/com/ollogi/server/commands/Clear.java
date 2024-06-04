package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.managers.CollectionManager;
import com.general.network.Request;
import com.general.network.Response;

/**
 * Команда 'clear'. Очищает коллекцию.
 */
public class Clear extends Command {
    private final CollectionManager<?> collectionManager;

    public Clear(CollectionManager<?> collectionManager) {
        super("clear", "очистить коллекцию");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return Response с результатом выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            if (request.getData() != null) throw new WrongAmountOfElementsException();

            collectionManager.clearCollection();
            return new Response(true, "Коллекция очищена!");

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        }
    }
}
