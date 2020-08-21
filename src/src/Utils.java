import java.util.ArrayList;
import java.util.List;

public class Utils {

    /*****************************************************************************************************************/
    /****************************************************CONSTANTS:***************************************************/
    /*****************************************************************************************************************/


    public static final double[][] DCTMatrix = new double[][]{
            {0.3536, 0.3536, 0.3536, 0.3536, 0.3536, 0.3536, 0.3536, 0.3536},
            {0.4904, 0.4157, 0.2778, 0.0975, -0.0975, -0.2778, -0.4157, -0.4904},
            {0.4619, 0.1913, -0.1913, -0.4619, -0.4619, -0.1913, 0.1913, 0.4619},
            {0.4157, -0.0975, -0.4904, -0.2778, 0.2778, 0.4904, 0.0975, -0.4157},
            {0.3536, -0.3536, -0.3536, 0.3536, 0.3536, -0.3536, -0.3536, 0.3536},
            {0.2778, -0.4904, 0.0975, 0.4157, -0.4157, -0.0975, 0.4904, -0.2778},
            {0.1913, -0.4619, 0.4619, -0.1913, -0.1913, 0.4619, -0.4619, 0.1913},
            {0.0975, -0.2778, 0.4157, -0.4904, 0.4904, -0.4157, 0.2778, -0.0975}
    };

    public static final double[][] DCTMatrixT = new double[][]{
            {0.3536, 0.4904, 0.4619, 0.4157, 0.3536, 0.2778, 0.1913, 0.0975},
            {0.3536, 0.4157, 0.1913, -0.0975, -0.3536, -0.4904, -0.4619, -0.2778},
            {0.3536, 0.2778, -0.1913, -0.4904, -0.3536,	0.0975, 0.4619, 0.4157},
            {0.3536, 0.0975, -0.4619, -0.2778, 0.3536, 0.4157, -0.1913, -0.4904},
            {0.3536, -0.0975, -0.4619, 0.2778, 0.3536, -0.4157, -0.1913, 0.4904},
            {0.3536, -0.2778, -0.1913, 0.4904, -0.3536, -0.0975, 0.4619, -0.4157},
            {0.3536, -0.4157, 0.1913, 0.0975, -0.3536, 0.4904, -0.4619, 0.2778},
            {0.3536, -0.4904, 0.4619, -0.4157, 0.3536, -0.2778, 0.1913, -0.0975}
    };

    public static final int[][] LoshellerMatrix = new int[][]{
            {16, 11, 10, 16, 24, 40, 51, 61},
            {12, 12, 14, 19, 26, 58, 60, 55},
            {14, 13, 16, 24, 40, 57, 69, 56},
            {14, 17, 22, 29, 51, 87, 80, 62},
            {18, 22, 37, 56, 68, 109, 103, 77},
            {24, 35, 55, 64, 81, 104, 113, 92},
            {49, 64, 78, 87, 103, 121, 120, 101},
            {72, 92, 95, 98, 112, 100, 103, 99}
    };

    public static final int[][] ChrominanceQuantizationMatrix = new int[][]{
            {17, 18, 24, 47, 99, 99, 99, 99},
            {18, 21, 26, 66, 99, 99, 99, 99},
            {24, 26, 56, 99, 99, 99, 99, 99},
            {47, 66, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99},
            {99, 99, 99, 99, 99, 99, 99, 99}
    };

