package geoguard.geoguard;

import java.io.Serializable;

/**
 * Created by monca on 10/21/2015.
 */
public class Node implements Serializable{
    private Node previous;
    private Node next;
    private String name;
    private String password;

    public Node(String name, String password) {
        next = previous = null;
        this.password = password;
        this.name = name;
    }
    public Node getNext() {
        return next;
    }
    public Node getPrevious() {
        return previous;
    }
    public Node(String name, String password, Node previous, Node next) {
        this.password = password;
        this.name = name;
        this.previous = previous;
        this.next = next;
    }
    public String getName() {
        return name;
    }
    public String getValue() {return password;}
    public void setPassword(String password) {
        this.password = password;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setPrevious(Node previous) {
        this.previous = previous;
    }
    public void setNext(Node next) {
        this.next = next;
    }
}
