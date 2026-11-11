package com.lokanda.hotel.console.core;

import java.util.ArrayList;
import java.util.List;

/** Shared presentation layer for the standalone LOKANDA terminal application. */
public final class ConsoleUI {
    private static final String RESET = "\u001B[0m";
    private static final String BOLD = "\u001B[1m";
    private static final String CYAN = "\u001B[36m";
    private static final String GREEN = "\u001B[32m";
    private static final String RED = "\u001B[31m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final int WIDTH = 64;
    private static String currentTitle = "Console";
    private static final List<String[]> currentOptions = new ArrayList<>();
    private static boolean currentDivider;

    private ConsoleUI() { }

    private static String color(String value, String ansi) {
        if (Boolean.getBoolean("lokanda.console.noColor")) {
            return value;
        }
        return ansi + value + RESET;
    }

    /**
     * Clears a real terminal when one is attached. IDE Run consoles usually do not
     * expose a native console, so they receive enough blank lines to move old output
     * out of the visible area instead of printing unsupported ANSI escape codes.
     */
    public static void clearScreen() {
        if (System.console() != null && tryNativeClear()) {
            return;
        }

        // IntelliJ/Eclipse output panes are not real terminals: ANSI CLS is often
        // displayed literally or ignored. Scrolling is the most portable fallback.
        System.out.print("\n".repeat(120));
        System.out.flush();
    }

    private static boolean tryNativeClear() {
        String os = System.getProperty("os.name", "").toLowerCase();
        try {
            ProcessBuilder builder = os.contains("win")
                    ? new ProcessBuilder("cmd.exe", "/c", "cls")
                    : new ProcessBuilder("clear");
            Process process = builder.inheritIO().start();
            return process.waitFor() == 0;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static void printWelcome() {
        clearScreen();
        System.out.println(color("+" + "=".repeat(WIDTH - 2) + "+", CYAN + BOLD));
        System.out.println(color("|" + center("LOKANDA HOTEL MANAGEMENT SYSTEM", WIDTH - 2) + "|", CYAN + BOLD));
        System.out.println(color("|" + center("PROFESSIONAL CONSOLE", WIDTH - 2) + "|", CYAN));
        System.out.println(color("+" + "=".repeat(WIDTH - 2) + "+", CYAN + BOLD));
        System.out.println();
    }

    public static void header(String title) {
        currentTitle = title == null ? "Console" : title;
        currentOptions.clear();
        currentDivider = false;
        drawHeader(currentTitle);
    }

    private static void drawHeader(String title) {
        String line = "-".repeat(WIDTH - 2);
        System.out.println(color("+" + line + "+", CYAN));
        System.out.println(color("|" + center("LOKANDA  |  " + title.toUpperCase(), WIDTH - 2) + "|", CYAN + BOLD));
        System.out.println(color("+" + line + "+", CYAN));
    }

    /** Repaints the current menu after the user clears the terminal. */
    public static void redrawCurrentScreen() {
        drawHeader(currentTitle);
        for (String[] option : currentOptions) {
            printOption(option[0], option[1]);
        }
        if (currentDivider) {
            printDivider();
        }
    }

    public static void section(String title) {
        System.out.println();
        System.out.println(color("  > " + title, BLUE + BOLD));
        System.out.println(color("  " + "-".repeat(Math.min(WIDTH - 2, title.length() + 8)), BLUE));
    }

    public static void option(String key, String label) {
        currentOptions.add(new String[]{key, label});
        printOption(key, label);
    }

    private static void printOption(String key, String label) {
        System.out.println("  " + color("[" + key + "]", CYAN + BOLD) + "  " + label);
    }

    public static void success(String message) {
        System.out.println(color("  [SUCCESS] ", GREEN + BOLD) + message);
    }

    public static void error(String message) {
        System.out.println(color("  [ERROR]   ", RED + BOLD) + safeMessage(message));
    }

    public static void warning(String message) {
        System.out.println(color("  [WARNING] ", YELLOW + BOLD) + message);
    }

    public static void info(String message) {
        System.out.println(color("  [INFO]    ", CYAN + BOLD) + message);
    }

    public static void prompt(String prompt) {
        System.out.print(color("  > " + prompt + " ", CYAN + BOLD));
    }

    public static void divider() {
        currentDivider = true;
        printDivider();
    }

    private static void printDivider() {
        System.out.println(color("  " + "-".repeat(WIDTH - 2), CYAN));
    }

    public static void pauseHint() {
        System.out.println(color("\n  Press ENTER to continue  |  Type cls to clear the screen", YELLOW));
    }

    public static void printTable(List<String> headers, List<List<String>> rows) {
        if (headers == null || headers.isEmpty()) {
            return;
        }

        int columns = headers.size();
        int[] widths = new int[columns];
        for (int i = 0; i < columns; i++) {
            widths[i] = visibleLength(headers.get(i));
        }
        if (rows != null) {
            for (List<String> row : rows) {
                for (int i = 0; i < columns; i++) {
                    String cell = i < row.size() ? safeCell(row.get(i)) : "";
                    widths[i] = Math.min(34, Math.max(widths[i], visibleLength(cell)));
                }
            }
        }

        String border = border(widths);
        System.out.println(color(border, CYAN));
        printRow(headers, widths, true);
        System.out.println(color(border, CYAN));
        if (rows == null || rows.isEmpty()) {
            System.out.println("  No records found.");
        } else {
            for (List<String> row : rows) {
                printRow(row, widths, false);
            }
        }
        System.out.println(color(border, CYAN));
    }

    private static void printRow(List<String> values, int[] widths, boolean heading) {
        StringBuilder line = new StringBuilder("|");
        for (int i = 0; i < widths.length; i++) {
            String value = i < values.size() ? safeCell(values.get(i)) : "";
            int width = widths[i];
            if (value.length() > width) {
                value = value.substring(0, Math.max(0, width - 1)) + "~";
            }
            line.append(' ').append(value);
            line.append(" ".repeat(Math.max(0, width - value.length()))).append(" |");
        }
        System.out.println(heading ? color(line.toString(), BOLD + CYAN) : line);
    }

    private static String border(int[] widths) {
        StringBuilder line = new StringBuilder("+");
        for (int width : widths) {
            line.append("-".repeat(width + 2)).append('+');
        }
        return line.toString();
    }

    private static String safeCell(String value) {
        return value == null ? "-" : value.replace('\n', ' ').replace('\r', ' ');
    }

    private static int visibleLength(String value) {
        return safeCell(value).length();
    }

    private static String center(String value, int width) {
        if (value.length() >= width) {
            return value.substring(0, width);
        }
        int left = (width - value.length()) / 2;
        return " ".repeat(left) + value + " ".repeat(width - value.length() - left);
    }

    private static String safeMessage(String message) {
        return message == null || message.isBlank() ? "Unexpected error." : message;
    }
}
