
import java.util.List;

public class Subsequence {

    public boolean find(List x, List y) {
        if (x == null || y == null) throw new IllegalArgumentException();
        if (x.isEmpty()) return true;
        int a = 0;
        for (int i = 0; i < x.size(); i++) {
            if (y.size() == 0 || x.size() > y.size()) return false;
            if (!x.get(i).equals(y.get(i))) y.remove(i--);
            else a++;
        }
        return x.size() == a;
    }
}