package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.io.Console;
import com.general.network.Request;
import com.general.network.Response;

/**
 * Команда 'exit'. Завершает выполнение.
 */
public class Exit extends Command {
    private final Console console;

    public Exit(Console console) {
        super("exit", "завершить программу (без сохранения в файл)");
        this.console = console;
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

            console.println("Завершение выполнения...");
            // Здесь можно добавить логику завершения программы, если это необходимо
            return new Response(true, "Программа завершена");

        } catch (WrongAmountOfElementsException exception) {
            console.printError("Неправильное количество аргументов!");
            console.println("Правильное использование: '" + getName() + "'");
            return new Response(false, "Неправильное количество аргументов!");
        }
    }
}
