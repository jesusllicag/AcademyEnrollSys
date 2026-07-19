package shared.datastructure;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Arbol AVL (Adelson-Velsky y Landis) generico, autobalanceado.
 * Variante de BinarySearchTree que garantiza O(log n) en insert/search/delete
 * en todos los casos, reequilibrando la altura tras cada insercion o eliminacion.
 */
public class AVLTree<T> {

    private static class Node<T> {
        T data;
        Node<T> left;
        Node<T> right;
        int height;

        Node(T data) {
            this.data = data;
            this.height = 1;
        }
    }

    private Node<T> root;
    private final Function<T, String> keyExtractor;

    public AVLTree(Function<T, String> keyExtractor) {
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
        else {
            node.data = data;
            return node;
        }
        return rebalance(node);
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
        return cmp < 0 ? searchRec(node.left, key) : searchRec(node.right, key);
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
        return rebalance(node);
    }

    private Node<T> findMin(Node<T> node) {
        while (node.left != null)
            node = node.left;
        return node;
    }

    // Reequilibra el nodo tras insert/delete evaluando los 4 casos clasicos
    // (LL, RR, LR, RL) segun el factor de balance.
    private Node<T> rebalance(Node<T> node) {
        updateHeight(node);
        int balance = balanceFactor(node);

        if (balance > 1) {
            if (balanceFactor(node.left) < 0)
                node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        if (balance < -1) {
            if (balanceFactor(node.right) > 0)
                node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        return node;
    }

    private int height(Node<T> node) {
        return node == null ? 0 : node.height;
    }

    private void updateHeight(Node<T> node) {
        node.height = 1 + Math.max(height(node.left), height(node.right));
    }

    private int balanceFactor(Node<T> node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private Node<T> rotateRight(Node<T> y) {
        Node<T> x = y.left;
        Node<T> t2 = x.right;
        x.right = y;
        y.left = t2;
        updateHeight(y);
        updateHeight(x);
        return x;
    }

    private Node<T> rotateLeft(Node<T> x) {
        Node<T> y = x.right;
        Node<T> t2 = y.left;
        y.left = x;
        x.right = t2;
        updateHeight(x);
        updateHeight(y);
        return y;
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

    public int height() {
        return height(root);
    }

    public void clear() {
        root = null;
    }
}
