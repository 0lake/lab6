package com.client.models.forms;

import com.general.exceptions.IncorrectInputInScriptException;
import com.general.io.Console;
import com.general.io.Interrogator;
import com.general.models.View;
import com.general.models.forms.Form;

import java.util.NoSuchElementException;

/**
 * Форма вида.
 */
public class ViewForm extends Form<View> {
    private final Console console;

    public ViewForm(Console console) {
        this.console = console;
    }

    @Override
    public View build() throws IncorrectInputInScriptException {
        var fileMode = Interrogator.fileMode();

        String strView;
        View view;
        while (true) {
            try {
                console.println("Список видов - " + View.names());
                console.println("Введите вид:");
                console.ps2();

                strView = Interrogator.getUserScanner().nextLine().trim();
                if (fileMode) console.println(strView);

                view = View.valueOf(strView.toUpperCase());
                break;
            } catch (NoSuchElementException exception) {
                console.printError("Вид не распознан!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalArgumentException exception) {
                console.printError("Такого вида нет в списке!");
                if (fileMode) throw new IncorrectInputInScriptException();
            } catch (IllegalStateException exception) {
                console.printError("Непредвиденная ошибка!");
                System.exit(0);
            }
        }
        return view;
    }
}
