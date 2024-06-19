package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.managers.CollectionManager;
import com.general.network.Request;
import com.general.network.Response;

/**
 * Команда 'save'. Сохраняет коллекцию в файл.
 */
public class Save extends Command {
    private final CollectionManager<?> collectionManager;

    public Save(CollectionManager<?> collectionManager) {
        super("save", "сохранить коллекцию в файл");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return Response с результатом выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        if (request.getData() != null) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        }

        collectionManager.sortCollection();

        collectionManager.saveCollection();
        return new Response(true, "Коллекция успешно сохранена.");
    }
}
