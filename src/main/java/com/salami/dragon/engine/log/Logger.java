package com.salami.dragon.engine.log;

public class Logger {
    private static final String ANSI_RESET = "\u001B[0m";
    private static final String ANSI_BLACK = "\u001B[30m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_GREEN = "\u001B[32m";
    private static final String ANSI_YELLOW = "\u001B[33m";
    private static final String ANSI_BLUE = "\u001B[34m";
    private static final String ANSI_PURPLE = "\u001B[35m";
    private static final String ANSI_CYAN = "\u001B[36m";
    private static final String ANSI_WHITE = "\u001B[37m";

    public static final int BLACK = 0;
    public static final int RED = 1;
    public static final int GREEN = 2;
    public static final int YELLOW = 3;
    public static final int BLUE = 4;
    public static final int PURPLE = 5;
    public static final int CYAN = 6;
    public static final int WHITE = 7;

    private static String print(int colour, String message) {
        switch(colour) {
            case BLACK:
                return ANSI_BLACK + message + ANSI_RESET;
            case RED:
                return ANSI_RED + message + ANSI_RESET;
            case GREEN:
                return ANSI_GREEN + message + ANSI_RESET;
            case YELLOW:
                return ANSI_YELLOW + message + ANSI_RESET;
            case BLUE:
                return ANSI_BLUE + message + ANSI_RESET;
            case PURPLE:
                return ANSI_PURPLE + message + ANSI_RESET;
            case CYAN:
                return ANSI_CYAN + message + ANSI_RESET;
            case WHITE:
                return ANSI_WHITE + message + ANSI_RESET;
            default:
                return message;
        }
    }

    ///
    /// PUBLIC STATIC FUNCTIONS, YEAH, THE ONES ACTUALLY USED TO LOG SHIT
    ///

    public static void log_info(String message) {
        System.out.println(
            print(WHITE, message)
        );
    }

    public static void log_warning(String message) {
        System.out.println(
            print(YELLOW, message)
        );
    }

    public static void log_error(String message) {
        System.out.println(
            print(RED, message)
        );
    }

    public static void log_highlight(String message) {
        System.out.println(
            print(GREEN, message)
        );
    }
}
