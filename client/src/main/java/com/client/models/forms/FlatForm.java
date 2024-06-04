package com.client.models.forms;


import com.general.exceptions.IncorrectInputInScriptException;
import com.general.exceptions.InvalidFormException;
import com.general.exceptions.MustBeNotEmptyException;
import com.general.exceptions.NotInDeclaredLimitsException;
import com.general.io.Console;
import com.general.io.Interrogator;
import com.general.models.Coordinates;
import com.general.models.Flat;
import com.general.models.House;
import com.general.models.View;
import com.general.models.forms.Form;

import java.util.NoSuchElementException;

/**
 * Форма квартиры.
 */

public class FlatForm extends Form<Flat> {
    private final Console console;

    public FlatForm(Console console) {
        this.console = console;
    }

    @Override
    public Flat build() throws IncorrectInputInScriptException, InvalidFormException, MustBeNotEmptyException {
        var flat = new Flat(
                -1,
                askName(),
                askCoordinates(),
                askArea(),
                askNumberOfRooms(),
                askHeight(),
                askKitchenArea(),
                askView(),
                askHouse()
        );
        if (!flat.validate()) throw new InvalidFormException();
        return flat;
    }

    private String askName() throws IncorrectInputInScriptException {
        String name;
        while (true) {
            try {
                console.println("Введите название квартиры:");
                console.ps2();

                name = Interrogator.getUserScanner().nextLine().trim();
                if (name.equals("")) throw new MustBeNotEmptyException();
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Название не распознано!");
                throw new IncorrectInputInScriptException();
            } catch (MustBeNotEmptyException exception) {
                console.printError("Название не может быть пустым!");
                // Предлагаем пользователю ввести название квартиры заново
                continue;
            } catch (IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return name;
    }


    private Coordinates askCoordinates() throws IncorrectInputInScriptException, InvalidFormException {
        return new CoordinatesForm(console).build();
    }

    private int askArea() throws IncorrectInputInScriptException {
        int area;
        final int MIN_AREA = 1; // Минимальная площадь

        while (true) {
            try {
                console.println("Введите общую площадь квартиры:");
                console.ps2();

                String input = Interrogator.getUserScanner().nextLine().trim();
                if (input.isEmpty()) {
                    throw new NotInDeclaredLimitsException(); // Если ввод пустой, выбрасываем исключение
                }
                area = Integer.parseInt(input);
                if (area <= 0) throw new NotInDeclaredLimitsException();
                if (area >= Integer.MAX_VALUE) {
                    console.printError("Площадь должна быть меньше максимального значения Integer!");
                    continue;
                }
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Площадь квартиры не распознана! Пожалуйста, введите число.");
            } catch (NotInDeclaredLimitsException exception) {
                console.printError("Площадь квартиры должна быть больше 0! Пожалуйста, введите корректное значение.");
            } catch (NumberFormatException exception) {
                console.printError("Площадь квартиры должна быть представлена числом в Integer! Пожалуйста, введите число в Integer.");
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return area;
    }


    private int askHeight() throws IncorrectInputInScriptException {
        int height;
        final int MIN_HEIGHT = 1; // Минимальная высота

        while (true) {
            try {
                console.println("Введите высоту:");
                console.ps2();

                String input = Interrogator.getUserScanner().nextLine().trim();
                if (input.isEmpty()) {
                    throw new NotInDeclaredLimitsException(); // Если ввод пустой, выбрасываем исключение
                }
                height = Integer.parseInt(input);
                if (height <= 0) throw new NotInDeclaredLimitsException();
                if (height >= 2147483647) {
                    console.printError("Высота должна быть меньше максимального значения int!");
                    continue;
                }
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Высота не распознана! Пожалуйста, введите число.");
            } catch (NotInDeclaredLimitsException exception) {
                console.printError("Высота должна быть больше 0! Пожалуйста, введите корректное значение.");
            } catch (NumberFormatException exception) {
                console.printError("Высота должна быть представлена числом в int! Пожалуйста, введите число в int.");
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return height;
    }


    private int askNumberOfRooms() throws IncorrectInputInScriptException {
        int numberOfRooms;
        while (true) {
            try {
                console.println("Введите количество комнат:");
                console.ps2();

                String input = Interrogator.getUserScanner().nextLine().trim();
                if (input.isEmpty()) {
                    throw new NotInDeclaredLimitsException(); // Если ввод пустой, выбрасываем исключение
                }
                numberOfRooms = Integer.parseInt(input);
                if (numberOfRooms < 1) throw new NotInDeclaredLimitsException();
                if (numberOfRooms >= 2147483647) {
                    console.printError("Количество комнат должно быть меньше максимального значения int!");
                    continue;
                }
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Количество комнат не распознано! Пожалуйста, введите число.");
            } catch (NotInDeclaredLimitsException exception) {
                console.printError("Количество комнат должно быть больше нуля! Пожалуйста, введите корректное значение.");
            } catch (NumberFormatException exception) {
                console.printError("Количество комнат должно быть представлено числом в int! Пожалуйста, введите число в int.");
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return numberOfRooms;
    }



    private Float askKitchenArea() throws IncorrectInputInScriptException {
        while (true) {
            try {
                console.println("Введите площадь кухни:");
                console.ps2();

                String input = Interrogator.getUserScanner().nextLine().trim();

                if (input.isEmpty()) {
                    console.printError("Площадь кухни не может быть пустой! Пожалуйста, введите корректное значение.");
                } else {
                    Float kitchenArea = Float.parseFloat(input);

                    if (kitchenArea >= Float.MAX_VALUE) {
                        console.printError("Площадь кухни должна быть меньше максимального значения Float!");
                        continue;
                    }
                    if (kitchenArea <= Float.MIN_VALUE) {
                        console.printError("Площадь кухни должна быть больше минимального значения Float!");
                        continue;
                    }
                    if (kitchenArea <= 0) {
                        console.printError("Площадь кухни должна быть больше 0!");
                    } else {
                        return kitchenArea; // Если ввод корректен, возвращаем площадь кухни
                    }
                }
            } catch (NumberFormatException exception) {
                console.printError("Площадь кухни должна быть представлена числом! Пожалуйста, введите корректное значение.");
            } catch (NullPointerException | IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
    }





    private View askView() throws IncorrectInputInScriptException, InvalidFormException {
        return new ViewForm(console).build();
    }

    private House askHouse() throws IncorrectInputInScriptException, InvalidFormException, MustBeNotEmptyException {
        return new HouseForm(console).build();
    }


}

