import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Accessor {

    private static Accessor singletonAccessor = null;
    Statement stat = null;
    private Connection con = null;

    private Accessor() throws Exception {

        String server = "localhost"; // Адреса сервера PostgreSQL
        String port = "5433"; // Порт, на якому працює PostgreSQL
        String database = "hotel"; // Назва бази даних
        String username = "postgres"; // Ваше ім'я користувача PostgreSQL
        String password = "1"; // Ваш пароль до PostgreSQL

        Class.forName("org.postgresql.Driver"); // Завантаження драйвера

        // Стрічка підключення до бази даних PostgreSQL
        String connectionString = "jdbc:postgresql://" + server + ":" + port + "/" + database;

        try {
            con = DriverManager.getConnection(connectionString, username, password); // Встановлення з'єднання
            stat = con.createStatement(); // Створення об'єкта заяви
        } catch (SQLException e) {
            e.printStackTrace();
            throw e; // Прокидуємо SQLException, якщо виникає проблема з підключенням
        }

    }

    //singelton
    public static Accessor getInstance()
            throws Exception {
        if (singletonAccessor == null)
            singletonAccessor = new Accessor();
        return singletonAccessor;
    }

    //close DB connection
    public void closeConnection()
            throws SQLException {
        if (con != null)
            con.close();
    }

    /*********************************** Exsamples ******************************************/


//return list of Comforts
    public ArrayList<String> getComfort() throws SQLException {
        ArrayList<String> arr = new ArrayList<String>();
        ResultSet rs = stat.executeQuery("SELECT description FROM Comfort");
        while (rs.next()) {
            arr.add(rs.getString("description"));

        }
        rs.close();
        return arr;
    }


    // insert new Client to DB
    public boolean saveClient(String fio, String pasport) throws SQLException {
        int id = 0;
        //checking if client already exists
        ResultSet rs = stat.executeQuery("SELECT fio FROM Client WHERE fio='" + fio + "'");
        if (rs.next())
            return false;
        //get the last ID
        rs = stat.executeQuery("SELECT max(id_client) FROM Client");
        if (rs.next())
            id = rs.getInt(1);
        //insert new Client. executeUpdate returns count of affected rows
        int n = stat.executeUpdate("INSERT INTO Client (fio, passport) VALUES ( '" + fio + "','" + pasport + "')");
        if (n > 0)
            return true;
        return false;
    }

    /**
     * PRAC 1
     */
    public Map<Integer, Double> task1(int capacity) throws SQLException {
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        ResultSet rs = stat.executeQuery("SELECT number_room, price FROM Room WHERE capacity='" + capacity + "'");
        while (rs.next()) {
            map.put(rs.getInt("number_room"), rs.getDouble("price"));
        }
        rs.close();
        return map;
    }

    public ArrayList<Integer> task2(String description, Date date) throws SQLException {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        ResultSet rs = stat.executeQuery("SELECT number_room FROM Room WHERE ref_comfort = (SELECT id_comfort FROM Comfort WHERE description = '" + description + "') AND( number_room  IN (SELECT ref_room FROM Renting WHERE date_out < '" + date + "' ) OR  number_room NOT IN (SELECT ref_room FROM Renting))");
        while (rs.next()) {
            arr.add(rs.getInt("number_room"));
        }
        rs.close();
        return arr;
    }

    public double task3(int numberOfDays, int number_room) throws SQLException {
        double res = 0;
        ResultSet rs = stat.executeQuery("SELECT price FROM Room WHERE number_room ='" + number_room + "'");
        while (rs.next()) {
            res += (rs.getDouble("price"));
        }
        return res * numberOfDays;
    }

    public Map<String, Integer> task4() throws SQLException {
        Map<String, Integer> map = new HashMap<String, Integer>();
        ResultSet rs = stat.executeQuery("SELECT description, COUNT(*) AS count FROM Room JOIN Comfort ON Room.ref_comfort = Comfort.id_comfort GROUP BY description");
        while (rs.next()) {
            map.put(rs.getString("description"), rs.getInt("count"));
        }
        rs.close();
        return map;
    }

    public ArrayList<Integer> task5() throws SQLException {
        ArrayList<Integer> arr = new ArrayList<Integer>();
        ResultSet rs = stat.executeQuery("SELECT ref_room FROM Renting WHERE date_out = CURRENT_DATE");
        while (rs.next()) {
            arr.add(rs.getInt("ref_room"));
        }
        rs.close();
        return arr;
    }

    /**
     * PRAC 2
     *
     */
    /**
     * @param number_room
     * @param capacity
     * @param comfort
     * @return n - number of rows that changed
     * @throws SQLException
     * @throws Exception
     */
    public int addRoom(int number_room, int capacity, String comfort) throws SQLException, Exception {
        ResultSet rs = stat.executeQuery("SELECT number_room FROM Room WHERE number_room='" + number_room + "'");
        if (rs.next())
            throw new Exception("room is already exist");
        rs = stat.executeQuery("SELECT id_comfort FROM Comfort WHERE description='" + comfort + "'");
        int id_comfort = -1;
        if (!rs.next())
            throw new Exception("no such comfort");
        else {
            id_comfort = rs.getInt("id_comfort");

        }

        int n = stat.executeUpdate("INSERT INTO room (number_room, capacity, ref_comfort, price) VALUES ( '" + number_room + "','" + capacity + "', '" + id_comfort + "', NULL)");
        return n;
    }

    /**
     * @param fio
     * @param number_room
     * @param date
     * @return n - number of rows that changed
     * @throws SQLException
     * @throws Exception
     */
    public int bookClient(String fio, int number_room, Date date) throws SQLException, Exception {
        ResultSet rs = stat.executeQuery("SELECT id_client FROM client WHERE fio='" + fio + "'");
        int id_client = -1;
        if (!rs.next())
            throw new Exception("no such client");
        else {
            id_client = rs.getInt("id_client");
        }
        rs = stat.executeQuery("SELECT ref_room from renting where ref_room = '" + number_room + "' and (date_in <= '" + date + "' and (date_out >= '" + date + "' OR date_out is NULL))");
        if (rs.next())
            throw new Exception("room is reserved");
        int n = stat.executeUpdate("INSERT INTO renting (ref_client, ref_room, date_in, date_out) VALUES ('" + id_client + "','" + number_room + "','" + date + "', NULL )");
        return n;
    }

    /**
     * @param number_room
     * @param price
     * @return n - number of rows that changed
     * @throws SQLException
     * @throws Exception
     */
    public int updatePrice(int number_room, double price) throws SQLException, Exception {
        ResultSet rs = stat.executeQuery("SELECT number_room FROM Room WHERE number_room='" + number_room + "'");
        if (!rs.next())
            throw new Exception("room isn`t exist");
        int n = stat.executeUpdate("UPDATE room Set price = '" + price + "'  WHERE number_room='" + number_room + "'");
        return n;
    }

    /**
     * @param fio
     * @return n - number of rows that changed
     * @throws SQLException
     * @throws Exception
     */
    public int deleteClient(String fio) throws SQLException, Exception {
        ResultSet rs = stat.executeQuery("SELECT id_client FROM client WHERE fio='" + fio + "'");
        int id_client = -1;
        if (!rs.next())
            throw new Exception("no such client");
        id_client = rs.getInt("id_client");
        int n = stat.executeUpdate("DELETE FROM renting WHERE ref_client = '" + id_client + "'");
        n += stat.executeUpdate("DELETE FROM client WHERE fio = '" + fio + "'");
        return n;
    }
}

/*********************************** Task 3  ******************************************/

