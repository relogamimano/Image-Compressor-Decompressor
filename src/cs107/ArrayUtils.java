package cs107;

import static cs107.QOISpecification.*;

/**
 * Utility class to manipulate arrays.
 *
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @apiNote First Task of the 2022 Mini Project
 * @since 1.0
 */
public final class ArrayUtils {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private ArrayUtils() {
    }

    // ==================================================================================
    // =========================== ARRAY EQUALITY METHODS ===============================
    // ==================================================================================

    /**
     * Check if the content of both arrays is the same
     *
     * @param a1 (byte[]) - First array
     * @param a2 (byte[]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[] a1, byte[] a2) {
//        Testons si deux tableaux à une dimension sont similaires :
        if (a1 == null && a2 == null) {
            return true;
        }
        assert (a1 != null && a2 != null);

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (a1[i] != a2[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Check if the content of both arrays is the same
     *
     * @param a1 (byte[][]) - First array
     * @param a2 (byte[][]) - Second array
     * @return (boolean) - true if both arrays have the same content (or both null), false otherwise
     * @throws AssertionError if one of the parameters is null
     */
    public static boolean equals(byte[][] a1, byte[][] a2) {
//        Testons si deux tableaux à deux dimensions sont similaires :
        if (a1 == null && a2 == null) {
            return true;
        }
        assert (a1 != null && a2 != null);

        if (a1.length != a2.length) {
            return false;
        }

        for (int i = 0; i < a1.length; i++) {
            if (!equals(a1[i], a2[i])) {
                return false;
            }
        }
        return true;
    }


    // ==================================================================================
    // ============================ ARRAY WRAPPING METHODS ==============================
    // ==================================================================================

    /**
     * Wrap the given value in an array
     *
     * @param value (byte) - value to wrap
     * @return (byte[]) - array with one element (value)
     */
    public static byte[] wrap(byte value) {
//        Enveloppons la valeur du byte dans un tableau dont cette dernière est le seul element :
        byte[] tab = {value};
        return tab;
    }

    // ==================================================================================
    // ========================== INTEGER MANIPULATION METHODS ==========================
    // ==================================================================================

    /**
     * Create an Integer using the given array. The input needs to be considered
     * as "Big Endian"
     * (See handout for the definition of "Big Endian")
     *
     * @param bytes (byte[]) - Array of 4 bytes
     * @return (int) - Integer representation of the array
     * @throws AssertionError if the input is null or the input's length is different from 4
     */
    public static int toInt(byte[] bytes) {
        assert (bytes != null && bytes.length == RGBA);
//        Construisons un entier contenant les 4 differents bytes selon le schema big-endian
//        0xff est une valeur en hexadecimale qui permet de passer le byte d'une valeur signée à une valeur non signée
        int value = (bytes[0] & 0xff) << 24 | (bytes[1] & 0xff) << 16 | (bytes[2] & 0xff) << 8 | (bytes[3] & 0xff);
        return value;

    }

    /**
     * Separate the Integer (word) to 4 bytes. The Memory layout of this integer is "Big Endian"
     * (See handout for the definition of "Big Endian")
     *
     * @param value (int) - The integer
     * @return (byte[]) - Big Endian representation of the integer
     */
    public static byte[] fromInt(int value) {
//        Decomposons un entier contenant les 4 differents bytes selon le schema big-endian
        byte tab[] = new byte[RGBA];
        for (int i = 0; i < tab.length; i++) {
            tab[i] = (byte) (value >>> (24 - 8 * i));
        }
        return tab;
    }


    // ==================================================================================
    // ========================== ARRAY CONCATENATION METHODS ===========================
    // ==================================================================================

