import database.*;
import ds.*;
import menu.*;

import java.sql.*;
import java.util.*;

public class Main {
    public static void main(String[] args) {

        Connection con = DatabaseConnection.getConnection();
        if (con == null) {
            System.out.println("Connection failed! Exiting.");
            return;
        }

        DatabaseSetup.createTables();

        Scanner sc = new Scanner(System.in);
        MenuStack navStack = new MenuStack();

        MainMenu mainMenu = new MainMenu(sc, navStack);
        mainMenu.show();
    }
}
