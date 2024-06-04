package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.managers.CollectionManager;
import com.general.network.Request;
import com.general.network.Response;

import java.time.LocalDateTime;

/**
 * Команда 'info'. Выводит информацию о коллекции.
 */
public class Info extends Command {
    private final CollectionManager<?> collectionManager;

    public Info(CollectionManager<?> collectionManager) {
        super("info", "вывести информацию о коллекции");
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

        LocalDateTime lastInitTime = collectionManager.getLastInitTime();
        String lastInitTimeString = (lastInitTime == null) ? "в данной сессии инициализации еще не происходило" :
                lastInitTime.toLocalDate().toString() + " " + lastInitTime.toLocalTime().toString();

        LocalDateTime lastSaveTime = collectionManager.getLastSaveTime();
        String lastSaveTimeString = (lastSaveTime == null) ? "в данной сессии сохранения еще не происходило" :
                lastSaveTime.toLocalDate().toString() + " " + lastSaveTime.toLocalTime().toString();

        String infoMessage = String.format(
                "Сведения о коллекции:\n" +
                        " Тип: %s\n" +
                        " Количество элементов: %d\n" +
                        " Дата последнего сохранения: %s\n" +
                        " Дата последней инициализации: %s",
                collectionManager.collectionType(),
                collectionManager.collectionSize(),
                lastSaveTimeString,
                lastInitTimeString
        );

        return new Response(true, infoMessage);
    }
}
