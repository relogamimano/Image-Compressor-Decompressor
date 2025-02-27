package cs107;

import java.util.Arrays;

/**
 * Main entry point of the program.
 * @apiNote Students are free to change it.
 * This class will not be graded unless asked by the students
 * and only if the changes are considered as a bonus
 * @author Hamza REMMAL (hamza.remmal@epfl.ch)
 * @version 1.3
 * @since 1.0
 */
public final class Main {

    /**
     * DO NOT CHANGE THIS, MORE ON THAT IN WEEK 7
     */
    private Main(){}

    /**
     * Main entry point to the program
     * @param args (String[]) - Arguments passed to the program via the command line
     */
    public static void main(String[] args){
        /*
        We've listed all the test methods here.
        Once you've implemented a new functionality, you can uncomment
        the corresponding test and run it.
        All the test starts with the 'assert' keyword. This means that if
        a test passes, your program will continue the execution of the code.
        Otherwise, if the test fails, your program will stop and a message will
        appear in your terminal :
        """
        Exception in thread "main" java.lang.AssertionError
        """
        You can check which test fails and why by inspecting the StackTrace.
        You can always change the code of this method to change the behavior of
        your program
         */

        // ========== Test ArrayUtils ==========
        assert testWrap();
        assert testToInt();
        assert testFromInt();
        assert testConcatArrayBytes();
        assert testConcatBytes();
        assert testExtract();
        assert testPartition();
        assert testImageToChannels();
        assert testChannelsToImage();

        // ========== Test QOIEncoder ==========
        assert testQoiHeader();
        assert testQoiOpRGB();
        assert testQoiOpRGBA();
        assert testQoiOpIndex();
        assert testQoiOpDiff();
        assert testQoiOpLuma();
        assert testQoiOpRun();
        assert testEncodeData();

        // ========== Test QOIDecoder ==========
        assert testDecodeHeader();
        assert testDecodeQoiOpRGB();
        assert testDecodeQoiOpRGBA();
        assert testDecodeQoiOpDiff();
        assert testDecodeQoiOpLuma();
        assert testDecodeQoiOpRun();
        assert testDecodeData();

        System.out.println("All the tests passes. Congratulations");

    }

    // ============================================================================================

    /**
     * Encodes a given file from "PNG" to "QOI"
     * @param inputFile (String) - The path of the file to encode
     * @param outputFile (String) - The path where to store the generated "Quite Ok Image"
     */
    public static void pngToQoi(String inputFile, String outputFile){
        // Read a PNG file
        var inputImage = Helper.readImage(inputFile);
        // Encode the Image to QOI
        var outputFileContent = QOIEncoder.qoiFile(inputImage);
        // Write in binary mode the file content to 'output_file'
        Helper.write(outputFile, outputFileContent);
    }

    /**
     * Encodes a given file from "QOI" to "PNG"
     * @param inputFile (String) - The path of the file to decode
     * @param outputFile (String) - The path where to store the generated "PNG" Image
     */
    public static void qoiToPng(String inputFile, String outputFile){
        // Read in binary mode the file 'input_file'
        var inputFileContent = Helper.read(inputFile);
        // Decode the file using the 'QOI' decoder
        var computedImage = QOIDecoder.decodeQoiFile(inputFileContent);
        // Write an image to 'output_file'
        Helper.writeImage(outputFile, computedImage);
    }

    /**
     * Computes the ratio
     * @param png (int) - Size of the "PNG" file
     * @param qoi (int) - Size of the "QOI" file
     * @return (int) - The ratio
     */
    public static double ratio(int png, int qoi){
        return 100d * png / qoi;
    }

    // ============================================================================================
    // ============================== ArrayUtils examples =========================================
    // ============================================================================================

    @SuppressWarnings("unused")
    private static boolean testWrap(){
        byte a = 1;
        byte[] wrappedA = ArrayUtils.wrap(a);
        byte [] expected = {1};
        return Arrays.equals(wrappedA, expected);
    }

    @SuppressWarnings("unused")
    private static boolean testToInt(){
        byte[] array = {123, 8, 4, 7};
        int value = ArrayUtils.toInt(array);
        int expected = 2064122887;
        return value == expected;
    }

    @SuppressWarnings("unused")
    private static boolean testFromInt(){
        int value = 12345678;
        byte[] array = ArrayUtils.fromInt(value);
        byte[] expected = {0, -68, 97, 78};
        return Arrays.equals(array, expected);
    }

    @SuppressWarnings("unused")
    private static boolean testConcatArrayBytes(){
        byte[] tab1 = {1, 2, 3};
        byte[] tab2 = new byte[0];
        byte[] tab3 = {4};
        byte[] tab = ArrayUtils.concat(tab1, tab2, tab3);
        byte[] expected = {1, 2, 3, 4};
        return Arrays.equals(expected, tab);
    }

