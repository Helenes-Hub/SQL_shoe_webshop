import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
import java.util.Scanner;

public class Login {
    public Login() throws IOException {
       // Det skapas upp en ny bestiällning
       // En vara läggs till i en befintlig beställning
       // En vara som redan finns i en betällning ökas på, i antal, i beställningen.
       // En vara tar slut i lager och hamnar i OutOfStock (för VG)
       // Arlo Almighty (3) har aktiv beställning, Ada Lovelace (1) har -inte- en aktiv beställning

        while (true) {
            Scanner sc = new Scanner(System.in);
            System.out.println("Välkommen till Login! Tänk på att systemet är case-sensetive");
            System.out.println("Ange användarnamn:");
            String userName = sc.nextLine().trim();
            System.out.println("Ange lösenord: ");
            String password = sc.nextLine().trim();

            Customer customer = authenticate(userName, password);

            if (customer.loggedIn) {
                try {
                    new Shop(customer);
                } catch (SQLException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public Customer authenticate(String userName, String password) throws IOException {

        Properties p = new Properties();
        p.load(new FileInputStream("src/settings.properties"));
        Customer c=new Customer();

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("userName"),
                p.getProperty("password"));

             PreparedStatement pstmt= con.prepareStatement("SELECT id, name, password FROM customer WHERE name = ?"))
              {
                  pstmt.setString(1, userName);
                  ResultSet rs = pstmt.executeQuery();
                  {

                      while (rs.next()) {
                          c.id=rs.getInt("id");
                          c.password=password;
                          c.name=userName;

                          String sqlName = rs.getString("name");
                          String sqlPassword = rs.getString("password");
                          if (c.name.equals(sqlName) && c.password.equals(sqlPassword)) {
                              c.loggedIn=true;
                              System.out.println("yes! Du loggas in");
                              return c;
                          }

                      }
                  }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        System.out.println("Ingen match mellan användarnamn och lösenord hittades \n");
        return c;
    }

    public static void main(String[] args) throws IOException {
        new Login();
    }
}
//comment for commit