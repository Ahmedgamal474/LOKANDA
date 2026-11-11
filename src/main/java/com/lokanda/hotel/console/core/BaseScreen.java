package com.lokanda.hotel.console.core;

public abstract class BaseScreen {
    protected final ConsoleInput input;

    protected BaseScreen(ConsoleInput input) {
        this.input = input;
    }

    @FunctionalInterface
    protected interface ScreenAction {
        void run() throws Exception;
    }

    protected void execute(ScreenAction action) {
        try {
            action.run();
        } catch (Exception exception) {
            ConsoleUI.error(exception.getMessage());
        }
        input.pause();
    }

    protected int readId(String prompt) {
        return input.readInt(prompt, 1, Integer.MAX_VALUE);
    }
}
