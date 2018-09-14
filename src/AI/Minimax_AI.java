package AI;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import main.game.Board;
import main.game.Game;
import main.game.Player;
import main.game.GameConfig;

/**
 *
 * @author asus
 */
public class Minimax_AI {

    // menentukan kedalam untuk algoritma minimax
    private int depth;
    private Game game;

    // pemain yang memainkan permainan
    private Player player;

    // konstruktor
    public Minimax_AI() {
        depth = GameConfig.AI_DEPTH;
        player = Player.COMPUTER;
    }

    public Minimax_AI(final int depth,  Player player) {
        this.depth = depth;
        this.player = player;
    }

    /**
     * menggenerate langkah pemain dengan minimax
     *
     * @param state board
     * @param player white/black
     * @return RuntimeError 
     */
    public Board move(final Board state, final Player player) {
        if (state.getTurn() == player) {
            ArrayList<Board> successors = state.getSuccessors();
            return MinimaxMove(successors);
        } else {
            throw new RuntimeException("Cannot generate moves for player if it's not their turn");
        }
    }

    /**
     * Chooses best successor state based on minimax algorithm
     *
     * @param successors
     * @return
     */
    private Board MinimaxMove(final ArrayList<Board> successors) {
        if (successors.size() == 1) {
            return successors.get(0);
        }
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Board> equalBests = new ArrayList<>();
        for (Board succ : successors) {
            int val = Minimax(succ, this.depth, this.player);
            if (val > bestScore) {
                bestScore = val;
                equalBests.clear();
            }
            if (val == bestScore) {
                equalBests.add(succ);
            }
        }
        if (equalBests.size() > 1) {
            System.out.println(player.toString() + " choosing random best move");
        }
        // choose randomly from equally scoring best moves
        return randomMove(equalBests);
    }

    /**
     * Chooses a successor state randomly.
     *
     * @param successors to evaluate pieces moves
     * @return successors
     */
    public Board randomMove(final ArrayList<Board> successors) {
        if (successors.size() < 1) {
            throw new RuntimeException("Minimax Can't randomly choose from empty list.");
        }
        Random rand = new Random();
        int i = rand.nextInt(successors.size());
        return successors.get(i);
    }

    /**
     * Implements the minimax
     *
     * @param succ
     * @param depth
     * @return
     */
    private int Minimax(Board succ, int depth, Player maximizingPlayer) {
        // initialize val (computed as a max)
        int val = Integer.MIN_VALUE;

        // call Minimax_AI
        return Minimax(succ, depth, val, maximizingPlayer);
    }

    /**
     * Implements Minimax algorithm
     *
     * @param state board
     * @param depth kedalaman
     * @param val nilai evaluasi yang didapatkan dari pencarian
     * @param maximizingPlayer pemain yang melakukan maksimasi
     * @return best value 
     */
    public int Minimax(final Board state, final int depth, int val, Player maximizingPlayer) {
        GameConfig.TOTAL_NODES_EXPANDED_FOR_MINIMAX_AI = GameConfig.TOTAL_NODES_EXPANDED_FOR_MINIMAX_AI.add(BigInteger.ONE);
        // Mengecek apakah depth sekarang adalah depth akhir atau pencarian telah selasai pada
        // pohon permainan
        if (depth == 0 || state.isGameOver()) {
            // mengembalikan fungsi evaluasi hasil pencarian pada pohon pencarian
            return state.computeHeuristic(maximizingPlayer);
        }

        // MAX player = player // memerika kondisi apakah pemain sekarang adalah pemain yang memaksimasi nilai
        if (state.getTurn() == maximizingPlayer) {
            // Pemain berusaha untuk memaksimasi pergerakan
            int value = 0;
            int bestVal = Integer.MIN_VALUE;
            for (Board child : state.getSuccessors()) {
                value = Minimax(child, depth - 1, val, maximizingPlayer);
                bestVal = Math.max(bestVal, val);
            }
            return bestVal;
        }

        // MIN player = opponent || (* minimizing player *)
        if (state.getTurn() == player.getOpposite()) {
            // lawan berusaha untuk meminimasi pergerakan || nilai
            int value = 0;
            int bestVal = Integer.MAX_VALUE;
            for (Board child : state.getSuccessors()) {
                value = Minimax(child, depth - 1, val, maximizingPlayer);
                bestVal = Math.min(bestVal, val);
            }
            return bestVal;
        }
        throw new RuntimeException("Error minimax algorithm");
    }

}
