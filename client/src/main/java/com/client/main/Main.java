package com.client.main;

import com.client.io.StandardConsole;
import com.client.network.TCPClient;
import com.client.runtime.Runner;
import com.general.io.Interrogator;
import com.general.network.Request;

import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class Main {
    public static final int PORT = 28374;
    public static void main(String[] args) {
        var console = new StandardConsole();
        Interrogator.setUserScanner(new Scanner(System.in));
        TCPClient client = new TCPClient("localhost", PORT, console);
        try {
            console.println("Попытка подключения к северу");
            client.connect();
            if(client.isConnected()) {
                console.println("Запрос от сервера списка доступных команд...");
                console.println(client.sendCommand(new Request("help", null)));
            } else{
                console.printError("Ошибка соединения с сервером.");
            }
        } catch (TimeoutException e) {
            System.out.println("Нет подключения к серверу");
        }
        new Runner(client, console).interactiveMode();
    }

}
