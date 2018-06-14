import java.io.Serializable;

public class MyString implements Serializable {
    String string;

    public MyString() {}

    public MyString(String string) {
        this.string = string;
    }

    public String toString() {
        return string;
    }
}
