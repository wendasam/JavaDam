package main.gui;

import main.game.Player;

/**
 *
 * @author asus
 */
public class GameSettings {

    public static Colour AI_COLOUR; // Note: starting player gets white pieces
    public static boolean HELP_MODE = true;
    public static boolean HINT_MODE = false;
    public static boolean DRAG_DROP = false;
    public static int AiMinPauseDurationInMs = 800;
    public static final int SQUARE_SIZE = 70;
    public static int CHECKERS_WIDTH = 5 * SQUARE_SIZE / 6;
    public static int CHECKERS_HEIGHT = 5 * SQUARE_SIZE / 6;
    public static int availableButtonWidth = 30 * SQUARE_SIZE / 29;
    public static int availableButtonHeight = 5 * SQUARE_SIZE / 6;
    public static final String RULES = "ATURAN PERMAINAN\n\n"
            + "1. Gerakan hanya diijinkan pada kotak yang berwarna gelap, sehingga kepingan selalu bergerak secara diagonal.\n"
            + "    Gerakan kepingan biasa terbatas shingga gerakan selalu bergerak maju(ke arah lawan).\n"
            + "2. Gerakan sebuah kepingan yang tidak menangkap kepingan lawan (melakukan \n"
            + "     lompatan) hanya boleh dapat bergerak maju satu kotak.\n"
            + "3. Sebuah kepingan yang melakukan gerakan lompatan dapat melompati kepingan lawan yang berada didepannya,\n"
            + "    dan mendarat pada kotak kosong setelah kepingan lawan yang dilompati tersebut. Kepingan tersebut dapat\n"
            + "    melompati kepingan lawan lainnya apabila terdapat kesempatan lebih dari dari satu kali lompatan.\n"
            + "4. Ketika suatu kepingan dilompati oleh kepingan lawan, maka kepingan tersebut dikeluarkan dari papan permainan.\n"
            + "5. Apabila salah satu pemain memiliki kesempatan untuk melompati kepingan lawan maka, mau dan tidak pemain\n"
            + "   tersebut harus melakukan lompatan. Dan apabila terdapat lebih dari satu kesempatan melompat yang tersedia maka\n"
            + "    pemain dapat memilih langkah mana yang tepat untuk dilakukan.\n"
            + "6. Ketika sebuah kepingan biasa yang telah sampai pada baris raja di posisi\n"
            + "    lawan maka kepingan tersebut dimahkotai dan menjadi raja.\n"
            + "7. Cara gerak kepingan raja sama dengan biasa yaitu bergerak dari satu kotak ke kotak lainya, hanya saja kepingan\n"
            + "    raja bebas dapat bergerak maju dan mundur pada arah diagonal.\n"
            + "8. Permainan berakhir apabila salah satu kepingan milik pemain telah habis atau terdapat jalan buntu dalam permainan.\n\n"
            + "   Selamat Bermain.";
    
    public static final String ABOUT = "PROJECT TUGAS AKHIR\n\n"
            + "Aplikasi permainan Dam ini merupakan aplikasi yang dibangun\n"
            + "dengan menerapkan algoritma Minimax dan Alpha Beta Pruning, namun\n"
            + "dalam penerapannya algoritma Alpha Beta Pruning diterapkan sebagai\n"
            + "agen cerdas yang dapat bermain permainan Dam ini melawan\n"
            + "pemain (User). Dan juga kedua algoritma tersebut dapat digunakan\n"
            + "oleh pemain untuk medapatkan solusi permainan.\n\n"
            + ""
            + "Nama      : Sam T. Wenda\n"
            + "NIM          : 14 411 101\n"
            + "Jurusan  : Teknik Informatika S1\n\n"
            + "                      Fakultas Ilmu Komputer dan Manajemen\n"
            + "                    Universitas Sains dan Teknologi Jayapura\n"
            + "                                                       2018.";

    /**
     * Gets the correct colour (black/white) for the given player
     *
     * @param player black/white player
     * @return result of correct colour (black/white) for the given player
     */
    public static Colour getColour(final Player player) {
        Colour result = null;
        if (player == Player.COMPUTER) 
        {
            result = GameSettings.AI_COLOUR;
        } 
        else if (player == Player.HUMAN) 
        {
            result = GameSettings.AI_COLOUR.getOpposite();
        } 
        
        if (result == null) 
        {
            throw new RuntimeException("Null player has no piece.");
        }
        return result;
    }
}