    //Recommended Huffman Tables For AC Coefficients:
    public static final String[][] LuminanceACCoefficients = new String[][]{//EOB = 1010, ZRL = 11111111001
            //                                row (from 1 to 10);
            /*RunLength*/
            /*0*/    {"00", "01", "100", "1011", "11010",
            "1111000", "11111000", "1111110110", "1111111110000010", "1111111110000011"},
            /*1*/    {"1100", "11011", "1111001", "111110110", "11111110110",
            "1111111110000100", "1111111110000101", "1111111110000110", "1111111110000111", "1111111110001000"},
            /*2*/    {"11100", "11111001", "1111110111", "111111110100", "1111111110001001",
            "1111111110001010", "1111111110001011", "1111111110001100", "1111111110001101", "1111111110001110"},
            /*3*/    {"111010", "111110111", "111111110101", "1111111110001111", "1111111110010000",
            "1111111110010001", "1111111110010010", "1111111110010011", "1111111110010100", "1111111110010101"},
            /*4*/    {"111011", "1111111000", "1111111110010110", "1111111110010111", "1111111110011000",
            "1111111110011001", "1111111110011010", "1111111110011011", "1111111110011100", "1111111110011101"},
            /*5*/    {"1111010", "11111110111", "1111111110011110", "1111111110011111", "1111111110100000",
            "1111111110100001", "1111111110100010", "1111111110100011", "1111111110100100", "1111111110100101"},
            /*6*/    {"1111011", "111111110110", "1111111110100110", "1111111110100111", "1111111110101000",
            "1111111110101001", "1111111110101010", "1111111110101011", "1111111110101100", "1111111110101101"},
            /*7*/    {"11111010", "111111110111", "1111111110101110", "1111111110101111", "1111111110110000",
            "1111111110110001", "1111111110110010", "1111111110110011", "1111111110110100", "1111111110110101"},
            /*8*/    {"111111000", "111111111000000", "1111111110110110", "1111111110110111", "1111111110111000",
            "1111111110111001", "1111111110111010", "1111111110111011", "1111111110111100", "1111111110111101"},
            /*9*/    {"111111001", "1111111110111110", "1111111110111111", "1111111111000000", "1111111111000001",
            "1111111111000010", "1111111111000011", "1111111111000100", "1111111111000101", "1111111111000110"},
            /*10*/   {"111111010", "1111111111000111", "1111111111001000", "1111111111001001", "1111111111001010",
            "1111111111001011", "1111111111001100", "1111111111001101", "1111111111001110", "1111111111001111"},
            /*11*/   {"1111111001", "1111111111010000", "1111111111010001", "1111111111010010", "1111111111010011",
            "1111111111010100", "1111111111010101", "1111111111010110", "1111111111010111", "1111111111011000"},
            /*12*/   {"1111111010", "1111111111011001", "1111111111011010", "1111111111011011", "1111111111011100",
            "1111111111011101", "1111111111011110", "1111111111011111", "1111111111100000", "1111111111100001"},
            /*13*/   {"11111111000", "1111111111100010", "1111111111100011", "1111111111100100", "1111111111100101",
            "1111111111100110", "1111111111100111", "1111111111101000", "1111111111101001", "1111111111101010"},
            /*14*/   {"1111111111101011", "1111111111101100", "1111111111101101", "1111111111101110", "1111111111101111",
            "1111111111110000", "1111111111110001", "1111111111110010", "1111111111110011", "1111111111110100"},
            /*15*/   {"1111111111110101", "1111111111110110", "1111111111110111", "1111111111111000", "1111111111111001",
            "1111111111111010", "1111111111111011", "1111111111111100", "1111111111111101", "1111111111111110"},

    };

