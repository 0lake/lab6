package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.managers.CommandManager;
import com.general.network.Request;
import com.general.network.Response;

/**
 * Команда 'help'. Выводит справку по доступным командам.
 */
public class Help extends Command {
    private final CommandManager commandManager;

    public Help(CommandManager commandManager) {
        super("help", "вывести справку по доступным командам");
        this.commandManager = commandManager;
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

        return new Response(true, commandManager.toString());
    }
}
