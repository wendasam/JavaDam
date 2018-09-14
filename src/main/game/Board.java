package main.game;

import AI.AlphaBetaPruning_Red;
import AI.AlphaBetaPruning_White;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author asus
 */
public class Board {

    // side length of the board
    public static final int SIDE_LENGTH = 8;
    public static final int BOARD_SIZE = SIDE_LENGTH * SIDE_LENGTH; // 8 x 8
    // state of the board
    Piece[] state;
    // origin and destination position of the most recent move
    private int fromPos = -1;
    private int toPos = -1;
    // origin position of double jump move, used to invalidate other moves during multi-move
    private int doublejumpPos = -1;
    // player's turn
    private Player turn;

    // track number of human/AI pieces on board
    public HashMap<Player, Integer> pieceCount;
    public HashMap<Player, Integer> kingCount;
    private int noHumanPieces, noCompPieces;

    public Board() {
        state = new Piece[Board.BOARD_SIZE];
    }


    /**
     * Set up initial board state.
     *
     * @return board
     */
    public static Board InitialStateVSComputer() {
        Board bs = new Board();
        
        bs.turn = GameConfig.FIRSTMOVE;
        for (int i = 0; i < bs.state.length; i++) {
            int y = i / SIDE_LENGTH;
            int x = i % SIDE_LENGTH;
            // place on black squares only
            if ((x + y) % 2 == 1) {
                // COMPUTER pieces in first 3 rows
                if (y < 3) {
                    bs.state[i] = new Piece(Player.COMPUTER, false);
                } // Human pieces in last 3 rows
                else if (y > 4) {
                    bs.state[i] = new Piece(Player.HUMAN, false);
                }
            }
            
        }
        // count initial pieces (generalizable, not hard-coded)
        int aiCount = (int) Arrays.stream(bs.state).filter(x -> x != null).filter(x -> x.getPlayer() == Player.COMPUTER).count();
        int humanCount = (int) Arrays.stream(bs.state).filter(x -> x != null).filter(x -> x.getPlayer() == Player.HUMAN).count();
        bs.pieceCount = new HashMap<>();
        bs.pieceCount.put(Player.COMPUTER, aiCount);
        bs.pieceCount.put(Player.HUMAN, humanCount);

        bs.kingCount = new HashMap<>();
        bs.kingCount.put(Player.COMPUTER, 0);
        bs.kingCount.put(Player.HUMAN, 0);
        return bs;
    }

    public Board deepCopy() {
        Board bs = new Board();
        System.arraycopy(this.state, 0, bs.state, 0, bs.state.length);
        return bs;
    }

    /**
     * Compute heuristic indicating how desirable this state is to a given
     * player.
     *
     * @param player move ont he board
     * @return RuntimeError
     */
    public int computeHeuristic(Player player) {
        switch (GameConfig.HEURISTIC) {
            case 1:
                return heuristic1(player);
            case 2:
                return heuristic2(player);
        }
        throw new RuntimeException("Invalid heuristic");
    }

    public int heuristic1(Player player) {
        // 'infinite' value for winning
        if (this.pieceCount.get(player.getOpposite()) == 0) {
            return Integer.MAX_VALUE;
        }
        // 'negative infinite' for losing
        if (this.pieceCount.get(player) == 0) {
            return Integer.MIN_VALUE;
        }
        // difference between piece counts with kings counted twice
        return pieceScore(player) - pieceScore(player.getOpposite());
    }

    public int heuristic2(Player player) {
        // 'infinite' value for winning
        if (this.pieceCount.get(player.getOpposite()) == 0) {
            return Integer.MAX_VALUE;
        } // 'negative infinite' for losing
        else if (this.pieceCount.get(player) == 0) {
            return Integer.MIN_VALUE;
        } else {
            return pieceScore(player) / pieceScore(player.getOpposite());
        }
    }

    public int pieceScore(Player player) {
        return this.pieceCount.get(player) + this.kingCount.get(player);
    }