    /**
     * Concatenate a given sequence of bytes and stores them in an array
     *
     * @param bytes (byte ...) - Sequence of bytes to store in the array
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     */
    public static byte[] concat(byte... bytes) {
//        Concaténons une série de bytes dans un tableau du même type :
        assert (bytes != null);
        byte[] tab = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            tab[i] = bytes[i];
        }
        return tab;
    }

    /**
     * Concatenate a given sequence of arrays into one array
     *
     * @param tabs (byte[] ...) - Sequence of arrays
     * @return (byte[]) - Array representation of the sequence
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null.
     */
    public static byte[] concat(byte[]... tabs) {
//        Concaténons une série de tableaux, à une dimension, de bytes dans un tableau, du même type, à deux dimensions :
        assert (tabs != null);
//        On cherche d'abord à connaitre le nombre de tableaux prit en entrée
        int longueur = 0;
        for (byte[] tab : tabs) {
            assert (tab != null);
            longueur += tab.length;
        }
        int compteur = 0;
        byte[] tabConcat = new byte[longueur];
        for (byte[] tab : tabs) {
            for (byte value : tab) {
                tabConcat[compteur] = value;
                compteur++;
            }
        }
        return tabConcat;
    }

    // ==================================================================================
    // =========================== ARRAY EXTRACTION METHODS =============================
    // ==================================================================================

    /**
     * Extract an array from another array
     *
     * @param input  (byte[]) - Array to extract from
     * @param start  (int) - Index in the input array to start the extract from
     * @param length (int) - The number of bytes to extract
     * @return (byte[]) - The extracted array
     * @throws AssertionError if the input is null or start and length are invalid.
     *                        start + length should also be smaller than the input's length
     */
    public static byte[] extract(byte[] input, int start, int length) {
//        Cette fonction renvoie une copie d'un morceau délimité du tableau de bytes :
        assert (input != null && length >= 0 && start < input.length && (start + length) <= input.length);
        byte tabExtract[] = new byte[length];
        for (int i = 0; i < tabExtract.length; i++) {
            tabExtract[i] = input[i + start];
        }
        return tabExtract;
    }

    /**
     * Create a partition of the input array.
     * (See handout for more information on how this method works)
     *
     * @param input (byte[]) - The original array
     * @param sizes (int ...) - Sizes of the partitions
     * @return (byte[][]) - Array of input's partitions.
     * The order of the partition is the same as the order in sizes
     * @throws AssertionError if one of the parameters is null
     *                        or the sum of the elements in sizes is different from the input's length
     */
    public static byte[][] partition(byte[] input, int... sizes) {
//        Cette fonction divise un tableau de bytes en plusieurs plus petits tableaux consecutifs dont les tailles sont données en entrée
//        Verifions que les entrées ne soient pas nulles et que la somme des tailles est bien égale à la taille du tableau final
        assert (input != null && sizes != null);
        int verifSizes = 0;
        for (int size : sizes) {
            verifSizes += size;
        }

        assert (verifSizes == input.length);

        byte partTab[][] = new byte[sizes.length][];
        for (int i = 0; i < partTab.length; i++) {
            partTab[i] = new byte[sizes[i]];
        }
        int debut = 0;
        for (int i = 0; i < partTab.length; i++) {
            partTab[i] = extract(input, debut, sizes[i]);
            debut += sizes[i];
        }
        return partTab;
    }

    // ==================================================================================
    // ============================== ARRAY FORMATTING METHODS ==========================
    // ==================================================================================

    /**
     * Format a 2-dim integer array
     * where each dimension is a direction in the image to
     * a 2-dim byte array where the first dimension is the pixel
     * and the second dimension is the channel.
     * See handouts for more information on the format.
     *
     * @param input (int[][]) - image data
     * @return (byte [][]) - formatted image data
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null
     */
    public static byte[][] imageToChannels(int[][] input) {
//        Cette fonction crée tableau à deux dimensions (de bytes) dans lequel chaque colonne contient l’ensemble des bytes d’un canal donné, et chaque ligne un pixel donné.
        assert (input != null);
        int longueur = (input.length) * (input[0].length);
//        pixelIndex est l'indice qui se chargera de parcourir tous les pixels contenus dans le tableau pixelChannel
        int pixelIndex = 0;
        byte[][] pixelChannel = new byte[longueur][RGBA];

        for (int[] ints : input) {
            assert (ints != null && ints.length == input[0].length);
        }
        for (int[] ints : input) {
            for (int anInt : ints) {
//                Nous ré-ordonnons les canaux ARGB en RGBA pixel par pixel :
                byte[] valueSwitch = fromInt(anInt);
                pixelChannel[pixelIndex][r] = valueSwitch[g];
                pixelChannel[pixelIndex][g] = valueSwitch[b];
                pixelChannel[pixelIndex][b] = valueSwitch[a];
                pixelChannel[pixelIndex][a] = valueSwitch[r];

                pixelIndex++;
            }
        }
        return pixelChannel;
    }

    /**
     * Format a 2-dim byte array where the first dimension is the pixel
     * and the second is the channel to a 2-dim int array where the first
     * dimension is the height and the second is the width
     *
     * @param input  (byte[][]) : linear representation of the image
     * @param height (int) - Height of the resulting image
     * @param width  (int) - Width of the resulting image
     * @return (int[][]) - the image data
     * @throws AssertionError if the input is null
     *                        or one of the inner arrays of input is null
     *                        or input's length differs from width * height
     *                        or height is invalid
     *                        or width is invalid
     */
    public static int[][] channelsToImage(byte[][] input, int height, int width) {
//        Cette fonction rétablit la structure en 2 dimensions d’une image dont les canaux RGBA ont été décomposés.
        assert (input != null & input.length == (height * width));
        for (byte[] bytes : input) {
            assert (bytes != null && bytes.length == RGBA);
        }
        int[][] imageInt = new int[height][width];
//        L'indice index a pour but de parcourir chaque pixel dans le tableau input
        int index = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
//                Nous ré-ordonnons les canaux RGBA en ARGB pixel par pixel:
                byte[][] pixelTab = partition(input[index], RGB, 1); // 1 correspond à la taille du canal Alpha
                byte[] tabARGB = concat(pixelTab[1], pixelTab[0]);
                imageInt[i][j] = toInt(tabARGB);

                ++index;
            }
        }
        return imageInt;
    }

}