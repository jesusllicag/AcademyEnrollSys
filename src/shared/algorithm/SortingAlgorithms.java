package shared.algorithm;

import java.util.Comparator;
import java.util.List;

/**
 * @author Jesus Llica
 * Algoritmos de ordenamiento
 */
public class SortingAlgorithms {

    private SortingAlgorithms() {
    }

    public static <T> void insertionSort(List<T> list, Comparator<T> comparator) {
        int n = list.size();
        for (int i = 1; i < n; i++) {
            T key = list.get(i);
            int j = i - 1;
            while (j >= 0 && comparator.compare(list.get(j), key) > 0) {
                list.set(j + 1, list.get(j));
                j--;
            }
            list.set(j + 1, key);
        }
    }

}
