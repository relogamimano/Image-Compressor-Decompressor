package cs107;

import static cs107.Helper.Image;

/**
 * "Quite Ok Image" Decoder
 *
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @apiNote Third task of the 2022 Mini Project
 * @since 1.0
 */
public final class QOIDecoder {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7.
     */
    private QOIDecoder() {
    }

    // ==================================================================================
    // =========================== QUITE OK IMAGE HEADER ================================
    // ==================================================================================

    /**
     * Extract useful information from the "Quite Ok Image" header
     *
     * @param header (byte[]) - A "Quite Ok Image" header
     * @return (int[]) - Array such as its content is {width, height, channels, color space}
     * @throws AssertionError See handouts section 6.1
     */
    public static int[] decodeHeader(byte[] header) {

        //Nous verifions que notre tableau header ne soit pas nul, puis nous verifions que sa taille normes du format.
        assert (header != null && header.length == QOISpecification.HEADER_SIZE);


        //Dans cette seconde partie, nous partitionnons notre tableau "header" pour en retrouver :
        //Le tag du QOI Format.
        //La taille de l'image.
        //Le nombre de canaux ainsi que l'espace de couleur.

        //Nous vérifions ensuite que les données que nous trouvons correspondent, elles aussi, aux normes du format.
        //Enfin nous traduisons ces différentes données en entier puis nous les renvoyons sous la forme d'un tableau.

        byte[][] partHeader = ArrayUtils.partition(header, 4, 4, 4, 1, 1);
        byte[] magicNumb = partHeader[0];
        int canaux = partHeader[3][0];
        int colorSpace = partHeader[4][0];

        assert (ArrayUtils.equals(magicNumb, QOISpecification.QOI_MAGIC));
        assert (canaux == QOISpecification.RGB || canaux == QOISpecification.RGBA);
        assert (colorSpace == QOISpecification.ALL || colorSpace == QOISpecification.sRGB);

        int largeur = ArrayUtils.toInt(partHeader[1]);
        int hauteur = ArrayUtils.toInt(partHeader[2]);

        int[] decoded = new int[]{largeur, hauteur, canaux, colorSpace};

        return decoded;
    }

