package cs107;

import java.util.ArrayList;

import static cs107.QOISpecification.*;

/**
 * "Quite Ok Image" Encoder
 *
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @apiNote Second task of the 2022 Mini Project
 * @since 1.0
 */
public final class QOIEncoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIEncoder() {
    }

    // ==================================================================================
    // ============================ QUITE OK IMAGE HEADER ===============================
    // ==================================================================================

    /**
     * Generate a "Quite Ok Image" header using the following parameters
     *
     * @param image (Helper.Image) - Image to use
     * @return (byte[]) - Corresponding "Quite Ok Image" Header
     * @throws AssertionError if the colorspace or the number of channels is corrupted or if the image is null.
     *                        (See the "Quite Ok Image" Specification or the handouts of the project for more information)
     */
    public static byte[] qoiHeader(Helper.Image image) {
//      Verifions que :
//        - l'image n'est pas nul
//        - les valeurs encodant l'image respectent les constantes données
        assert (image != null
                && (RGB == image.channels() || RGBA == image.channels())
                && (sRGB == image.color_space() || ALL == image.color_space()));
//        Declarons les 5 composantes de l'entete :
        byte[] nbMagique = QOI_MAGIC;
        byte[] largeur = ArrayUtils.fromInt(image.data()[0].length);
        byte[] hauteur = ArrayUtils.fromInt(image.data().length);
        byte[] nbCanaux = ArrayUtils.wrap(image.channels());
        byte[] espacesCouleurs = ArrayUtils.wrap(image.color_space());

//        Reunissons les composantes dans un même tableau à l'aide de la fonction concat() :
        byte[] header = ArrayUtils.concat(nbMagique, largeur, hauteur, nbCanaux, espacesCouleurs);
        return header;
    }

    // ==================================================================================
    // ============================ ATOMIC ENCODING METHODS =============================
    // ==================================================================================

    /**
     * Encode the given pixel using the QOI_OP_RGB schema
     *
     * @param pixel (byte[]) - The Pixel to encode
     * @return (byte[]) - Encoding of the pixel using the QOI_OP_RGB schema
     * @throws AssertionError if the pixel's length is not 4
     */
    public static byte[] qoiOpRGB(byte[] pixel) {
//        Verifions que le pixel a bien la bonne taille
        assert (pixel.length == RGBA);

//        assigner a tag la constante QOI_OP_RGB_TAG
        byte[] tag = ArrayUtils.wrap(QOI_OP_RGB_TAG);
//        assigner a rgb les trois premiers termes de pixel
        byte[] rgb = ArrayUtils.extract(pixel, 0, 3);
//        combiner les deux avec concat()
        return ArrayUtils.concat(tag, rgb);
    }

    /**
     * Encode the given pixel using the QOI_OP_RGBA schema
     *
     * @param pixel (byte[]) - The pixel to encode
     * @return (byte[]) Encoding of the pixel using the QOI_OP_RGBA schema
     * @throws AssertionError if the pixel's length is not 4
     */
    public static byte[] qoiOpRGBA(byte[] pixel) {
//        Verifions que le pixl a bien la bonne taille
        assert (pixel.length == RGBA);
//        assigner a tag la constante QOI_OP_RGBA_TAG
        byte[] tag = ArrayUtils.wrap(QOI_OP_RGBA_TAG);
//        Combiner les deux avec concat()
        return ArrayUtils.concat(tag, pixel);
    }

    /**
     * Encode the index using the QOI_OP_INDEX schema
     *
     * @param index (byte) - Index of the pixel
     * @return (byte[]) - Encoding of the index using the QOI_OP_INDEX schema
     * @throws AssertionError if the index is outside the range of all possible indices
     */
    public static byte[] qoiOpIndex(byte index) {
//        Verifions que l'index est bien compris entre 0 et 64
        assert (index < 64 && index > -1);
        byte tag = QOI_OP_INDEX_TAG;

        byte[] encoding = ArrayUtils.wrap((byte) (tag | index)); // ASSISTANT

        return encoding;

    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_DIFF schema
     *
     * @param diff (byte[]) - The difference between 2 pixels
     * @return (byte[]) - Encoding of the given difference
     * @throws AssertionError if diff doesn't respect the constraints or diff's length is not 3
     *                        (See the handout for the constraints)
     */
    public static byte[] qoiOpDiff(byte[] diff) {
//        Verifions que diff n'est pas nul et que sa taille est bien égale à 3 (RGB)
        assert (diff != null && diff.length == RGB);
        for (byte value : diff) {
            assert (-3 < value && value < 2);
        }
        byte tag = QOI_OP_DIFF_TAG;
//        Traitement des differences de chaque canal
        byte dr = (byte) ((diff[r] + 2) << 4);
        byte dg = (byte) ((diff[g] + 2) << 2);
        byte db = (byte) (diff[b] + 2);
//        Assemblage du tout à l'aide de l'opérateur binaire or
        byte encoding = (byte) (tag | dr | dg | db);

        return ArrayUtils.wrap(encoding);

    }

    /**
     * Encode the difference between 2 pixels using the QOI_OP_LUMA schema
     *
     * @param diff (byte[]) - The difference between 2 pixels
     * @return (byte[]) - Encoding of the given difference
     * @throws AssertionError if diff doesn't respect the constraints
     *                        or diff's length is not 3
     *                        (See the handout for the constraints)
     */
    public static byte[] qoiOpLuma(byte[] diff) {
//        Vérifions que les conditions sur les differences sont biens respecté
        assert (diff != null && (diff.length == RGB && diff[g] > -33
                && diff[g] < 32 && (diff[0] - diff[1]) > -9
                && (diff[r] - diff[g]) < 8
                && (diff[b] - diff[g]) > -9
                && (diff[b] - diff[g]) < 8));
//        Traitement des differences de chaque canal
        byte tag = QOI_OP_LUMA_TAG;
        byte dg = (byte) (diff[g] + 32);
        byte dr_dg = (byte) (((diff[r] - diff[g]) + 8) << 4);
        byte db_dg = (byte) ((diff[b] - diff[g]) + 8);
//        Assemblage en deux bytes à l'aide de l'opérateur binaire or
        byte tagDg = (byte) (tag | dg);
        byte varDr_Dg = (byte) (dr_dg | db_dg);

        byte encoding[] = {tagDg, varDr_Dg};

        return encoding;
    }

    /**
     * Encode the number of similar pixels using the QOI_OP_RUN schema
     *
     * @param count (byte) - Number of similar pixels
     * @return (byte[]) - Encoding of count
     * @throws AssertionError if count is not between 0 (exclusive) and 63 (exclusive)
     */
    public static byte[] qoiOpRun(byte count) {

        assert (count > 0 && count < 63);

        byte tag = QOI_OP_RUN_TAG;
        byte countModif = (byte) (count - 1);
        byte encoding = (byte) (tag | countModif);

        return ArrayUtils.wrap(encoding);

    }

    // ==================================================================================
    // ============================== GLOBAL ENCODING METHODS  ==========================
    // ==================================================================================

    /**
     * Encode the given image using the "Quite Ok Image" Protocol
     * (See handout for more information about the "Quite Ok Image" protocol)
     *
     * @param image (byte[][]) - Formatted image to encode
     * @return (byte[]) - "Quite Ok Image" representation of the image
     */
    public static byte[] encodeData(byte[][] image) {
//        Vérifions la condition nécessaire global sur l'image puis les conditions sur chaque pixel à l'aide d'une boucle
        assert (image != null);
        for (byte[] bytes : image) {
            assert (bytes != null && bytes.length == RGBA);

        }
        /** STARTER / Pixel Précédent */
        byte[] prevPix = START_PIXEL;
        /** Table de hashage */
        byte[][] hashTable = new byte[64][RGBA];
        /** Compteur */
        int compteur = 0;
        /** Tableau dynamique */
        ArrayList<byte[]> arrayEncode = new ArrayList<>();

        for (int i = 0; i < image.length; i++) {
            /** Differences RGB */
//            Tableau pour calculer la difference entre le pixel précédent et celui qui suit
//            Ce tableau nous sera utile lorsque qu'on nous opérerons sur les pixels avec Luma et Diff
            byte[] deltaDiff = new byte[RGB];
            deltaDiff[r] = (byte) (image[i][r] - prevPix[r]); //composante R
            deltaDiff[g] = (byte) (image[i][g] - prevPix[g]); //composante G
            deltaDiff[b] = (byte) (image[i][b] - prevPix[b]); //composante B

            /** ===================  QOI_OP_RUN  =================== */
//            Si deux pixel voisins sont confondus, on demarre un compteur qui nous indiquera le nombre de pixels similaires consecutifs :
            if (ArrayUtils.equals(image[i], prevPix)) {
                compteur++;
//                Si le compteur a atteint sa limite, on ajoute un bloc RUN (contenant le compteur) au tableau et on réinitialise se dernier :
                if (compteur == 62 || i == (image.length - 1)) {
                    arrayEncode.add(qoiOpRun((byte) (compteur)));
                    compteur = 0;
                }
                prevPix = image[i];
                continue;
            } else if (compteur > 0) {
                arrayEncode.add(qoiOpRun((byte) compteur));
                compteur = 0;

            }

            /** ==================  QOI_OP_INDEX  ================== */
            int index = hash(image[i]);
//            Si le pixel a la même valeur que le pixel stocké à la position qui lui revient selon sa clé de hachage,
//            on ajoute au tableau un bloc contenant son indice. Sinon, on met à jour la table de hachage en ajoutant le pixel :
            if (ArrayUtils.equals(hashTable[index], image[i])) {
                arrayEncode.add(qoiOpIndex((byte) index));
                prevPix = image[i];
                continue;
            } else {
                hashTable[index] = image[i];

            }


            if (image[i][a] == prevPix[a]) {
                /** ===================  QOI_OP_DIFF  ================== */
//                Nous testons si la taille des differences entre les canaux de deux pixels consecutifs tient dans un bytes.
//                Si les conditions sont respectées, on ajoute un bloc DIFF (contenant les differences des canaux) au tableau.
                if ((deltaDiff[r] < 2 && deltaDiff[r] > -3)
                        && (deltaDiff[g] < 2 && deltaDiff[g] > -3)
                        && (deltaDiff[b] < 2 && deltaDiff[b] > -3)) {

                    arrayEncode.add(qoiOpDiff(deltaDiff));
                    prevPix = image[i];
                    continue;
                }
                /** ===================  QOI_OP_LUMA  ================== */
//                Nous testons si la taille des differences entre les canaux de deux pixels consecutifs tient dans deux bytes.
//                Si les conditions sont respectées, on ajoute un bloc DIFF (contenant les differences des canaux) au tableau.
                if ((deltaDiff[g] > -33)
                        && (deltaDiff[1] < 32)
                        && ((byte) (deltaDiff[r] - deltaDiff[g]) > -9)
                        && ((byte) (deltaDiff[r] - deltaDiff[g]) < 8)
                        && ((byte) (deltaDiff[b] - deltaDiff[g]) > -9)
                        && ((byte) (deltaDiff[b] - deltaDiff[g]) < 8)) {
                    arrayEncode.add(qoiOpLuma(deltaDiff));
                    prevPix = image[i];
                    continue;
                }
                /** ===================  QOI_OP_RGB  =================== */
                // Puisque que le canal alpha est le même entre le pixel courant et le précédent,
                // on ajoute un bloc RGB (vide de la valeur alpha) au tableau.
                arrayEncode.add(qoiOpRGB(image[i]));
                prevPix = image[i];
                continue;
            }
            /** ===================  QOI_OP_RGBA  ================== */
//            Si le canal alpha n'est pas identique d'un pixel à l'autre, et qu'aucune autre optimisation n'est possible,
//            ajouter simplement le pixel comme tel dans le tableau :
            arrayEncode.add(qoiOpRGBA(image[i]));
            prevPix = image[i];
        }
//        Nous récuperons la valeur de la taille du tableau dynamique pour pouvoir ensuite construire le tableau final :
        int totScale = 0;
        for (byte[] tab : arrayEncode) {
            totScale += tab.length;
        }
//         Conversion d'un tableau dynamique (arrayEncode) à un tableau statique (encode) :
        int totCount = 0;
        byte[] encode = new byte[totScale];
        for (byte[] tab : arrayEncode) {
            for (byte value : tab) {
                encode[totCount] = value;
                totCount++;
            }
        }
        return encode;
    }

    /**
     * Creates the representation in memory of the "Quite Ok Image" file.
     *
     * @param image (Helper.Image) - Image to encode
     * @return (byte[]) - Binary representation of the "Quite Ok File" of the image
     * @throws AssertionError if the image is null
     * @apiNote THE FILE IS NOT CREATED YET, THIS IS JUST ITS REPRESENTATION.
     * TO CREATE THE FILE, YOU'LL NEED TO CALL Helper::write
     */
    public static byte[] qoiFile(Helper.Image image) {
        assert (image != null);
//        Assemblage des differentes composantes de l'image encodée, pour en faire representation binaire en Quite Ok File :
        byte[] header = qoiHeader(image);
        int[][] imageData = image.data();
        byte[][] imageChannels = ArrayUtils.imageToChannels(imageData);
        byte[] encoding = encodeData(imageChannels);
        byte[] tag = QOI_EOF;
        byte[] file = ArrayUtils.concat(header, encoding, tag);

        return file;
    }

}