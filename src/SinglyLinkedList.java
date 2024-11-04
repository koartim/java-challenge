import java.util.ArrayList;
import java.util.Objects;

public class SinglyLinkedList {
    public Node head;
    public Node tail;
    public int length;

    public SinglyLinkedList() {
        this.head = null;
        this.tail = null;
        this.length = 0;
    }

// adds a new node to the end of the list
    public void push(int val) {
        Node newNode = new Node(val);
        // if we have no head, the list is empty, and the newNode becomes both the head and the tail
        if (Objects.isNull(this.head)) {
            this.head = newNode;
            this.tail = this.head;
        } else {
            // otherwise push the newNode onto the end by setting tail.next
            this.tail.next = newNode;
            // set the newNode to be the new tail
            this.tail = newNode;
        }
        // increase the length to account for the new node;
        this.length++;
    }

// removes the last node in the list
    public Node pop() {
        if (Objects.isNull(this.head)) return null; // if the list is empty, return null
        Node current = this.head; // start traversing from the head
        Node newTail = current; // keep track of the node before the last node
        // while there is a node next to us, we are not end at the end of the list
        while (!Objects.isNull(current.next)) {
            newTail = current; // current node becomes the new tail
            current = current.next; // move to the next node
        }
        // when current.next is null we have reached the end of the list
        this.tail = newTail; // update to the new tail
        this.tail.next = null; // set the tail's next to null, signifying the end of the list
        this.length--; // decrement the length to reflect the removal of the old tail
// if the length is now 0, the tail is empty, and we need to reset the head and tail to null
        if (this.length == 0) {
            this.head = null;
            this.tail = null;
        }
        return current; // return the removed node
    }
// removes a node from the front of the list
    public Node shift() {
        if (Objects.isNull(this.head)) return null; // if the list is empty, return null
        // since we are removing the head, we need to set the new head and update its next node
        Node current = this.head;
        this.head = current.next; // update head to the next node
        this.length--; // decrement, because we are removing a node
        if (this.length == 0) { // if the list is now empty, reset the tail and head
            this.tail = null;
            this.head = null;
        }
        return current; // return the removed node
    }
// adds a node to the beginning of the list
    public void unshift(int val) {
        Node newNode = new Node(val);
        if (Objects.isNull(this.head)) { // if the list is empty...
            this.head = newNode; // set the newNode to be the head
            this.tail = this.head; // as well as the tail
        }
        newNode.next = this.head; // disconnect the old head by setting it to be the value of newNode.next
        this.head = newNode; // update the head to be the new node
        this.length++; // increment because we added a new node
    }
// gets a node at a specific index
    public Node get(int idx) {
        // handle out of bounds index inputs
        if (idx < 0 || idx >= this.length) {
            return null;
        }
        int counter = 0; // init counter for tracking position
        Node current = this.head; // being traversal at the head
        while (counter != idx) { // traverse until we reach the index
            current = current.next; // move to the next node
            counter++; // increment counter
        }
        return current; // return the node once we reach the specified index
    }
// sets a node in place at the specified index
    public void set(int idx, int val) {
        Node foundNode = this.get(idx);
        if (!Objects.isNull(foundNode)) {
            foundNode.val = val;
        }
    }
// inserts a node at a specified index
    public boolean insert(int idx, int val) {
        // check for out of bounds exceptions
        if (idx < 0 || idx >= this.length) return false;
        // if the idx is equal to the list, use push
        if (idx == this.length) {
            this.push(val);
            return true;
        };
        if (idx == 0) {
            // if the idx is 0, we are at the start and we can use unshift
            this.unshift(val);
            return true;
        };
        // create the node to be inserted
        Node newNode = new Node(val);
        // get the node directly behind where it is to be inserted
        Node prev = this.get(idx - 1);
        // store the next node in a temp variable
        Node temp = prev.next;
        // link the prev node to the new node
        prev.next = newNode;
        // link the newNode to the next node
        newNode.next = temp;
        // increment the length
        this.length++;
        // return true to signify that the node was successfully inserted
        return true;
    }
// removes a node at a specified index
    public Node remove(int idx) {
        // check if our index is inbounds
        if (idx < 0 || idx >= this.length) return null;
        // if idx is 0, we are at the beginning and can use shift
        if (idx == 0) {
            return this.shift();
        }
        // if idx is the length of the array, we are at the end, use pop
        if (idx == this.length - 1) {
            return this.pop();
        }
        // otherwise get the value just before where we want to insert
        Node prev = this.get(idx - 1);
        // prev.next is the node we want to remove
        Node removed = prev.next;
        // link the prev node to the node after the node we are removing
        prev.next = removed.next;
        // decrement the length
        this.length--;
        // return the removed node
        return removed;
    }
// reverses the linkedList in place
    public SinglyLinkedList reverse() {
        Node node = this.head; // begin with the head
        this.head = this.tail; // swap the head and tail
        this.tail = node; // assing node as the new tail
        Node next;
        Node prev = null; // init prev node as null
        for (int i = 0; i < this.length; i++) {
            next = node.next; // store next as a temp node
            node.next = prev; // reverse the current node's pointer
            prev = node; // move prev forward
            node = next; // move node forward
        }
        return this; // return the reversed list
    }

    public void printList() {
        ArrayList arr = new ArrayList();
        Node current = this.head; // start from the head
        while (current != null) {// while there is a node, traverse
            arr.add(current.val); // add each node to our array
            current = current.next; // move to the next node
        }
        System.out.println(arr);
    }

    @Override
    public String toString() {
        return "SinglyLinkedList{head='" + head.val + "', tail=" + tail.val + ", length=" + length + '}';
    }

}
