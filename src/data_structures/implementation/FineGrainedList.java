package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data_structures.Sorted;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
    private class Node{
        volatile Node next;
        volatile T element;
        volatile boolean isDummy;
        volatile private Lock lock = new ReentrantLock();

        Node(T data, boolean dummy){
            isDummy = dummy;
            element = data;
            next = null;
        }
    }
    volatile private Node dummy_head;

    public FineGrainedList(){
        dummy_head = new Node(null, true);
        dummy_head.next = new Node(null, true);
    }

    public void add(T t) {
        Node currentNode, previousNode;
        Node newNode = new Node(t, false);
        dummy_head.lock.lock();
        previousNode = dummy_head;
        try {
            currentNode = previousNode.next;
            currentNode.lock.lock();
            try {
                while (!currentNode.isDummy && currentNode.element.compareTo(t) < 0) {
                    previousNode.lock.unlock();
                    previousNode = currentNode;
                    currentNode = currentNode.next;
                    currentNode.lock.lock();
                }
                previousNode.next = newNode;
                newNode.next = currentNode;
            } finally {
                currentNode.lock.unlock();
            }
        } finally{
            previousNode.lock.unlock();
        }
    }

    public void remove(T t) {
        Node currentNode;
        Node previousNode = null;
        dummy_head.lock.lock();
        try{
            previousNode = dummy_head;
            currentNode = previousNode.next;
            currentNode.lock.lock();
            try {
                while (!currentNode.isDummy && currentNode.element.compareTo(t) < 0) {
                    previousNode.lock.unlock();
                    previousNode = currentNode;
                    currentNode = currentNode.next;
                    currentNode.lock.lock();
                }
                previousNode.next = currentNode.next;
            }finally{
                currentNode.lock.unlock();
            }
        } finally{
            previousNode.lock.unlock();
        }
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> linkedList = new ArrayList<>();
        Node tempNode = dummy_head.next;

        while(tempNode.next != null){
            linkedList.add(tempNode.element);
            tempNode = tempNode.next;
        }
        return linkedList;
    }
}
