package main.game;

import AI.AlphaBetaPruning_Red;
import AI.AlphaBetaPruning_White;
import AI.Minimax_AI;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;
import javax.swing.JOptionPane;

/**
 *
 * @author asus
 */
public class Game {

    private Board board;
    public Stack<Board> state;
    private final int memory;
    private AlphaBetaPruning_White alphaBetaWhite;
    private AlphaBetaPruning_Red alphaBetaRed;
    private Minimax_AI minimax;
    private String nama;
    public long htgLangkahHum = 0;
    public long htgLangkahComp = 0;
    public boolean isGameStopped = false;

    private boolean player2Won;
    private boolean player1Won;

    public Game() {
        memory = GameConfig.UNDO_MEMORY;
        state = new Stack<>();
        board = new Board();
        state.push(Board.InitialStateVSComputer());
        
        alphaBetaWhite = new AlphaBetaPruning_White();
        alphaBetaRed = new AlphaBetaPruning_Red();
        minimax = new Minimax_AI();
    }

    public void playerMove(final Board newState) {
        if (!isGameOver() && state.peek().getTurn() == Player.HUMAN) {
            updateState(newState);
        }
        htgLangkahHum++;
    }

    public void playerMove2(final Board newState) {
        if (!isGameOver() && state.peek().getTurn() == Player.HUMAN) {
            updateState(newState);
            htgLangkahHum++;
        }else if (!isGameOver() && state.peek().getTurn() == Player.COMPUTER) {
            updateState(newState);
            htgLangkahComp++;
        }
    }

    // fungsi untuk memberikan 
    public MoveMessage playerMove(final int fromPos, final int dx, final int dy) {
        final int toPos = fromPos + dx + Board.SIDE_LENGTH * dy;
        if (toPos > getState().state.length) {
            return MoveMessage.NOT_ON_BOARD;
        }

        // mengecek kemungkinan gerakan yang harus meompati kepingan lawan
        final ArrayList<Board> jumpSuccessors = this.state.peek().getSuccessors(true);
        final boolean jumps = jumpSuccessors.size() > 0;
        if (jumps) {
            for (Board succ : jumpSuccessors) {
                if (succ.getFromPos() == fromPos && succ.getToPos() == toPos) {
                    updateState(succ);
                    return MoveMessage.SUCCESS;
                }
            }
            return MoveMessage.FORCED_JUMP;
        }

        // check diagonal
        if (Math.abs(dx) != Math.abs(dy)) {
            return MoveMessage.NOT_DIAGONAL;
        }

        // melihat kemungkinan langkah kepingan
        if (this.getState().state[toPos] != null) {
            return MoveMessage.NO_FREE_SPACE;
        }

        // melihat kemungkinan gerakan yang tidak melompat
        final ArrayList<Board> nonJumpSuccessors = this.state.peek().getSuccessors(fromPos, false);
        for (Board succ : nonJumpSuccessors) {
            if (succ.getFromPos() == fromPos && succ.getToPos() == toPos) {
                updateState(succ);
                return MoveMessage.SUCCESS;
            }
        }
        if (dy > 1) {
            return MoveMessage.NO_BACKWARD_MOVES_FOR_SINGLES;
        }

        if (Math.abs(dx) == 2) {
            return MoveMessage.ONLY_SINGLE_DIAGONALS;
        }
        
        return MoveMessage.UNKNOWN_INVALID;
    }

    public MoveMessage moveFeedbackClick(final int pos) {
        final ArrayList<Board> jumpSuccessors = this.state.peek().getSuccessors(true);
        if (jumpSuccessors.size() > 0) {
            return MoveMessage.FORCED_JUMP;
        } else {
            return MoveMessage.PIECE_BLOCKED;
        }
    }

    /**
     *
     * @param pos current position
     * @return state if found validMove for pieces
     */
    public ArrayList<Board> getValidMoves(final int pos) {
        return state.peek().getSuccessors(pos);
    }

    public void computerMove() {
        // update state with AlphaBetaPruning_White move
        if (!isGameOver() && state.peek().getTurn() == Player.COMPUTER) {
            alphaBetaWhite = new AlphaBetaPruning_White(GameConfig.AI_DEPTH, Player.COMPUTER);
            Board newState = alphaBetaWhite.move(this.state.peek(), Player.COMPUTER);
            updateState(newState);
            htgLangkahComp++;
        } 
    }

    // Ai akan mengambil alih kepingan player ketika pemain ingin dapatkan solusi dengan algoritma
    public void solveAlphaBetaRed() {
        // update stake with AlphaBetaPruning_Red move
        if (!isGameOver() && state.peek().getTurn() == Player.HUMAN) {
            alphaBetaRed = new AlphaBetaPruning_Red(GameConfig.AI_DEPTH, Player.HUMAN); // memberi kedalaman kepada AI untuk pemain
            Board newState = alphaBetaRed.move(this.state.peek(), Player.HUMAN);
            updateState(newState);
            htgLangkahHum++;
        }
    }

    // Ai akan mengambil alih kepingan player ketika pemain ingin dapatkan solusi dengan game
    // algoritma alpha beta pruning & pada method ini player diambil alih oleh COMPUTER
    public void solveMinimaxMove() {
        // update stake with COMPUTER move
        if (!isGameOver() && state.peek().getTurn() == Player.HUMAN) {
            minimax = new Minimax_AI(7, Player.HUMAN); // memberi kedalaman kepada AI untuk pemain
            Board newState = minimax.move(this.state.peek(), Player.HUMAN);
            updateState(newState);
            htgLangkahHum++;
        }
    }

    // memperbaharui status permainan
    private void updateState(final Board newState) {
        state.push(newState);
        if (state.size() > memory) {
            state.remove(0);
        }
    }
    
    // mengembalikan nilai status
    public Board getState() {
        return state.peek();
    }

    // untu mendapatkan giliran main
    public Player getTurn() {
        return state.peek().getTurn();
    }

    // mengecek status permainan
    public boolean isGameOver() {
        boolean isOver = state.peek().isGameOver();
        if (isOver) {
            // get win / lose status
            player1Won = state.peek().noHumanPieces() == 0;
            player2Won = state.peek().noHumanPieces() == 0;
        }
        return isOver;
    }

    // kembalikan posisi sebelumnya
    public void undo() {
        if (state.size() > 2) {
            state.pop();
            while (state.peek().getTurn() == Player.COMPUTER) {
                state.pop();
            }
        }
    }

    public void undo1() {
        if (state.size() > 2) {
            state.pop();
            while (state.peek().getTurn() == Player.COMPUTER) {
                state.pop();
            }
        }
    }
}
