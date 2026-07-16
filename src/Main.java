import database.*;
import ds.MenuStack;
import menu.MainMenu;

import java.sql.Connection;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        // Step 1: Connect to Database
        Connection con = DatabaseConnection.getConnection();
        if (con == null) {
            System.out.println("Connection failed! Exiting.");
            return;
        }

        // Step 2: Ensure all tables exist
        DatabaseSetup.createTables();

        // Step 3: Launch the app - Scanner and MenuStack are created once here,
        // and passed down into every menu so they're shared across the whole app
        Scanner sc = new Scanner(System.in);
        MenuStack navStack = new MenuStack();

        MainMenu mainMenu = new MainMenu(sc, navStack);
        mainMenu.show();
    }
}
