import java.util.Collections;
import java.util.LinkedList;
import java.util.ListIterator;

public class LinkedListController {

    // ── Callback interface — UI implements this ───
    public interface Callback {
        void onStatusOk(String message);
        void onStatusError(String message);
        void onStatusInfo(String message);
        void onListChanged();
        void onHighlight(int index);
        void onIteratorMoved(int index);
        void onIteratorButtonsChanged(boolean hasNext, boolean hasPrevious,
                                      boolean canModify, boolean isActive);
    }

    // ── State ─────────────────────────────────────
    private final LinkedList<Integer> list     = new LinkedList<>();
    private ListIterator<Integer>     iterator = null;
    private int                       iteratorIndex = -1;
    private final Callback            cb;

    // ── Constructor ───────────────────────────────
    public LinkedListController(Callback cb) {
        this.cb = cb;
        list.addLast(12);
        list.addLast(37);
        list.addLast(55);
    }

    // ── Read-only list access for the canvas ──────
    public LinkedList<Integer> getList() {
        return list;
    }

    public boolean isIteratorActive() {
        return iterator != null;
    }

    public int getIteratorIndex() {
        return iteratorIndex;
    }

    // ── Add ───────────────────────────────────────
    public void addFirst(int value) {
        resetIterator();
        list.addFirst(value);
        cb.onStatusOk("addFirst(" + value + ")");
        cb.onListChanged();
    }

    public void addLast(int value) {
        resetIterator();
        list.addLast(value);
        cb.onStatusOk("addLast(" + value + ")");
        cb.onListChanged();
    }

    public void addAt(int index, int value) {
        try {
            resetIterator();
            list.add(index, value);
            cb.onStatusOk("add(" + index + ", " + value + ")");
            cb.onListChanged();
        } catch (IndexOutOfBoundsException e) {
            cb.onStatusError("Index out of bounds: " + e.getMessage());
        }
    }

    // ── Remove ────────────────────────────────────
    public void removeFirst() {
        if (list.isEmpty()) { cb.onStatusError("List is empty"); return; }
        resetIterator();
        int v = list.removeFirst();
        cb.onStatusOk("removeFirst() → " + v);
        cb.onListChanged();
    }

    public void removeLast() {
        if (list.isEmpty()) { cb.onStatusError("List is empty"); return; }
        resetIterator();
        int v = list.removeLast();
        cb.onStatusOk("removeLast() → " + v);
        cb.onListChanged();
    }

    public void removeValue(int value) {
        resetIterator();
        boolean found = list.remove(Integer.valueOf(value));
        if (found) { cb.onStatusOk("remove(" + value + ") — removed"); cb.onListChanged(); }
        else         cb.onStatusError("remove(" + value + ") — not found");
    }

    // ── Query ─────────────────────────────────────
    public void contains(int value) {
        boolean found = list.contains(value);
        if (found) cb.onStatusOk("contains(" + value + ") → true");
        else       cb.onStatusError("contains(" + value + ") → false");
        cb.onHighlight(list.indexOf(value));
    }

    public void get(int index) {
        try {
            int v = list.get(index);
            cb.onStatusOk("get(" + index + ") → " + v);
            cb.onHighlight(index);
        } catch (IndexOutOfBoundsException e) {
            cb.onStatusError("Index out of bounds: " + e.getMessage());
        }
    }

    // ── List operations ───────────────────────────
    public void reverse() {
        resetIterator();
        Collections.reverse(list);
        cb.onStatusOk("Collections.reverse(list)");
        cb.onListChanged();
    }

    public void clear() {
        list.clear();
        resetIterator();
        cb.onStatusOk("list.clear()");
        cb.onListChanged();
    }

    // ── Iterator ──────────────────────────────────
    public void iterStart() {
        if (list.isEmpty()) { cb.onStatusError("List is empty"); return; }
        iterator = list.listIterator(0);
        iteratorIndex = -1;
        cb.onStatusInfo("listIterator(0) — cursor before index 0");
        cb.onIteratorMoved(iteratorIndex);
        notifyIterButtons();
    }

    public void iterNext() {
        if (iterator == null || !iterator.hasNext()) {
            cb.onStatusError("No next element"); return;
        }
        int v = iterator.next();
        iteratorIndex = iterator.previousIndex();
        cb.onStatusInfo("iterator.next() → " + v + "  (index " + iteratorIndex + ")");
        cb.onIteratorMoved(iteratorIndex);
        notifyIterButtons();
    }

    public void iterPrevious() {
        if (iterator == null || !iterator.hasPrevious()) {
            cb.onStatusError("No previous element"); return;
        }
        int v = iterator.previous();
        iteratorIndex = iterator.nextIndex();
        cb.onStatusInfo("iterator.previous() → " + v + "  (index " + iteratorIndex + ")");
        cb.onIteratorMoved(iteratorIndex);
        notifyIterButtons();
    }

    public void iterSet(int value) {
        if (iterator == null || iteratorIndex < 0) {
            cb.onStatusError("Call next() or previous() before set()"); return;
        }
        iterator.set(value);
        cb.onStatusInfo("iterator.set(" + value + ") — replaced node at index " + iteratorIndex);
        cb.onListChanged();
    }

    public void iterRemove() {
        if (iterator == null || iteratorIndex < 0) {
            cb.onStatusError("Call next() or previous() before remove()"); return;
        }
        iterator.remove();
        cb.onStatusInfo("iterator.remove() — removed node at index " + iteratorIndex);
        iteratorIndex = Math.min(iteratorIndex, list.size() - 1);
        cb.onIteratorMoved(iteratorIndex < 0 ? -1 : iteratorIndex);
        cb.onListChanged();
        notifyIterButtons();
    }

    public void iterEnd() {
        resetIterator();
        cb.onStatusOk("Iterator ended");
        cb.onIteratorMoved(-1);
    }

    // ── Helpers ───────────────────────────────────
    private void resetIterator() {
        iterator = null;
        iteratorIndex = -1;
        notifyIterButtons();
    }

    private void notifyIterButtons() {
        boolean active      = iterator != null;
        boolean hasNext     = active && iterator.hasNext();
        boolean hasPrev     = active && iterator.hasPrevious();
        boolean canModify   = active && iteratorIndex >= 0;
        cb.onIteratorButtonsChanged(hasNext, hasPrev, canModify, active);
    }
}
