package org.ikomarchenko.terminalbuffer;

import java.util.Scanner;

public final class TerminalBufferCli {

    private TerminalBufferCli() {
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        TerminalBuffer buffer = new TerminalBuffer(10, 4, 5);

        System.out.println("=== Terminal Buffer Demo ===");
        System.out.println("Buffer size: 10x4, scrollback limit: 5");
        System.out.println();
        System.out.println("Commands:");
        System.out.println("  write <text>");
        System.out.println("  insert <text>");
        System.out.println("  cursor <column> <row>");
        System.out.println("  print");
        System.out.println("  clear");
        System.out.println("  exit");
        System.out.println();
        System.out.println("Use English keyboard layout for commands.");
        System.out.println();

        printState(buffer);

        while (true) {
            System.out.print("> ");
            String input = scanner.nextLine();

            if (input == null) {
                continue;
            }

            input = input.trim();

            if (input.isEmpty()) {
                continue;
            }

            try {
                if (input.equalsIgnoreCase("exit")) {
                    System.out.println("Bye.");
                    break;
                }

                if (input.equalsIgnoreCase("print")) {
                    printState(buffer);
                    continue;
                }

                if (input.equalsIgnoreCase("clear")) {
                    buffer.clearScreen();
                    printState(buffer);
                    continue;
                }

                if (input.equalsIgnoreCase("write")) {
                    System.out.println("Usage: write <text>");
                    continue;
                }

                if (input.startsWith("write ")) {
                    String text = input.substring("write ".length());
                    buffer.write(text);
                    printState(buffer);
                    continue;
                }

                if (input.equalsIgnoreCase("insert")) {
                    System.out.println("Usage: insert <text>");
                    continue;
                }

                if (input.startsWith("insert ")) {
                    String text = input.substring("insert ".length());
                    buffer.insert(text);
                    printState(buffer);
                    continue;
                }

                if (input.startsWith("cursor ")) {
                    String[] parts = input.split("\\s+");
                    if (parts.length != 3) {
                        System.out.println("Usage: cursor <column> <row>");
                        continue;
                    }

                    int column = Integer.parseInt(parts[1]);
                    int row = Integer.parseInt(parts[2]);

                    buffer.setCursor(column, row);
                    printState(buffer);
                    continue;
                }

                System.out.println("Unknown command. Use: write <text>, insert <text>, cursor <column> <row>, print, clear, exit");

            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void printState(TerminalBuffer buffer) {
        System.out.println();
        System.out.println("Cursor: (" + buffer.getCursor().getColumn() + ", " + buffer.getCursor().getRow() + ")");
        System.out.println("Scrollback size: " + buffer.getScrollback().size());
        System.out.println("--- Screen ---");
        System.out.println(buffer.getScreenContentAsString());
        System.out.println("--- Full buffer ---");
        System.out.println(buffer.getBufferContentAsString());
        System.out.println();
    }
}
