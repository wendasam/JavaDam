package main.gui;

import AI.AlphaBetaPruning_White;
import AI.Minimax_AI;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultCaret;

import main.game.*;

/**
 *
 * @author asus
 */
public class GUI extends JFrame {
    private static final long serialVersionUID = 1L;

    private JPanel panelPlayer1, panelPlayer2;
    private JTextArea textAreaHum, textAreaComp;
    private JRadioButton rbMinimax, rbAlphaBetaPruning;
    private JButton btSolveGame;
    private JRadioButtonMenuItem onePlayerMenuItem, twoPlayerMenuItem;
    private JRadioButtonMenuItem rbSolveMinimaxMenuItem, rbSolveAlpaBetaMenuItem;
    private ButtonGroup gameTypeButtons;
    private JMenuItem drawItem, undoItemOnePlayer, undoItemTwoPlayer;
    private JLabel redPlayerNoPieces, currentPlayerLabel;
    private JLabel bluePlayerNoPieces, labelMessage;
    private JPanel checkerboardPanel, contentPanel, panelMessage;
    private String nama1, nama2;
    private JTextField txt1, txt2;
    private final ImageIcon redIconUpdate = new ImageIcon(getClass().getResource("/gambar/merah.png"));
    private final ImageIcon whiteIconUpdate = new ImageIcon(getClass().getResource("/gambar/putih.png"));
    private final ImageIcon drawIcon = new ImageIcon(getClass().getResource("/gambar/tipOfDay.png"));
    private List<Integer> helpMoves;

    private Game game;
    private ArrayList<Board> possibleMoves;
    private SquarePanel[] SQUARE;
    private Stack<Board> state;
    private Board hintMove; // hint feature

    public GUI() {
        super();
        start();
    }

    private void start() {
        game = new Game();
        possibleMoves = new ArrayList<>();
        hintMove = null;
        createGUI();
        GameConfig.cleanStatistics();
    }

