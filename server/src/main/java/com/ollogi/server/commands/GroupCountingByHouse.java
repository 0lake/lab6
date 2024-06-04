package com.ollogi.server.commands;

import com.general.command.Command;
import com.general.managers.CollectionManager;
import com.general.models.Flat;
import com.general.network.Request;
import com.general.network.Response;

import java.util.HashMap;
import java.util.Map;

/**
 * Команда 'group_counting_by_house'. Группирует элементы коллекции по значению поля house и выводит количество элементов в каждой группе.
 */
public class GroupCountingByHouse extends Command {
    private final CollectionManager<Flat> collectionManager;

    public GroupCountingByHouse(CollectionManager<Flat> collectionManager) {
        super("group_counting_by_house", "сгруппировать элементы коллекции по значению поля house, вывести количество элементов в каждой группе");
        this.collectionManager = collectionManager;
    }

    /**
     * Выполняет команду
     *
     * @return Response с результатом выполнения команды.
     */
    @Override
    public Response execute(Request request) {
        Map<String, Long> houseCountMap = groupByHouse();

        if (houseCountMap.isEmpty()) {
            return new Response(false, "Коллекция пуста.");
        } else {
            return new Response(true, "Группировка выполнена успешно.", houseCountMap);
        }
    }

    private Map<String, Long> groupByHouse() {
        Map<String, Long> houseCountMap = new HashMap<>();
        collectionManager.getCollection().stream()
                .map(Flat::getHouse)
                .filter(house -> house != null)
                .forEach(house -> houseCountMap.put(house.getName(), houseCountMap.getOrDefault(house.getName(), 0L) + 1));
        return houseCountMap;
    }
}
