package main.game;

/**
 *
 * @author asus
 */
public class Piece {

    private final Player player;
    private final boolean king;

    public Piece(Player player, boolean king) {
        this.player = player;
        this.king = king;
    }

    public boolean isKing() {
        return king;
    }

    public Player getPlayer() {
        return player;
    }

    /**
     * Get possible y-direction movements
     *
     * @return possible y-direction movements
     */
    public int[] getYMovements() {
        int[] result = new int[]{};
        if (king) {
            result = new int[]{-1, 1};
        } else {
            switch (player) {
                case COMPUTER:
                    result = new int[]{1};
                    break;
                case HUMAN:
                    result = new int[]{-1};
                    break;
            }
        }
        return result;
    }

    /**
     * Get possible x-direction movements
     *
     * @return possible x-direction movements
     */
    public int[] getXMovements() {
        return new int[]{-1, 1};
    }

}