    @SuppressWarnings("unused")
    private static boolean testConcatBytes(){
        byte[] tab = ArrayUtils.concat((byte) 4, (byte) 5, (byte) 6, (byte) 7);
        byte[] expected = {4, 5, 6, 7};
        return Arrays.equals(tab, expected);
    }

    @SuppressWarnings("unused")
    private static boolean testExtract(){
        byte[] tab = {1, 2, 3, 4, 5, 6, 7, 8};
        byte[] extracted = ArrayUtils.extract(tab, 2, 5);
        byte[] expected = {3, 4, 5, 6, 7};
        return Arrays.equals(expected, extracted);
    }

    @SuppressWarnings("unused")
    private static boolean testPartition(){
        byte[] tab = {1, 2, 3, 4, 5, 6, 7, 8, 9};
        byte[][] partitions = ArrayUtils.partition(tab, 3, 1, 2, 1, 2);
        byte[][] expected = {{1, 2, 3}, {4}, {5, 6}, {7}, {8, 9}};
        return Arrays.deepEquals(expected, partitions);
    }

    // Example of the format used for Helper.Image::data
    private static final int[][] input = {
            {1, 2, 3, 4, 5},
            {6, 7, 8, 9 ,10},
            {11, 12, 13, 14, 15}
    };

    // Example of the expected format in ArrayUtils::image_to_channels & ArrayUtils::channels_to_image
    private static final byte[][] formattedInput = {
            {0, 0,  1, 0}, {0, 0,  2, 0}, {0, 0,  3, 0}, {0, 0,  4, 0},{0, 0,  5, 0},
            {0, 0,  6, 0}, {0, 0,  7, 0}, {0, 0,  8, 0}, {0, 0,  9, 0},{0, 0, 10, 0},
            {0, 0, 11, 0}, {0, 0, 12, 0}, {0, 0, 13, 0}, {0, 0, 14, 0},{0, 0, 15, 0}
    };



    @SuppressWarnings("unused")
    private static boolean testImageToChannels(){
        byte[][] output = ArrayUtils.imageToChannels(input);
        return Arrays.deepEquals(output, formattedInput);
    }

    @SuppressWarnings("unused")
    private static boolean testChannelsToImage(){
        int[][]  output = ArrayUtils.channelsToImage(formattedInput, 3, 5);
        return Arrays.deepEquals(output, input);
    }

    // ============================================================================================
    // ============================== QOIEncoder examples =========================================
    // ============================================================================================

    @SuppressWarnings("unused")
    private static boolean testQoiHeader(){
        Helper.Image image = Helper.generateImage(new int[32][64], QOISpecification.RGB, QOISpecification.sRGB);
        byte[] expected = {113, 111, 105, 102, 0, 0, 0, 64, 0, 0, 0, 32, 3, 0};
        byte[] header = QOIEncoder.qoiHeader(image);
        return Arrays.equals(expected, header);
    }

    @SuppressWarnings("unused")
    private static boolean testQoiOpRGB(){
        byte[] pixel = {100, 0, 55, 0};
        byte[] expected = {-2, 100, 0, 55};
        byte[] encoding = QOIEncoder.qoiOpRGB(pixel);
        return Arrays.equals(expected, encoding);
    }

    @SuppressWarnings("unused")
    private static boolean testQoiOpRGBA(){
        byte[] pixel = {100, 0, 55, 73};
        byte[] expected = {-1, 100, 0, 55, 73};
        byte[] encoding = QOIEncoder.qoiOpRGBA(pixel);
        return Arrays.equals(expected, encoding);
    }

    @SuppressWarnings("unused")
    private static boolean testQoiOpIndex(){
        byte index = 43;
        byte[] expected = {43};
        byte[] encoding = QOIEncoder.qoiOpIndex(index);
        return Arrays.equals(expected, encoding);
    }

    @SuppressWarnings("unused")
    private static boolean testQoiOpDiff(){
        byte[] diff = {-2, -1, 0};
        byte[] expected = {70};
        byte[] encoding = QOIEncoder.qoiOpDiff(diff);
        return Arrays.equals(expected, encoding);
    }

    @SuppressWarnings("unused")
    private static boolean testQoiOpLuma(){
        byte[] diff = {19, 27, 20};
        byte[] expected = {-69, 1};
        byte[] encoding = QOIEncoder.qoiOpLuma(diff);
        return Arrays.equals(expected, encoding);
    }

    @SuppressWarnings("unused")
    private static boolean testQoiOpRun(){
        byte count = 41;
        byte[] expected = {-24};
        byte[] encoding = QOIEncoder.qoiOpRun(count);
        return Arrays.equals(expected, encoding);
    }

