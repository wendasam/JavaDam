package main.gui;

import javax.swing.*;
import java.awt.*;
import javax.swing.border.LineBorder;

/**
 * Black or white square panel on checkerboard
 */
@SuppressWarnings("serial")
public class SquarePanel extends JPanel {

    private final JLabel lbNom = new JLabel();
    private String nomor;
    private Color color;

    public SquarePanel(final int i, final int j) {
        this.setPreferredSize(new Dimension(GameSettings.SQUARE_SIZE, GameSettings.SQUARE_SIZE));
        this.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(255, 1, 1), 0, true),
                BorderFactory.createEmptyBorder(0, 0, 0, 0)));
        if (((i % 2) + (j % 2)) % 2 == 0) {
            //color = new Color(255, 255, 204); // dark color square
            color = Color.LIGHT_GRAY;
        } else {
            //color = new Color(80, 140, 10); // bright color square
            color = Color.DARK_GRAY;
        }
    }

    // fungsi untuk memberi warna pada kotak yang diklik tersebut
    public void setHighlighted() {
        color = Color.GREEN;
    }

    @Override
    protected void paintComponent(final Graphics g) {
        super.paintComponent(g);
        g.setColor(color);
        g.fillRect(0, 0, getWidth(), getHeight());
    }
}
