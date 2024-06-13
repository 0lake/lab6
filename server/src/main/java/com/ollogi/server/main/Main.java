package com.ollogi.server.main;

import com.general.command.Command;
import com.general.io.Interrogator;
import com.general.managers.CollectionManager;
import com.general.managers.CommandManager;
import com.general.models.Flat;
import com.general.network.Request;
import com.general.network.Response;
import com.ollogi.server.commands.*;
import com.ollogi.server.managers.FlatCollectionManager;
import com.ollogi.server.managers.UseManager;
import com.ollogi.server.network.Handler;
import com.ollogi.server.network.TCPServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;

public class Main {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
    private static final int PORT = 28374;

    public static void main(String[] args) {
        Interrogator.setUserScanner(new Scanner(System.in));
        CollectionManager<Flat> collectionManager = null;

        if (args.length == 0) {
            logger.info("Введите имя загружаемого файла как аргумент командной строки");
            collectionManager = waitForFileName();
        } else {
            collectionManager = initializeCollectionManager(args[0]);
        }

        startConsoleListener(collectionManager);

        if (collectionManager != null) {
            addShutdownHook(collectionManager);
            CommandManager commandManager = initializeCommandManager(collectionManager);
            startServer(commandManager);
        }
    }

    private static CollectionManager<Flat> waitForFileName() {
        Scanner scanner = Interrogator.getUserScanner();
        while (true) {
            logger.info("Введите имя файла: ");
            String fileName = scanner.nextLine().trim();
            if (!fileName.isEmpty()) {
                if (!Files.isReadable(Paths.get(fileName))) {
                    if (!Files.isReadable(Paths.get("../" + fileName))) {
                        logger.error("Отсутствует файл или права на чтение файла. Пожалуйста, убедитесь, что у вас есть права на доступ к файлу, а также проверьте его наличие.");
                        continue;
                    } else {
                        fileName = "../" + fileName;
                    }
                }
                return initializeCollectionManager(fileName);
            } else {
                logger.error("Имя файла не может быть пустым. Пожалуйста, введите имя файла.");
            }
        }
    }

    private static CollectionManager<Flat> initializeCollectionManager(String fileName) {
        try {
            if (!Files.isReadable(Paths.get(fileName))) {
                if (!Files.isReadable(Paths.get("../" + fileName))) {
                    logger.error("Отсутствуют права на чтение файла. Пожалуйста, убедитесь, что у вас есть права на доступ к файлу.");
                    return null;
                } else {
                    fileName = "../" + fileName;
                }
            }
            UseManager useManager = new UseManager(fileName);
            FlatCollectionManager collectionManager = new FlatCollectionManager(useManager);
            collectionManager.validateAll();
            return collectionManager;
        } catch (Exception e) {
            logger.error("Ошибка инициализации CollectionManager: " + e.getMessage());
            return null;
        }
    }

    private static void addShutdownHook(CollectionManager<Flat> collectionManager) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Сохранение коллекции перед завершением работы...");
            collectionManager.saveCollection();
        }));
    }

    private static CommandManager initializeCommandManager(CollectionManager<Flat> collectionManager) {
        CommandManager commandManager = new CommandManager();
        initCommands(collectionManager, commandManager);
        return commandManager;
    }

    private static void startServer(CommandManager commandManager) {
        Handler.setCommandManager(commandManager);
        new TCPServer(PORT).start();
    }

    public static void initCommands(CollectionManager<Flat> collectionManager, CommandManager commandManager) {
        commandManager.register("help", new Help(commandManager));
        commandManager.register("info", new Info(collectionManager));
        commandManager.register("show", new Show<>(collectionManager));
        commandManager.register("add", new Add<>(collectionManager));
        commandManager.register("update", new Update<>(collectionManager));
        commandManager.register("remove_by_id", new RemoveById<>(collectionManager));
        commandManager.register("clear", new Clear(collectionManager));
        commandManager.register("remove_greater", new RemoveGreater<>(collectionManager));
        commandManager.register("remove_lower", new RemoveLower<>(collectionManager));
        commandManager.register("add_if_min", new AddIfMin<>(collectionManager));
        commandManager.register("sum_of_height", new SumOfHeight(collectionManager));
        commandManager.register("group_counting_by_house", new GroupCountingByHouse(collectionManager));
        commandManager.register("filter_starts_with_name", new FilterStartsWithName(collectionManager));
        Command executeScriptCommand = new Command("execute_script", "исполнить скрипт из указанного файла") {

            public Request execute(String[] arguments) {
                return null; // Stub implementation
            }

            @Override
            public Response execute(Request request) {
                return null; // Stub implementation
            }
        };
        commandManager.register("execute_script", executeScriptCommand);
    }



    private static void startConsoleListener(CollectionManager<Flat> collectionManager) {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine().trim();
                if ("exit".equalsIgnoreCase(input)) {
                    logger.info("Завершение работы программы...");
                    collectionManager.saveCollection();
                    System.exit(0);
                } else if ("save".equalsIgnoreCase(input)) {
                    logger.info("Сохранение коллекции...");
                    collectionManager.saveCollection();
                } else {
                    logger.warn("Неизвестная команда: " + input);
                }
            }
        }).start();
    }
}
