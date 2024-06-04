package com.general.models.forms;


import com.general.exceptions.IncorrectInputInScriptException;
import com.general.exceptions.InvalidFormException;
import com.general.exceptions.MustBeNotEmptyException;

/**
 * Абстрактный класс формы для ввода пользовательских данных.
 * @param <T> создаваемый объект
 */

public abstract class Form<T> {
    public abstract T build() throws IncorrectInputInScriptException, InvalidFormException, MustBeNotEmptyException;
}