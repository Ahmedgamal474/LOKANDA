# LOKANDA Standalone Console

This package adds a separate terminal interface to the existing LOKANDA project.
It does not replace or edit the original `Main.java`, Models, DAOs, Services, or validation methods.

## Start it

Run this class from IntelliJ IDEA or your terminal:

`com.lokanda.hotel.console.ConsoleMain`

The current project login is reused:

- Username: `admin`
- Password: `admin123`

The existing `pom.xml` already contains the MySQL Connector/J dependency. Make sure the MySQL server is running and the `hotel_reservation` database is created using `src/main/java/com/lokanda/hotel/sql/hotel_reservation`.

## Console commands

- `cls` or `clear`: clear a native CMD/Terminal screen while staying at the current input. In IntelliJ/Eclipse Run output panes (which are not real terminals), the console scrolls old output out of view as a visual fallback.
- `help`: show the available terminal commands.

For a real Windows screen clear, launch `ConsoleMain` from Windows CMD/Windows Terminal (not the IDE Run output pane). The IDE output pane does not expose a native terminal buffer, so Java cannot truly erase its scrollback; the fallback keeps the current screen readable by scrolling it away.

## Package layout

- `ConsoleMain`: separate application entry point.
- `ConsoleLoginScreen`: console-native login; supports `cls` and `clear` while entering credentials. It mirrors the current `admin` / `admin123` credentials without editing the original `LoginService`.
- `LokandaConsole`: main menu and navigation.
- `core`: shared terminal UI, input parsing, retry behavior, and common screen helpers.
- `screens`: booking, guest, room, offer, payment, feedback, and receptionist screens.
- `service/ConsoleBookingRepository`: booking persistence for this console, using the `package_name` column from the SQL schema supplied with the project while reusing the existing model validation.
- `model/BookingSummary`: read-only booking rows for the terminal tables.

## Important

The existing `Main.java` remains the original entry point and is not changed. To use this console, run `ConsoleMain` instead.
