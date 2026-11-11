package com.lokanda.hotel.console;

import com.lokanda.hotel.console.core.ConsoleInput;
import com.lokanda.hotel.console.core.ConsoleUI;
import com.lokanda.hotel.console.screens.ConsoleLoginScreen;

import java.util.Scanner;

/** Run this class to start the separate LOKANDA console without changing Main.java. */
public final class ConsoleMain {
    private ConsoleMain() { }

    public static void main(String[] args) {
        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleUI.printWelcome();
            // Use a console-native login screen so cls/clear works during login too.
            // The original LoginService and all other existing project classes stay untouched.
            if (!ConsoleLoginScreen.login(new ConsoleInput(scanner))) {
                return;
            }
            new LokandaConsole(scanner).run();
        } catch (IllegalStateException exception) {
            ConsoleUI.error(exception.getMessage());
        } catch (Exception exception) {
            ConsoleUI.error("Console stopped: " + exception.getMessage());
        }
    }
}
