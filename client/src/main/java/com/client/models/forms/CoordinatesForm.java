package com.client.models.forms;

import com.general.exceptions.IncorrectInputInScriptException;
import com.general.exceptions.InvalidFormException;
import com.general.io.Console;
import com.general.io.Interrogator;
import com.general.models.Coordinates;
import com.general.models.forms.Form;

import java.util.NoSuchElementException;

public class CoordinatesForm extends Form<Coordinates> {
    private final Console console;

    public CoordinatesForm(Console console) {
        this.console = console;
    }

    @Override
    public Coordinates build() throws IncorrectInputInScriptException, InvalidFormException {
        Long x = askX();
        Float y = askY();
        Coordinates coordinates = new Coordinates(x, y);
        if (!coordinates.validate()) throw new InvalidFormException();
        return coordinates;
    }

    /**
     * Запрашивает у пользователя координату X.
     * @return Координата X.
     * @throws IncorrectInputInScriptException Если запущен скрипт и возникает ошибка.
     */
    public Long askX() throws IncorrectInputInScriptException {
        var fileMode = Interrogator.fileMode();
        Long x;
        while (true) {
            try {
                console.println("Введите координату X:");
                console.ps2();
                var strX = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strX);

                if (strX == null || strX.isEmpty()) {
                    console.printError("Координата X не может быть пустой!");
                    continue;
                }
                x = Long.parseLong(strX);
                if (x >= Long.MAX_VALUE) {
                    console.printError("Значение координаты X должно быть меньше максимального значения Long!");
                    continue;
                }
                x = Long.parseLong(strX);
                if (x <= Long.MIN_VALUE) {
                    console.printError("Значение координаты X должно быть больше минимального значения Long!");
                    continue;
                }

                x = Long.parseLong(strX);
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Координата X не распознана!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                console.printError("Координата X должна быть представлена числом в Long!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return x;
    }

    /**
     * Запрашивает у пользователя координату Y.
     * @return Координата Y.
     * @throws IncorrectInputInScriptException Если запущен скрипт и возникает ошибка.
     */
    public Float askY() throws IncorrectInputInScriptException {
        var fileMode = Interrogator.fileMode();
        Float y;
        while (true) {
            try {
                console.println("Введите координату Y:");
                console.ps2();
                var strY = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strY);

                if (strY == null || strY.isEmpty()) {
                    console.printError("Координата Y не может быть пустой!");
                    continue;
                }

                y = Float.parseFloat(strY);
                if (y <= -519) {
                    console.printError("Значение координаты Y должно быть больше -519!");
                    continue;
                }
                y = Float.parseFloat(strY);
                if (y >= Float.MAX_VALUE) {
                    console.printError("Значение координаты Y должно быть меньше максимального значения Float!");
                    continue;
                }
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Координата Y не распознана!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                console.printError("Координата Y должна быть представлена числом!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return y;
    }
}
