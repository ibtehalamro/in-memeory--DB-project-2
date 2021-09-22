package com.example.inmemoryweb.bplusalgorithm;

import com.example.inmemoryweb.Exceptions.TableException;
import com.example.inmemoryweb.databasestructure.Column;
import com.example.inmemoryweb.databasestructure.DataRow;
import com.example.inmemoryweb.databasestructure.Value;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.example.inmemoryweb.Configuration.Constants.TABLE_MAX_SIZE;


public class BPlusTree<K extends Comparable<? super K>, V> {

    private static final int DEFAULT_BRANCHING_FACTOR = 128;

    private final int branchingFactor;
    private Node root;
    Predicate<K> isKeyExists = key -> root.getValue(key) != null;
    private int numberOfLeaves = 0;

    public BPlusTree() {
        this(DEFAULT_BRANCHING_FACTOR);
    }

    public BPlusTree(int branchingFactor) {
        if (branchingFactor <= 2)
            throw new IllegalArgumentException("Illegal branching factor: " + branchingFactor);
        this.branchingFactor = branchingFactor;
        root = new LeafNode();
    }

    public V search(K key) {
        return root.getValue(key);
    }

    public void insert(K key, V value) {
        if (!isKeyExists.test(key)) {
            if (getNumberOfLeaves() == TABLE_MAX_SIZE) {
                throw new TableException("Can not insert more values, table row limit [ " + TABLE_MAX_SIZE + " ] exceeded.");
            }
            setNumberOfLeaves(getNumberOfLeaves() + 1);
        }
        root.insertValue(key, value);
    }


    public void delete(K key) {
        root.deleteValue(key);
        setNumberOfLeaves(getNumberOfLeaves() - 1);
    }

