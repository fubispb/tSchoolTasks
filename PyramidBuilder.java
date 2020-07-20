
import java.util.ArrayList;
import java.util.List;

public class PyramidBuilder {

    public int[][] buildPyramid(List<Integer> inputNumbers) {
        if (inputNumbers.contains(null) || inputNumbers.size() >= Integer.MAX_VALUE - 8)
            throw new CannotBuildPyramidException();
        List<Integer> currentList = new ArrayList<>(inputNumbers);
        currentList.sort(Integer::compareTo);
        int[][] mass;
        int a = 0, b = 0;
        for (int i = 0; i < currentList.size(); i++) {
            a = i + 1;
            b += a;
            if (b == currentList.size()) {
                b = a * 2 - 1;
                mass = new int[a][b];
                for (int j = 0; j < a; j++) {
                    mass[j][b / 2 - j] = currentList.get(0);
                    currentList.remove(0);
                    for (int k = 0; k < j; k++) {
                        mass[j][b / 2 - j + 2 + (k * 2)] = currentList.get(0);
                        currentList.remove(0);
                    }
                }
                return mass;
            }
        }
        throw new CannotBuildPyramidException();
    }
}