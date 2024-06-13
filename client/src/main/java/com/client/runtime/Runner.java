package com.client.runtime;

import com.client.network.TCPClient;
import com.client.models.forms.FlatForm;
import com.general.exceptions.*;
import com.general.io.Console;
import com.general.io.Interrogator;
import com.general.network.Request;
import com.general.network.Response;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * Класс Runner представляет собой исполняющую среду для клиентской части приложения.
 * Он обеспечивает интерактивный и скриптовый режимы работы с сервером через TCP-клиент.
 */
public class Runner {
    /**
     * Перечисление ExitCode определяет возможные коды завершения для Runner.
     */
    public enum ExitCode {
        OK,     // Успешное завершение
        ERROR,  // Ошибка
        EXIT    // Завершение работы
    }

    private final Console console;        // Консоль для взаимодействия с пользователем
    private final TCPClient tcpClient;    // TCP-клиент для обмена данными с сервером
    private final List<String> commandHistory = new ArrayList<>(); // История выполненных команд
    private final List<String> scriptStack = new ArrayList<>();   // Стек скриптов

    /**
     * Конструктор для создания объекта Runner.
     *
     * @param tcpClient TCP-клиент для обмена данными с сервером
     * @param console   Консоль для взаимодействия с пользователем
     */
    public Runner(TCPClient tcpClient, Console console) {
        this.tcpClient = tcpClient;
        this.console = console;
    }

    /**
     * Интерактивный режим работы.
     */
    public void interactiveMode() {
        var userScanner = Interrogator.getUserScanner();
        try {
            ExitCode commandStatus;
            String[] userCommand = {"", ""};

            do {
                console.ps1();
                try {
                    userCommand = (userScanner.nextLine().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();

                    commandHistory.add(userCommand[0]);
                    commandStatus = launchCommand(userCommand);
                } catch (NoSuchElementException exception) {
                    console.printError("Пользовательский ввод не обнаружен! Попытка автоматического завершения работы...");
                    commandStatus = launchCommand(new String[]{"save", ""});
                    if (commandStatus == ExitCode.OK) {
                        commandStatus = launchCommand(new String[]{"exit", ""});
                    }
                }
            } while (commandStatus != ExitCode.EXIT);

        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");
        } catch (Exception exception) {
            console.printError("Произошла ошибка: " + exception.getMessage());
        }
    }

    /**
     * Режим для запуска скрипта.
     *
     * @param argument Аргумент скрипта
     * @return Код завершения.
     */
    public ExitCode scriptMode(String argument) {
        String[] userCommand = {"", ""};
        ExitCode commandStatus;
        scriptStack.add(argument);
        if (!new File(argument).exists()) {
            argument = "../" + argument;
        }
        try (Scanner scriptScanner = new Scanner(new File(argument))) {
            if (!scriptScanner.hasNext()) throw new NoSuchElementException();
            Scanner tmpScanner = Interrogator.getUserScanner();
            Interrogator.setUserScanner(scriptScanner);
            Interrogator.setFileMode();

            do {
                do {
                    userCommand = (scriptScanner.nextLine().trim() + " ").split(" ", 2);
                    userCommand[1] = userCommand[1].trim();
                } while (scriptScanner.hasNextLine() && userCommand[0].isEmpty());
                console.println(console.getPS1() + String.join(" ", userCommand));
                if (userCommand[0].equals("execute_script")) {
                    for (String script : scriptStack) {
                        if (userCommand[1].equals(script)) throw new ScriptRecursionException();
                    }
                }
                commandStatus = launchCommand(userCommand);
            } while (commandStatus == ExitCode.OK && scriptScanner.hasNextLine());

            Interrogator.setUserScanner(tmpScanner);
            Interrogator.setUserMode();

            if (commandStatus == ExitCode.ERROR && !(userCommand[0].equals("execute_script") && !userCommand[1].isEmpty())) {
                console.println("Проверьте скрипт на корректность введенных данных!");
            }

            return commandStatus;

        } catch (FileNotFoundException exception) {
            console.printError("Файл со скриптом не найден!");
        } catch (NoSuchElementException exception) {
            console.printError("Файл со скриптом пуст!");
        } catch (ScriptRecursionException exception) {
            console.printError("Скрипты не могут вызываться рекурсивно!");
        } catch (IllegalStateException exception) {
            console.printError("Непредвиденная ошибка!");


            System.exit(0);
        } finally {
            scriptStack.remove(scriptStack.size() - 1);
        }
        return ExitCode.ERROR;
    }

    /**
     * @param userCommand Команда для запуска
     * @return Код завершения.
     */
    private ExitCode launchCommand(String[] userCommand) {
        if (userCommand[0].isEmpty()) return ExitCode.OK;
        Response response = null;
        try {
            switch (userCommand[0]) {
                case "exit" -> {
                    try {
                        tcpClient.sendRequest(new Request("exit", null));
                    } catch (Exception ignored) {
                    }
                    return ExitCode.EXIT;
                }
                case "execute_script" -> {
                    return scriptMode(userCommand[1]);
                }
                case "add", "add_if_min", "remove_greater", "remove_lower" -> {
                    try {
                        if (!userCommand[1].isEmpty())
                            throw new WrongAmountOfElementsException();
                        console.println("* Создание новой квартиры:");
                        var flat = (new FlatForm(console)).build();
                        response = tcpClient.sendCommand(new Request(userCommand[0], flat));
                    } catch (WrongAmountOfElementsException | MustBeNotEmptyException exception) {
                        console.printError("Неправильное количество аргументов!");
                        console.println("Правильное использование, используйте help для получения списка команд и их аргументов");
                    } catch (InvalidFormException exception) {
                        console.printError("Поля квартиры не валидны! Квартира не создана!");
                    } catch (IncorrectInputInScriptException ignored) {
                    }
                }
                case "update" -> {
                    try {
                        if (userCommand[1].isEmpty())
                            throw new WrongAmountOfElementsException();
                        var id = Long.parseLong(userCommand[1]);
                        console.println("* Создание новой квартиры:");
                        var flat = (new FlatForm(console)).build();
                        flat.setId(id);
                        response = tcpClient.sendCommand(new Request(userCommand[0], flat));
                    } catch (WrongAmountOfElementsException | MustBeNotEmptyException | NumberFormatException exception) {
                        console.printError("Неправильное количество аргументов!");
                        console.println("Правильное использование, используйте help для получения списка команд и их аргументов");
                    } catch (InvalidFormException exception) {
                        console.printError("Поля квартиры не валидны! Квартира не создана!");
                    } catch (IncorrectInputInScriptException ignored) {
                    }
                }
                case "remove_by_id" -> {
                    try{
                        if (userCommand[1].isEmpty())
                            throw new WrongAmountOfElementsException();
                        var id = Long.parseLong(userCommand[1]);
                        response = tcpClient.sendCommand(new Request(userCommand[0], id));
                    } catch (WrongAmountOfElementsException | NumberFormatException exception) {
                        console.printError("Неправильное количество аргументов!");
                        console.println("Правильное использование, используйте help для получения списка команд и их аргументов");
                    }
                }
                case "history" -> {
                    console.println("История команд: ");
                    for (String com : commandHistory) {
                        console.println(com);
                        ;
                    }
                }
                default -> {
                    response = tcpClient.sendCommand(new Request(userCommand[0], userCommand[1].isEmpty() ? null : userCommand));
                    if (response == null || !response.isSuccess()) return ExitCode.ERROR;
                }
            }
        } finally {
            if (!(response == null)) {
                if (response.isSuccess()) console.println(response);
                else console.printError(response);
            }
        }

        return ExitCode.OK;
    }
}