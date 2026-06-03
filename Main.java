public class Main {

    public static void main(String[] args) {

        LinkedList list = new LinkedList();

        System.out.println("=== Building the list ===");
        list.addLast(10);
        list.addLast(20);
        list.addLast(30);
        list.addFirst(5);
        list.addAt(2, 15);   // insert 15 at index 2
        list.print();        // head → [5] → [10] → [15] → [20] → [30] → null

        System.out.println("\n=== Search ===");
        System.out.println("contains(15): " + list.contains(15));  // true
        System.out.println("contains(99): " + list.contains(99));  // false

        System.out.println("\n=== Get by index ===");
        System.out.println("get(0): " + list.get(0));  // 5
        System.out.println("get(2): " + list.get(2));  // 15

        System.out.println("\n=== Remove operations ===");
        list.removeFirst();        // removes 5
        list.removeLast();         // removes 30
        list.remove(15);           // removes 15 by value
        list.print();              // head → [10] → [20] → null

        System.out.println("\n=== Reverse ===");
        list.reverse();
        list.print();              // head → [20] → [10] → null

        System.out.println("\nSize: " + list.size());
    }
}
