import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public int getIdComfort(String comfort) throws SQLException, Exception {
        int res = 0;
        ResultSet rs = stat.executeQuery("SELECT id_comfort FROM Comfort WHERE description = '" + comfort + "'");
        if (rs.next()) {
            res = (rs.getInt("id_comfort"));
        } else {
            throw new Exception("No such comfort");
        }
        rs.close();
        return res;
    }


    //return list of Comforts
    public ArrayList<String> getComfort() throws SQLException {
        ArrayList<String> arr = new ArrayList<>();
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
        return n > 0;
    }

    /**
     * PRAC 1
     */
    public Map<Integer, Float> task1(int capacity) throws SQLException {
        Map<Integer, Float> map = new HashMap<>();
        ResultSet rs = stat.executeQuery("SELECT number_room, price FROM Room WHERE capacity='" + capacity + "'");
        while (rs.next()) {
            map.put(rs.getInt("number_room"), rs.getFloat("price"));
        }
        rs.close();
        return map;
    }

    public ArrayList<Integer> task2(String description, Date date) throws SQLException {
        ArrayList<Integer> arr = new ArrayList<>();
        ResultSet rs = stat.executeQuery("SELECT number_room FROM Room WHERE ref_comfort = (SELECT id_comfort FROM Comfort WHERE description = '" + description + "') AND( number_room  IN (SELECT ref_room FROM Renting WHERE date_out < '" + date + "' ) OR  number_room NOT IN (SELECT ref_room FROM Renting))");
        while (rs.next()) {
            arr.add(rs.getInt("number_room"));
        }
        rs.close();
        return arr;
    }

    public float task3(int numberOfDays, int number_room) throws SQLException {
        float res = 0;
        ResultSet rs = stat.executeQuery("SELECT price FROM Room WHERE number_room ='" + number_room + "'");
        while (rs.next()) {
            res += (rs.getFloat("price"));
        }
        return res * numberOfDays;
    }

    public Map<String, Integer> task4() throws SQLException {
        Map<String, Integer> map = new HashMap<>();
        ResultSet rs = stat.executeQuery("SELECT description, COUNT(*) AS count FROM Room JOIN Comfort ON Room.ref_comfort = Comfort.id_comfort GROUP BY description");
        while (rs.next()) {
            map.put(rs.getString("description"), rs.getInt("count"));
        }
        rs.close();
        return map;
    }

    public ArrayList<Integer> task5() throws SQLException {
        ArrayList<Integer> arr = new ArrayList<>();
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
    public int updatePrice(int number_room, float price) throws SQLException, Exception {
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

    /*********************************** Task 3  ******************************************/
    public int addClient(Client client) throws Exception {
        int id = 0;
        //checking if client already exists
        ResultSet rs = stat.executeQuery("SELECT fio FROM Client WHERE fio='" + client.getFio() + "' AND passport = '" + client.getPassport() + "'");
        if (rs.next())
            throw new Exception("such client is exist");
        //get the last ID
        rs = stat.executeQuery("SELECT max(id_client) FROM Client");
        if (rs.next())
            id = rs.getInt(1);
        //insert new Client. executeUpdate returns count of affected rows
        int n = stat.executeUpdate("INSERT INTO Client (fio, passport) VALUES ( '" + client.getFio() + "','" + client.getPassport() + "')");
        return n;
    }

    List<Client> getAllClients() throws SQLException, Exception {
        List<Client> res = new ArrayList<>();
        ResultSet rs = stat.executeQuery("SELECT id_client, fio, passport FROM Client");
        while (rs.next()) {
            Client client = new Client(rs.getString("fio"), rs.getString("passport"));
            res.add(client);
        }
        return res;
    }

    Map<String, String> getMetaData(String tableName) throws SQLException {
        ResultSet rs = stat.executeQuery("SELECT * FROM " + tableName);
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        Map<String, String> res = new HashMap<>();
        for (int i = 0; i < resultSetMetaData.getColumnCount(); i++) {
            res.put(resultSetMetaData.getColumnName(i + 1), resultSetMetaData.getColumnTypeName(i + 1));
        }
        res.put("number of colums", String.valueOf(resultSetMetaData.getColumnCount()));
        return res;
    }

    //TODO Спитати Чому у 326 треба писати Types.REAL а не Types.FLOAT
    /// Task 4
    /*

CREATE OR REPLACE FUNCTION register_client(
    p_room_number INT,
    p_fio VARCHAR(100),
    p_passport VARCHAR(20),
    p_date_in DATE,
    p_num_days INT
) RETURNS FLOAT AS
$$
DECLARE
    v_price FLOAT;
    v_client_id INT;
BEGIN
    IF NOT EXISTS (SELECT 1 FROM room WHERE number_room = p_room_number) THEN
        RAISE EXCEPTION 'Room with number % does not exist', p_room_number;
    END IF;

    IF EXISTS (SELECT ref_room FROM renting WHERE ref_room = p_room_number AND (current_date >= date_in and ((current_date < date_out)or date_out is NULL))) THEN
        RAISE EXCEPTION 'Room with number % is already occupied', p_room_number;
    END IF;


    SELECT id_client INTO v_client_id FROM client WHERE fio = p_fio AND passport = p_passport;


    IF v_client_id IS NULL THEN
        INSERT INTO client (fio, passport) VALUES (p_fio, p_passport) RETURNING id_client INTO v_client_id;
    END IF;


    SELECT price * p_num_days INTO v_price
    FROM room
    WHERE number_room = p_room_number;


    INSERT INTO renting (ref_client, ref_room, date_in, date_out)
    VALUES (v_client_id, p_room_number, p_date_in, p_date_in + p_num_days);

    RETURN v_price;
END;
$$
    LANGUAGE plpgsql;

     */
    public float registerClient(int roomNumber, String clientName, String passportData, Date checkInDate, int stayDuration) throws SQLException {
        float totalCost = 0;
        CallableStatement cstmt = null;

        try {
            cstmt = con.prepareCall("{call register_client(?, ?, ?, ?, ?, ?)}");
            cstmt.setInt(1, roomNumber);
            cstmt.setString(2, clientName);
            cstmt.setString(3, passportData);
            cstmt.setDate(4, checkInDate);
            cstmt.setInt(5, stayDuration);
            cstmt.registerOutParameter(6, Types.REAL);

            cstmt.execute();

            totalCost = cstmt.getFloat(6);

        } finally {
            if (cstmt != null) {
                cstmt.close();
            }
        }

        return totalCost;
    }


    //PRAC 5
    public boolean addRooms(int capacity, float price) throws Exception {
        PreparedStatement pst = null;
        try {
            con.setAutoCommit(false);
            int max_id_comfort = 0;
            ResultSet rs = stat.executeQuery("SELECT count(id_comfort) FROM comfort");
            if (rs.next()) {
                max_id_comfort = rs.getInt(1);
            }
            pst = con.prepareStatement("insert into room values (?, ?, ?, ?)");
            for (int i = 0; i < max_id_comfort; i++) {
                rs = stat.executeQuery("SELECT max(number_room) FROM room");
                int idRoom = 0;
                if (rs.next()) {
                    idRoom = rs.getInt(1);
                }
                pst.setInt(1, idRoom + 1 + i);
                pst.setInt(2, capacity);
                pst.setInt(3, i + 1);
                pst.setFloat(4, price);
                pst.addBatch();
            }
            int[] result = pst.executeBatch();
            for (int aResult : result) {
                if (aResult != 1) {
                    con.rollback();
                    pst.clearBatch();
                    break;
                }
            }
        } catch (SQLException e) {
            con.rollback();
            throw e;
        } finally {
            if (pst != null) {
                pst.close();
            }
            con.setAutoCommit(true);
        }
        return true;
    }
}