    public List<V> getTableRowsWithWhereCondition(BiPredicate<Value, Value> whereFilter, Column whereColumn, Value whereValue) {
        Queue<List<Node>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(root));
        List<V> data = new ArrayList<>();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                for (Node node : nodes) {
                    if (node instanceof BPlusTree.LeafNode) {

                        data.addAll(node.printNode().stream().filter(v -> {
                            DataRow row = (DataRow) v;

                            Value column = row.getTableRowMap().get(whereColumn.getColumnName());
                            if (column == null) return false;
                            return whereFilter.test(column, whereValue);
                        }).collect(Collectors.toList()));
                    }

                    if (node instanceof BPlusTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
            }
            queue = nextQueue;
        }
        return data;
    }

    public List<V> getTableRows() {
        Queue<List<Node>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(root));
        List<V> data = new ArrayList<>();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                for (Node node : nodes) {
                    if (node instanceof BPlusTree.LeafNode) {
                        data.addAll(node.printNode());
                    }
                    if (node instanceof BPlusTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
            }
            queue = nextQueue;
        }
        return data;
    }

    public List<V> getSpecificColumnsRows(List<String> columns) {
        Queue<List<Node>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(root));
        List<V> data = new ArrayList<>();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                for (Node node : nodes) {
                    if (node instanceof BPlusTree.LeafNode) {
                        List<V> temp = node.printNode();
                        //list of nodes
                        List<V> z = temp.stream().map(getRowFromColumnsList(columns)).collect(Collectors.toList());
                        data.addAll(z);

                    }
                    if (node instanceof BPlusTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
            }
            queue = nextQueue;
        }
        return data;
    }

    public List<V> getListOfValueForColumns(List<String> columns, BiPredicate<Value, Value> whereFilter, Column whereColumn, Value whereValue) {
        Queue<List<Node>> queue = new LinkedList<>();
        queue.add(Collections.singletonList(root));
        List<V> data = new ArrayList<>();
        while (!queue.isEmpty()) {
            Queue<List<Node>> nextQueue = new LinkedList<>();
            while (!queue.isEmpty()) {
                List<Node> nodes = queue.remove();
                for (Node node : nodes) {
                    if (node instanceof BPlusTree.LeafNode) {
                        data.addAll(node.printNode().stream().filter(v -> {
                                    DataRow row = (DataRow) v;
                                    return whereFilter.test(row.getTableRowMap().get(whereColumn.getColumnName()), whereValue);
                                }).map(getRowFromColumnsList(columns)).collect(Collectors.toList())
                        );
                    }
                    if (node instanceof BPlusTree.InternalNode)
                        nextQueue.add(((InternalNode) node).children);
                }
            }
            queue = nextQueue;
        }
        return data;
    }

    private Function<V, V> getRowFromColumnsList(List<String> columns) {
        return n -> {
            DataRow row = (DataRow) n;
            Map<String, Value> rowMap = row.getTableRowMap();
            DataRow ret = new DataRow();
            columns.forEach(c ->
                    ret.mapValueDataToTableColumn(rowMap.get(c), c)
            );
            return (V) ret;
        };
    }

    public int getNumberOfLeaves() {
        return numberOfLeaves;
    }

    public void setNumberOfLeaves(int numberOfLeaves) {
        this.numberOfLeaves = numberOfLeaves;
    }

    public abstract class Node {
        List<K> keys;

        int keyNumber() {
            return keys.size();
        }

        abstract V getValue(K key);

        abstract void deleteValue(K key);

        abstract void insertValue(K key, V value);

        abstract K getFirstLeafKey();

        abstract void merge(Node sibling);

        abstract Node split();

        abstract boolean isOverflow();

        abstract boolean isUnderflow();

        public String toString() {
            return keys.toString();
        }

        public List<V> printNode() {
            return keys.stream().map(BPlusTree.this::search).collect(Collectors.toList());
        }
    }

    private class InternalNode extends Node {
        List<Node> children;

        InternalNode() {
            this.keys = new ArrayList<>();
            this.children = new ArrayList<>();
        }

        @Override
        V getValue(K key) {
            return getChild(key).getValue(key);
        }

        @Override
        void deleteValue(K key) {
            Node child = getChild(key);
            synchronized (root.getValue(key)) {
                child.deleteValue(key);
                if (child.isUnderflow()) {
                    Node childLeftSibling = getChildLeftSibling(key);
                    Node childRightSibling = getChildRightSibling(key);
                    Node left = childLeftSibling != null ? childLeftSibling : child;
                    Node right = childLeftSibling != null ? child
                            : childRightSibling;
                    left.merge(right);
                    if (right != null) {
                        deleteChild(right.getFirstLeafKey());
                    }
                    if (left.isOverflow()) {
                        Node sibling = left.split();
                        insertChild(sibling.getFirstLeafKey(), sibling);
                    }
                    if (root.keyNumber() == 0)
                        root = left;
                }
            }
        }

        @Override
        void insertValue(K key, V value) {
            Node child = getChild(key);
            child.insertValue(key, value);
            if (child.isOverflow()) {
                Node sibling = child.split();
                insertChild(sibling.getFirstLeafKey(), sibling);
            }
            if (root.isOverflow()) {
                Node sibling = split();
                InternalNode newRoot = new InternalNode();
                newRoot.keys.add(sibling.getFirstLeafKey());
                newRoot.children.add(this);
                newRoot.children.add(sibling);
                root = newRoot;
            }
        }

        @Override
        K getFirstLeafKey() {
            return children.get(0).getFirstLeafKey();
        }


        @Override
        void merge(Node sibling) {

            InternalNode node = (InternalNode) sibling;
            keys.add(node.getFirstLeafKey());
            keys.addAll(node.keys);
            children.addAll(node.children);

        }

        @Override
        Node split() {
            int from = keyNumber() / 2 + 1, to = keyNumber();
            InternalNode sibling = new InternalNode();
            sibling.keys.addAll(keys.subList(from, to));
            sibling.children.addAll(children.subList(from, to + 1));

            keys.subList(from - 1, to).clear();
            children.subList(from, to + 1).clear();

            return sibling;
        }

        @Override
        boolean isOverflow() {
            return children.size() > branchingFactor;
        }

        @Override
        boolean isUnderflow() {
            return children.size() < (branchingFactor + 1) / 2;
        }

        Node getChild(K key) {
            int loc = Collections.binarySearch(keys, key);
            int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
            return children.get(childIndex);
        }

        void deleteChild(K key) {
            int loc = Collections.binarySearch(keys, key);
            if (loc >= 0) {
                keys.remove(loc);
                children.remove(loc + 1);
            }
        }

        void insertChild(K key, Node child) {
            int loc = Collections.binarySearch(keys, key);
            int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
            if (loc >= 0) {
                children.set(childIndex, child);
            } else {
                keys.add(childIndex, key);
                children.add(childIndex + 1, child);
            }
        }

        Node getChildLeftSibling(K key) {
            int loc = Collections.binarySearch(keys, key);
            int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
            if (childIndex > 0)
                return children.get(childIndex - 1);

            return null;
        }

        Node getChildRightSibling(K key) {
            int loc = Collections.binarySearch(keys, key);
            int childIndex = loc >= 0 ? loc + 1 : -loc - 1;
            if (childIndex < keyNumber())
                return children.get(childIndex + 1);

            return null;
        }
    }

    private class LeafNode extends Node {
        List<V> values;
        LeafNode next;

        LeafNode() {
            keys = new ArrayList<>();
            values = new ArrayList<>();
        }

        @Override
        V getValue(K key) {
            int loc = Collections.binarySearch(keys, key);
            return loc >= 0 ? values.get(loc) : null;
        }

        @Override
        void deleteValue(K key) {
            int loc = Collections.binarySearch(keys, key);
            if (loc >= 0) {
                keys.remove(loc);
                values.remove(loc);
            }
        }

        @Override
        void insertValue(K key, V value) {
            int loc = Collections.binarySearch(keys, key);
            int valueIndex = loc >= 0 ? loc : -loc - 1;
            if (loc >= 0) {
                values.set(valueIndex, value);

            } else {
                keys.add(valueIndex, key);
                values.add(valueIndex, value);
            }
            if (root.isOverflow()) {
                Node sibling = split();
                InternalNode newRoot = new InternalNode();
                newRoot.keys.add(sibling.getFirstLeafKey());
                newRoot.children.add(this);
                newRoot.children.add(sibling);
                root = newRoot;
            }
        }

        @Override
        K getFirstLeafKey() {
            return keys.get(0);
        }

        @Override
        void merge(Node sibling) {

            LeafNode node = (LeafNode) sibling;
            keys.addAll(node.keys);
            values.addAll(node.values);
            next = node.next;
        }

        @Override
        Node split() {
            LeafNode sibling = new LeafNode();
            int from = (keyNumber() + 1) / 2, to = keyNumber();
            sibling.keys.addAll(keys.subList(from, to));
            sibling.values.addAll(values.subList(from, to));

            keys.subList(from, to).clear();
            values.subList(from, to).clear();

            sibling.next = next;
            next = sibling;
            return sibling;
        }

        @Override
        boolean isOverflow() {
            return values.size() > branchingFactor - 1;
        }

        @Override
        boolean isUnderflow() {
            return values.size() < branchingFactor / 2;
        }
    }
}