    // ==================================================================================
    // =========================== ATOMIC DECODING METHODS ==============================
    // ==================================================================================

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param input    (byte[]) - Stream of bytes to read from
     * @param alpha    (byte) - Alpha component of the pixel
     * @param position (int) - Index in the buffer
     * @param idx      (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.1
     */
    public static int decodeQoiOpRGB(byte[][] buffer, byte[] input, byte alpha, int position, int idx) {

        //Nous vérifions que les paramètres vérifient les conditions énoncées :
        // que le tableau que nous allons remplir ainsi que celui contentant les données ne soient pas vides.
        // puis nous verifions que les indications de construction sur ce nouveau tableau soient réalisable.
        assert buffer != null && input != null;
        assert position >= 0 && position < buffer.length;
        assert idx >= 0 && idx < input.length;

        //On extrait les valeurs que l'on souhaite dans notre tableau de données.
        //Puis, on concaténise celle-ci avec la valeur de alpha que nous avons en paramètres.
        //Ainsi, nous ajoutons cela dans le tableau que nous sommes entrain de remplir.
        //Enfin, nous retournons le nombre de pixel que nous avons consommés dans notre tableau de données.
        byte[] decode = ArrayUtils.extract(input, idx, 3);
        buffer[position] = ArrayUtils.concat(decode, ArrayUtils.wrap(alpha));

        return decode.length; // SPEC.RGB
    }

    /**
     * Store the pixel in the buffer and return the number of consumed bytes
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param input    (byte[]) - Stream of bytes to read from
     * @param position (int) - Index in the buffer
     * @param idx      (int) - Index in the input
     * @return (int) - The number of consumed bytes
     * @throws AssertionError See handouts section 6.2.2
     */
    public static int decodeQoiOpRGBA(byte[][] buffer, byte[] input, int position, int idx) {


        //Nous vérifions que les paramètres vérifient les conditions énoncées, Celles-ci sont presque similaire à celles de la fonction précédente :
        // que le tableau que nous allons remplir ainsi que celui contentant les données ne soient pas vides.
        // puis nous verifions que les indications de construction sur ce nouveau tableau soient réalisable.
        assert (buffer != null && input != null
                && position < buffer.length
                && idx < input.length && idx >= 0
                && input.length > QOISpecification.RGB);


        //Ici, on extrait la partie qui nous intéresse dans notre tableau de données.
        // Puis on ajoute cette extraction dans notre nouveau tableau à la position souhaitée.
        // Enfin, nous retournons le nombre de donnés que nous avons consommées dans notre tableau de données.
        byte[] value = ArrayUtils.extract(input, idx, 4);
        buffer[position] = value;
        int returnedValue = value.length;
        return returnedValue;
    }


    /**
     * Create a new pixel following the "QOI_OP_DIFF" schema.
     *
     * @param previousPixel (byte[]) - The previous pixel
     * @param chunk         (byte) - A "QOI_OP_DIFF" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.4
     */
    public static byte[] decodeQoiOpDiff(byte[] previousPixel, byte chunk) {

        //Nous verfions si le tag est le meme que celui correspondant à celui présent dans la QOISpecification.
        //Puis nous vérifions que le pixel précédent respecte bien les conditions demandées (différent de nul et un tableau de taille 4).
        byte tag = (byte) (chunk & 0b11_00_00_00);
        assert (tag == QOISpecification.QOI_OP_DIFF_TAG);
        assert (previousPixel.length == 4);
        assert (previousPixel != null);


        byte[] deltaDiff = new byte[4]; //tableau permetant de retrouvé la variation entre le pixel précedent et celui traité actuellement.
        deltaDiff[0] = (byte) (((chunk >>> 4) & 0b00_00_00_11) - 2); //dr
        deltaDiff[1] = (byte) (((chunk >>> 2) & 0b00_00_00_11) - 2);  //dg
        deltaDiff[2] = (byte) ((chunk & 0b00_00_00_11) - 2);         //db
        // la composante alpha est la meme que le pixel précédent dans ce cas donc nous n'avons pas besoin de l'implementer dans notre tableau "Delta".


        byte[] curentPixel = new byte[4]; //tableau permettant de retrouver la valeur du pixel actuel en combinant le pixel précédent et la variation de ce dernier.
        for (int i = 0; i < curentPixel.length; i++) {
            curentPixel[i] = (byte) (previousPixel[i] + deltaDiff[i]);
        }
        return curentPixel;
    }

    /**
     * Create a new pixel following the "QOI_OP_LUMA" schema
     *
     * @param previousPixel (byte[]) - The previous pixel
     * @param data          (byte[]) - A "QOI_OP_LUMA" data chunk
     * @return (byte[]) - The newly created pixel
     * @throws AssertionError See handouts section 6.2.5
     */
    public static byte[] decodeQoiOpLuma(byte[] previousPixel, byte[] data) {

        //Nous vérifions que le tag corresponde bien à celui de la fonction luma, puis nous vérifions que nous pouvons utiliser le pixel précédent correctement.
        byte tag = (byte) (data[0] & 0b11_00_00_00);
        assert (tag == QOISpecification.QOI_OP_LUMA_TAG);
        assert (previousPixel.length == 4);
        assert (previousPixel != null);

        //Nous isolons la variation sur la composant g, puis sur la composant r et b.
        //Comme la fonction luma induit que la composante alpha ne change pas, la variation est nul, donc on ne la précise pas (deltaLuma[3] = 0).
        byte[] deltaLuma = new byte[4];
        deltaLuma[1] = (byte) ((data[0] & 0b00_11_11_11) - 32);
        deltaLuma[0] = (byte) ((((data[1] >>> 4) & 0b00_00_11_11) + deltaLuma[1]) - 8);
        deltaLuma[2] = (byte) (((data[1] & 0b00_00_11_11) + deltaLuma[1]) - 8);

        //Enfin nous définissons le nouveau pixel en ajoutant sa variation avec le pixel précédent pour chaque composante.
        // (tableau permettant de retrouver la valeur du pixel actuel en combinant le pixel précédent et la variation de ce dernier).
        byte[] curentPixel = new byte[4];
        for (int i = 0; i < curentPixel.length; i++) {
            curentPixel[i] = (byte) (previousPixel[i] + deltaLuma[i]);
        }

        return curentPixel;
    }

    /**
     * Store the given pixel in the buffer multiple times
     *
     * @param buffer   (byte[][]) - Buffer where to store the pixel
     * @param pixel    (byte[]) - The pixel to store
     * @param chunk    (byte) - a QOI_OP_RUN data chunk
     * @param position (int) - Index in buffer to start writing from
     * @return (int) - number of written pixels in buffer
     * @throws AssertionError See handouts section 6.2.6
     */
    public static int decodeQoiOpRun(byte[][] buffer, byte[] pixel, byte chunk, int position) {

        //Nous vérifions que nous pouvons remplir le tableau comportant les données en PNG.
        //Nous regardons aussi que la position dasn le tableau que nous voulons remplir est valable.
        // Enfin nous verifionzs que les données que nous voulons implementer dans le nouveau tableau sont conformes (toutes les composantes d'un pixel).
        assert (buffer != null
                && position < buffer.length
                && pixel != null
                && pixel.length == 4
                && buffer.length >= 4);

        //Dans cette fonction nous isolons le nombre de fois que nous voulons copier le pixel à l'aide d'un mask.
        //Puis nous faisons une copie du pixel dans notre tableau comportant les données en Png, le nombre de fois indiqué par le byte chunk, isolé de son tag.
        byte count = (byte) (chunk & 0b00_11_11_11);
        for (int i = 0; i < count + 1; i++) {
            System.arraycopy(pixel, 0, buffer[position + i], 0, 4);
        }
        return count;
    }

    // ==================================================================================
    // ========================= GLOBAL DECODING METHODS ================================
    // ==================================================================================

    /**
     * Decode the given data using the "Quite Ok Image" Protocol
     *
     * @param data   (byte[]) - Data to decode
     * @param width  (int) - The width of the expected output
     * @param height (int) - The height of the expected output
     * @return (byte[][]) - Decoded "Quite Ok Image"
     * @throws AssertionError See handouts section 6.3
     */
    public static byte[][] decodeData(byte[] data, int width, int height) {

        //Initialisation des différentes variables que nous allons utiliser durant le processus de décodage.
        byte[] prevPix = QOISpecification.START_PIXEL; //r/g/b/a
        byte[][] hashTable = new byte[64][4];
        int idx = 0;
        int longeurTab = width * height;

        //Nous vérifions que les données initiales permettent de construire une nouvelle image.
        assert (data != null && width > 0 && height > 0);

        byte[][] buffer = new byte[longeurTab][4];

        //Dans cette boucle for, nous itérons sur le tableau que nous voulons créer avec les valeurs de l'image au format png.
        //Grace aux données que nous avons dans le tableau data, nous itérons de la sorte :
        // - nous regardons à quelle fonction correspond le tag de la valeur de notre data
        // - suivant ce tag nous effectuons les différentes fonctions que nous avons rédigées précédemment.
        // - leur utilisation diffère pour la plupart, mais leur idée est meme.
        // Nous remplissons le tableau correspondant à notre nouvelle image, puis nous décalons notre "position : idx" dans notre tableau de données.
        // Enfin, nous faisons attentions de toujours ajouter le pixel traité à la table de hachage,
        // et d'initialiser notre pixel précédent pour la prochaine itération de la boucle for.

        for (int i = 0; i < buffer.length; i++) {
            int intTag = data[idx];

                    /** Decode pour la fonction RGBA */
            if (intTag == QOISpecification.QOI_OP_RGBA_TAG) {
                int deltaIdx = decodeQoiOpRGBA(buffer, data, i, idx + 1);
                idx += deltaIdx + 1;

                    /** Decode pour la fonction RGB */
                //ces valeurs n'étaient pas évidentes, mais grace au debugger,
                // nous avons compris comment se comportait le programme ainsi,
                // nous avons compris qu'il fallait ajouter 1 à l'index pour prendre en compte la composante alpha.
            } else if (intTag == QOISpecification.QOI_OP_RGB_TAG) {
                int deltaIdx = decodeQoiOpRGB(buffer, data, prevPix[3], i, idx + 1) + 1;
                idx += deltaIdx;

            } else {
                byte tagMask = (byte) (data[idx] & 0b11_00_00_00);

                    /** Decode en incrémentant une nouvelle fonction INDEX */
                if (tagMask == QOISpecification.QOI_OP_INDEX_TAG) {
                    buffer[i] = hashTable[data[idx]];
                    idx++;
                }
                    /** Decode pour la fonction LUMA */
                else if (tagMask == QOISpecification.QOI_OP_LUMA_TAG) {
                    buffer[i] = decodeQoiOpLuma(prevPix, ArrayUtils.concat(data[idx], data[idx + 1]));
                    idx += 2; // (vu qu'on a consommé data[idx] et data[idx+1]
                }
                    /** Decode pour la fonction DIFF */
                else if (tagMask == QOISpecification.QOI_OP_DIFF_TAG) {
                    buffer[i] = decodeQoiOpDiff(prevPix, data[idx]);
                    idx++;
                }
                    /** Decode pour la fonction RUN */
                else if (tagMask == QOISpecification.QOI_OP_RUN_TAG) {
                    int deltaIdx = decodeQoiOpRun(buffer, prevPix, data[idx], i);
                    idx++;
                    i += deltaIdx;
                }
            }
                /** Initialisation des valeurs pour les prochaines itérations. */
            int hashIdx = QOISpecification.hash(buffer[i]);
            hashTable[hashIdx] = buffer[i];

            prevPix = buffer[i];
        }

        //La dernière assertion de l'annoncé nous demande de verifier que data contient assez de données pour reformer l’image de base.
        // Nous pensons que cela vient du fait que le décodage pouvait être vu d'une différente manière que celle que nous avons construite.
        // En effet, nous avons fait le choix de parcours notre tableau à construire en avançant notre "position : idx" le tableau de donnés à disposition.
        // Toutes fois il était possible aussi de parcourir le tableau de données à disposition en avançant notre "position : idx" dans le tableau que nous construisons.
        // Alors la dernière assertion aurait eu beaucoup plus de sens.
        // Car si nous incrémentons cette assertion dans notre construction, elle vérifiera juste si la boucle "for i" ira jusqu'à la dernière valeur.
        // Cela sera tout le temps le cas. Cependant, suivant notre construction, nous avons traduit cette assertion par l'assertion suivante.
        // Nous vérifions qu'à la fin de la construction de notre tableau, nous avons bien itérés sur toutes les données présentent dans notre data.
        // Celle-ci ne traduit pas complètement l'énoncé, mais c'est l'assertion ayant le plus de sens selon la manière dont nous avons construit notre DECODER.

        assert idx == data.length;
        return buffer;
    }


    /**
     * Decode a file using the "Quite Ok Image" Protocol
     *
     * @param content (byte[]) - Content of the file to decode
     * @return (Image) - Decoded image
     * @throws AssertionError if content is null
     */
    public static Image decodeQoiFile(byte[] content) {

        //Nous partitionnons le tableau content afin d'obtenir une partie contenant le Header de l'image, puis son contenue et enfin sa signature.
        int imageLength = content.length - QOISpecification.HEADER_SIZE - QOISpecification.QOI_EOF.length;
        byte[][] partContent = ArrayUtils.partition(content, QOISpecification.HEADER_SIZE, imageLength, QOISpecification.QOI_EOF.length);

        //La fonction decodeHeader ici, nous permet d'isoler les données nécessaires afin de décoder notre image.
        int[] header = decodeHeader(partContent[0]);

        int largeur = header[0];
        int hauteur = header[1];
        int colorChannel = header[2];
        int colorSpace = header[3];
        byte[] data = partContent[1];

        //Nous vérifions quue la signature corresponde à celle de référence.
        //Puis, nous vérifions que le tableau mis à disposition contient des éléments.
        assert (ArrayUtils.equals(partContent[2], QOISpecification.QOI_EOF));
        assert (content != null);

        //Enfin, nous décodons notre image, grâce aux différents éléments isolés dans notre atbleau "content".
        //Puis nous convertissons notre image en tableau d'entier.
        //Pour finalemen renvoyer grâce à cette fonction, l'image générée en png grâce aux données obtenues.
        byte[][] channels = decodeData(data, largeur, hauteur);
        int[][] image = ArrayUtils.channelsToImage(channels, hauteur, largeur);
        return Helper.generateImage(image, (byte) colorChannel, (byte) colorSpace);


        //verifier que les 8 derniers bytes correspondent bien à la signature.

    }

}