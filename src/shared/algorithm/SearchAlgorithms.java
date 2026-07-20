package shared.algorithm;

import java.util.Comparator;
import java.util.List;

/**
 * @author Jesus Llica
 * Algoritmos de busqueda
 */
public class SearchAlgorithms {

    private SearchAlgorithms() {

    }

    /**
     * Busqueda binaria sobre lista ordenada. Retorna el indice o -1 si no existe.
     */
    public static <T> int binarySearch(List<T> list, T target, Comparator<T> comparator) {
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = comparator.compare(list.get(mid), target);
            if (cmp == 0)
                return mid;
            if (cmp < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }
        return -1;
    }

    /**
     * Busqueda binaria por clave string sobre lista ordenada por esa clave.
     */
    public static <T> T binarySearchByKey(List<T> list, String key,
            java.util.function.Function<T, String> keyExtractor) {
        int low = 0;
        int high = list.size() - 1;
        while (low <= high) {
            int mid = (low + high) / 2;
            int cmp = keyExtractor.apply(list.get(mid)).compareTo(key);
            if (cmp == 0)
                return list.get(mid);
            if (cmp < 0)
                low = mid + 1;
            else
                high = mid - 1;
        }
        return null;
    }
}