    /**
     * Gets valid successor states for a player
     *
     * @return successor 
     */
    public ArrayList<Board> getSuccessors() {
        // compute jump successors
        ArrayList<Board> successors = getSuccessors(true);
        if (GameConfig.FORCETAKES) {
            if (successors.size() > 0) {
                // return only jump successors if available (forced)
                return successors;
            } else {
                // return non-jump successors (since no jumps available)
                return getSuccessors(false);
            }
        } else {
            // return jump and non-jump successors
            successors.addAll(getSuccessors(false));
            return successors;
        }
    }

    /**
     * Get valid jump or non-jump successor states for a player
     * membangkitkan gerakan-gerakan yang baik untuk dilakukan bagi pemain
     * @param jump pieces on the board
     * @return valid jump or non-jump successor states for a player
     */
    public ArrayList<Board> getSuccessors(boolean jump) {
        ArrayList<Board> result = new ArrayList<>();
        for (int i = 0; i < this.state.length; i++) {
            if (state[i] != null) {
                if (state[i].getPlayer() == turn) {
                    result.addAll(getSuccessors(i, jump));
                }
            }
        }
        return result;
    }

    /**
     * Gets valid successor states for a specific position on the board
     * membangkitkan gerakan yang terbaik
     * @param position on the board
     * @return rsult of valid successor states for a specific position on the board
     */
    public ArrayList<Board> getSuccessors(int position) {
        if (GameConfig.FORCETAKES) {
            // compute jump successors GLOBALLY
            ArrayList<Board> jumps = getSuccessors(true);
            if (jumps.size() > 0) {
                // return only jump successors if available (forced)
                return getSuccessors(position, true);
            } else {
                // return non-jump successors (since no jumps available)
                return getSuccessors(position, false);
            }
        } else {
            // return jump and non-jump successors
            ArrayList<Board> result = new ArrayList<>();
            result.addAll(getSuccessors(position, true));
            result.addAll(getSuccessors(position, false));
            return result;
        }
    }

    /**
     * 
     * @param position kepingan pada papan permainan
     * @param jump 
     * @return 
     */
    public ArrayList<Board> getSuccessors(int position, boolean jump) {
        try {
            if (this.getPiece(position).getPlayer() != turn) {
                throw new IllegalArgumentException("No such piece at that position");
            }

            Piece piece = this.state[position];
            if (jump) {
                return jumpSuccessors(piece, position);
            } else {
                return nonJumpSuccessors(piece, position);
            }
        } catch (RuntimeException ex) {
            throw new RuntimeException("Error: No search piece position");
        }
    }

    /**
     * Gets valid non-jump moves at a given position for a given piece
     *
     * @param piece
     * @param position
     * @return
     */
    private ArrayList<Board> nonJumpSuccessors(Piece piece, int position) {
        ArrayList<Board> result = new ArrayList<>();
        int x = position % SIDE_LENGTH;
        int y = position / SIDE_LENGTH;
        // loop through allowed movement directions
        for (int dx : piece.getXMovements()) {
            for (int dy : piece.getYMovements()) {
                int newX = x + dx;
                int newY = y + dy;
                // new position valid?
                if (isValid(newY, newX)) {
                    // new position available?
                    if (getPiece(newY, newX) == null) {
                        int newpos = SIDE_LENGTH * newY + newX;
                        result.add(createNewState(position, newpos, piece, false, dy, dx));
                    }
                }
            }
        }
        return result;
    }

    /**
     * Gets valid jump moves at a given position for a given piece
     *
     * @param piece
     * @param position
     * @return
     */
    private ArrayList<Board> jumpSuccessors(Piece piece, int position) {
        ArrayList<Board> result = new ArrayList<>();
        // no other jump moves are valid while doing double jump
        if (doublejumpPos > 0 && position != doublejumpPos) {
            return result;
        }
        int x = position % SIDE_LENGTH;
        int y = position / SIDE_LENGTH;
        // loop through allowed movement directions
        for (int dx : piece.getXMovements()) {
            for (int dy : piece.getYMovements()) {
                int newX = x + dx;
                int newY = y + dy;

                // new position valid?
                if (isValid(newY, newX)) {

                    // new position contain opposite player?
                    if (getPiece(newY, newX) != null && getPiece(newY, newX).getPlayer() == piece.getPlayer().getOpposite()) {
                        newX = newX + dx;
                        newY = newY + dy;

                        // jump position valid?
                        if (isValid(newY, newX)) {

                            // jump position available?
                            if (getPiece(newY, newX) == null) {
                                int newpos = SIDE_LENGTH * newY + newX;
                                result.add(createNewState(position, newpos, piece, true, dy, dx));
                            }
                        }
                    }
                }
            }
        }
        return result;
    }
    
