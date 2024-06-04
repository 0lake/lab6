package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.io.Console;
import com.general.managers.CollectionManager;
import com.general.models.Flat;
import com.general.network.Request;
import com.general.network.Response;

import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Команда 'filter_starts_with_name'. Выводит элементы, значение поля name которых начинается с заданной подстроки.
 */
public class FilterStartsWithName extends Command {
    private final CollectionManager<Flat> collectionManager;

    public FilterStartsWithName(CollectionManager<Flat> collectionManager) {
        super("filter_starts_with_name name", "вывести элементы, значение поля name которых начинается с заданной подстроки");
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
            if (request.getData() == null || !(request.getData() instanceof String[] args)) {
                throw new WrongAmountOfElementsException();
            }

            String nameSubstring = args[1];
            if (nameSubstring.isEmpty()) {
                throw new WrongAmountOfElementsException();
            }

            List<Flat> flats = filterByNameStart(nameSubstring);
            StringBuilder result = new StringBuilder();

            for (Flat element : flats) {
                result.append(element.toString()).append("\n");
            }

            if (flats.isEmpty()) {
                return new Response(false, "Элементов, чьи имена начинаются с '" + nameSubstring + "' не обнаружено.");
            } else {
                return new Response(true,
                        "Элементов, чьи имена начинаются с '" + nameSubstring + "' обнаружено " + flats.size() + " шт.",
                        result.toString().trim());
            }
        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        }
    }

    private List<Flat> filterByNameStart(String nameSubstring) {
        return collectionManager.getCollection().stream()
                .filter(flat -> flat.getName() != null && flat.getName().startsWith(nameSubstring))
                .collect(Collectors.toList());
    }
}
