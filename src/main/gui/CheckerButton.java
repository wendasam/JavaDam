package main.gui;

import main.game.Game;
import main.game.Piece;
import main.game.Player;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Class ini merepresentasikan komponen JButton yang bisa diklik jika terdapat
 * icon kepingan pada button tersebut Black or white checker piece (clickable
 * button component)
 */
@SuppressWarnings("serial")
public class CheckerButton extends JButton {

    private final int position;
    private int destination;
    private final Piece piece;
    private Game game;

    // drag drop
    private int X, Y;
    private int screenX = 0;
    private int screenY = 0;

    public CheckerButton(final int position, final Piece piece, final GUI gui) {
        super();
        this.position = position;
        this.piece = piece;
        this.game = game;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        setIcon(piece);

        if (piece.getPlayer() == Player.HUMAN && GameSettings.DRAG_DROP) {
            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent mouseEvent) {
                    screenX = mouseEvent.getXOnScreen();
                    screenY = mouseEvent.getYOnScreen();
                    X = getX();
                    Y = getY();
                }

                @Override
                public void mouseReleased(MouseEvent mouseEvent) {
                    int deltaX = mouseEvent.getXOnScreen() - screenX;
                    int deltaY = mouseEvent.getYOnScreen() - screenY;
                    int dx = (int) Math.round((double) deltaX / (double) main.gui.GameSettings.SQUARE_SIZE);
                    int dy = (int) Math.round((double) deltaY / (double) main.gui.GameSettings.SQUARE_SIZE);
                    gui.onMouseRelease(position, dx, dy);
                }
            });

            this.addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent mouseEvent) {
                    int deltaX = mouseEvent.getXOnScreen() - screenX;
                    int deltaY = mouseEvent.getYOnScreen() - screenY;
                    setLocation(X + deltaX, Y + deltaY);
                }
            });
        }
    }

    public int getPosition() {
        return position;
    }

    public final int getDestination() {
        return destination;
    }

    public final Piece getPiece() {
        return piece;
    }

    private void setIcon(Piece piece) {
        ImageIcon imageIcon;
        final Colour colour = GameSettings.getColour(piece.getPlayer());
        if (colour == Colour.BLACK) {
            if (piece.isKing()) {
                imageIcon = new ImageIcon(getClass().getResource("/gambar/rajaPutih.png"));
            } else {
                imageIcon = new ImageIcon(getClass().getResource("/gambar/putih.png"));
            }
        } else {
            if (piece.isKing()) {
                imageIcon = new ImageIcon(getClass().getResource("/gambar/rajaMerah.png"));
            } else {
                imageIcon = new ImageIcon(getClass().getResource("/gambar/merah.png"));
            }
        }

        this.setIcon(imageIcon);
    }

}
