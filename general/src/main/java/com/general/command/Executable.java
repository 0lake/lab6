package com.general.command;

import com.general.network.Request;
import com.general.network.Response;

/**
 * Интерфейс для всех выполняемых команд.
 */
public interface Executable {
    Response execute(Request request);
}