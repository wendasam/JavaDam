package main.gui;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import main.game.Board;

/**
 * Class ini akan menampilkan semua kotak yang tersedia bagi kepingan player
 * ketika diklik kepingan yang ingin dipindahkan tersebut
 */
@SuppressWarnings("serial")
public class AvailableMoveButton extends JButton {

    private final Board boardstate;

    public AvailableMoveButton(final Board state) {
        super();
        this.boardstate = state;
        this.setBorder(BorderFactory.createEmptyBorder());
        this.setContentAreaFilled(false);
        
        //setBorderColor();
        setIcon();
    }

    // setIcon onto JButton component
    private void setIcon() {
        ImageIcon imageIcon;
        if (GameSettings.HELP_MODE) {
            imageIcon = new ImageIcon(getClass().getResource("/gambar/dottedcircle.png"));
            
        } else {
            imageIcon = new ImageIcon(getClass().getResource("/gambar/dottedcircle.png"));
        }
        
        this.setIcon(imageIcon);
        
    }
    
    private void setBorderColor() {
        if (GameSettings.HELP_MODE) {
            setBorder(BorderFactory.createLineBorder(Color.BLUE, 5));
        } else {
            setBorder(BorderFactory.createLineBorder(Color.GREEN, 5));
        }
    }

    public Board getBoardstate() {
        return boardstate;
    }
}
