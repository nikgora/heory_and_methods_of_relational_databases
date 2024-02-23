import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;


public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Accessor ac = Accessor.getInstance();
            if (ac != null)
                System.out.println("Connection  successful");
            else {
                System.out.println("Connection faild");
                System.exit(0);
            }

            ArrayList<String> v = ac.getComfort();
            System.out.println("List of comfort:");
            for (int i = 0; i < v.size(); i++)
                System.out.println("\t" + v.get(i));

            //call method to add new Client
            if (ac.saveClient("Sdobnik M. S.", "SA 567899"))
                System.out.println("successful");
            else
                System.out.println("not successful");

            System.out.println("Task 1:");
            Map<Integer, Float> task1 = ac.task1(2);
            for (var e : task1.entrySet()) {
                System.out.println("\t" + e.getKey() + " " + e.getValue());
            }
            ArrayList<Integer> task2 = ac.task2("полу-люкс", new Date(124, 1, 9));
            System.out.println("List of task2:");
            for (int i = 0; i < task2.size(); i++)
                System.out.println("\t" + task2.get(i));

            System.out.println("Task 3:");
            System.out.println("\t" + ac.task3(1, 105));

            System.out.println("Task 4:");
            Map<String, Integer> task4 = ac.task4();
            for (var e : task4.entrySet()) {
                System.out.println("\t" + e.getKey() + " " + e.getValue());
            }
            ArrayList<Integer> task5 = ac.task5();
            System.out.println("List of task5:");
            for (int i = 0; i < task5.size(); i++)
                System.out.println("\t" + task5.get(i));

            System.out.println("PRAC 2\nTASK 1");
            try {
                int n = ac.addRoom(110, 2, "полу-люкс");
                System.out.println("\t Changed rows: " + n);
            } catch (Exception e) {
                e.printStackTrace();
            }

            System.out.println("Task 2");
            try {
                Date date = new Date(124, 1, 16);
                int n = ac.bookClient("Sdobnik M. S.", 110, date);
                System.out.println("\t Changed rows: " + n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Task 3");
            try {
                int n = ac.updatePrice(110, 230);
                System.out.println("\t Changed rows: " + n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Task 4");
            try {
                int n = ac.deleteClient("Sdobnik M. S.");
                System.out.println("\t Changed rows: " + n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("PRAC 3\nTASK 1");
            Client client = new Client("Mudrik M.D", "XA 412341");
            System.out.println("Task 2");
            Scanner scanner = new Scanner(System.in);
            System.out.println("Enter fio");
            String fio = scanner.nextLine();
            System.out.println("Enter passport like XX 123456");
            String passport = scanner.nextLine();
            try {
                client = new Client(fio, passport);
                int n = ac.addClient(client);
                System.out.println("\t Changed rows: " + n);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("Task 3");
            List<Client> clients = ac.getAllClients();
            for (Client client1 : clients) {
                System.out.println(client1);
            }
            //close connection to DB
            ac.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