    /**
     * Sets up initial GUI configuration.
     */
    public void createGUI() {
        switch (GameConfig.FIRSTMOVE) {
            case COMPUTER:
                main.gui.GameSettings.AI_COLOUR = Colour.WHITE;
                break;
            case HUMAN:
                main.gui.GameSettings.AI_COLOUR = Colour.BLACK;
                break;
        }
        // set menubar
        setupMenuBar();

        labelMessage = new JLabel();
        labelMessage.setPreferredSize(new Dimension(665, 30));
        labelMessage.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        JPanel panelWest = new JPanel(new GridLayout(3, 1, 5, 5));
        panelWest.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelWest.setPreferredSize(new Dimension(310, 652));

        JPanel panelEast = new JPanel(new GridLayout(3, 1, 5, 5));
        panelEast.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelEast.setPreferredSize(new Dimension(310, 652));

        ////////////////////////////////////////////////////////////////////////
        JScrollPane scrollPane1 = new JScrollPane();
        panelPlayer1 = new JPanel();
        textAreaHum = new JTextArea(5, 20);
        textAreaHum.setBackground(new Color(204, 204, 204));
        textAreaHum.setLineWrap(true);
        textAreaHum.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        textAreaHum.setEditable(false);

        scrollPane1.setViewportView(textAreaHum);
        GroupLayout grpLayoutEast1 = new GroupLayout(panelPlayer1);
        grpLayoutEast1.setHorizontalGroup(grpLayoutEast1.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(grpLayoutEast1.createSequentialGroup().addContainerGap().addComponent(scrollPane1, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        grpLayoutEast1.setVerticalGroup(grpLayoutEast1.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, grpLayoutEast1.createSequentialGroup()
                        .addContainerGap().addComponent(scrollPane1, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE).addContainerGap()));
        panelPlayer1.setLayout(grpLayoutEast1);

        panelPlayer1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 153, 0), 1, true),
                "Langkah Pemain1", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 153, 0)));

        JScrollPane scrollPane2 = new JScrollPane();
        panelPlayer2 = new JPanel();
        textAreaComp = new JTextArea(5, 20);
        textAreaComp.setBackground(new Color(204, 204, 204));
        textAreaComp.setLineWrap(true);
        textAreaComp.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
        textAreaComp.setEditable(false);

        scrollPane2.setViewportView(textAreaComp);
        GroupLayout grpLayoutEast2 = new GroupLayout(panelPlayer2);
        grpLayoutEast2.setHorizontalGroup(grpLayoutEast2.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(grpLayoutEast2.createSequentialGroup().addContainerGap().addComponent(scrollPane2, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        grpLayoutEast2.setVerticalGroup(grpLayoutEast2.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(GroupLayout.Alignment.TRAILING, grpLayoutEast2.createSequentialGroup()
                        .addContainerGap().addComponent(scrollPane2, GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE).addContainerGap()));

        panelPlayer2.setLayout(grpLayoutEast2);
        panelPlayer2.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 255), 1, true),
                "Langkah Pemain2", TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 0, 255)));

        // panel east
        panelEast.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(255, 0, 0), 1, true),
                "Statistik", TitledBorder.CENTER, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 14), new Color(255, 0, 0)));

        GroupLayout grpLayoutEast3 = new GroupLayout(panelEast);
        panelEast.setLayout(grpLayoutEast3);

        grpLayoutEast3.setHorizontalGroup(grpLayoutEast3.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(grpLayoutEast3.createSequentialGroup()
                .addContainerGap().addGroup(grpLayoutEast3.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                        .addComponent(panelPlayer1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(panelPlayer2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        grpLayoutEast3.setVerticalGroup(grpLayoutEast3.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(grpLayoutEast3.createSequentialGroup()
                .addContainerGap().addComponent(panelPlayer1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(panelPlayer2, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        DefaultCaret caret;
        caret = (DefaultCaret) textAreaHum.getCaret();
        caret = (DefaultCaret) textAreaComp.getCaret();

        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        /**
         * ************************* PANEL MENU WEST**************************
         */
        JPanel panelAtasWest = new JPanel(); // panel sebelah kiri atas
        JPanel panelTengahWest = new JPanel(); // panel sebelah kiri tengah
        JPanel panelBawahWest = new JPanel(); // panel sebelah kiri bawah
        JSeparator separator1 = new JSeparator();
        JSeparator separator2 = new JSeparator();
        JSeparator separator3 = new JSeparator();
        JLabel lbKopGiliran = new JLabel("Giliran Pemain"); // label untuk Player Turn
        JLabel lbKopSolveGame = new JLabel("Dapatkan Solusi"); // label untuk Solve Game
        JLabel lbKopPawnRemain = new JLabel("Sisa Kepingan"); // label untuk Pawn Remainder
        panelWest.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0, 0, 0), 1, true), BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        panelWest.setPreferredSize(new Dimension(340, 652)); // ukuran panel sebelah kiri

        panelAtasWest.setBorder(new LineBorder(new Color(255, 0, 0), 1, true));
        lbKopGiliran.setFont(new Font("Tahoma", 1, 14)); // font label untuk Player Turn
        lbKopGiliran.setHorizontalAlignment(SwingConstants.CENTER); // posisi label untuk Player Turn

        // set up the current player display
        currentPlayerLabel = new JLabel();
        currentPlayerLabel.setIcon(redIconUpdate);
        currentPlayerLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        GroupLayout grpLayoutWest1 = new GroupLayout(panelAtasWest);
        panelAtasWest.setLayout(grpLayoutWest1);
        grpLayoutWest1.setHorizontalGroup(grpLayoutWest1.createParallelGroup(GroupLayout.Alignment.LEADING).addGroup(grpLayoutWest1.createSequentialGroup()
                .addContainerGap().addGroup(grpLayoutWest1.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(lbKopGiliran, GroupLayout.DEFAULT_SIZE, 325, Short.MAX_VALUE)
                        .addComponent(separator1).addComponent(currentPlayerLabel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)).addContainerGap()));

        grpLayoutWest1.setVerticalGroup(grpLayoutWest1.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(grpLayoutWest1.createSequentialGroup()
                .addContainerGap().addComponent(lbKopGiliran, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(separator1, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE).addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(currentPlayerLabel, GroupLayout.PREFERRED_SIZE, 60, GroupLayout.PREFERRED_SIZE).addContainerGap(28, Short.MAX_VALUE)));

        // add panel atas barat
        panelAtasWest.add(currentPlayerLabel);
        panelWest.add(panelAtasWest);

        panelTengahWest.setBorder(new LineBorder(new Color(0, 153, 0), 1, true));
        lbKopSolveGame.setFont(new Font("Tahoma", 1, 14));
        lbKopSolveGame.setHorizontalAlignment(SwingConstants.CENTER);

        rbMinimax = new JRadioButton("Minimax");
        //rbMinimax.setEnabled(false);
        rbAlphaBetaPruning = new JRadioButton("Alpha Beta Pruning");
        //rbAlphaBetaPruning.setEnabled(false);

        btSolveGame = new JButton("Solusi");
        //btSolveGame.setEnabled(false);
        btSolveGame.addActionListener((ActionEvent e) -> {
            String msg = "Anda harus memilih algoritma yang tersedia lebih dulu!";
            if (rbMinimax.isSelected()) {
                onSolveMinimaxClick(); // solve game with minimax algorithm
            } else if (rbAlphaBetaPruning.isSelected()) {
                onSolveAlphaBetaPruningClick();
            } else {
                JOptionPane.showMessageDialog(GUI.this, msg, "Solusi", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        gameTypeButtons = new ButtonGroup();
        gameTypeButtons.add(rbMinimax);
        gameTypeButtons.add(rbAlphaBetaPruning);

        GroupLayout grpLayoutWest2 = new GroupLayout(panelTengahWest);
        panelTengahWest.setLayout(grpLayoutWest2);
        grpLayoutWest2.setHorizontalGroup(grpLayoutWest2.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(grpLayoutWest2.createSequentialGroup().addContainerGap().addGroup(grpLayoutWest2.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(lbKopSolveGame, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(separator2).addGroup(GroupLayout.Alignment.TRAILING, grpLayoutWest2.createSequentialGroup()
                                        .addGap(0, 29, Short.MAX_VALUE).addGroup(grpLayoutWest2.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(btSolveGame, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                                .addComponent(rbMinimax, GroupLayout.DEFAULT_SIZE, 117, Short.MAX_VALUE)).addGap(18, 18, 18)
                                        .addGroup(grpLayoutWest2.createParallelGroup(GroupLayout.Alignment.LEADING, false)
                                                .addComponent(rbAlphaBetaPruning, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                        .addGap(44, 44, 44))).addContainerGap()));

        grpLayoutWest2.setVerticalGroup(grpLayoutWest2.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(grpLayoutWest2.createSequentialGroup().addContainerGap().addComponent(lbKopSolveGame, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED).addComponent(separator2, GroupLayout.PREFERRED_SIZE, 10, GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED).addGroup(grpLayoutWest2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(rbMinimax).addComponent(rbAlphaBetaPruning)).addGap(18, 18, 18).addGroup(grpLayoutWest2.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btSolveGame, GroupLayout.PREFERRED_SIZE, 35, GroupLayout.PREFERRED_SIZE))
                        .addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        // add panel atasbarat
        panelWest.add(panelTengahWest);

        panelBawahWest.setBorder(new LineBorder(new Color(0, 0, 204), 1, true));
        panelBawahWest.setLayout(new GridLayout(4, 0, 1, 1));

        lbKopPawnRemain.setFont(new Font("Tahoma", 1, 14));
        lbKopPawnRemain.setHorizontalAlignment(SwingConstants.CENTER);

        // set up the number of pieces display
        redPlayerNoPieces = new JLabel(new ImageIcon(getClass().getResource("/gambar/merah.png")));
        bluePlayerNoPieces = new JLabel(new ImageIcon(getClass().getResource("/gambar/putih.png")));
        
        panelBawahWest.add(lbKopPawnRemain);
        panelBawahWest.add(separator3);
        panelBawahWest.add(redPlayerNoPieces);
        panelBawahWest.add(bluePlayerNoPieces);

        panelWest.add(panelBawahWest);

        contentPanel = new JPanel();
        checkerboardPanel = new JPanel(new GridBagLayout());
        checkerboardPanel.setBackground(new Color(146, 57, 49));
        checkerboardPanel.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        panelMessage = new JPanel();
        panelMessage.setBorder(BorderFactory.createCompoundBorder(new LineBorder(new Color(0, 0, 0), 1, true),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        contentPanel.setLayout(new BorderLayout());
        this.setContentPane(contentPanel);

        // add component to panel
        contentPanel.add(checkerboardPanel, BorderLayout.CENTER);
        contentPanel.add(panelWest, BorderLayout.WEST);
        contentPanel.add(panelEast, BorderLayout.EAST);
        contentPanel.add(panelMessage, BorderLayout.SOUTH);

        panelMessage.add(labelMessage);
        nonActiveItem();
        updatePieceNoDisplay();
        addSquares(); // hanya menampilkan papan permainan\
        updateText("");

        this.setIconImage(new ImageIcon(getClass().getResource("/gambar/iconGame_1.png")).getImage());
        this.pack();
        this.setTitle("Dam");
        this.setResizable(false);
        this.setVisible(true);
        this.setSize(new Dimension(1400, 735)); // setWindow size
        if (GameConfig.FIRSTMOVE == Player.COMPUTER) {
            aiMove();
        }
    }

    /**
     * Sets up the menu bar component.
     */
    private void setupMenuBar() {

        // ensure exit method is called on window closing
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(
                new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        onExitClick();
                    }
                }
        );

        // initialize components menuBar
        JMenuBar menuBar = new JMenuBar();
        JSeparator separatorItemOptionsMenu = new JSeparator();
        JSeparator separatorItemFileMenu = new JSeparator();
        JMenu fileMenu = new JMenu("Permainan");
        JMenuItem newGameItem = new JMenuItem("Permainan Baru");
        JMenuItem exitItem = new JMenuItem("Keluar");
        JMenu optionsMenu = new JMenu("Pilihan");
        undoItemOnePlayer = new JMenuItem("Kembali");
        undoItemTwoPlayer = new JMenuItem("Kembali");
        JMenu solveItem = new JMenu("Solusi");
        gameTypeButtons = new ButtonGroup();    //button group untuk menampung radioButton solve
        rbSolveMinimaxMenuItem = new JRadioButtonMenuItem("Solusi Dengan Algoritma Minimax");
        rbSolveMinimaxMenuItem.setSelected(main.gui.GameSettings.HINT_MODE);
        rbSolveAlpaBetaMenuItem = new JRadioButtonMenuItem("Solusi Dengan Algoritma Alpha Beta Pruning");
        rbSolveAlpaBetaMenuItem.setSelected(main.gui.GameSettings.HINT_MODE);

        JMenu helpMenu = new JMenu("Bantuan");
        JMenuItem rulesItem = new JMenuItem("Aturan Permainan");
        JMenuItem aboutItem = new JMenuItem("Tentang");

        // add action listeners
        exitItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, ActionEvent.CTRL_MASK));
        exitItem.addActionListener((ActionEvent actionEvent) -> {
            onExitClick();
        });

        newGameItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,
                ActionEvent.CTRL_MASK));
        newGameItem.addActionListener((ActionEvent e) -> {
            PermainanBaru permainanBaru = new PermainanBaru(GUI.this);
        });

        rulesItem.addActionListener((ActionEvent e) -> {
            onRulesClick();
        });

        aboutItem.addActionListener((ActionEvent e) -> {
            onAboutClick();
        });

        undoItemOnePlayer.setEnabled(false); // nonaktifkan undoItem satu player akan aktif ketika mode game vs computer
        undoItemOnePlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undoItemOnePlayer.addActionListener((ActionEvent actionEvent) -> {
            onUndoClick();
            //removeLangkah();
            rbSolveMinimaxMenuItem.setSelected(false);
            rbSolveAlpaBetaMenuItem.setSelected(false);
            rbMinimax.setSelected(false);
            rbAlphaBetaPruning.setSelected(false);
        });

        undoItemTwoPlayer.setVisible(false); // nonaktifkan undoItem dua player akan aktif ketika mode game 2 player
        undoItemTwoPlayer.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, ActionEvent.CTRL_MASK));
        undoItemTwoPlayer.addActionListener((ActionEvent actionEvent) -> {
            onUndo2PlayerClick();
        });

        //rbSolveMinimaxMenuItem.setEnabled(false);
        rbSolveMinimaxMenuItem.addActionListener((ActionEvent e) -> {
            onSolveMinimaxClick(); // melakukan solve dengan minimax algorithm
        });

        //rbSolveAlpaBetaMenuItem.setEnabled(false);
        rbSolveAlpaBetaMenuItem.addActionListener((ActionEvent actionEvent) -> {
            onSolveAlphaBetaPruningClick(); // get solusi dengan alpha beta pruning
        });

        fileMenu.add(newGameItem);
        fileMenu.add(separatorItemFileMenu);
        fileMenu.add(exitItem);

        optionsMenu.add(undoItemOnePlayer);
        optionsMenu.add(undoItemTwoPlayer);
        optionsMenu.add(separatorItemOptionsMenu);
        gameTypeButtons.add(rbSolveMinimaxMenuItem);
        gameTypeButtons.add(rbSolveAlpaBetaMenuItem);
        solveItem.add(rbSolveMinimaxMenuItem);
        solveItem.add(rbSolveAlpaBetaMenuItem);
        optionsMenu.add(solveItem);

        helpMenu.add(rulesItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(optionsMenu);
        menuBar.add(helpMenu);
        this.setJMenuBar(menuBar);
    }

    // perbaharui pesan
    private void updateText(String text) {
        labelMessage.setText(text);
        labelMessage.setForeground(Color.red);
        labelMessage.setHorizontalAlignment(SwingConstants.CENTER);
    }

    /**
     * Updates the checkerboard GUI based on the game state.
     */
    private void updateCheckerBoard() {
        checkerboardPanel.removeAll();
        addPieces();
        addSquares();
        
        addAvailableMoveButtons();
        checkerboardPanel.setVisible(true);
        checkerboardPanel.repaint();
        this.setVisible(true);
    }

    /**
     * Update 2Playerboard
     */
    public void update2PlayerBoard() {
        checkerboardPanel.removeAll();
        addPieces2();
        addSquares();
        addAvailableMoveButtons2();
        checkerboardPanel.setVisible(true);
        checkerboardPanel.repaint();
        this.setVisible(true);
    }

    // add papan ke mainpanel
    private void addSquares() {
        SQUARE = new SquarePanel[Board.BOARD_SIZE];
        int fromPos = -1;
        int toPos = -1;
        if (hintMove != null) {
            fromPos = hintMove.getFromPos();
            toPos = hintMove.getToPos();
        }

        GridBagConstraints c = new GridBagConstraints();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            c.gridx = i % Board.SIDE_LENGTH;
            c.gridy = i / Board.SIDE_LENGTH;
            SQUARE[i] = new SquarePanel(c.gridx, c.gridy);
            if (i == fromPos) {
                SQUARE[i].setHighlighted();
            }
            if (i == toPos) {
                SQUARE[i].setHighlighted();
            }
            if (helpMoves != null) {
                if (helpMoves.contains(i)) {
                    SQUARE[i].setHighlighted();
                }
            }
            JLabel lb = new JLabel(" " + Integer.toString(i)); // label for CellNumber
            lb.setSize(5, 5); // set Sise for label number
            lb.setForeground(Color.WHITE); // setColor for number
            SQUARE[i].setLayout(new BorderLayout()); // setLayout to SQUARE
            SQUARE[i].add(lb, BorderLayout.PAGE_START); // add label to square
            SQUARE[i].setToolTipText("Kotak ke: " + Integer.toString(i)); // setTooltip on cell
            checkerboardPanel.add(SQUARE[i], c); // add square onto checkerboardPanel
        }
    }

    /**
     * add kepingan permainan pada papan permainan sesuai dengan posisi yang
     * telah ditentukan
     */
    private void addPieces() {
        GridBagConstraints gbc = new GridBagConstraints();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            gbc.gridx = i % Board.SIDE_LENGTH;
            gbc.gridy = i / Board.SIDE_LENGTH;
            if (game.getState().getPiece(i) != null) {
                Piece piece = game.getState().getPiece(i);
                CheckerButton button = new CheckerButton(i, piece, this);
                button.addActionListener((ActionEvent actionEvent) -> {
                    onPieceClick(actionEvent);
                    //onPieceClick2Player(actionEvent);
                });
                checkerboardPanel.add(button, gbc);
            }
        }
    }

    /**
     * add pieces to boardpanel untuk 2 pemain
     */
    private void addPieces2() {
        GridBagConstraints gbc = new GridBagConstraints();
        for (int i = 0; i < Board.BOARD_SIZE; i++) {
            gbc.gridx = i % Board.SIDE_LENGTH;
            gbc.gridy = i / Board.SIDE_LENGTH;

            if (game.getState().getPiece(i) != null) {
                Piece piece = game.getState().getPiece(i);
                CheckerButton button = new CheckerButton(i, piece, this);
                button.addActionListener((ActionEvent actionEvent) -> {
                    onPieceClick2Player(actionEvent);
                });
                checkerboardPanel.add(button, gbc);
            }
        }
    }

    /**
     * Add button untuk menampilkan kemungkinan langkah yang tersedia
     */
    private void addAvailableMoveButtons() {
        possibleMoves.stream().forEach((stat) -> {
            int newPos = stat.getToPos();
            AvailableMoveButton button = new AvailableMoveButton(stat);
            button.addActionListener((ActionEvent e) -> {
                onAvailableButtonClick(e);
            });
            SQUARE[newPos].add(button);
        });
    }

    private void addAvailableMoveButtons2() {
        possibleMoves.stream().forEach((sta) -> {
            int newPos = sta.getToPos();
            AvailableMoveButton button = new AvailableMoveButton(sta);
            button.addActionListener((ActionEvent e) -> {
                onAvailableButtonClick2Player(e);
            });
            SQUARE[newPos].add(button);
        });
    }

    /**
     * ************************************************************
     */
    /**
     * ********************* ON CLICK METHODS
     *
     * @param position oh the board
     * @param dx posotion
     * @param dy position
     */
    public void onMouseRelease(int position, int dx, int dy) {
        MoveMessage feedback = game.playerMove(position, dx, dy);
        if (feedback == MoveMessage.SUCCESS) {
            updateCheckerBoard();
            aiMove();
        } else {
            updateCheckerBoard();
            System.out.println(feedback.toString());
        }
    } // end of onMouseRelease method

    private void onHintAlphaBetaClick() {
        if (!game.isGameOver()) {
            AlphaBetaPruning_White abp = new AlphaBetaPruning_White(GameConfig.AI_DEPTH, Player.HUMAN);
            textAreaHum.append(" Alpha Beta Pruning Help Mode Depth: " + GameConfig.AI_DEPTH + "\n");
            helpMoves = null;
            hintMove = abp.move(this.game.getState(), Player.HUMAN);
            updateCheckerBoard();
        }
    }

    /**
     * private void onHintAlphaBetaClick2() { if (!game.isGameOver()) {
     * AlphaBetaPruning_White abp1 = new
     * AlphaBetaPruning_White(GameConfig.AI_DEPTH_1, Player.HUMAN);
     * textAreaHum.append(" Alpha Beta Pruning Help Mode Depth: " +
     * GameConfig.AI_DEPTH_1 + "\n"); helpMoves = null; hintMove =
     * abp1.move(this.game.getState(), Player.HUMAN); updateCheckerBoard();
     * //update2PlayerBoard(); setCurrentPlayerIcon(); } }
     */
    public void onHintMinimaxClick() {
        if (!game.isGameOver()) {
            Minimax_AI minimax = new Minimax_AI(GameConfig.AI_DEPTH, Player.HUMAN);
            textAreaHum.append(" Minimax Help Mode Depth: " + GameConfig.AI_DEPTH + "\n");
            helpMoves = null;
            hintMove = minimax.move(this.game.getState(), Player.HUMAN);
            updateCheckerBoard();
        }
    }
    
    public void removeLangkah() {
        String hapus = null;
        if (textAreaHum.getText().length() > 1) {
            StringBuilder builder = new StringBuilder(textAreaHum.getText());
            builder.deleteCharAt(textAreaHum.getText().length() - 1);
            hapus = builder.toString();
            textAreaHum.setText(hapus);
        }
    }

    // menampilkan kepingan langkah kepingan yang tersedia
    private void showMoveAbles() {
        hintMove = null;
        helpMoves = game.getState().getSuccessors().stream().map(x -> x.getFromPos()).collect(Collectors.toList());
        updateCheckerBoard();
    }

    /**
     * Occurs when user clicks on checker piece
     *
     * @param actionEvent
     */
    private void onPieceClick(ActionEvent actionEvent) {
        if (game.getTurn() == Player.HUMAN) {
            CheckerButton button = (CheckerButton) actionEvent.getSource();
            int pos = button.getPosition();
            if (button.getPiece().getPlayer() == Player.HUMAN) {
                possibleMoves = game.getValidMoves(pos);
                updateCheckerBoard();
                if (possibleMoves.isEmpty()) {
                    MoveMessage feedback = game.moveFeedbackClick(pos);
                    updateText(feedback.toString());
                    if (feedback == MoveMessage.FORCED_JUMP) {
                        labelMessage.getToolkit().beep();// beep sound when player forget to take opponent piece
                        showMoveAbles(); // show movable jump pieces
                    }
                } else {
                    updateText("");
                }
            }
        }
    }

    /**
     * Occurs when user clicks to move checker piece to new (ghost) location.
     *
     * @param actionEvent
     */
    private void onAvailableButtonClick(ActionEvent actionEvent) {
        if (!game.isGameOver() && game.state.peek().getTurn() == Player.HUMAN) {
            hintMove = null;
            helpMoves = null;
            AvailableMoveButton button = (AvailableMoveButton) actionEvent.getSource();
            
            game.playerMove(button.getBoardstate());
            possibleMoves = new ArrayList<>();
            //helpMoves = game.getState().getSuccessors().stream().map(x -> x.getFromPos()).collect(Collectors.toList());
            updateCheckerBoard();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    aiMove();
                }
            });
            playerMoves(); // player moves
        }
    }

    //show game over message
    private void gameOverMessage() {
        try {
            Object[] options = {"Ya", "Tidak",};
            // show message gameOver if Computer won
            if (game.state.peek().isHumanLose()) {
                int lagi = JOptionPane.showOptionDialog(null, "Komputer Menang\n\nApakah anda ingin main lagi?",
                        "Permainan Berakhir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                        whiteIconUpdate, options, options[0]);
                if (lagi == 0) {
                    start();
                    onActiveItem();
                    updatePieceNoDisplay();
                    updateCheckerBoard(); // memperbaharui papan permainan
                    panelPlayer1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 153, 0), 1, true),
                            "Pemain", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 153, 0)));
                    panelPlayer2.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 255), 1, true), "Komputer",
                            TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 0, 255)));

                }
            } else if (game.state.peek().isComputerLose()) {
                int lagi = JOptionPane.showOptionDialog(null, "Pemain Menang\n\nApakah anda ingin main lagi?",
                        "Permainan Berakhir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                        whiteIconUpdate, options, options[0]);
                if (lagi == 0) {
                    start();
                    onActiveItem();
                    updatePieceNoDisplay();
                    updateCheckerBoard(); // memperbaharui papan permainan
                    panelPlayer1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 153, 0), 1, true),
                            "Pemain", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 153, 0)));
                    panelPlayer2.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 255), 1, true), "Komputer",
                            TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 0, 255)));

                }
            }

            if ((((!game.isGameOver() && (game.getState().getHumanPieceCount() == 1
                    || game.getState().getHumanPieceCount() == 2 || game.getState().getHumanPieceCount() == 3))
                    && (!game.isGameOver() && (game.getState().getCompPieceCount()
                    >= game.getState().getHumanPieceCount())))
                    && (game.htgLangkahHum > 200 || game.htgLangkahComp > 200))
                    || (((!game.isGameOver() && (game.getState().getCompPieceCount() == 1
                    || game.getState().getCompPieceCount() == 2 || game.getState().getCompPieceCount() == 3))
                    && (!game.isGameOver() && (game.getState().getCompPieceCount()
                    <= game.getState().getHumanPieceCount())))
                    && (game.htgLangkahComp > 200 || game.htgLangkahHum > 200))) {
                 int lagi = JOptionPane.showOptionDialog(null, "Permainan Berakhir Seri\n\nApakah anda ingin main lagi?",
                        "Permainan Berakhir", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, 
                        drawIcon, options, options[0]);
                if (lagi == 0) {
                    start();
                    onActiveItem();
                    updatePieceNoDisplay();
                    updateCheckerBoard(); // memperbaharui papan permainan
                    panelPlayer1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 153, 0), 1, true),
                            "Pemain", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 153, 0)));
                    panelPlayer2.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 255), 1, true), "Komputer",
                            TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 0, 255)));
                } else if (lagi == 1) {
                    game = new Game();
                    game = null;
                    playerMoves();
                }
            }
        } catch (RuntimeException ex) {
            ex.printStackTrace();
        }
    }

    private void gameOverMessage2() {
        Object[] options = {"Ya", "Tidak",};
        
        if (game.getState().isHumanLose()) {
            int lg = JOptionPane.showOptionDialog(this, "\t" + txt2.getText() 
                    + " Menang\n\nApakah anda ingin main lagi?", "Permainan Berakhir",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, whiteIconUpdate, options, options[0]);
            if (lg == 0) {
                start();
                update2PlayerBoard();
                updatePieceNoDisplay();
                panelPlayer1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 153, 0), 1, true),
                                "Langkah " + nama1, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 153, 0)));
                        panelPlayer2.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 255), 1, true),
                                "Langkah " + nama2, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 0, 255)));
            }
        } else if (game.getState().isComputerLose()) {
            int lg = JOptionPane.showOptionDialog(this, "\t" + txt1.getText() 
                    + " Menang\n\nApakah anda ingin main lagi?", "Permainan Berakhir",
                    JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, redIconUpdate, options, options[0]);
            if (lg == 0) {
                start();
                update2PlayerBoard();
                updatePieceNoDisplay();
                panelPlayer1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 153, 0), 1, true),
                                "Langkah " + nama1, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 153, 0)));
                        panelPlayer2.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 255), 1, true),
                                "Langkah " + nama2, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 0, 255)));
            }
        }
    }

    // player move method
    public void playerMoves() {
        if (game.state.peek().getTurn().getOpposite() == Player.HUMAN) {
            updatePieceNoDisplay();
            currentPlayerLabel.setIcon(whiteIconUpdate);
            textAreaHum.append("---------------------------------------------\n");
            textAreaHum.append(" " + game.htgLangkahHum + "). Giliran Pemain : \n");
            textAreaHum.append(" Langkah : " + game.state.peek().getFromPos()
                    + " - " + game.state.peek().getToPos() + "\n");
            //textAreaHum.append(" Skor kepingan : " + game.state.peek().pieceScore(Player.HUMAN) + "\n");
            textAreaHum.append("---------------------------------------------\n\n");
        }
    }

    // solve the game dengan algoritma alpha beta pruning
    public void onSolveAlphaBetaPruningClick() {
        if (!game.isGameOver() && game.getTurn() == Player.HUMAN) {

            // perform compute move
            updatePieceNoDisplay();

            long startTime = System.nanoTime();
            game.solveAlphaBetaRed();

            // compute time taken
            long aiMoveDurationInMs = (System.nanoTime() - startTime) / 10000;

            // compute necessary delay time (not less than zero)
            long delayInMs = Math.max(0, GameSettings.AiMinPauseDurationInMs - aiMoveDurationInMs);

            textAreaHum.append("---------------------------------------------\n");
            textAreaHum.append(" " + game.htgLangkahHum + "). Giliran Alpha Beta Pruning : \n");
            textAreaHum.append(" Langkah : " + game.state.peek().getFromPos() + " - "
                    + game.state.peek().getToPos() + "\n");
            textAreaHum.append(" Waktu : " + delayInMs + " ms\n");
            textAreaHum.append(" Node : " + GameConfig.TOTAL_NODES_EXPANDED_FOR_RED_ABP + "\n");
            textAreaHum.append(" Kedalaman : " + GameConfig.AI_DEPTH + "\n");
            //textAreaHum.append(" Skor kepingan : " + game.state.peek().getHumanPieceCount() + "\n\n");
            textAreaHum.append("---------------------------------------------\n\n");
            currentPlayerLabel.setIcon(whiteIconUpdate);

            ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
            exec.schedule(new Runnable() {
                @Override
                public void run() {
                    invokeAiUpdate();
                }
            }, delayInMs, TimeUnit.MILLISECONDS);
        }
    }

    // solve the game dengan algoritma alpha beta pruning
    public void onSolveMinimaxClick() {
        if (!game.isGameOver() && game.getTurn() == Player.HUMAN) {

            // perform COMPUTER move
            updatePieceNoDisplay();

            long startTime = System.nanoTime();
            game.solveMinimaxMove();

            // compute time taken
            long aiMoveDurationInMs = (System.nanoTime() - startTime) / 10000;

            // compute necessary delay time (not less than zero)
            long delayInMs = Math.max(0, GameSettings.AiMinPauseDurationInMs - aiMoveDurationInMs);

            textAreaHum.append("---------------------------------------------\n");
            textAreaHum.append(" " + game.htgLangkahHum + "). Giliran Minimax : \n");
            textAreaHum.append(" Langkah : " + game.state.peek().getFromPos() + " - "
                    + game.state.peek().getToPos() + "\n");
            textAreaHum.append(" Waktu : " + delayInMs + " ms\n");
            textAreaHum.append(" Node : " + GameConfig.TOTAL_NODES_EXPANDED_FOR_MINIMAX_AI + "\n");
            textAreaHum.append(" Kedalaman : " + GameConfig.AI_DEPTH + "\n");
            //textAreaHum.append(" Skor Kepingan : " + game.state.peek().pieceScore(Player.HUMAN) + "\n");
            textAreaHum.append("---------------------------------------------\n\n");
            currentPlayerLabel.setIcon(whiteIconUpdate);
            currentPlayerLabel.removeAll();

            ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
            exec.schedule(new Runnable() {
                @Override
                public void run() {
                    invokeAiUpdate();
                }
            }, delayInMs, TimeUnit.MILLISECONDS);
        }
    }

    // perform langkah komputer
    private void aiMove() {
        long startTime = System.nanoTime();
        game.computerMove();
        
        updatePieceNoDisplay();

        // compute time taken
        long aiMoveDurationInMs = (System.nanoTime() - startTime) / 10000;

        // compute necessary delay time (not less than zero)
        long delayInMs = Math.max(0, GameSettings.AiMinPauseDurationInMs - aiMoveDurationInMs);

        currentPlayerLabel.setIcon(redIconUpdate);
        currentPlayerLabel.removeAll();
        textAreaComp.append("---------------------------------------------\n");
        textAreaComp.append(" " + game.htgLangkahComp + "). Giliran Komputer : \n");
        textAreaComp.append(" Langkah : " + game.state.peek().getFromPos() + " - " + game.state.peek().getToPos() + "\n");
        textAreaComp.append(" Waktu : " + delayInMs + " ms\n");
        textAreaComp.append(" Node : " + GameConfig.TOTAL_NODES_EXPANDED_FOR_WHITE_ABP + "\n");
        textAreaComp.append(" Kedalaman : " + GameConfig.AI_DEPTH + "\n");
        //textAreaComp.append(" Skor Kepingan : " + game.state.peek().pieceScore(Player.COMPUTER) + "\n");
        textAreaComp.append("---------------------------------------------\n\n");

        if (game.isGameOver()) {
            textAreaComp.append(" Waktu : " + delayInMs + " ms\n");
        }
        // schedule delayed update
        ScheduledExecutorService exec = Executors.newScheduledThreadPool(1);
        exec.schedule(new Runnable() {
            @Override
            public void run() {
                invokeAiUpdate();
            }
        }, delayInMs, TimeUnit.MILLISECONDS);
    }

    /**
     * Update checkerboard and trigger new AlphaBetaPruning_White move if
     * necessary
     */
    private void invokeAiUpdate() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                updateCheckerBoard();
                gameOverMessage();
                if (!game.isGameOver() && game.getTurn() == Player.COMPUTER) {
                    aiMove();
                } else if (!game.isGameOver() && game.getTurn() == Player.HUMAN) {
                    // in hint mode, display hint after AlphaBetaPruning_White move
                    if (rbMinimax.isSelected() || rbSolveMinimaxMenuItem.isSelected()) {
                        onSolveMinimaxClick(); // solve game with minimax algorithm
                    } else if (rbAlphaBetaPruning.isSelected() || rbSolveAlpaBetaMenuItem.isSelected()) {
                        onSolveAlphaBetaPruningClick(); // get solusi dengan alpha beta pruning
                    }
                }
            }
        });
    }

    /**
     * Open dialog for quitting the program
     */
    private void onExitClick() {
        Object[] options = {"Ya", "Tidak",};
        int exit = JOptionPane.showOptionDialog(this, "\nApakah anda yakin ingin keluar dari permainan?",
                "Keluar", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
        if (exit == 0) {
            // close logging file
            this.dispose();
            System.exit(0);
        } // end if
    } // end of onExitClick() method

    /**
     * Open help dialog.
     */
    private void onRulesClick() {
        JOptionPane.showMessageDialog(GUI.this, GameSettings.RULES,
                "Aturan Permainan", JOptionPane.INFORMATION_MESSAGE);
    }

    public void onAboutClick() {
        JOptionPane.showMessageDialog(GUI.this, GameSettings.ABOUT,
                "Tentang", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Undo the last move
     */
    private void onUndoClick() {
        game.undo();
        updateCheckerBoard();
        if (main.gui.GameSettings.HINT_MODE) {
            onHintAlphaBetaClick();
            onHintMinimaxClick();
        }
    } // end of undo click method

    // undo click for two player
    private void onUndo2PlayerClick() {
        game.undo();
        update2PlayerBoard();
    } // end of onUndo2PlayerClick method

    // nonaktive other itme
    public void nonActiveItem() {
        rbSolveMinimaxMenuItem.setEnabled(false); // aktifkan radioButtonItem solveMinimax
        rbSolveAlpaBetaMenuItem.setEnabled(false); // aktifkan radioButtonItem solveAlphaBeta
        rbMinimax.setEnabled(false); // aktifkan radioButton solveMinimax
        rbAlphaBetaPruning.setEnabled(false); // aktifkan btSolve
        btSolveGame.setEnabled(false); // aktifkan btSolve
        undoItemOnePlayer.setEnabled(false);
    } // end of nonActiveItem method

    public void onActiveItem() {
        rbSolveMinimaxMenuItem.setEnabled(true); // aktifkan radioButtonItem solveMinimax
        rbSolveAlpaBetaMenuItem.setEnabled(true); // aktifkan radioButtonItem solveAlphaBeta
        rbMinimax.setEnabled(true); // aktifkan radioButton solveMinimax
        rbAlphaBetaPruning.setEnabled(true); // aktifkan btSolve
        btSolveGame.setEnabled(true); // aktifkan btSolve
        undoItemOnePlayer.setEnabled(true);
    }

    // update the Piece Number Display
    private void updatePieceNoDisplay() {
        //if (!game.isGameOver() && game.state.peek().getTurn() == Player.COMPUTER) {
        bluePlayerNoPieces.setText(" " + game.state.peek().getCompPieceCount());
        //} else if (!game.isGameOver() && game.state.peek().getTurn() == Player.HUMAN) {
        redPlayerNoPieces.setText(" " + game.state.peek().getHumanPieceCount());
        //}
    }

    // fungsi ini akan menampilkan jenda dialogbox untuk menu permaina baru
    public class PermainanBaru extends JDialog {

        private static final long serialVersionUID = 1L;
        private final GUI gui;

        private final JRadioButton rbOnePlayer, rbTwoPlayer;
        private final JLabel lbName1, lbName2, plSetting;
        private final JPanel pn1, pn2;
        private final JButton btPlay;
        private final ButtonGroup bg;
        private final JSeparator separa = new JSeparator();

        // konstruktor inner class newGames
        public PermainanBaru(GUI gui) {
            super(gui);
            this.setTitle("Permainan Baru");
            this.gui = gui;
            this.setIconImage(new ImageIcon(getClass().getResource("/gambar/iconGame_2.png")).getImage());

            Container b = getContentPane();
            b.setLayout(null);

            //TextField untuk input nama1 pemain satu
            txt1 = new JTextField(15);
            txt1.setEditable(false);
            txt1.setToolTipText("Masukan nama pemain 1");
            txt1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
            txt1.setBounds(180, 170, 200, 25);
            b.add(txt1);

            //TextField untuk input nama1 pemain dua
            txt2 = new JTextField(15);
            txt2.setEditable(false);
            txt2.setToolTipText("Masukan nama pemain 2");
            txt2.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
            txt2.setBounds(180, 210, 200, 25);
            b.add(txt2);

            plSetting = new JLabel("Mode Permainan");
            plSetting.add(separa);
            plSetting.setFont(new Font("Vardana", Font.BOLD, 20));
            plSetting.setBounds(170, 40, 300, 25);
            b.add(plSetting);

            // radioButton untuk satu pemain
            rbOnePlayer = new JRadioButton("Satu Pemain");
            rbOnePlayer.setSelected(GameConfig.FIRSTMOVE == Player.HUMAN);
            rbOnePlayer.setToolTipText("Pilih jika ingin bermain melawan komputer");
            rbOnePlayer.addActionListener((ActionEvent e) -> {
                txt1.setEditable(false);
                txt2.setEditable(false);
                txt1.setText("");
                txt2.setText("");
            });

            // radioButton untuk dua pemain
            rbTwoPlayer = new JRadioButton("Dua Pemain");
            rbTwoPlayer.setToolTipText("Pilih jika ingin bermain dua pemain");
            rbTwoPlayer.addActionListener((ActionEvent e) -> {
                txt1.setEditable(true);
                txt1.setText("Pemain1");
                txt2.setEditable(true);
                txt2.setText("Pemain2");
            });

            bg = new ButtonGroup();
            bg.add(rbOnePlayer);
            bg.add(rbTwoPlayer);

            // panel untuk menampung radioButton pemain
            pn1 = new JPanel();
            pn1.add(rbOnePlayer);
            pn1.add(rbTwoPlayer);
            pn1.setBounds(100, 100, 300, 40);
            b.add(pn1);

            // label nama1 pemain satu
            lbName1 = new JLabel("Pemain1 : ");
            lbName1.setBounds(120, 170, 200, 25);
            b.add(lbName1);

            // label nama1 pemain dua
            lbName2 = new JLabel("Pemain2 : ");
            lbName2.setBounds(120, 210, 200, 25);
            b.add(lbName2);

            pn2 = new JPanel();
            pn2.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            pn2.setBounds(100, 150, 300, 110);
            b.add(pn2);

            //Penggunaan Text Area
            btPlay = new JButton("Main");
            btPlay.setToolTipText("Tekan tombol Play untuk memulai permainan");
            btPlay.setBounds(320, 280, 80, 40);
            btPlay.addActionListener((ActionEvent e) -> {
                if (rbOnePlayer.isSelected()) {
                    GameConfig.FIRSTMOVE = rbOnePlayer.isSelected() ? Player.HUMAN : Player.COMPUTER;
                    start();
                    onActiveItem();
                    updatePieceNoDisplay();
                    updateCheckerBoard(); // memperbaharui papan permainan
                    PermainanBaru.this.dispose();

                    rbSolveMinimaxMenuItem.setSelected(false);
                    rbSolveAlpaBetaMenuItem.setSelected(false);
                    rbMinimax.setSelected(false);
                    rbAlphaBetaPruning.setSelected(false);

                    undoItemOnePlayer.setVisible(true);
                    undoItemTwoPlayer.setVisible(false);
                    bluePlayerNoPieces.setToolTipText("Sisa Jumlah Kepingan Komputer");
                    redPlayerNoPieces.setToolTipText("Sisa Jumlah Kepingan Pemain");

                    panelPlayer1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 153, 0), 1, true),
                            "Pemain", TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 153, 0)));
                    panelPlayer2.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 255), 1, true), "Komputer",
                            TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 0, 255)));

                } else if (rbTwoPlayer.isSelected()) {
                    txt1.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
                    txt2.setCursor(new java.awt.Cursor(java.awt.Cursor.TEXT_CURSOR));
                    nama1 = txt1.getText();
                    nama2 = txt2.getText();

                    if (nama1.equals("") && nama2.equals("")) {
                        JOptionPane.showMessageDialog(PermainanBaru.this,
                                "Silahkan masukan nama pemain dengan benar!", "Info",
                                JOptionPane.INFORMATION_MESSAGE);
                    } else if (nama1.equals("")) {
                        JOptionPane.showMessageDialog(PermainanBaru.this, "Nama pemain satu masih kosong!",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else if (nama2.equals("")) {
                        JOptionPane.showMessageDialog(PermainanBaru.this, "Nama pemain dua masih kosong!",
                                "Info", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        start();
                        PermainanBaru.this.dispose();
                        onActiveItem();
                        update2PlayerBoard();
                        updatePieceNoDisplay();

                        redPlayerNoPieces.setToolTipText(txt1.getText() + " Jumlah Kepingan");
                        bluePlayerNoPieces.setToolTipText(txt2.getText() + " Jumlah Kepingan");

                        undoItemTwoPlayer.setVisible(true); // aktifkan undoItem dua player
                        undoItemOnePlayer.setVisible(false); // nonaktifkan undoItem satu player
                        rbSolveMinimaxMenuItem.setEnabled(false);
                        rbSolveAlpaBetaMenuItem.setEnabled(false);
                        rbMinimax.setEnabled(false);
                        rbAlphaBetaPruning.setEnabled(false);
                        btSolveGame.setEnabled(false);

                        panelPlayer1.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 153, 0), 1, true),
                                "Langkah " + nama1, TitledBorder.DEFAULT_JUSTIFICATION, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 153, 0)));
                        panelPlayer2.setBorder(BorderFactory.createTitledBorder(new LineBorder(new Color(0, 0, 255), 1, true),
                                "Langkah " + nama2, TitledBorder.LEADING, TitledBorder.DEFAULT_POSITION, new Font("Tahoma", 1, 12), new Color(0, 0, 255)));
                    }
                }
            });

            b.add(btPlay);
            this.setLocationRelativeTo(null);
            this.setLocationByPlatform(true);
            this.setResizable(false);
            this.setSize(490, 400);
            this.setVisible(true);
        }
    }

    public String getNama1() {
        return nama1;
    }

    public String getNama2() {
        return nama2;
    }

    ////////////////////////////////////////////////////////////////////////////
    /**
     * Occurs when user clicks on checker piece
     *
     * @param actionEvent
     */
    private void onPieceClick2Player(ActionEvent actionEvent) {
        try {
            if (game.state.peek().getTurn() == Player.HUMAN) {
                CheckerButton button = (CheckerButton) actionEvent.getSource();
                int source = button.getPosition();
                currentPlayerLabel.remove(this);
                currentPlayerLabel.setIcon(redIconUpdate);
                if (button.getPiece().getPlayer() == game.getTurn().HUMAN) {
                    possibleMoves = game.getValidMoves(source);

                    update2PlayerBoard(); // update board
                    if (possibleMoves.isEmpty()) {
                        MoveMessage feedback = game.moveFeedbackClick(source);
                        updateText(feedback.toString());
                        if (feedback == MoveMessage.FORCED_JUMP) {
                            labelMessage.getToolkit().beep();// beep sound when player forget to take opponent piece
                            //setCurrentPlayerIcon();
                        }
                    } else {
                        updateText("");
                    }
                }
            } else if (game.state.peek().getTurn() == game.getTurn().COMPUTER) {

                CheckerButton button = (CheckerButton) actionEvent.getSource();
                currentPlayerLabel.remove(this);
                currentPlayerLabel.setIcon(whiteIconUpdate);

                int source = button.getPosition();
                if (button.getPiece().getPlayer() == Player.COMPUTER) {
                    possibleMoves = game.getValidMoves(source);

                    update2PlayerBoard(); // update board
                    if (possibleMoves.isEmpty()) {
                        MoveMessage feedback = game.moveFeedbackClick(source);
                        updateText(feedback.toString());
                        if (feedback == MoveMessage.FORCED_JUMP) {
                            labelMessage.getToolkit().beep();// beep sound when player forget to take opponent piece
                            //setCurrentPlayerIcon();
                        }
                    } else {
                        updateText("");
                    }
                }
            }
        } catch (ClassCastException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
        //setCurrentPlayerIcon();
    }

    /**
     * Occurs when user clicks to move checker piece to new (ghost) location.
     *
     * @param actionEvent
     */
    private void onAvailableButtonClick2Player(ActionEvent actionEvent) {
        hintMove = null;
        helpMoves = null;
        if (!game.isGameOver() && game.state.peek().getTurn() == Player.HUMAN) {
            AvailableMoveButton button = (AvailableMoveButton) actionEvent.getSource();
            game.playerMove2(button.getBoardstate());
            possibleMoves = new ArrayList<>();
            // show moveAbles
            helpMoves = game.getState().getSuccessors().stream().map(x -> x.getFromPos()).collect(Collectors.toList());
            update2PlayerBoard();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (game.isGameOver()) {
                        gameOverMessage2();
                    }
                }
            });
            
            updatePieceNoDisplay();
            // tampilkan langkah pemain1
            //redPlayerNoPieces.setText(" " + game.state.peek().getHumanPieceCount());
            textAreaHum.append("--------------------------------------------------------\n");
            textAreaHum.append(" " + game.htgLangkahHum + "). Giliran " + txt1.getText() + ":\n");
            textAreaHum.append(" Langkah : " + game.state.peek().getFromPos() + " - " + game.state.peek().getToPos() + "\n");
            textAreaHum.append(" Skor Kepingan : " + game.state.peek().pieceScore(Player.HUMAN) + "\n");
            textAreaHum.append("--------------------------------------------------------\n\n");
        } else if (!game.isGameOver() && game.state.peek().getTurn() == Player.COMPUTER) {
            
            AvailableMoveButton button = (AvailableMoveButton) actionEvent.getSource();
            game.playerMove2(button.getBoardstate());
            possibleMoves = new ArrayList<>();
            // show moveAbles
            helpMoves = game.getState().getSuccessors().stream().map(x -> x.getFromPos()).collect(Collectors.toList());
            update2PlayerBoard();
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    if (game.isGameOver()) {
                        gameOverMessage2();
                    }
                }
            });
            updatePieceNoDisplay();
            // tampilkan langkah pemain2
            //bluePlayerNoPieces.setText(" " + game.state.peek().getCompPieceCount());
            textAreaComp.append("--------------------------------------------------------\n");
            textAreaComp.append(" " + game.htgLangkahComp + "). Giliran " + txt2.getText() + ":\n");
            textAreaComp.append(" Langkah : " + game.state.peek().getFromPos() + " - " + game.state.peek().getToPos() + "\n");
            textAreaComp.append(" Skor Kepingan : " + game.state.peek().pieceScore(Player.COMPUTER) + "\n");
            textAreaComp.append("--------------------------------------------------------\n\n");
        }
    }
}
