import java.sql.*;
public class DB {
    private static final String URL = "jdbc:sqlite:yatranest.db";
    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void init() {
        try(Connection conn = connect();
            Statement stmt = conn.createStatement()) {

            // USERS TABLE
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS users (" +
                         "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                          "username TEXT UNIQUE," +"password TEXT)"
            );

            // ROOMS TABLE
           stmt.execute(
        "CREATE TABLE IF NOT EXISTS rooms (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "room_no TEXT," + "type TEXT," +
                "capacity INTEGER," + "price REAL," + "status TEXT)"
);

            // BOOKINGS TABLE
            stmt.execute(
                "CREATE TABLE IF NOT EXISTS bookings (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "username TEXT," + "name TEXT," +
                "phone TEXT," + "address TEXT," +
                "email TEXT," + "checkin TEXT," +
                "checkout TEXT," + "room_no TEXT," +
                "amount REAL," + "payment_method TEXT)"
            );
        } catch(Exception e) {
            e.printStackTrace();
        }
    }
}