package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.exceptions.CollectionIsEmptyException;
import com.general.exceptions.WrongAmountOfElementsException;
import com.general.managers.CollectionManager;
import com.general.models.Flat;
import com.general.network.Request;
import com.general.network.Response;

/**
 * Команда 'sum_of_height'. Сумма значений поля height для всех квартир.
 */
public class SumOfHeight extends Command {
    private final CollectionManager<Flat> collectionManager;

    public SumOfHeight(CollectionManager<Flat> collectionManager) {
        super("sum_of_height", "вывести сумму значений поля height для всех элементов коллекции");
        this.collectionManager = collectionManager;
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

            int sumOfHeight = getSumOfHeight();
            if (sumOfHeight == 0) {
                throw new CollectionIsEmptyException();
            }

            String resultMessage = "Сумма значений поля height для всех квартир: " + sumOfHeight;
            return new Response(true, resultMessage);

        } catch (WrongAmountOfElementsException exception) {
            return new Response(false, "Неправильное количество аргументов! Правильное использование: '" + getName() + "'");
        } catch (CollectionIsEmptyException exception) {
            return new Response(false, "Коллекция пуста!");
        }
    }

    private int getSumOfHeight() {
        return collectionManager.getCollection().stream()
                .mapToInt(Flat::getHeight)
                .sum();
    }
}
