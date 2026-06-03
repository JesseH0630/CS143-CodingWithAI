import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;

public class LinkedListUI extends JFrame implements LinkedListController.Callback {

	// ── Controller ────────────────────────────────
	private final LinkedListController controller = new LinkedListController(this);

	// ── Panels ────────────────────────────────────
	private ListCanvas canvas;
	private JLabel statusLabel;

	// ── Input fields ──────────────────────────────
	private JTextField valueField;
	private JTextField indexField;

	// ── Iterator buttons (toggled by controller) ──
	private JButton btnIterStart;
	private JButton btnIterNext;
	private JButton btnIterPrev;
	private JButton btnIterSet;
	private JButton btnIterRemove;
	private JButton btnIterEnd;

	// ── Colors ────────────────────────────────────
	static final Color BG = new Color(0xF9F8F6);
	static final Color NODE_FILL = new Color(0xE6F1FB);
	static final Color NODE_BORDER = new Color(0x185FA5);
	static final Color HEAD_FILL = new Color(0xB5D4F4);
	static final Color ITER_FILL = new Color(0xEAF3DE);
	static final Color ITER_BORDER = new Color(0x3B6D11);
	static final Color HIGH_FILL = new Color(0xFAC775);
	static final Color HIGH_BORDER = new Color(0xBA7517);
	static final Color NULL_COLOR = new Color(0xB4B2A9);
	static final Color ARROW_CLR = new Color(0x185FA5);
	static final Color TEXT_MAIN = new Color(0x2C2C2A);
	static final Color TEXT_MUT = new Color(0x888780);
	static final Color PANEL_BG = Color.WHITE;
	static final Color STATUS_OK = new Color(0x0F6E56);
	static final Color STATUS_ERR = new Color(0xA32D2D);
	static final Color STATUS_INFO = new Color(0x185FA5);

	// ── Constructor ───────────────────────────────
	public LinkedListUI() {
		super("Linked List Visualizer — java.util.LinkedList");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(980, 560);
		setMinimumSize(new Dimension(750, 460));
		setLocationRelativeTo(null);
		getContentPane().setBackground(BG);
		setLayout(new BorderLayout(0, 0));

		add(buildControlPanel(), BorderLayout.WEST);
		add(buildCanvasPanel(), BorderLayout.CENTER);
		add(buildStatusBar(), BorderLayout.SOUTH);

		// Reflect initial seeded list
		onIteratorButtonsChanged(false, false, false, false);
		canvas.repaint();
	}

	// ════════════════════════════════════════════
	//  Callback interface — called by controller
	// ════════════════════════════════════════════

	@Override
	public void onStatusOk(String msg) {
		setStatus(msg, STATUS_OK);
	}

	@Override
	public void onStatusError(String msg) {
		setStatus(msg, STATUS_ERR);
	}

	@Override
	public void onStatusInfo(String msg) {
		setStatus(msg, STATUS_INFO);
	}

	@Override
	public void onListChanged() {
		canvas.repaint();
	}

	@Override
	public void onHighlight(int index) {
		canvas.setHighlight(index);
	}

	@Override
	public void onIteratorMoved(int index) {
		canvas.setIterator(index);
	}

	@Override
	public void onIteratorButtonsChanged(boolean hasNext, boolean hasPrev,
	                                     boolean canModify, boolean isActive) {
		btnIterStart.setEnabled(!isActive);
		btnIterNext.setEnabled(hasNext);
		btnIterPrev.setEnabled(hasPrev);
		btnIterSet.setEnabled(canModify);
		btnIterRemove.setEnabled(canModify);
		btnIterEnd.setEnabled(isActive);
	}

	// ════════════════════════════════════════════
	//  Layout builders
	// ════════════════════════════════════════════

