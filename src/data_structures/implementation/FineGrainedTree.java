package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import data_structures.Sorted;

public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {
    volatile private Node root = null;

    private class Node{
        private volatile Node parent;
        private volatile Node left;
        private volatile Node right;
        private volatile T element;
        volatile private Lock lock = new ReentrantLock();

        Node(T data){
            element = data;
            parent = left = right = null;
        }
    }

    Node locationOfNodeToAdd(T elementToAdd){
        Node currentNode, parentNode;
        currentNode = parentNode = root;

        while(currentNode != null) {
            currentNode.lock.lock();
            try {
                parentNode = currentNode;
                if (elementToAdd.compareTo(currentNode.element) < 0) { //t is smaller than current.element, GO LEFT
                    currentNode = currentNode.left;
                } else { //t is bigger than current.element, GO RIGHT
                    currentNode = currentNode.right;
                }
            }finally{
                parentNode.lock.unlock();
            }
        }
        return parentNode;
    }

    public void add(T t) {
        Node newNode = new Node(t);
        if(root == null){
            root = newNode;
        }
        else{

            Node currentNode;
            newNode.parent = currentNode = locationOfNodeToAdd(t);
            currentNode.lock.lock();
            try {
                if(t.compareTo(currentNode.element) > 0){
                    currentNode.right = newNode;
                }
                else if (t.compareTo(currentNode.element) < 0) {
                    currentNode.left = newNode;
                }
            }finally {
                currentNode.lock.unlock();
            }
        }
    }

    private Node locationOfNodeToRemove(T elementToRemove){
        Node tempNode = root;
        while(tempNode != null && tempNode.element.compareTo(elementToRemove) != 0){
            tempNode.lock.lock();
            if (elementToRemove.compareTo(tempNode.element) < 0) { //t is smaller than current.element
                tempNode.lock.unlock();
                tempNode = tempNode.left;
            } else { //t is bigger than current.element
                tempNode.lock.unlock();
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

    public void remove(T t) {
        Node currentNode = locationOfNodeToRemove(t);

        if(currentNode == null){
            return;
        }

        currentNode.lock.lock();
        if(currentNode.left == null && currentNode.right == null){
            linkNewChildWithParent(currentNode, null);
        }
        else if(currentNode.left == null){
            currentNode.right.lock.lock();
            linkNewChildWithParent(currentNode, currentNode.right);
            currentNode.right.lock.unlock();
        }
        else if(currentNode.right == null){
            currentNode.left.lock.lock();
            linkNewChildWithParent(currentNode, currentNode.left);
            currentNode.left.lock.unlock();
        }
        else{
            Node successor = minimumNode(currentNode.right);
            //successor.lock.lock();
            linkNewChildWithParent(currentNode, successor);
            linkChildren(currentNode.left, successor.left, successor);

            if(currentNode.right != successor){
                linkNewChildWithParent(successor, successor.right);
                linkChildren(currentNode.right, successor.right, successor);
            }
            //successor.lock.unlock();
        }
        currentNode.lock.unlock();
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
