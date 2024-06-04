package com.client.models.forms;


import com.general.exceptions.IncorrectInputInScriptException;
import com.general.exceptions.InvalidFormException;
import com.general.exceptions.MustBeNotEmptyException;
import com.general.exceptions.NotInDeclaredLimitsException;
import com.general.io.Console;
import com.general.io.Interrogator;
import com.general.models.House;
import com.general.models.forms.Form;

import java.util.NoSuchElementException;
/**
 * Форма дома.
 */

public class HouseForm extends Form<House> {
    private final Console console;

    public HouseForm(Console console) {
        this.console = console;
    }

    @Override
    public House build() throws IncorrectInputInScriptException, InvalidFormException, MustBeNotEmptyException {
        console.println("Введите название, что приведет к созданию нового дома.");
        console.ps2();

        var fileMode = Interrogator.fileMode();
        String input = Interrogator.getUserScanner().nextLine().trim();
        if (fileMode) console.println(input);

        while (input.equals("null") || input.isEmpty()) { // Пока введенное название равно null или пустой строке, запрашиваем правильное значение
            console.printError("Название не может быть пустым! Пожалуйста, введите корректное название:");
            input = Interrogator.getUserScanner().nextLine().trim();
            if (fileMode) console.println(input);
        }

        console.println("! Создание нового дома:");
        try {
            var house = new House(
                    input,
                    askYear(),
                    (long) askNumberOfFlatsOnFloor(),
                    (long) askNumberOfLifts()
            );
            if (!house.validate()) throw new InvalidFormException();
            return house;
        } catch (InvalidFormException | MustBeNotEmptyException exception) {
            console.printError("Неверные данные. Пожалуйста, введите корректные данные.");
            throw exception;
        }
    }

    private int askYear() throws IncorrectInputInScriptException, MustBeNotEmptyException {
        var fileMode = Interrogator.fileMode();
        int year = 0;
        while (true) {
            try {
                console.println("Введите год постройки дома:");
                console.ps2();

                String strYear = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strYear);

                if (strYear.isEmpty()) {
                    throw new MustBeNotEmptyException();
                }

                year = Integer.parseInt(strYear);
                if (year <= 0) throw new NotInDeclaredLimitsException();
                if (year >= Integer.MAX_VALUE) {
                    console.printError("Год должен быть меньше максимального значения Integer!");
                    continue;
                }

                break;
            } catch (NoSuchElementException exception) {
                console.printError("Год постройки дома не распознан!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NotInDeclaredLimitsException exception) {
                console.printError("Год постройки дома должен быть больше 0!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                console.printError("Год постройки дома должен быть представлен числом в Integer!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (MustBeNotEmptyException exception) {
                console.printError("Год постройки дома не может быть пустым!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return year;
    }


    private Long askNumberOfFlatsOnFloor() throws IncorrectInputInScriptException, MustBeNotEmptyException {
        var fileMode = Interrogator.fileMode();
        Long numberOfFlatsOnFloor = 0L;
        while (true) {
            try {
                console.println("Введите количество квартир на этаже:");
                console.ps2();

                String strNumberOfFlatsOnFloor = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strNumberOfFlatsOnFloor);

                if (strNumberOfFlatsOnFloor.isEmpty()) {
                    throw new MustBeNotEmptyException();
                }

                numberOfFlatsOnFloor = Long.parseLong(strNumberOfFlatsOnFloor);
                if (numberOfFlatsOnFloor <= 0) throw new NotInDeclaredLimitsException();
                if (numberOfFlatsOnFloor >= Long.MAX_VALUE) {
                    console.printError("Количество комант быть меньше максимального значения Long!");
                    continue;
                }

                break;
            } catch (NoSuchElementException exception) {
                console.printError("Количество квартир на этаже не распознано!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NotInDeclaredLimitsException exception) {
                console.printError("Количество квартир на этаже должно быть больше 0!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                console.printError("Количество квартир на этаже должно быть представлено числом в Long!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (MustBeNotEmptyException exception) {
                console.printError("Количество квартир на этаже не может быть пустым!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return numberOfFlatsOnFloor;
    }

    private long askNumberOfLifts() throws IncorrectInputInScriptException, MustBeNotEmptyException {
        var fileMode = Interrogator.fileMode();
        long numberOfLifts = 0; // тип long
        while (true) {
            try {
                console.println("Введите количество лифтов:");
                console.ps2();

                String strNumberOfLifts = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strNumberOfLifts);

                if (strNumberOfLifts.isEmpty()) {
                    throw new MustBeNotEmptyException();
                }

                numberOfLifts = Long.parseLong(strNumberOfLifts); //Long.parseLong
                if (numberOfLifts <= 0) throw new NotInDeclaredLimitsException();
                if (numberOfLifts >= Long.MAX_VALUE) {
                    console.printError("Количество лифтов быть меньше максимального значения long!");
                    continue;
                }

                break;
            } catch (NoSuchElementException exception) {
                console.printError("Количество лифтов не распознано!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NotInDeclaredLimitsException exception) {
                console.printError("Количество лифтов должно быть больше 0!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NumberFormatException exception) {
                console.printError("Количество лифтов должно быть представлено числом в long!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (MustBeNotEmptyException exception) {
                console.printError("Количество лифтов не может быть пустым!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return numberOfLifts;
    }
}