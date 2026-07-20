package shared.datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * @author Jesus Llica
 * Arbol Binario de Busqueda generico.
 * Utilizado para almacenar y buscar alumnos por codigo
 */
public class BinarySearchTree<T> {

    private static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;

        Node(T data) {
            this.data = data;
        }
    }

    private Node<T> root;
    private final Function<T, String> keyExtractor;

    public BinarySearchTree(Function<T, String> keyExtractor) {
        this.keyExtractor = keyExtractor;
    }

    public void insert(T data) {
        root = insertRec(root, data);
    }

    private Node<T> insertRec(Node<T> node, T data) {
        if (node == null)
            return new Node<>(data);
        String key = keyExtractor.apply(data);
        String nodeKey = keyExtractor.apply(node.data);
        int cmp = key.compareTo(nodeKey);
        if (cmp < 0)
            node.left = insertRec(node.left, data);
        else if (cmp > 0)
            node.right = insertRec(node.right, data);
        else
            node.data = data;
        return node;
    }

    public T search(String key) {
        return searchRec(root, key);
    }

    private T searchRec(Node<T> node, String key) {
        if (node == null)
            return null;
        String nodeKey = keyExtractor.apply(node.data);
        int cmp = key.compareTo(nodeKey);
        if (cmp == 0)
            return node.data;
        if (cmp < 0)
            return searchRec(node.left, key);
        return searchRec(node.right, key);
    }

    public boolean delete(String key) {
        int[] deleted = { 0 };
        root = deleteRec(root, key, deleted);
        return deleted[0] == 1;
    }

    private Node<T> deleteRec(Node<T> node, String key, int[] deleted) {
        if (node == null)
            return null;
        String nodeKey = keyExtractor.apply(node.data);
        int cmp = key.compareTo(nodeKey);
        if (cmp < 0) {
            node.left = deleteRec(node.left, key, deleted);
        } else if (cmp > 0) {
            node.right = deleteRec(node.right, key, deleted);
        } else {
            deleted[0] = 1;
            if (node.left == null)
                return node.right;
            if (node.right == null)
                return node.left;
            Node<T> min = findMin(node.right);
            node.data = min.data;
            node.right = deleteRec(node.right, keyExtractor.apply(min.data), new int[1]);
        }
        return node;
    }

    private Node<T> findMin(Node<T> node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    public List<T> inOrder() {
        List<T> result = new ArrayList<>();
        inOrderRec(root, result);
        return result;
    }

    private void inOrderRec(Node<T> node, List<T> result) {
        if (node == null)
            return;
        inOrderRec(node.left, result);
        result.add(node.data);
        inOrderRec(node.right, result);
    }

    public void update(T data) {
        insert(data);
    }

    public int size() {
        return sizeRec(root);
    }

    private int sizeRec(Node<T> node) {
        if (node == null)
            return 0;
        return 1 + sizeRec(node.left) + sizeRec(node.right);
    }

    public void clear() {
        root = null;
    }
}