    public static final String[][] ChrominanceACCoefficients = new String[][]{//EOB = 00, ZRL = 1111111010
            //                              row (from 1 to 10)
            /*RunLength*/
            /*0*/    {"01",  "100",  "1010","11000", "11001","111000","1111000","111110100", "1111110110", "111111110100"},
            /*1*/    {"1011", "111001", "11110110", "111110101", "11111110110", "111111110101", "111111110001000",
            "111111110001001", "111111110001010", "111111110001011"},
            /*2*/    {"11010", "11110111", "1111110111", "111111110110", "111111111000010",
            "1111111110001100", "1111111110001101", "1111111110001110", "1111111110001111", "1111111110010000"},
            /*3*/    {"11011", "11111000", "1111111000", "111111110111", "1111111110010001",
            "1111111110010010", "1111111110010011", "1111111110010100", "1111111110010101", "1111111110010110"},
            /*4*/    {"111010", "111110110", "1111111110010111", "1111111110011000", "1111111110011001",
            "1111111110011010", "1111111110011011", "1111111110011100", "1111111110011101", "1111111110011110"},
            /*5*/    {"111011", "1111111001", "1111111110011111", "1111111110100000", "1111111110100001",
            "1111111110100010", "1111111110100011", "1111111110100100", "1111111110100101", "1111111110100110"},
            /*6*/    {"1111001", "11111110111", "1111111110100111", "1111111110101000", "1111111110101001",
            "1111111110101010", "1111111110101011", "1111111110101100", "1111111110101101", "1111111110101110"},
            /*7*/    {"1111010", "11111111000", "1111111110101111", "1111111110110000", "1111111110110001",
            "1111111110110010", "1111111110110011", "1111111110110100", "1111111110110101", "1111111110110110"},
            /*8*/    {"11111001", "1111111110110111", "1111111110111000", "1111111110111001", "1111111110111010",
            "1111111110111011", "1111111110111100", "1111111110111101", "1111111110111110", "1111111110111111"},
            /*9*/    {"111110111", "1111111111000000", "1111111111000001", "1111111111000010", "1111111111000011",
            "1111111111000100", "1111111111000101", "1111111111000110", "1111111111000111", "1111111111001000"},
            /*10*/   {"111111000" ,"1111111111001001", "1111111111001010", "1111111111001011", "1111111111001100",
            "1111111111001101" ,"1111111111001110", "1111111111001111", "1111111111010000", "1111111111010001"},
            /*11*/   {"111111001" ,"1111111111010010", "1111111111010011", "1111111111010100", "1111111111010101",
            "1111111111010110" ,"1111111111010111", "1111111111011000", "1111111111011001", "1111111111011010"},
            /*12*/   {"111111010" ,"1111111111011011", "1111111111011100", "1111111111011101", "1111111111011110",
            "1111111111011111" ,"1111111111100000", "1111111111100001", "1111111111100010" ,"1111111111100011"},
            /*13*/   {"11111111001", "1111111111100100", "1111111111100101", "1111111111100110" ,"1111111111100111",
            "1111111111101000", "1111111111101001", "1111111111101010", "1111111111101011" ,"1111111111101100"},
            /*14*/   {"11111111100000", "1111111111101101", "1111111111101110", "1111111111101111", "1111111111110000",
            "1111111111110001" ,"1111111111110010", "1111111111110011", "1111111111110100" ,"1111111111110101"},
            /*15*/   {"111111111000011", "111111111010110", "1111111111110111", "1111111111111000", "1111111111111001",
            "1111111111111010", "1111111111111011" ,"1111111111111100", "1111111111111101", "1111111111111110"},
    };






    /*****************************************************************************************************************/
    /*********************************************COMPRESSION FUNCTIONS:**********************************************/
    /*****************************************************************************************************************/

    //Returns the RGB pixel in Y'CbCr:
    public static byte[] RGBtoYCbCr(int red, int green, int blue){
        byte[] YCbCr = new byte[3];
        int y,cb,cr;

        y =  Math.round(16 + (float)(0.257 * red + 0.504 * green + 0.098 * blue));
        cb = Math.round(128 +(float)(-0.148 * red - 0.291 * green + 0.439 * blue));
        cr = Math.round(128 +(float)(0.439 * red - 0.368 * green - 0.071 * blue));
        if(y > 235) y = 235;
        if(cb > 240) cb = 240;
        if(cr > 240) cr = 240;
        if(y < 16) y = 16;
        if(cb < 16) cb = 16;
        if(cr < 16) cr = 16;

        YCbCr[0] = (byte)y;
        YCbCr[1] = (byte)cb;
        YCbCr[2] = (byte)cr;

        return YCbCr;
    }