    @SuppressWarnings("unused")
    private static boolean testEncodeData(){
        byte[][]  pixels = { {0,0,0,-1}, {0,0,0,-1}, {0,0,0,-1}, {0,-1,0,-1},{-18,-20,-18,-1},{0,0,0,-1}, {100,100,100,-1}, {90,90,90,90}};
        byte[] expected = {-62, 102, -115, -103, -76, 102, -2, 100, 100, 100, -1, 90, 90, 90, 90};
        byte[] encoding = QOIEncoder.encodeData(pixels);
        return Arrays.equals(expected, encoding);
    }

    // ============================================================================================
    // ============================== QOIDecoder examples =========================================
    // ============================================================================================

    @SuppressWarnings("unused")
    private static boolean testDecodeHeader(){
        byte[] header = {'q', 'o', 'i', 'f', 0, 0, 0, 64, 0, 0, 0, 32, 3, 0};
        int[] decoded = QOIDecoder.decodeHeader(header);
        int[] expected = {64, 32, 3, 0};
        return Arrays.equals(decoded, expected);
    }

    @SuppressWarnings("unused")
    private static boolean testDecodeQoiOpRGB(){
        byte[][] buffer = new byte[2][4]; // buffer = [[0, 0, 0, 0], [0, 0, 0, 0]]
        byte[] input    = {0, 0, 0, -2, 100, 0, 55, 8, 0, 0, 0};
        byte alpha = 34;
        int position = 0;
        int idx = 3;
        int returnedValue = QOIDecoder.decodeQoiOpRGB(buffer, input, alpha, position, idx);
        byte[][] expected_buffer = {{-2, 100, 0, 34}, {0, 0, 0, 0}};
        return Arrays.deepEquals(expected_buffer, buffer) && (returnedValue == 3);
    }

    @SuppressWarnings("unused")
    private static boolean testDecodeQoiOpRGBA(){
        byte[][] buffer = new byte[2][4];
        byte[] input    = {0, 0, 0, -2, 100, 0, 55, 8, 0, 0, 0};
        int position = 0;
        int idx = 3;
        int returnedValue = QOIDecoder.decodeQoiOpRGBA(buffer, input, position, idx);
        byte[][] expected_buffer = {{-2, 100, 0, 55}, {0, 0, 0, 0}};
        return Arrays.deepEquals(expected_buffer, buffer) && (returnedValue == 4);
    }

    @SuppressWarnings("unused")
    private static boolean testDecodeQoiOpDiff(){
        byte[] previous_pixel = {23, 117, -4, 7};
        byte chunk            = (byte) 0b01_11_11_11;
        var currentPixel = QOIDecoder.decodeQoiOpDiff(previous_pixel, chunk);
        byte[] expected = {24, 118, -3, 7};
        return Arrays.equals(currentPixel, expected);
    }

    @SuppressWarnings("unused")
    private static boolean testDecodeQoiOpLuma(){
        byte[] previousPixel = {23, 117, -4, 7};
        byte[] chunk          = {(byte) 0b10_10_01_01, (byte) 0b11_00_11_01};
        byte[] currentPixel = QOIDecoder.decodeQoiOpLuma(previousPixel, chunk);
        byte[] expected = {32, 122, 6, 7};
        return Arrays.equals(expected, currentPixel);
    }

    @SuppressWarnings("unused")
    private static boolean testDecodeQoiOpRun(){
        byte[][] buffer = new byte[6][4]; // Array is full of zeros
        byte[] pixel    = {1, 2, 3, 4};
        byte chunk       = -61;
        int position    = 1;
        int returnedValue = QOIDecoder.decodeQoiOpRun(buffer, pixel, chunk, position);
        byte[][] expectedBuffer = {{0, 0, 0, 0}, {1, 2, 3, 4}, {1, 2, 3, 4}, {1, 2, 3, 4}, {1, 2, 3, 4}, {0, 0, 0, 0}};
        return Arrays.deepEquals(expectedBuffer, buffer) && (returnedValue == 3);
    }

    @SuppressWarnings("unused")
    private static boolean testDecodeData(){
        byte[] encoding = {-62, 102, -115, -103, -76, 102, -2, 100, 100, 100, -1, 90, 90, 90, 90};
        byte[][] expected = { {0,0,0,-1}, {0,0,0,-1}, {0,0,0,-1}, {0,-1,0,-1},{-18,-20,-18,-1},{0,0,0,-1}, {100,100,100,-1}, {90,90,90,90}};
        return Arrays.deepEquals(expected, QOIDecoder.decodeData(encoding, 4, 2));
    }

}