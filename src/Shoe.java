import java.util.ArrayList;
import java.util.List;

public class Shoe {
    int id;
    int size;
    int tempCount;
    String name;
    String theme;
    List <String> colors = new ArrayList<>();
    List <String> categories = new ArrayList<>();
    double price;
    boolean avaliable=true;

    public Shoe(int id, int size, String name, String theme, double price) {
        this.id=id;
        this.size=size;
        this.name = name;
        this.theme = theme;
        this.price = price;
    }

}
