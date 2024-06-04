package com.general.managers;

import com.general.command.Command;
import com.general.network.Request;
import com.general.network.Response;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Управляет командами.инвоке
 */
@Getter
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final List<String> commandHistory = new ArrayList<>();

    /**
     * Добавляет команду
     */

    public void register(String commandName, Command command) {
        commands.put(commandName, command);
    }


    /**
     * Добавляет команду в историю.
     *
     * @param command Команда.
     */
    public void addToHistory(String command) {
        commandHistory.add(command);
    }

    public void getCommand(String command) {
        commands.get(command);
    }

    public Response handle(Request request) {
        if (commands.get(request.getCommand()) == null) {
            return new Response(false, "Команда не найдена, введите help для справки");
        } else {
            return commands.get(request.getCommand()).execute(request);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Command List:\n");
        commands.values().forEach(command -> {
            sb.append(String.format(" %-35s%-1s%n", command.getName(), command.getDescription()));
        });
        return sb.toString();
    }
}