    public boolean isBlocked(Player player) {
        ArrayList<Board> successors = getSuccessors(true);
        return successors.isEmpty();
    }

    private Board createNewState(int oldPos, int newPos, Piece piece, boolean jumped, int dy, int dx) {
        Board result = this.deepCopy();
        result.pieceCount = new HashMap<>(pieceCount);
        result.kingCount = new HashMap<>(kingCount);

        // check if king position
        boolean kingConversion = false;
        if (isKingPosition(newPos, piece.getPlayer())) {
            piece = new Piece(piece.getPlayer(), true);
            kingConversion = true;

            // increase king count
            result.kingCount.replace(piece.getPlayer(), result.kingCount.get(piece.getPlayer()) + 1);
        }
        // move piece
        result.state[oldPos] = null;
        result.state[newPos] = piece;

        // store meta data
        result.fromPos = oldPos;
        result.toPos = newPos;
        Player oppPlayer = piece.getPlayer().getOpposite();
        result.turn = oppPlayer;
        if (jumped) {

            // remove captured piece
            result.state[newPos - SIDE_LENGTH * dy - dx] = null;
            result.pieceCount.replace(oppPlayer, result.pieceCount.get(oppPlayer) - 1);

            // is another jump available? (not allowed if just converted into king)
            if (result.jumpSuccessors(piece, newPos).size() > 0 && kingConversion == false) {

                // don't swap turns
                result.turn = piece.getPlayer();

                // remember double jump position
                result.doublejumpPos = newPos;
            }
        }
        return result;
    }

    private boolean isKingPosition(int pos, Player player) {
        int y = pos / SIDE_LENGTH;
        if (y == 0 && player == Player.HUMAN) {
            return true;
        } else {
            return y == SIDE_LENGTH - 1 && player == Player.COMPUTER;
        }
    }
    
    /**
     * Gets the destination position of the most recent move.
     *
     * @return destination square
     */
    public int getToPos() {
        return this.toPos;
    }

    /**
     * Gets the destination position of the most recent move.
     *
     * @return source pieces position
     */
    public int getFromPos() {
        return this.fromPos;
    }

    /**
     * Gets the player whose turn it is
     *
     * @return player turn
     */
    public Player getTurn() {
        return turn;
    }

    /**
     * Is the board in a game over state?
     *
     * @return if game is over
     */
    public boolean isGameOver() {
        return (pieceCount.get(Player.COMPUTER) == 0 || pieceCount.get(Player.HUMAN) == 0);
    }
    
    public boolean isHumanLose() {
        return (pieceCount.get(Player.HUMAN) == 0);
    }
    
    public boolean isComputerLose() {
        return (pieceCount.get(Player.COMPUTER) == 0);
    }
    
    public int getHumanPieceCount() {
        return (pieceCount.get(Player.HUMAN) - 0);
    }
    
    public int getCompPieceCount() {
        return (pieceCount.get(Player.COMPUTER) - 0);
    }
    
    // returns the number of player pieces
    public int noHumanPieces() {
        return noHumanPieces;
    }

    // returns the number of computer pieces
    public int noCompPieces() {
        return noCompPieces;
    }

    /**
     * Get player piece at given position.
     *
     * @param i Position in board.
     * @return state pieces on the board
     */
    public Piece getPiece(int i) {
        return state[i];
    }

    /**
     * Get piece by grid position
     */
    private Piece getPiece(int y, int x) {
        return getPiece(SIDE_LENGTH * y + x);
    }

    /**
     * Check if grid indices are valid
     */
    private boolean isValid(int y, int x) {
        return (0 <= y) && (y < SIDE_LENGTH) && (0 <= x) && (x < SIDE_LENGTH);
    }
}
