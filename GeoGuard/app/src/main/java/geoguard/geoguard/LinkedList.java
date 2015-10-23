package geoguard.geoguard;

/**
 * Created by monca on 10/21/2015.
 */
import java.io.Serializable;

import geoguard.geoguard.Node;
public class LinkedList implements Serializable {
    private Node head;
    private Node tail;
    private Node iterator;
    public LinkedList(String name, String password) {
        head = tail = new Node(name, password);
        iterator = null;
    }
    public LinkedList() {
        head = tail = iterator = null;
    }
    public boolean isEmpty() {
        if (head == null) {
            return true;
        }
        return false;
    }
    public Node getHead() {
        return head;
    }
    public void add(String name, String password) {
        Node newNode = new Node(name, password);
        if(head == null) {
            head = tail = newNode;
            reset();
            return;
        } else if(name.compareToIgnoreCase(head.getName()) <= 0) {
            newNode.setNext(head);
            head.setPrevious(newNode);
            head = newNode;
        } else {
            Node after = head.getNext();
            Node before = head;
            while (after != null) {
                if(name.compareToIgnoreCase(after.getName()) <= 0) {
                    break;
                }
                before = after;
                after = after.getNext();
            }
            newNode.setNext(before.getNext());
            newNode.setPrevious(before);
            if(after == null) {
                tail = newNode;
            }
            else {
                newNode.setNext(after);
                after.setPrevious(newNode);
            }
            before.setNext(newNode);

        }
    }
    public String toString() {
        Node temp = head;
        String output = "";
        while (temp != null) {
            output = output + "(" + temp.getName() + " " + temp.getValue() + ")";
            temp = temp.getNext();
        }
        return output;
    }
    public boolean contains(String compName) {
        Node n = head;
        while (n != null) {
            if(n.getName().equals(compName)) {
                return true;
            }
            n = n.getNext();
        }
        return false;
    }
    public Node next() {
        if(iterator != null ) {
            Node n = iterator;
            iterator = iterator.getNext();
            return n;
        } else return null;
    }
    public void reset() {
        iterator = head;
    }
}