    //Returns y, cb, cr, channels separated with a downsampling of sub:
    //sub = 0: downsampling of 4:4:4, sub = 1: downsampling of 4:2:2, sub = 2: downsampling of 4:2:0
    public static ArrayList<byte[][]> downsampling(byte[] ycbcrContent, int width, int height, int subType){
        ArrayList<byte[][]> ret = new ArrayList<>();
        //y is always the same:
        byte[][] y = new byte[height][width];
        //4:4:4
        int horizontalReduction = 1;
        int verticalReduction = 1;
        if(subType == 1){//4:2:2
            horizontalReduction = 2;
        } else if(subType == 2){//4:2:0
            horizontalReduction = 2;
            verticalReduction = 2;
        }

        int heightOfTables = height/verticalReduction;
        if(height%verticalReduction!=0) ++heightOfTables;
        int widthOfTables = width/horizontalReduction;
        if(width%horizontalReduction!=0) ++widthOfTables;
        byte[][] cb = new byte[heightOfTables][widthOfTables];
        byte[][] cr = new byte[heightOfTables][widthOfTables];

        int actualPix = 0;
        for(int i = 0; i < height; ++i){
            for(int j = 0; j < width; ++j){
                y[i][j] = ycbcrContent[3*actualPix];
                if(subType == 0){
                    cb[i][j] = ycbcrContent[3*actualPix+1];
                    cr[i][j] = ycbcrContent[3*actualPix+2];
                }else if(subType == 1 && j%2==0){
                    cb[i][j/2] = ycbcrContent[3*actualPix+1];
                    cr[i][j/2] = ycbcrContent[3*actualPix+2];
                } else if(subType == 2 && i%2==0 && j%2==0){
                    cb[i/2][j/2] = ycbcrContent[3*actualPix+1];
                    cr[i/2][j/2] = ycbcrContent[3*actualPix+2];
                }
                ++actualPix;
            }
        }
        ret.add(y);
        ret.add(cb);
        ret.add(cr);
        return ret;
    }

