// ─────────────────────────────────────────────
//  Node.java  (inner class — see bottom of file)
// ─────────────────────────────────────────────

public class LinkedList {

    // ── Node class ──────────────────────────────
    static class Node {
        int data;
        Node next;

        Node(int data) {
            this.data = data;
            this.next = null;
        }
    }

    // ── LinkedList fields ────────────────────────
    private Node head;
    private int size;

    // ── Constructor ──────────────────────────────
    public LinkedList() {
        head = null;
        size = 0;
    }

    // ── Add to front — O(1) ──────────────────────
    public void addFirst(int data) {
        Node newNode = new Node(data);
        newNode.next = head;
        head = newNode;
        size++;
    }

    // ── Add to end — O(n) ────────────────────────
    public void addLast(int data) {
        Node newNode = new Node(data);
        if (head == null) {
            head = newNode;
        } else {
            Node curr = head;
            while (curr.next != null) {
                curr = curr.next;
            }
            curr.next = newNode;
        }
        size++;
    }

    // ── Insert at index — O(n) ───────────────────
    public void addAt(int index, int data) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == 0) {
            addFirst(data);
            return;
        }
        Node newNode = new Node(data);
        Node curr = head;
        for (int i = 0; i < index - 1; i++) {
            curr = curr.next;
        }
        newNode.next = curr.next;
        curr.next = newNode;
        size++;
    }

    // ── Remove first — O(1) ──────────────────────
    public int removeFirst() {
        if (head == null) throw new RuntimeException("List is empty");
        int val = head.data;
        head = head.next;
        size--;
        return val;
    }

    // ── Remove last — O(n) ───────────────────────
    public int removeLast() {
        if (head == null) throw new RuntimeException("List is empty");
        if (head.next == null) {
            int val = head.data;
            head = null;
            size--;
            return val;
        }
        Node curr = head;
        while (curr.next.next != null) {
            curr = curr.next;
        }
        int val = curr.next.data;
        curr.next = null;
        size--;
        return val;
    }

    // ── Remove by value — O(n) ───────────────────
    public boolean remove(int data) {
        if (head == null) return false;
        if (head.data == data) {
            head = head.next;
            size--;
            return true;
        }
        Node curr = head;
        while (curr.next != null) {
            if (curr.next.data == data) {
                curr.next = curr.next.next;
                size--;
                return true;
            }
            curr = curr.next;
        }
        return false;
    }

    // ── Search — O(n) ────────────────────────────
    public boolean contains(int data) {
        Node curr = head;
        while (curr != null) {
            if (curr.data == data) return true;
            curr = curr.next;
        }
        return false;
    }

    // ── Get by index — O(n) ──────────────────────
    public int get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        Node curr = head;
        for (int i = 0; i < index; i++) {
            curr = curr.next;
        }
        return curr.data;
    }

    // ── Reverse in place — O(n) ──────────────────
    public void reverse() {
        Node prev = null;
        Node curr = head;
        while (curr != null) {
            Node next = curr.next;
            curr.next = prev;
            prev = curr;
            curr = next;
        }
        head = prev;
    }

    // ── Size ─────────────────────────────────────
    public int size() {
        return size;
    }

    // ── Is empty ─────────────────────────────────
    public boolean isEmpty() {
        return head == null;
    }

    // ── Print — O(n) ─────────────────────────────
    public void print() {
        Node curr = head;
        System.out.print("head → ");
        while (curr != null) {
            System.out.print("[" + curr.data + "] → ");
            curr = curr.next;
        }
        System.out.println("null");
    }


}
