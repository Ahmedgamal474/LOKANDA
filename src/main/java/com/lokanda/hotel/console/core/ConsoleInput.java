package com.lokanda.hotel.console.core;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import java.util.function.Function;

/** Reads terminal input and repeats a prompt until its parser accepts the value. */
public final class ConsoleInput {
    private final Scanner scanner;

    public ConsoleInput(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null.");
        }
        this.scanner = scanner;
    }

    public String readLine(String prompt) {
        while (true) {
            ConsoleUI.prompt(prompt);
            if (!scanner.hasNextLine()) {
                throw new IllegalStateException("Input stream was closed.");
            }
            String value = scanner.nextLine().trim();
            if (isClearCommand(value)) {
                ConsoleUI.clearScreen();
                ConsoleUI.redrawCurrentScreen();
                ConsoleUI.info("Screen cleared. Continue with the same prompt.");
                continue;
            }
            if (value.equalsIgnoreCase("help")) {
                ConsoleUI.info("Commands: cls / clear = clear screen, help = show commands.");
                continue;
            }
            return value;
        }
    }

    public String readRequired(String prompt) {
        return readValidated(prompt, value -> {
            if (value == null || value.isBlank()) {
                throw new IllegalArgumentException("This field cannot be empty.");
            }
            return value;
        });
    }

    public <T> T readValidated(String prompt, Function<String, T> parser) {
        if (parser == null) {
            throw new IllegalArgumentException("Input parser cannot be null.");
        }
        while (true) {
            String value = readLine(prompt);
            try {
                return parser.apply(value);
            } catch (RuntimeException exception) {
                ConsoleUI.error(exception.getMessage());
            }
        }
    }

    public int readInt(String prompt, int minimum, int maximum) {
        if (minimum > maximum) {
            throw new IllegalArgumentException("Minimum cannot exceed maximum.");
        }
        return readValidated(prompt, value -> {
            int number;
            try {
                number = Integer.parseInt(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Please enter a whole number.");
            }
            if (number < minimum || number > maximum) {
                throw new IllegalArgumentException(
                        "Enter a value between " + minimum + " and " + maximum + ".");
            }
            return number;
        });
    }

    public BigDecimal readDecimal(String prompt) {
        return readValidated(prompt, value -> {
            try {
                return new BigDecimal(value);
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Please enter a valid numeric amount.");
            }
        });
    }

    public double readDouble(String prompt) {
        return readValidated(prompt, value -> {
            try {
                double number = Double.parseDouble(value);
                if (!Double.isFinite(number)) {
                    throw new IllegalArgumentException("Please enter a finite number.");
                }
                return number;
            } catch (NumberFormatException exception) {
                throw new IllegalArgumentException("Please enter a valid number.");
            }
        });
    }

    public LocalDate readDate(String prompt) {
        return readValidated(prompt, value -> {
            try {
                return LocalDate.parse(value);
            } catch (DateTimeParseException exception) {
                throw new IllegalArgumentException("Use date format YYYY-MM-DD.");
            }
        });
    }

    public boolean readYesNo(String prompt) {
        return readValidated(prompt + " (y/n)", value -> {
            if (value.equalsIgnoreCase("y") || value.equalsIgnoreCase("yes")) {
                return true;
            }
            if (value.equalsIgnoreCase("n") || value.equalsIgnoreCase("no")) {
                return false;
            }
            throw new IllegalArgumentException("Enter y or n.");
        });
    }

    public <E extends Enum<E>> E readEnum(String prompt, Class<E> enumType) {
        return readValidated(prompt, value -> {
            String normalized = value.trim().toUpperCase().replace(' ', '_').replace('-', '_');
            try {
                return Enum.valueOf(enumType, normalized);
            } catch (IllegalArgumentException exception) {
                throw new IllegalArgumentException(
                        "Valid values: " + String.join(", ", enumType.getEnumConstants().length == 0
                                ? new String[0] : enumNames(enumType)));
            }
        });
    }

    private static <E extends Enum<E>> String[] enumNames(Class<E> enumType) {
        E[] values = enumType.getEnumConstants();
        String[] names = new String[values.length];
        for (int i = 0; i < values.length; i++) {
            names[i] = values[i].name();
        }
        return names;
    }

    public void pause() {
        ConsoleUI.pauseHint();
        readLine("> Press ENTER");
    }

    private boolean isClearCommand(String value) {
        return value.equalsIgnoreCase("cls") || value.equalsIgnoreCase("clear");
    }
}
