import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.*;

public class Shop {

    List<Shoe> shoes = new ArrayList<>();
    Scanner sc=new Scanner(System.in);
    List<Shoe> tempShoes = new ArrayList<>();
    Customer customer;

    public Shop(Customer customer) throws IOException, SQLException {
        this.customer = customer;
        loadShoes();

        while (true) {

            System.out.println("Välkommen till sko-shoppen! \n" +
                    "Vänligen välj ett av följande alternativ: \n" +
                    "1. sortera efter storlek\n" +
                    "2. sortera efter färg \n" +
                    "3. lista alla skor\n" +
                    "4. Logga ut \n" +
                    "Ange val med siffra");
            int customerChoice = readInt();
            switch (customerChoice) {
                case 1:
                    sortSize();
                    break;
                case 2:
                    sortColor();
                    break;
                case 3:
                    listAllShoes();
                    break;
                case 4:
                    new Login();
                    break;
                default:
                    System.out.println("Inget aktivt val angett, systemet avslutas");
                    System.exit(0);
            }

        }

    }

    public void listAllShoes() throws SQLException, IOException {

        int counter=1;
        int customerShoeChoice;
        tempShoes.clear();
        for (Shoe shoe : shoes) {
            shoe.tempCount = 0;
            tempShoes.add(shoe);

            if (shoe.theme != null && shoe.avaliable) {
                System.out.println(counter + "." + shoe.name + " " + shoe.theme + " med färg: " + shoe.colors.toString() +
                        " och storlek: "+ shoe.size);
                shoe.tempCount = counter;
                counter++;
            }
        }
        if (tempShoes.size() == 0) {
            System.out.println("ingen sko hittades");
        }
        else {
            System.out.println("Ange sko du vill köpa med hjälp av siffran framför \n");
            customerShoeChoice = readInt();
            for (Shoe findShoe : shoes) {
                if (findShoe.tempCount == customerShoeChoice) {
                    buyThisShoe(customer, findShoe);
                }
            }
        }
    }

    public void sortColor() throws SQLException, IOException {
        String customerColor;
        int counter=1;
        int customerShoeChoice;
        tempShoes.clear();
        System.out.println("Ange färgen du vill söka på (engelska): ");
        customerColor=sc.nextLine();

        for (Shoe shoe : shoes) {
            shoe.tempCount = 0;
            if (shoe.colors.contains(customerColor) && shoe.avaliable){
                tempShoes.add(shoe);

                if (shoe.theme != null) {

                    System.out.println(counter + "." + shoe.name + " " + shoe.theme + " med färg: " + shoe.colors.toString() +
                            " och storlek: " + shoe.size);
                    shoe.tempCount = counter;
                    counter++;
                    }
                }

        }
        if (tempShoes.size() == 0) {
            System.out.println("ingen sko hittades");
        }
        else {
            System.out.println("Ange sko du vill köpa med hjälp av siffran framför \n");
            customerShoeChoice = readInt();
            for (Shoe findShoe : shoes) {
                if (findShoe.tempCount == customerShoeChoice) {
                    buyThisShoe(customer, findShoe);
                }

            }
        }

    }

    public void buyThisShoe(Customer customer, Shoe s) throws IOException, SQLException {

        Properties p = new Properties();
        p.load(new FileInputStream("src/settings.properties"));
        String message;

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("userName"),
                p.getProperty("password"));

             CallableStatement stmt = con.prepareCall("call buyShoe(?,?,?)");

        ) {
            stmt.setInt(1, customer.id);
            stmt.setInt(2, s.id);
            stmt.registerOutParameter(3, Types.VARCHAR);
            stmt.executeQuery();
            message = stmt.getString(3);
            System.out.println(message);
            if (message.equalsIgnoreCase("Skon är slut på lagret")){
                s.avaliable=false;
            }
        }
    }

    public void sortSize() throws SQLException, IOException {

    int customerSize;
    int counter=1;
    int customerShoeChoice;
    tempShoes.clear();

    System.out.println("Ange storleken du vill söka på: ");
    customerSize=readInt();

        for (Shoe shoe : shoes) {
        shoe.tempCount = 0;
        if (customerSize == shoe.size && shoe.avaliable) {
            tempShoes.add(shoe);

            if (shoe.theme != null) {
                System.out.println(counter + "." + shoe.name + " " + shoe.theme + " med färg: " + shoe.colors.toString());
                shoe.tempCount = counter;
                counter++;
            }
        }

    }
        if (tempShoes.size() == 0) {
            System.out.println("ingen sko hittades");
        }
        else {
            System.out.println("Ange sko du vill köpa med hjälp av siffran framför \n");
            customerShoeChoice=readInt();
            for (Shoe findShoe : shoes) {
                if (findShoe.tempCount == customerShoeChoice) {
                    buyThisShoe(customer, findShoe);
                }
            }

    }

}

    public void loadShoes() throws IOException {

        Properties p = new Properties();
        p.load(new FileInputStream("src/settings.properties"));

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("userName"),
                p.getProperty("password"));

             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select shoe.id, shoe.name, theme, shoesize, price from shoe\n")) {
            while (rs.next()) {

                Shoe s = new Shoe(rs.getInt("id"), rs.getInt("shoesize"), rs.getString("name")
                        , rs.getString("theme"), rs.getInt("price"));
                getStat(s);

                shoes.add(s);
            }
        } catch (SQLException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void getStat (Shoe s) throws SQLException, IOException {
        Properties p = new Properties();
        p.load(new FileInputStream("src/settings.properties"));

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("userName"),
                p.getProperty("password"));

             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select shoe.name, color.name from shoe\n" +
                     "                     left join color_table on shoe.id=color_table.shoeId\n" +
                     "                     left join color on color_table.colorId=color.id\n" +
                     "                     where shoe.id= "+ s.id)) {
            while (rs.next()) {
                s.colors.add(rs.getString("color.name"));
            }

        }


        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("userName"),
                p.getProperty("password"));

             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select shoe.name, category.name from shoe\n" +
                     "                     left join category_table on shoe.id=category_table.shoeId\n" +
                     "                     left join category on category_table.categoryId=category.id\n" +
                     "                     where shoe.id= "+ s.id)) {
            while (rs.next()) {
                s.categories.add(rs.getString("category.name"));
            }
        }

        try (Connection con = DriverManager.getConnection(
                p.getProperty("connectionString"),
                p.getProperty("userName"),
                p.getProperty("password"));

             Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery("select stock.status from shoe\n" +
                     "                     left join stock on shoe.id=stock.shoeId\n" +
                     "                     where shoe.id= "+ s.id)) {
            while (rs.next()) {
                int stockStatus=rs.getInt("stock.status");
                if (stockStatus <=0) {
                    s.avaliable=false;
                }
            }

        }
    }


    public int readInt(){
        int answer;
        while (true) {
            try {
                answer = Integer.parseInt(sc.nextLine());
                break;
            } catch (NumberFormatException e) {
                System.out.println("Vänligen ange en siffra");
            }
        }
        return answer;
    }
}
