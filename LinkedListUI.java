import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LinkedListUI extends JFrame {

    // ── State ─────────────────────────────────────
    private final LinkedList list = new LinkedList();

    // ── Panels ────────────────────────────────────
    private ListCanvas canvas;
    private JLabel statusLabel;

    // ── Input fields ──────────────────────────────
    private JTextField valueField;
    private JTextField indexField;

    // ── Colors ────────────────────────────────────
    static final Color BG         = new Color(0xF9F8F6);
    static final Color NODE_FILL  = new Color(0xE6F1FB);
    static final Color NODE_BORDER= new Color(0x185FA5);
    static final Color HEAD_FILL  = new Color(0xB5D4F4);
    static final Color NULL_COLOR = new Color(0xB4B2A9);
    static final Color ARROW_CLR  = new Color(0x185FA5);
    static final Color TEXT_MAIN  = new Color(0x2C2C2A);
    static final Color TEXT_MUT   = new Color(0x888780);
    static final Color PANEL_BG   = Color.WHITE;
    static final Color STATUS_OK  = new Color(0x0F6E56);
    static final Color STATUS_ERR = new Color(0xA32D2D);

    // ── Constructor ───────────────────────────────
    public LinkedListUI() {
        super("Linked List Visualizer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 520);
        setMinimumSize(new Dimension(700, 420));
        setLocationRelativeTo(null);
        getContentPane().setBackground(BG);
        setLayout(new BorderLayout(0, 0));

        add(buildControlPanel(), BorderLayout.WEST);
        add(buildCanvasPanel(),  BorderLayout.CENTER);
        add(buildStatusBar(),    BorderLayout.SOUTH);

        // seed with a few nodes so the canvas isn't empty on launch
        list.addLast(12);
        list.addLast(37);
        list.addLast(55);
        canvas.repaint();
    }

    // ── Control panel (left sidebar) ──────────────
    private JPanel buildControlPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(PANEL_BG);
        panel.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 0, 1, new Color(0xD3D1C7)),
            new EmptyBorder(20, 20, 20, 20)
        ));
        panel.setPreferredSize(new Dimension(220, 0));

        // ── Inputs ──
        panel.add(sectionLabel("Inputs"));
        panel.add(Box.createVerticalStrut(6));
        valueField = styledField("Value (int)");
        indexField = styledField("Index (optional)");
        panel.add(labeledField("Value", valueField));
        panel.add(Box.createVerticalStrut(8));
        panel.add(labeledField("Index", indexField));
        panel.add(Box.createVerticalStrut(16));

        // ── Add group ──
        panel.add(sectionLabel("Add"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Add to front",  e -> doAddFirst()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Add to back",   e -> doAddLast()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Add at index",  e -> doAddAt()));
        panel.add(Box.createVerticalStrut(16));

        // ── Remove group ──
        panel.add(sectionLabel("Remove"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Remove first",  e -> doRemoveFirst()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Remove last",   e -> doRemoveLast()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Remove value",  e -> doRemoveValue()));
        panel.add(Box.createVerticalStrut(16));

        // ── Query group ──
        panel.add(sectionLabel("Query"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Search value",  e -> doContains()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Get at index",  e -> doGet()));
        panel.add(Box.createVerticalStrut(16));

        // ── Misc group ──
        panel.add(sectionLabel("List"));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Reverse",       e -> doReverse()));
        panel.add(Box.createVerticalStrut(6));
        panel.add(actionButton("Clear",         e -> doClear()));

        panel.add(Box.createVerticalGlue());
        return panel;
    }

    // ── Canvas panel (right area) ─────────────────
    private JPanel buildCanvasPanel() {
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setBackground(BG);
        wrapper.setBorder(new EmptyBorder(24, 24, 12, 24));

        JLabel title = new JLabel("head →");
        title.setFont(new Font("Monospaced", Font.BOLD, 13));
        title.setForeground(ARROW_CLR);
        title.setBorder(new EmptyBorder(0, 0, 10, 0));

        canvas = new ListCanvas();
        JScrollPane scroll = new JScrollPane(canvas,
            JScrollPane.VERTICAL_SCROLLBAR_NEVER,
            JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setBackground(BG);
        scroll.getViewport().setBackground(BG);

        wrapper.add(title,  BorderLayout.NORTH);
        wrapper.add(scroll, BorderLayout.CENTER);
        return wrapper;
    }

    // ── Status bar (bottom) ───────────────────────
    private JPanel buildStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBackground(PANEL_BG);
        bar.setBorder(new CompoundBorder(
            new MatteBorder(1, 0, 0, 0, new Color(0xD3D1C7)),
            new EmptyBorder(6, 20, 6, 20)
        ));
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
        statusLabel.setForeground(TEXT_MUT);
        bar.add(statusLabel, BorderLayout.WEST);
        return bar;
    }

    // ── Actions ───────────────────────────────────
    private void doAddFirst() {
        Integer v = parseValue(); if (v == null) return;
        list.addFirst(v);
        setStatus("addFirst(" + v + ") — new head inserted", false);
        canvas.repaint();
    }

    private void doAddLast() {
        Integer v = parseValue(); if (v == null) return;
        list.addLast(v);
        setStatus("addLast(" + v + ") — appended to tail", false);
        canvas.repaint();
    }

    private void doAddAt() {
        Integer v = parseValue(); if (v == null) return;
        Integer idx = parseIndex(); if (idx == null) return;
        try {
            list.addAt(idx, v);
            setStatus("addAt(" + idx + ", " + v + ") — inserted at index " + idx, false);
            canvas.repaint();
        } catch (IndexOutOfBoundsException ex) {
            setStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void doRemoveFirst() {
        try {
            int v = list.removeFirst();
            setStatus("removeFirst() — removed " + v, false);
            canvas.repaint();
        } catch (RuntimeException ex) {
            setStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void doRemoveLast() {
        try {
            int v = list.removeLast();
            setStatus("removeLast() — removed " + v, false);
            canvas.repaint();
        } catch (RuntimeException ex) {
            setStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void doRemoveValue() {
        Integer v = parseValue(); if (v == null) return;
        boolean found = list.remove(v);
        if (found) {
            setStatus("remove(" + v + ") — node removed", false);
            canvas.repaint();
        } else {
            setStatus("remove(" + v + ") — value not found", true);
        }
    }

    private void doContains() {
        Integer v = parseValue(); if (v == null) return;
        boolean found = list.contains(v);
        setStatus("contains(" + v + ") → " + found, !found);
        canvas.highlightValue(v);
    }

    private void doGet() {
        Integer idx = parseIndex(); if (idx == null) return;
        try {
            int v = list.get(idx);
            setStatus("get(" + idx + ") → " + v, false);
            canvas.highlightIndex(idx);
        } catch (IndexOutOfBoundsException ex) {
            setStatus("Error: " + ex.getMessage(), true);
        }
    }

    private void doReverse() {
        list.reverse();
        setStatus("reverse() — list reversed in place", false);
        canvas.repaint();
    }

    private void doClear() {
        while (!list.isEmpty()) list.removeFirst();
        setStatus("List cleared", false);
        canvas.repaint();
    }

    // ── Parsing helpers ───────────────────────────
    private Integer parseValue() {
        String s = valueField.getText().trim();
        if (s.isEmpty()) { setStatus("Error: enter a value", true); return null; }
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { setStatus("Error: value must be an integer", true); return null; }
    }

    private Integer parseIndex() {
        String s = indexField.getText().trim();
        if (s.isEmpty()) { setStatus("Error: enter an index", true); return null; }
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { setStatus("Error: index must be an integer", true); return null; }
    }

    private void setStatus(String msg, boolean error) {
        statusLabel.setText(msg);
        statusLabel.setForeground(error ? STATUS_ERR : STATUS_OK);
    }

    // ── UI helpers ────────────────────────────────
    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text.toUpperCase());
        l.setFont(new Font("SansSerif", Font.BOLD, 10));
        l.setForeground(TEXT_MUT);
        l.setAlignmentX(LEFT_ALIGNMENT);
        return l;
    }

    private JTextField styledField(String placeholder) {
        JTextField f = new JTextField();
        f.setFont(new Font("Monospaced", Font.PLAIN, 13));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
        f.setBorder(new CompoundBorder(
            new LineBorder(new Color(0xB4B2A9), 1, true),
            new EmptyBorder(4, 8, 4, 8)
        ));
        return f;
    }

    private JPanel labeledField(String labelText, JTextField field) {
        JPanel p = new JPanel(new BorderLayout(0, 4));
        p.setBackground(PANEL_BG);
        p.setAlignmentX(LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 52));
        JLabel l = new JLabel(labelText);
        l.setFont(new Font("SansSerif", Font.PLAIN, 11));
        l.setForeground(TEXT_MUT);
        p.add(l, BorderLayout.NORTH);
        p.add(field, BorderLayout.CENTER);
        return p;
    }

    private JButton actionButton(String text, ActionListener al) {
        JButton b = new JButton(text);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setForeground(TEXT_MAIN);
        b.setBackground(PANEL_BG);
        b.setBorder(new CompoundBorder(
            new LineBorder(new Color(0xB4B2A9), 1, true),
            new EmptyBorder(6, 12, 6, 12)
        ));
        b.setFocusPainted(false);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setAlignmentX(LEFT_ALIGNMENT);
        b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 34));
        b.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) { b.setBackground(new Color(0xF1EFE8)); }
            public void mouseExited(MouseEvent e)  { b.setBackground(PANEL_BG); }
        });
        b.addActionListener(al);
        return b;
    }

    // ── Canvas ────────────────────────────────────
    class ListCanvas extends JPanel {
        private static final int NODE_W = 80;
        private static final int NODE_H = 56;
        private static final int GAP    = 40;
        private static final int PAD_Y  = 30;

        private int highlightedIndex = -1;

        ListCanvas() {
            setBackground(BG);
        }

        void highlightValue(int value) {
            // find index of first match
            highlightedIndex = -1;
            for (int i = 0; i < list.size(); i++) {
                if (list.get(i) == value) { highlightedIndex = i; break; }
            }
            repaint();
            // clear highlight after 1.5s
            Timer t = new Timer(1500, e -> { highlightedIndex = -1; repaint(); });
            t.setRepeats(false); t.start();
        }

        void highlightIndex(int index) {
            highlightedIndex = (index >= 0 && index < list.size()) ? index : -1;
            repaint();
            Timer t = new Timer(1500, e -> { highlightedIndex = -1; repaint(); });
            t.setRepeats(false); t.start();
        }

        @Override
        public Dimension getPreferredSize() {
            int n = list.size();
            int w = Math.max(400, n * (NODE_W + GAP) + GAP + 60);
            return new Dimension(w, NODE_H + PAD_Y * 2);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int n = list.size();
            int totalW = n * NODE_W + Math.max(0, n - 1) * GAP;
            int startX = 20;
            int y = PAD_Y;

            if (n == 0) {
                g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
                g2.setColor(NULL_COLOR);
                g2.drawString("List is empty", startX, y + NODE_H / 2 + 5);
                return;
            }

            for (int i = 0; i < n; i++) {
                int x = startX + i * (NODE_W + GAP);
                boolean isHead = (i == 0);
                boolean isHighlighted = (i == highlightedIndex);

                // node background
                Color fill   = isHighlighted ? new Color(0xFAC775)
                             : isHead        ? HEAD_FILL
                             :                 NODE_FILL;
                Color border = isHighlighted ? new Color(0xBA7517)
                             : NODE_BORDER;

                RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, NODE_W, NODE_H, 10, 10);
                g2.setColor(fill);
                g2.fill(rect);
                g2.setColor(border);
                g2.setStroke(new BasicStroke(isHead ? 1.8f : 1f));
                g2.draw(rect);

                // divider between data and next fields
                int divX = x + NODE_W / 2;
                g2.setColor(border);
                g2.setStroke(new BasicStroke(0.8f));
                g2.drawLine(divX, y + 1, divX, y + NODE_H - 1);

                // field labels
                g2.setFont(new Font("SansSerif", Font.PLAIN, 9));
                g2.setColor(TEXT_MUT);
                g2.drawString("data", x + 8, y + 13);
                g2.drawString("next", divX + 6, y + 13);

                // data value
                String dataStr = String.valueOf(list.get(i));
                g2.setFont(new Font("Monospaced", Font.BOLD, 15));
                g2.setColor(TEXT_MAIN);
                FontMetrics fm = g2.getFontMetrics();
                int dataX = x + (NODE_W / 2 - fm.stringWidth(dataStr)) / 2;
                g2.drawString(dataStr, dataX, y + NODE_H / 2 + 8);

                // next pointer arrow or null indicator
                boolean isLast = (i == n - 1);
                int nextCX = divX + (NODE_W / 2) / 2;
                int nextCY = y + NODE_H / 2;
                if (isLast) {
                    // null symbol
                    g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
                    g2.setColor(NULL_COLOR);
                    FontMetrics fm2 = g2.getFontMetrics();
                    g2.drawString("∅", nextCX - fm2.stringWidth("∅") / 2, nextCY + 4);
                } else {
                    // small dot indicating pointer
                    g2.setColor(ARROW_CLR);
                    g2.fillOval(nextCX - 3, nextCY - 3, 6, 6);
                }

                // head label badge
                if (isHead) {
                    g2.setFont(new Font("SansSerif", Font.BOLD, 9));
                    g2.setColor(NODE_BORDER);
                    g2.drawString("HEAD", x + 3, y + NODE_H + 12);
                }

                // arrow between nodes
                if (!isLast) {
                    int arrowStartX = x + NODE_W + 2;
                    int arrowEndX   = x + NODE_W + GAP - 2;
                    int arrowY      = y + NODE_H / 2;
                    drawArrow(g2, arrowStartX, arrowY, arrowEndX, arrowY);
                }
            }

            // null sentinel box at end
            int nullX = startX + n * (NODE_W + GAP);
            g2.setColor(new Color(0xEEEDE6));
            g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER,
                10f, new float[]{4f, 3f}, 0f));
            g2.draw(new RoundRectangle2D.Float(nullX, y + NODE_H / 2 - 12, 38, 24, 6, 6));
            g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
            g2.setColor(NULL_COLOR);
            g2.drawString("null", nullX + 5, y + NODE_H / 2 + 5);
        }

        private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
            g2.setColor(ARROW_CLR);
            g2.setStroke(new BasicStroke(1.4f));
            g2.drawLine(x1, y1, x2, y2);
            // arrowhead
            int hs = 6;
            int[] xp = {x2, x2 - hs, x2 - hs};
            int[] yp = {y2, y2 - hs / 2, y2 + hs / 2};
            g2.fillPolygon(xp, yp, 3);
        }
    }
}
