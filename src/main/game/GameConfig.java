package main.game;

import java.math.BigInteger;

/**
 *
 * @author asus
 */
public class GameConfig {

    public static boolean FORCETAKES = true; //
    public static Player FIRSTMOVE = Player.HUMAN; // who moves first
    public static Player SECONDMOVE = Player.HUMAN;
    public static int AI_DEPTH = 5;
    public static final int UNDO_MEMORY = 200;
    public static int HEURISTIC = 1;
    public static BigInteger TOTAL_NODES_EXPANDED_FOR_WHITE_ABP;
    public static BigInteger TOTAL_NODES_EXPANDED_FOR_RED_ABP;
    public static BigInteger TOTAL_NODES_EXPANDED_FOR_MINIMAX_AI;

    public static void cleanStatistics() {
        TOTAL_NODES_EXPANDED_FOR_WHITE_ABP = BigInteger.ZERO;
        TOTAL_NODES_EXPANDED_FOR_RED_ABP = BigInteger.ZERO;
        TOTAL_NODES_EXPANDED_FOR_MINIMAX_AI = BigInteger.ZERO;
    }
}
