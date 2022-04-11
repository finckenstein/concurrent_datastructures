package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import data_structures.Sorted;

public class CoarseGrainedTree<T extends Comparable<T>> implements Sorted<T> {
    volatile private Node root = null;
    volatile private Lock lock = new ReentrantLock();

    private class Node{
        private volatile Node parent;
        private volatile Node left;
        private volatile Node right;
        private volatile T element;

        Node(T data){
            element = data;
            parent = left = right = null;
        }
    }

    Node locationOfNodeToAdd(T elementToAdd){
        Node currentNode, parentNode;
        currentNode = parentNode = root;

        while(currentNode != null) {
            parentNode = currentNode;
            if(elementToAdd.compareTo(currentNode.element) < 0){ //t is smaller than current.element, GO LEFT
                currentNode = currentNode.left;
            }
            else{ //t is bigger than current.element, GO RIGHT
                currentNode = currentNode.right;
            }
        }
        return parentNode;
    }

    public void add(T t) {
        lock.lock();
        try{
            Node newNode = new Node(t);
            Node currentNode;

            if(root == null){
                root = newNode;
            }
            else{
                newNode.parent = currentNode = locationOfNodeToAdd(t);
                if(t.compareTo(currentNode.element) > 0){
                    currentNode.right = newNode;
                }
                else if(t.compareTo(currentNode.element) < 0){
                    currentNode.left = newNode;
                }
            }
        }
        finally{
            lock.unlock();
        }
    }

    private void linkNewChildWithParent(Node oldNode, Node newChild){ //find parents of toBeReplaced node
        if(newChild != null){
            newChild.parent = oldNode.parent;
        }
        if(root.element.compareTo(oldNode.element) == 0){
            root = newChild;
        }
        else if(oldNode.parent.left == oldNode){
            oldNode.parent.left = newChild;
        }
        else if(oldNode.parent.right == oldNode){
            oldNode.parent.right = newChild;
        }
    }

    private Node locationOfNodeToRemove(T elementToRemove){
        Node tempNode = root;
        while(tempNode != null && tempNode.element.compareTo(elementToRemove) != 0){
            if(elementToRemove.compareTo(tempNode.element) < 0){ //t is smaller than current.element
                tempNode = tempNode.left;
            }
            else { //t is bigger than current.element
                tempNode = tempNode.right;
            }
        }
        return tempNode;
    }

    private Node minimumNode(Node tempNode){
        while(tempNode.left != null){
            tempNode = tempNode.left;
        }
        return tempNode;
    }

    private void linkChildren(Node oldNodeChild, Node newNodeChild, Node newNode){
        newNodeChild = oldNodeChild;
        oldNodeChild.parent = newNode;
    }

    public void remove(T t) {
        lock.lock();
        try{
            Node currentNode = locationOfNodeToRemove(t);
            if(currentNode == null){
                return;
            }
            if(currentNode.left == null){
                linkNewChildWithParent(currentNode, currentNode.right);
            }
            else if(currentNode.right == null){
                linkNewChildWithParent(currentNode, currentNode.left);
            }
            else{
                Node successor = minimumNode(currentNode.right);

                linkNewChildWithParent(currentNode, successor);
                linkChildren(currentNode.left, successor.left, successor);

                if(currentNode.right != successor){
                    linkNewChildWithParent(successor, successor.right);
                    linkChildren(currentNode.right, successor.right, successor);
                }
            }
        }
        finally{
            lock.unlock();
        }
    }

    private ArrayList<T> inOrderTraversal(Node currentNode, ArrayList<T> binary_tree){
        if(currentNode !=  null){
            inOrderTraversal(currentNode.left, binary_tree);
            binary_tree.add(currentNode.element);
            inOrderTraversal(currentNode.right, binary_tree);
        }
        return binary_tree;
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> binary_tree = new ArrayList<>();
        return inOrderTraversal(root, binary_tree);
    }
}
