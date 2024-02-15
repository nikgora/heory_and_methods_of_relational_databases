import java.sql.Date;
import java.util.ArrayList;
import java.util.Map;


public class Main {

    /**
     * @param args
     */
    public static void main(String[] args) {
        try {
            Accessor ac = Accessor.getInstance();
            if (ac != null)
                System.out.println("Connection successful");
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
            Map<Integer, Double> task1 = ac.task1(2);
            for (var e : task1.entrySet()) {
                System.out.println("\t" + e.getKey() + " " + e.getValue());
            }
            ArrayList<Integer> task2 = ac.task2("полу-люкс", new Date(2023, 1, 13));
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
            //close connection to DB
            ac.closeConnection();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
