/*
 * Alpha Beta Pwhite use by player to get game solution
 */
package AI;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import main.game.Board;
import main.game.Game;
import main.game.GameConfig;
import main.game.Player;

/**
 * public class AlphaBetaPruning_Red class
 *
 * @author asus
 */
public class AlphaBetaPruning_Red {

    // determines the depth that the AlphaBetaPruning_White searches to 
    private int depth;
    // which player the AlphaBetaPruning_White searches with respect to
    private Player player;
    private Game game;

    public AlphaBetaPruning_Red() {
        depth = GameConfig.AI_DEPTH;
        player = Player.HUMAN;
    }

    public AlphaBetaPruning_Red(final int depth, final Player player) {
        this.depth = depth;
        this.player = player;
    }

    // menggenerate langkah pemain dengan alpha beta pruning
    public Board move(final Board state, final Player player) {
        if (state.getTurn() == player) {
            ArrayList<Board> successors = state.getSuccessors();
            return alphaBetaRedMove(successors);
        } else {
            throw new RuntimeException("Alpha Beta Pruning Cannot generate moves for player if it's not their turn");
        }

    }

    /**
     * Chooses best successor state based on alphaBetaRed algorithm.
     *
     * @param successors
     * @return
     */
    private Board alphaBetaRedMove(final ArrayList<Board> successors) {
        if (successors.size() == 1) {
            return successors.get(0);
        }
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Board> equalBests = new ArrayList<>();
        for (Board succ : successors) {
            int val = alphaBetaRed(succ, this.depth);
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
     * @param successors of pieces move on the board
     * @return successor value of pieces
     */
    public Board randomMove(ArrayList<Board> successors) {
        if (successors.size() < 1) {
            throw new RuntimeException("AlphaBetaPruning_Red Can't randomly choose from empty list.");
       }
        Random rand = new Random();
        int i = rand.nextInt(successors.size());
        return successors.get(i);
    }

    /**
     * Implements the minimax algorithm with alpha-beta pruning
     *
     * @param node
     * @param depth
     * @return alphaBetaRed score associated with node
     */
    private int alphaBetaRed(final Board node, final int depth) {
        // initialize alpha (computed as a max)
        int alpha = Integer.MIN_VALUE;
        // initialize beta (computed as a min)
        int beta = Integer.MAX_VALUE;
        // call alphaBetaRed
        return AlphaBetaPruning_Red.this.alphaBetaRed(node, depth, alpha, beta);
    }

    /**
     * Implements the alphaBetaRed algorithm with alpha-beta pruning
     *
     * @param state
     * @param depth
     * @param alpha
     * @param beta
     * @return
     */
    private int alphaBetaRed(final Board node, final int depth, int alpha, int beta) {
        GameConfig.TOTAL_NODES_EXPANDED_FOR_RED_ABP = GameConfig.TOTAL_NODES_EXPANDED_FOR_RED_ABP.add(BigInteger.ONE);
        // Mengecek apakah depth sekarang adalah depth akhir atau pencarian telah selasai pada
        // pohon permainan
        if (depth == 0 || node.isGameOver()) {
            // mengembalikan fungsi evaluasi hasil pencarian pada pohon pencarian
            return node.computeHeuristic(this.player);
        }

        // MAX player = player // memerika kondisi apakah pemain sekarang adalah pemain yang memaksimasi nilai
        if (node.getTurn() == player) {
            // player tries to maximize this value
            int value = Integer.MIN_VALUE;
            for (Board child : node.getSuccessors()) {
                value = Math.max(value, alphaBetaRed(child, depth - 1, alpha, beta));
                alpha = Math.max(alpha, value);
                // prune
                if (alpha >= beta) {
                    break; // (* β cut-off *)
                }
            }
            return value;
        }

        // MIN player = opponent
        if (node.getTurn() == player.getOpposite()) {
            // opponent tries to minimize this value
            int value = Integer.MAX_VALUE;
            for (Board child : node.getSuccessors()) {
                value = Math.min(value, alphaBetaRed(child, depth - 1, alpha, beta));
                beta = Math.min(beta, value);
                // prune
                if (alpha >= beta) {
                    break; // (* α cut-off *)
                }
            }
            return value;
        }
        throw new RuntimeException("Error in AlphaBetaSearch algorithm");
    }
}