	private JPanel buildControlPanel() {
		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(PANEL_BG);
		panel.setBorder(new CompoundBorder(
				new MatteBorder(0, 0, 0, 1, new Color(0xD3D1C7)),
				new EmptyBorder(16, 18, 16, 18)
		));
		panel.setPreferredSize(new Dimension(220, 0));

		// Inputs
		panel.add(sectionLabel("Inputs"));
		panel.add(Box.createVerticalStrut(6));
		valueField = styledField();
		indexField = styledField();
		panel.add(labeledField("Value (int)", valueField));
		panel.add(Box.createVerticalStrut(8));
		panel.add(labeledField("Index", indexField));
		panel.add(Box.createVerticalStrut(14));

		// Add
		panel.add(sectionLabel("Add"));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Add to front", e -> withValue(controller::addFirst)));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Add to back", e -> withValue(controller::addLast)));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Add at index", e -> withValueAndIndex(controller::addAt)));
		panel.add(Box.createVerticalStrut(14));

		// Remove
		panel.add(sectionLabel("Remove"));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Remove first", e -> controller.removeFirst()));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Remove last", e -> controller.removeLast()));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Remove value", e -> withValue(controller::removeValue)));
		panel.add(Box.createVerticalStrut(14));

		// Query
		panel.add(sectionLabel("Query"));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Search value", e -> withValue(controller::contains)));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Get at index", e -> withIndex(controller::get)));
		panel.add(Box.createVerticalStrut(14));

		// List
		panel.add(sectionLabel("List"));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Reverse", e -> controller.reverse()));
		panel.add(Box.createVerticalStrut(5));
		panel.add(actionButton("Clear", e -> controller.clear()));
		panel.add(Box.createVerticalStrut(14));

		// Iterator
		panel.add(sectionLabel("Iterator"));
		panel.add(Box.createVerticalStrut(5));
		btnIterStart = actionButton("▶  Start iterator", e -> controller.iterStart());
		btnIterNext = actionButton("→  Next", e -> controller.iterNext());
		btnIterPrev = actionButton("←  Previous", e -> controller.iterPrevious());
		btnIterSet = actionButton("✎  Set value", e -> withValue(controller::iterSet));
		btnIterRemove = actionButton("✕  Remove current", e -> controller.iterRemove());
		btnIterEnd = actionButton("■  End iterator", e -> controller.iterEnd());
		panel.add(btnIterStart);
		panel.add(Box.createVerticalStrut(5));
		panel.add(btnIterNext);
		panel.add(Box.createVerticalStrut(5));
		panel.add(btnIterPrev);
		panel.add(Box.createVerticalStrut(5));
		panel.add(btnIterSet);
		panel.add(Box.createVerticalStrut(5));
		panel.add(btnIterRemove);
		panel.add(Box.createVerticalStrut(5));
		panel.add(btnIterEnd);

		panel.add(Box.createVerticalGlue());
		return panel;
	}

	private JPanel buildCanvasPanel() {
		JPanel wrapper = new JPanel(new BorderLayout());
		wrapper.setBackground(BG);
		wrapper.setBorder(new EmptyBorder(24, 24, 12, 24));

		JLabel title = new JLabel("head \u2192");
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

		wrapper.add(title, BorderLayout.NORTH);
		wrapper.add(scroll, BorderLayout.CENTER);
		return wrapper;
	}

	private JPanel buildStatusBar() {
		JPanel bar = new JPanel(new BorderLayout());
		bar.setBackground(PANEL_BG);
		bar.setBorder(new CompoundBorder(
				new MatteBorder(1, 0, 0, 0, new Color(0xD3D1C7)),
				new EmptyBorder(6, 20, 6, 20)
		));
		statusLabel = new JLabel("Ready  \u2014  using java.util.LinkedList");
		statusLabel.setFont(new Font("Monospaced", Font.PLAIN, 12));
		statusLabel.setForeground(TEXT_MUT);
		bar.add(statusLabel, BorderLayout.WEST);
		return bar;
	}

	// ════════════════════════════════════════════
	//  Input helpers — parse fields, forward to controller
	// ════════════════════════════════════════════

	private void withValue(java.util.function.IntConsumer action) {
		Integer v = parseValue();
		if(v != null) action.accept(v);
	}

	private void withIndex(java.util.function.IntConsumer action) {
		Integer i = parseIndex();
		if(i != null) action.accept(i);
	}

	private void withValueAndIndex(java.util.function.BiConsumer<Integer,Integer> action) {
		Integer v = parseValue();
		if(v == null) return;
		Integer i = parseIndex();
		if(i == null) return;
		action.accept(i, v);
	}

	private Integer parseValue() {
		String s = valueField.getText().trim();
		if(s.isEmpty()) {
			onStatusError("Enter a value");
			return null;
		}
		try {
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e) {
			onStatusError("Value must be an integer");
			return null;
		}
	}

	private Integer parseIndex() {
		String s = indexField.getText().trim();
		if(s.isEmpty()) {
			onStatusError("Enter an index");
			return null;
		}
		try {
			return Integer.parseInt(s);
		}
		catch(NumberFormatException e) {
			onStatusError("Index must be an integer");
			return null;
		}
	}

	// ════════════════════════════════════════════
	//  UI component factories
	// ════════════════════════════════════════════

	private void setStatus(String msg, Color color) {
		statusLabel.setText(msg);
		statusLabel.setForeground(color);
	}

	private JLabel sectionLabel(String text) {
		JLabel l = new JLabel(text.toUpperCase());
		l.setFont(new Font("SansSerif", Font.BOLD, 10));
		l.setForeground(TEXT_MUT);
		l.setAlignmentX(LEFT_ALIGNMENT);
		return l;
	}

	private JTextField styledField() {
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
				new EmptyBorder(5, 10, 5, 10)
		));
		b.setFocusPainted(false);
		b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		b.setAlignmentX(LEFT_ALIGNMENT);
		b.setMaximumSize(new Dimension(Integer.MAX_VALUE, 32));
		b.addMouseListener(new MouseAdapter() {
			public void mouseEntered(MouseEvent e) {
				if(b.isEnabled()) b.setBackground(new Color(0xF1EFE8));
			}

			public void mouseExited(MouseEvent e) {
				b.setBackground(PANEL_BG);
			}
		});
		b.addActionListener(al);
		return b;
	}

	// ════════════════════════════════════════════
	//  Canvas — pure rendering, reads from controller
	// ════════════════════════════════════════════

	class ListCanvas extends JPanel {
		private static final int NODE_W = 80;
		private static final int NODE_H = 56;
		private static final int GAP = 40;
		private static final int PAD_Y = 30;

		private int highlightedIndex = -1;
		private int iterIndex = -1;

		ListCanvas() {
			setBackground(BG);
		}

		void setHighlight(int idx) {
			highlightedIndex = idx;
			repaint();
			Timer t = new Timer(1500, e -> {
				highlightedIndex = -1;
				repaint();
			});
			t.setRepeats(false);
			t.start();
		}

		void setIterator(int idx) {
			iterIndex = idx;
			repaint();
		}

		@Override
		public Dimension getPreferredSize() {
			int n = controller.getList().size();
			int w = Math.max(400, n * (NODE_W + GAP) + GAP + 70);
			return new Dimension(w, NODE_H + PAD_Y * 2 + 20);
		}

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			java.util.LinkedList<Integer> list = controller.getList();
			int n = list.size();
			int startX = 20, y = PAD_Y;

			if(n == 0) {
				g2.setFont(new Font("SansSerif", Font.ITALIC, 13));
				g2.setColor(NULL_COLOR);
				g2.drawString("List is empty", startX, y + NODE_H / 2 + 5);
				return;
			}

			Integer[] values = list.toArray(new Integer[0]);

			for(int i = 0; i < n; i++) {
				int x = startX + i * (NODE_W + GAP);
				boolean isHead = (i == 0);
				boolean isHighl = (i == highlightedIndex);
				boolean isIter = (i == iterIndex) && controller.isIteratorActive();

				Color fill, border;
				if(isHighl) {
					fill = HIGH_FILL;
					border = HIGH_BORDER;
				}
				else if(isIter) {
					fill = ITER_FILL;
					border = ITER_BORDER;
				}
				else if(isHead) {
					fill = HEAD_FILL;
					border = NODE_BORDER;
				}
				else {
					fill = NODE_FILL;
					border = NODE_BORDER;
				}

				// node
				RoundRectangle2D rect = new RoundRectangle2D.Float(x, y, NODE_W, NODE_H, 10, 10);
				g2.setColor(fill);
				g2.fill(rect);
				g2.setColor(border);
				g2.setStroke(new BasicStroke(isHead || isIter?1.8f:1f));
				g2.draw(rect);

				// divider
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
				String dataStr = String.valueOf(values[i]);
				g2.setFont(new Font("Monospaced", Font.BOLD, 15));
				g2.setColor(TEXT_MAIN);
				FontMetrics fm = g2.getFontMetrics();
				g2.drawString(dataStr, x + (NODE_W / 2 - fm.stringWidth(dataStr)) / 2, y + NODE_H / 2 + 8);

				// next field
				boolean isLast = (i == n - 1);
				int nextCX = divX + (NODE_W / 2) / 2;
				int nextCY = y + NODE_H / 2;
				if(isLast) {
					g2.setFont(new Font("Monospaced", Font.PLAIN, 10));
					g2.setColor(NULL_COLOR);
					FontMetrics fm2 = g2.getFontMetrics();
					g2.drawString("\u2205", nextCX - fm2.stringWidth("\u2205") / 2, nextCY + 4);
				}
				else {
					g2.setColor(ARROW_CLR);
					g2.fillOval(nextCX - 3, nextCY - 3, 6, 6);
				}

				// badges
				g2.setFont(new Font("SansSerif", Font.BOLD, 9));
				int badgeY = y + NODE_H + 12;
				if(isHead) {
					g2.setColor(NODE_BORDER);
					g2.drawString("HEAD", x + 3, badgeY);
				}
				if(isIter) {
					g2.setColor(ITER_BORDER);
					int tw = g2.getFontMetrics().stringWidth("CURSOR");
					g2.drawString("CURSOR", x + NODE_W / 2 - tw / 2, badgeY);
					int tx = x + NODE_W / 2;
					g2.fillPolygon(new int[] {tx - 6, tx + 6, tx}, new int[] {y - 14, y - 14, y - 4}, 3);
				}

				// arrow to next node
				if(!isLast) {
					drawArrow(g2, x + NODE_W + 2, y + NODE_H / 2, x + NODE_W + GAP - 2, y + NODE_H / 2);
				}
			}

			// null sentinel
			int nullX = startX + n * (NODE_W + GAP);
			g2.setColor(new Color(0xEEEDE6));
			g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10f, new float[] {4f, 3f}, 0f));
			g2.draw(new RoundRectangle2D.Float(nullX, y + NODE_H / 2 - 12, 40, 24, 6, 6));
			g2.setFont(new Font("Monospaced", Font.PLAIN, 11));
			g2.setColor(NULL_COLOR);
			g2.drawString("null", nullX + 6, y + NODE_H / 2 + 5);
		}

		private void drawArrow(Graphics2D g2, int x1, int y1, int x2, int y2) {
			g2.setColor(ARROW_CLR);
			g2.setStroke(new BasicStroke(1.4f));
			g2.drawLine(x1, y1, x2, y2);
			int hs = 6;
			g2.fillPolygon(new int[] {x2, x2 - hs, x2 - hs}, new int[] {y2, y2 - hs / 2, y2 + hs / 2}, 3);
		}
	}
}