    //Returns the DCT[8][8] (Discrete Cosine Transformation) of a block[8][8]:
    public static double[][] getDCT(int[][] block){
        double[][] D = new double[8][8];
        //D x block:
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    D[i][j] += DCTMatrix[i][k] * block[k][j];
                }
            }
        }
        double[][] DT = new double[8][8];
        double[][] d = new double[8][8];
        //D x block x D':
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                for (int k = 0; k < 8; k++) {
                    DT[i][j] += D[i][k] * DCTMatrixT[k][j];
                }
                d[i][j] =  DT[i][j];
            }
        }
        return d;
    }

    //Returns the quantized 8x8 table of dctBlock (quality of 50%):
    //lumOrChr = 0 for luminance quantization, lumOrChr = 1 for chrominance quantization
    public static int[][] getQuantization(double[][] dctBlock, int lumOrChr){
        int[][] quantized = new int[8][8];

        for(int i = 0; i < 8; ++i){
            for(int j = 0; j < 8; ++j){
                if(lumOrChr == 0) quantized[i][j] = (int) Math.round(dctBlock[i][j]/LoshellerMatrix[i][j]);
                else quantized[i][j] = (int) Math.round(dctBlock[i][j]/ChrominanceQuantizationMatrix[i][j]);
            }
        }

        return quantized;
    }

    //Returns the 8x8 block in zig-zag order:
    public static int[] blockToZigZag(int[][] block){
        int[] zigZag = new int[64];

        int i = 0;
        int j = 0;
        boolean down = false;
        boolean up = false;
        for(int k = 0; k < 64; ++k){
            zigZag[k] = block[i][j];
            if(i==0 && !down){
                ++j;
                down = true;
                up = false;
            } else if (i==7 && !up){
                ++j;
                down = false;
                up = true;
            }else if(j==0 && !up){
                ++i;
                down = false;
                up = true;
            } else if (j==7 && !down){
                ++i;
                down = true;
                up = false;
            } else if(down){
                ++i;
                --j;
            } else if(up){
                --i;
                ++j;
            }
        }

        return zigZag;
    }

    //Returns the (row, column) of dc in the table used to code DC coefficients.
    public static int[] getPosInDCTable(int dc){
        //Exceptions:
        if(dc == 0) return new int[]{0,0};
        if(dc == 32768) return new int[]{16,0};

        //Find row:
        int row = 0;
        boolean foundRow = false;
        while(!foundRow){
            int border = (int) (Math.pow(2, row) - 1);
            if(-border <= dc && dc <= border) foundRow = true;
            else ++row;
        }

        //Find column:
        int border = (int) (Math.pow(2, row) - 1);
        int col;
        if(dc < 0) col = border - Math.abs(dc);
        else col = dc;

        return new int[]{row, col};
    }

    //Returns the binary number used to encode the dc coefficient.
    public static String dcEncoding(int dc){
        StringBuilder encoding = new StringBuilder();

        int[] posTable = getPosInDCTable(dc);
        int row = posTable[0];
        int col = posTable[1];

        //Encodes the row in the table:
        encoding.append("1".repeat(Math.max(0, row)));
        if(row<16) encoding.append("0");

        //Encodes the column in the table:
        //      (col should always be positive)
        StringBuilder colStr = new StringBuilder(Integer.toBinaryString(col));
        while(colStr.length() < row) colStr.insert(0, "0");

        encoding.append(colStr);
        return encoding.toString();
    }

    //Returns the Huffman encoding (run-length encoding) of zigZag vector.
    //Last DC is the last DC coefficient
    //lumOrChr = 0 for luminance encoding, lumOrChr = 1 for chrominance encoding
    public static List<Byte> getHuffman(int[] zigZag, int lastDC, int lumOrChr){
        List<Byte> encoding = new ArrayList<>();

        //Start the codification of the block with the encoding of the DC coefficient:
        StringBuilder codificationInBits = new StringBuilder(dcEncoding(zigZag[0] - lastDC));

        //Then the 63 AC coefficients:
        //find EOB:
        int EOB = 63;
        while(EOB >= 0 && zigZag[EOB] == 0) --EOB;

        //encode:
        int x;
        int runLength = 0;
        for(int i = 1; i <= EOB; ++i) {
            x = zigZag[i];
            //1. finds the number Z of consecutive zeros preceding x (runLength):
            if (x == 0) {
                ++runLength;
                if (runLength == 16) {
                    //Special code for when there are 16 0s or more (Luminance ZRL = 11111111001, Chrominance ZRL = 1111111010):
                    if(lumOrChr==0) codificationInBits.append("11111111001");
                    else codificationInBits.append("1111111010");
                    runLength = 0;
                }
            } else {
                //2. finds x in coefficient Table and prepares its row and column numbers (row and col)
                int[] posInDC = getPosInDCTable(x);
                int row = posInDC[0];
                int col = posInDC[1];

                //3. the pair (row, runLength) is used as row and column numbers for AC Coefficient Table:
                String rowRunLenght;
                if(lumOrChr == 0) rowRunLenght = LuminanceACCoefficients[runLength][row-1];
                else rowRunLenght = ChrominanceACCoefficients[runLength][row-1];

                //4. the Huffman code found in that position in the table is concatenated to col (where
                //    col is written as an row-bit number) and the result is the code emitted by the JPEG
                //    encoder for the AC coefficient x and all the consecutive zeros preceding it:
                StringBuilder colStr = new StringBuilder(Integer.toBinaryString(col));
                while(colStr.length() < row) colStr.insert(0, "0");

                codificationInBits.append(rowRunLenght).append(colStr);

                runLength = 0;
            }
        }
        //Add EOB = 1010:
        if(lumOrChr==0) codificationInBits.append("1010");
        else codificationInBits.append("00");


        //From bits to bytes:
        //      Padding at the end of each block (to make it easier to read):
        while(codificationInBits.length()%8!=0) codificationInBits.append("0");
        String byteInBinary;
        for(int i = 0; i < codificationInBits.length(); i+=8){
            byteInBinary = codificationInBits.substring(i, i+8);
            encoding.add((byte) Integer.parseInt(byteInBinary, 2));
        }
        return encoding;
    }



    /*****************************************************************************************************************/
    /********************************************DECOMPRESSION FUNCTIONS:*********************************************/
    /*****************************************************************************************************************/
}
