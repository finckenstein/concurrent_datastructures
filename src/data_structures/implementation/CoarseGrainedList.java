package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import data_structures.Sorted;

public class CoarseGrainedList<T extends Comparable<T>> implements Sorted<T> {

    volatile private Node head = null;
    volatile private Lock lock = new ReentrantLock();

    private class Node{
        volatile Node next;
        volatile T element;

        Node(T data){
            element = data;
            next = null;
        }
    }

    public void add(T t) {
        Node currentNode, previousNode;
        Node newNode = new Node(t);
        lock.lock();
        try{
            currentNode = previousNode = head;
            if(head == null){
                head = newNode;
            }
            else if(head.element.compareTo(t) >= 0){
                newNode.next = head;
                head = newNode;
            }
            else{
                while(currentNode.element.compareTo(t) <= 0 && currentNode.next != null){
                    previousNode = currentNode;
                    currentNode = currentNode.next;
                }
                if(currentNode.element.compareTo(t) >= 0){
                    previousNode.next = newNode;
                    newNode.next = currentNode;
                }
                else if(currentNode.next == null){
                    currentNode.next = newNode;
                }
            }
        }
        finally{
            lock.unlock();
        }
    }

    public void remove(T t) {
        Node currentNode, previousNode;
        lock.lock();
        try{
            previousNode = currentNode = head;
            while(currentNode.element.compareTo(t) < 0 && currentNode.next != null){
                previousNode = currentNode;
                currentNode = currentNode.next;
            }
            if(currentNode == head && head.next != null){
                head = currentNode.next;
            }
            else if(currentNode == head){
                head = null;
            }
            else if(currentNode.element.compareTo(t) == 0){
                previousNode.next = currentNode.next;
            }
        }
        finally{
            lock.unlock();
        }
    }

    public ArrayList<T> toArrayList() {
        ArrayList<T> linkedList = new ArrayList<>();
        Node tempNode = head;

        if(head == null){
            return linkedList;
        }
        while(tempNode.next != null){
            linkedList.add(tempNode.element);
            tempNode = tempNode.next;
        }
        linkedList.add(tempNode.element);
        return linkedList;
    }
}