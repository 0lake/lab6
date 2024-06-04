package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.CollectionIsEmptyException;
import com.general.exceptions.NotFoundException;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.managers.CollectionManager;
import com.general.models.base.Element;
import com.general.network.Request;
import com.general.network.Response;

/**
 * Команда 'remove_by_id'. Удаляет элемент из коллекции.
 */
public class RemoveById<T extends Element & Comparable<T>> extends Command {
    private final CollectionManager<T> collectionManager;

    public RemoveById(CollectionManager<T> collectionManager) {
        super("remove_by_id <ID>", "удалить элемент из коллекции по ID");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     * @return Response с результатом выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        try {
            // Проверяем наличие данных в запросе и их тип
            if (request.getData() == null || !(request.getData() instanceof Long id)) {
                throw new WrongAmountOfElementsException();
            }

            // Проверяем, пуста ли коллекция
            if (collectionManager.collectionSize() == 0) {
                throw new CollectionIsEmptyException();
            }

            // Ищем элемент по ID
            T elementToRemove = collectionManager.getById(id);
            if (elementToRemove == null) {
                throw new NotFoundException();
            }

            // Удаляем элемент из коллекции
            collectionManager.removeFromCollection(elementToRemove);
            return new Response(true, "Элемент успешно удален.");

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        } catch (CollectionIsEmptyException exception) {
            return new Response(false, "Коллекция пуста!");
        } catch (NumberFormatException exception) {
            return new Response(false, "ID должен быть представлен числом!");
        } catch (NotFoundException exception) {
            return new Response(false, "Элемента с таким ID в коллекции нет!");
        } catch (Exception e) {
            return new Response(false, "Произошла непредвиденная ошибка: " + e.getMessage());
        }
    }
}
