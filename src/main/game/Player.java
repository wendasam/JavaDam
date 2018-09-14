package main.game;

/**
 *
 * @author asus
 */
public enum Player {

    COMPUTER,
    HUMAN;

    // get Lawan main
    public Player getOpposite() {
        Player result = null;
        if (this == COMPUTER) { // COMPUTER vs HUMAN
            result = HUMAN;
        } else if (this == HUMAN) { // HUMAN vs COMPUTER
            result = COMPUTER;
        } 
        if (result == null) {
            throw new RuntimeException("Null player has no opposite.");
        }
        return result;
    }
}
