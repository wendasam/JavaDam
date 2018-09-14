package AI;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import main.game.Board;
import main.game.Player;
import main.game.GameConfig;
import main.game.Game;

/**
 *
 * @author asus
 */
public class AlphaBetaPruning_White {

    // determines the depth that the AlphaBetaPruning_White searches to 
    private final int depth;
    // which player the AlphaBetaPruning_White searches with respect to
    private final Player player;
    private Game game;

    public AlphaBetaPruning_White() {
        depth = GameConfig.AI_DEPTH;
        player = Player.COMPUTER;
    }

    public AlphaBetaPruning_White(final int depth, final Player player) {
        this.depth = depth;
        this.player = player;
    }

    // menggenerate langkah pemain dengan alpha beta pruning
    public Board move(final Board state, final Player player) {
        if (state.getTurn() == player) {
            ArrayList<Board> successors = state.getSuccessors();
            return alphaBetaWhiteMove(successors);
        } else {
            throw new RuntimeException("Alpha Beta Pruning Cannot generate moves for player if it's not their turn");
        }
        
    }

    /**
     * Chooses best successor state based on alphaBetaWhite algorithm.
     *
     * @param successors
     * @return
     */
    private Board alphaBetaWhiteMove(final ArrayList<Board> successors) {
        if (successors.size() == 1) {
            return successors.get(0);
        }
        int bestScore = Integer.MIN_VALUE;
        ArrayList<Board> equalBests = new ArrayList<>();
        for (Board succ : successors) {
            int val = alphaBetaWhite(succ, this.depth);
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
     * @param successors to evaluate pieces move
     * @return the successor value
     */
    public Board randomMove(final ArrayList<Board> successors) {
        if (successors.size() < 1) {
            throw new RuntimeException("AlphaBetaPruning_White Can't randomly choose from empty list.");
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
     * @return alphaBetaWhite score associated with node
     */
    private int alphaBetaWhite(final Board node, final int depth) {
        // initialize alpha (computed as a max)
        int alpha = Integer.MIN_VALUE;
        // initialize beta (computed as a min)
        int beta = Integer.MAX_VALUE;
        // call alphaBetaWhite
        return alphaBetaWhite(node, depth, alpha, beta);
    }

    /**
     * Implements the alphaBetaWhite algorithm with alpha-beta pruning
     *
     * @param state
     * @param depth
     * @param alpha
     * @param beta
     * @return
     */
    private int alphaBetaWhite(final Board node, final int depth, int alpha, int beta) {
        GameConfig.TOTAL_NODES_EXPANDED_FOR_WHITE_ABP = GameConfig.TOTAL_NODES_EXPANDED_FOR_WHITE_ABP.add(BigInteger.ONE);
        // Mengecek apakah depth sekarang adalah depth akhir atau pencarian telah selasai pada
        // pohon permainan
        if (depth == 0 || node.isGameOver()) {
            // mengembalikan fungsi evaluasi hasil pencarian pada pohon pencarian
            return node.computeHeuristic(this.player);
        }
        
        // MAX player = player // memerika kondisi apakah pemain sekarang adalah pemain yang memaksimasi nilai
        if (node.getTurn() == player) {
            //GameConfig.TOTAL_NODES_EXPANDED_FOR_WHITE_ABP = GameConfig.TOTAL_NODES_EXPANDED_FOR_WHITE_ABP.add(BigInteger.ONE);
            // player tries to maximize this value
            int value = Integer.MIN_VALUE;
            for (Board child : node.getSuccessors()) {
                value = Math.max(value, alphaBetaWhite(child, depth - 1, alpha, beta));
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
            //GameConfig.TOTAL_NODES_EXPANDED_FOR_WHITE_ABP = GameConfig.TOTAL_NODES_EXPANDED_FOR_WHITE_ABP.add(BigInteger.ONE);
            // opponent tries to minimize this value
            int value = Integer.MAX_VALUE;
            for (Board child : node.getSuccessors()) {
                value = Math.min(value, alphaBetaWhite(child, depth - 1, alpha, beta));
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
