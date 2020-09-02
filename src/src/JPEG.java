import java.util.ArrayList;
import java.util.List;

public class JPEG {

    protected Statistics statistic;

    JPEG(){this.statistic = new Statistics();}




    /*****************************************************************************************************************/
    /*********************************************COMPRESSION ALGORITHM:**********************************************/
    /*****************************************************************************************************************/

    //File is the ppmFile representation of an image to compress.
    //subType is the type of Subsampling: 0 for 4:4:4, 1 for 4:2:2 and 2 for 4:2:0 (recommended)
    public byte[] compress(File file, int subType){

        statistic.setCompressionOrDecompression(0);
        statistic.setIniSize(file.getSize());
        statistic.setSubType(subType);
        statistic.startClock();

        int numPixels = file.getWidth()*file.getHeight();
        byte[] toCompress = file.getContent();
        byte[] compressedContent;
        List<Byte> encoding = new ArrayList<>();

        //Adding header (uncompressed):
        byte[] headerBytes = file.getHeader().getBytes();
        for(int i = 0; i < file.getHeaderSize(); ++i) encoding.add(headerBytes[i]);
        //Adding Subsampling type ID:
        encoding.add((byte) subType);


        //1: Color Space Transformation: RGB->Y'CbCr
        byte[] ycbcrContent = new byte[3*numPixels];
        for(int i = 0; i < numPixels; ++i) {
            int red = toCompress[3 * i];
            if (red < 0) red = 256 + red;
            int green = toCompress[3 * i + 1];
            if (green < 0) green = 256 + green;
            int blue = toCompress[3 * i + 2];
            if (blue < 0) blue = 256 + blue;
            byte[] ycbcrPix = Utils.RGBtoYCbCr(red, green, blue);
            ycbcrContent[3 * i] = ycbcrPix[0];
            ycbcrContent[3 * i + 1] = ycbcrPix[1];
            ycbcrContent[3 * i + 2] = ycbcrPix[2];
        }


        //2: Downsampling, depending on subType:
        ArrayList<byte[][]> ret = Utils.downsampling(ycbcrContent, file.getWidth(), file.getHeight(), subType);
        byte[][] y = ret.get(0);
        byte[][] cb = ret.get(1);
        byte[][] cr = ret.get(2);

        //LUMINANCE:
        //We encode sequentially, first Y (Luminance) and then Cb & Cr (Chrominance)
        //3. Discrete Cosine Transformation:
        //      3.1. Divide in blocks of 8x8:
        int nBlocksYrow = file.getHeight()/8;
        int nBlocksYcol = file.getWidth()/8;
        if(file.getHeight()%8!=0) ++nBlocksYrow;
        if(file.getWidth()%8!=0) ++nBlocksYcol;
        int lastDCY = 0;

        //Go through all 8x8 blocks of Y:
        for(int blocki = 0; blocki < nBlocksYrow; ++blocki){
            for(int blockj = 0; blockj < nBlocksYcol; ++blockj) {
                int[][] block = new int[8][8];
                int valY;
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        if(blocki*8+i >= y.length) valY = block[i-1][j];
                        else if(blockj*8+j >= y[0].length) valY = block[i][j-1];
                        else valY = y[blocki*8+i][blockj*8+j];
                        if(valY < 0) valY = 256 + valY;

                        //      3.2. Shifted block centered around 0:
                        block[i][j] = valY-128;
                    }
                }

                //      3.3. Apply DCT transformation (With a matrix multiplication):
                double[][] dctBlock = Utils.getDCT(block);

                //4. Quantization (we divide each block by the Losheller Matrix, for a quality of 50%, as specified in the JPEG Standard):
                int[][] quantizedBlock = Utils.getQuantization(dctBlock, 0);

                //5. Entropy Encoding
                //      5.1. Block to zig-zag:
                int[] zigZag = Utils.blockToZigZag(quantizedBlock);

                //      5.2. Huffman encoding:
                encoding.addAll(Utils.getHuffman(zigZag, lastDCY, 0));
                lastDCY = zigZag[0];
            }
        }

        //CHROMINANCE:
        //3. Discrete Cosine Transformation:
        //      3.1. Divide in blocks of 8x8:

        int horizontalReduction = 1;
        int verticalReduction = 1;
        if(subType == 1){//4:2:2
            horizontalReduction = 2;
        } else if(subType == 2){//4:2:0
            horizontalReduction = 2;
            verticalReduction = 2;
        }
        int heightOfTables = file.getHeight()/verticalReduction;
        if(file.getHeight()%verticalReduction!=0) ++heightOfTables;
        int widthOfTables = file.getWidth()/horizontalReduction;
        if(file.getWidth()%horizontalReduction!=0) ++widthOfTables;

        int nBlocksCbCrrow = heightOfTables/8;
        int nBlocksCbCrcol = widthOfTables/8;
        if(heightOfTables%8!=0) ++nBlocksCbCrrow;
        if(widthOfTables%8!=0) ++nBlocksCbCrcol;
        int lastDCCb = 0;
        int lastDCCr = 0;
        List<Byte> encodingCb = new ArrayList<>();
        List<Byte> encodingCr = new ArrayList<>();

        //Go through all 8x8 blocks of Cb and Cr:
        for(int blocki = 0; blocki < nBlocksCbCrrow; ++blocki){
            for(int blockj = 0; blockj < nBlocksCbCrcol; ++blockj) {
                int[][] blockCb = new int[8][8];
                int[][] blockCr = new int[8][8];
                int valCb;
                int valCr;
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        if(blocki*8+i >= cb.length){
                            valCb = blockCb[i-1][j];
                            valCr = blockCr[i-1][j];
                        }
                        else if(blockj*8+j >= cb[0].length) {
                            valCb = blockCb[i][j-1];
                            valCr = blockCr[i][j-1];
                        }
                        else {
                            valCb = cb[blocki*8+i][blockj*8+j];
                            valCr = cr[blocki*8+i][blockj*8+j];
                        }
                        if(valCb < 0) valCb = 256 + valCb;
                        if(valCr < 0) valCr = 256 + valCr;

                        //      3.2. Shifted block centered around 0:
                        blockCb[i][j] = valCb - 128;
                        blockCr[i][j] = valCr - 128;
                    }
                }

                //      3.3. Apply DCT transformation (With a matrix multiplication):
                double[][] dctBlockCb = Utils.getDCT(blockCb);
                double[][] dctBlockCr = Utils.getDCT(blockCr);

                //4. Quantization (we divide each block by the Chrominance Quantization Matrix, for a quality of 50%, as specified in the JPEG Standard):
                int[][] quantizedBlockCb = Utils.getQuantization(dctBlockCb, 1);
                int[][] quantizedBlockCr = Utils.getQuantization(dctBlockCr, 1);

                //5. Entropy Coding
                //      5.1. Block to zig-zag:
                int[] zigZagCb = Utils.blockToZigZag(quantizedBlockCb);
                int[] zigZagCr = Utils.blockToZigZag(quantizedBlockCr);

                //      5.2. Huffman encoding:
                encodingCb.addAll(Utils.getHuffman(zigZagCb, lastDCCb, 1));
                encodingCr.addAll(Utils.getHuffman(zigZagCr, lastDCCr, 1));
                lastDCCb = zigZagCb[0];
                lastDCCr = zigZagCr[0];
            }
        }

        encoding.addAll(encodingCb);
        encoding.addAll(encodingCr);

        compressedContent = new byte[encoding.size()];
        for(int i = 0; i < encoding.size(); ++i) compressedContent[i] = encoding.get(i);

        statistic.stopClock();
        statistic.setFinalSize(encoding.size());
        statistic.calculateCompression();
        statistic.calculateCompressionSpeed();

        return compressedContent;
    }



    /*****************************************************************************************************************/
    /**********************************************DECOMPRESS ALGORITHM:**********************************************/
    /*****************************************************************************************************************/
    public byte[] decompress(File file){


        statistic.setCompressionOrDecompression(1);
        statistic.setIniSize(file.getSize());
        statistic.startClock();

        //Read the values from the file:
        byte[] toDecompress = file.getContent();
        int subType = toDecompress[0];
        statistic.setSubType(subType);
        int numPixels = file.getHeight() * file.getWidth();
        byte[] decompressedContent = new byte[numPixels * 3];

        //Initialize Y, Cb and Cr tables:
        byte[][] y = new byte[file.getHeight()][file.getWidth()];
        int horizontalReduction = 1;
        int verticalReduction = 1;
        if(subType == 1){//4:2:2
            horizontalReduction = 2;
        } else if(subType == 2){//4:2:0
            horizontalReduction = 2;
            verticalReduction = 2;
        }
        int heightOfTables = file.getHeight() / verticalReduction;
        if(file.getHeight() % verticalReduction != 0) ++heightOfTables;
        int widthOfTables = file.getWidth() / horizontalReduction;
        if(file.getWidth() % horizontalReduction != 0) ++widthOfTables;
        byte[][] cb = new byte[heightOfTables][widthOfTables];
        byte[][] cr = new byte[heightOfTables][widthOfTables];



        //Do the compression process in reverse, starting with Y component:
        int nBlocksYrow = file.getHeight()/8;
        int nBlocksYcol = file.getWidth()/8;
        if(file.getHeight() % 8 != 0) ++nBlocksYrow;
        if(file.getWidth() % 8 != 0) ++nBlocksYcol;
        int lastDCY = 0;
        int bytePos = 1; //Starting at 1 because 0 is the subType ID.
        for(int blocki = 0; blocki < nBlocksYrow; ++blocki) {
            for (int blockj = 0; blockj < nBlocksYcol; ++blockj) {
                //5. Entropy Decoding:
                //      5.2. Huffman decoding:
                int[] zigZag = new int[64];
                int bitPos = 0;

                //First, get the encoded DC coefficient:
                String byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);

                int row = 0;
                while(byteInBinary.charAt(bitPos)=='1' && row < 16){
                    ++row;
                    ++bitPos;
                    if(bitPos == 8){
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                }
                if (row != 16) ++bitPos;
                StringBuilder colStr = new StringBuilder();
                for(int i = 0; i < row; ++i){
                    if(bitPos == 8) {
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                    colStr.append(byteInBinary.charAt(bitPos));
                    ++bitPos;
                }
                if(row == 0) ++bitPos;
                colStr.insert(0, "0");//To make C always unsigned
                int col = Integer.parseInt(colStr.toString(), 2);
                zigZag[0] = Utils.getNumberInDCTable(row, col)+lastDCY;
                lastDCY = zigZag[0];

                //Now the 63 AC coefficients:
                boolean EOB = false;
                int zigZagPos = 1;
                String acNumber = "";
                while(!EOB){
                    if(bitPos == 8) {
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                    acNumber += byteInBinary.charAt(bitPos);
                    ++bitPos;
                    //EOB for Luminance:
                    if (acNumber.equals("1010")) EOB = true;
                        //ZRL for Luminance:
                    else if (acNumber.equals("11111111001")) {
                        zigZagPos += 15;
                        acNumber = "";
                    }
                    else if (acNumber.length() > 1) {
                        int[] posAcTable = Utils.findPosAcTable(acNumber, 0);

                        //if we found the acNumber:
                        if(posAcTable[0] != -1){
                            //Add runLength 0s to the table:
                            int runLength = posAcTable[0];
                            zigZagPos += runLength;
                            int R = posAcTable[1];
                            StringBuilder cStr = new StringBuilder();
                            for(int i = 0; i < R; ++i){
                                if(bitPos == 8) {
                                    bitPos = 0;
                                    ++bytePos;
                                    byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                                }
                                cStr.append(byteInBinary.charAt(bitPos));
                                ++bitPos;
                            }
                            //To make C always unsigned:
                            cStr.insert(0, "0");
                            int C = Integer.parseInt(cStr.toString(), 2);
                            zigZag[zigZagPos] = Utils.getNumberInDCTable(R, C);

                            //reset AC number in bits:
                            acNumber = "";
                            ++zigZagPos;

                        }
                    }
                }

                ++bytePos;
                bitPos = 0;

                //Got the decoded block in zig-zag order.
                //Now, convert it to a 8x8 block:
                //      5.1. Block to zig-zag:
                int[][] quantizedBlock = Utils.zigZagToBlock(zigZag);

                //4. de-Quantization (we multiply each block by the Losheller Matrix, for a quality of 50%, as specified in the JPEG Standard):
                int[][] dctBlock = Utils.getDequantization(quantizedBlock, 0);

                //3. Apply inverse DCT transformation (With a matrix multiplication):
                double[][] block = Utils.getInverseDCT(dctBlock);

                //Get the Y' component pixels:
                int valY;
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        if(blocki*8+i < y.length && blockj * 8 + j < y[0].length) {
                            valY = (int) Math.round(block[i][j] + 128);
                            y[blocki * 8 + i][blockj * 8 + j] = (byte) valY;
                        }
                    }
                }

            }
        }

        //For Cb and Cr loops:
        int nBlocksCbCrRow = heightOfTables / 8;
        int nBlocksCbCrcol = widthOfTables / 8;
        if(heightOfTables % 8 != 0) ++nBlocksCbCrRow;
        if(widthOfTables % 8 != 0) ++nBlocksCbCrcol;

        //Now decode Cb:
        int lastDCCb = 0;
        for(int blocki = 0; blocki < nBlocksCbCrRow; ++blocki) {
            for (int blockj = 0; blockj < nBlocksCbCrcol; ++blockj) {
                //5. Entropy Decoding:
                //      5.2. Huffman decoding:
                int[] zigZag = new int[64];
                int bitPos = 0;

                //First, get the encoded DC coefficient:
                String byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);

                int row = 0;
                while(byteInBinary.charAt(bitPos) == '1' && row < 16){
                    ++row;
                    ++bitPos;
                    if(bitPos == 8){
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                }
                if(row != 16) ++bitPos;
                StringBuilder colStr = new StringBuilder();
                for(int i = 0; i < row; ++i){
                    if(bitPos == 8) {
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                    colStr.append(byteInBinary.charAt(bitPos));
                    ++bitPos;
                }
                if(row == 0) ++bitPos;
                //To make col always unsigned:
                colStr.insert(0, "0");
                int col = Integer.parseInt(colStr.toString(), 2);
                zigZag[0] = Utils.getNumberInDCTable(row, col) + lastDCCb;
                lastDCCb = zigZag[0];

                //Now the 63 AC coefficients:
                boolean EOB = false;
                int zigZagPos = 1;
                String acNumber = "";
                while(!EOB){
                    if(bitPos == 8) {
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                    acNumber += byteInBinary.charAt(bitPos);
                    ++bitPos;
                    //EOB for Chrominance:
                    if (acNumber.equals("00")) EOB = true;
                        //ZRL for Chrominance:
                    else if (acNumber.equals("1111111010")) {
                        zigZagPos += 15;
                        acNumber = "";
                    }
                    else if (acNumber.length() > 1) {

                        int[] posAcTable = Utils.findPosAcTable(acNumber, 1);

                        //if we found the acNumber:
                        if(posAcTable[0] != -1){
                            //Add runLength 0s to the table:
                            int runLength = posAcTable[0];
                            zigZagPos += runLength;
                            int R = posAcTable[1];
                            StringBuilder cStr = new StringBuilder();
                            for(int i = 0; i < R; ++i){
                                if(bitPos == 8) {
                                    bitPos = 0;
                                    ++bytePos;
                                    byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                                }
                                cStr.append(byteInBinary.charAt(bitPos));
                                ++bitPos;
                            }

                            cStr.insert(0, "0");//To make C always unsigned
                            int C = Integer.parseInt(cStr.toString(), 2);
                            zigZag[zigZagPos] = Utils.getNumberInDCTable(R, C);

                            //reset AC number in bits:
                            acNumber = "";
                            ++zigZagPos;

                        }
                    }
                }

                ++bytePos;
                bitPos = 0;

                //Got the decoded block in zig-zag order.
                //Now, convert it to a 8x8 block:
                //      5.1. Block to zig-zag:
                int[][] quantizedBlock = Utils.zigZagToBlock(zigZag);

                //4. de-Quantization (we multiply each block by the Losheller Matrix, for a quality of 50%, as specified in the JPEG Standard):
                int[][] dctBlock = Utils.getDequantization(quantizedBlock, 1);

                //3. Apply inverse DCT transformation (With a matrix multiplication):
                double[][] block = Utils.getInverseDCT(dctBlock);

                //Get the Cb component pixels:
                int valCb;
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        if(blocki*8+i < cb.length && blockj*8+j < cb[0].length) {
                            valCb = (int) Math.round(block[i][j] + 128);
                            cb[blocki*8+i][blockj*8+j] = (byte) valCb;
                        }
                    }
                }

            }
        }

        //Now decode Cr:
        int lastDCCr = 0;
        for(int blocki = 0; blocki < nBlocksCbCrRow; ++blocki) {
            for (int blockj = 0; blockj < nBlocksCbCrcol; ++blockj) {
                //5. Entropy Decoding:
                //      5.2. Huffman decoding:
                int[] zigZag = new int[64];
                int bitPos = 0;

                //First, get the encoded DC coefficient:
                String byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);

                int row = 0;
                while(byteInBinary.charAt(bitPos) == '1' && row < 16){
                    ++row;
                    ++bitPos;
                    if(bitPos == 8){
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                }
                if(row != 16) ++bitPos;
                StringBuilder colStr = new StringBuilder();
                for(int i = 0; i < row; ++i){
                    if(bitPos == 8) {
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                    colStr.append(byteInBinary.charAt(bitPos));
                    ++bitPos;
                }
                if(row == 0) ++bitPos;
                //To make col always unsigned:
                colStr.insert(0, "0");
                int col = Integer.parseInt(colStr.toString(), 2);
                zigZag[0] = Utils.getNumberInDCTable(row, col)+lastDCCr;
                lastDCCr = zigZag[0];

                //Now the 63 AC coefficients:
                boolean EOB = false;
                int zigZagPos = 1;
                String acNumber = "";
                while(!EOB){
                    if(bitPos == 8) {
                        bitPos = 0;
                        ++bytePos;
                        byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                    }
                    acNumber += byteInBinary.charAt(bitPos);
                    ++bitPos;
                    //EOB for Chrominance:
                    if (acNumber.equals("00")) EOB = true;
                        //ZRL for Chrominance:
                    else if (acNumber.equals("1111111010")) {
                        zigZagPos += 15;
                        acNumber = "";
                    }
                    else if (acNumber.length() > 1) {

                        int[] posAcTable = Utils.findPosAcTable(acNumber, 1);

                        //if we found the acNumber:
                        if(posAcTable[0] != -1){
                            //Add runLength 0s to the table:
                            int runLength = posAcTable[0];
                            zigZagPos += runLength;
                            int R = posAcTable[1];
                            StringBuilder cStr = new StringBuilder();
                            for(int i = 0; i < R; ++i){
                                if(bitPos == 8) {
                                    bitPos = 0;
                                    ++bytePos;
                                    byteInBinary = Utils.byteToBinaryString(toDecompress[bytePos]);
                                }
                                cStr.append(byteInBinary.charAt(bitPos));
                                ++bitPos;
                            }

                            cStr.insert(0, "0");//To make C always unsigned
                            int C = Integer.parseInt(cStr.toString(), 2);
                            zigZag[zigZagPos] = Utils.getNumberInDCTable(R, C);

                            //reset AC number in bits:
                            acNumber = "";
                            ++zigZagPos;

                        }
                    }
                }

                ++bytePos;
                bitPos = 0;

                //Got the decoded block in zig-zag order.
                //Now, convert it to a 8x8 block:
                //      5.1. Block to zig-zag:
                int[][] quantizedBlock = Utils.zigZagToBlock(zigZag);

                //4. de-Quantization (we multiply each block by the Losheller Matrix, for a quality of 50%, as specified in the JPEG Standard):
                int[][] dctBlock = Utils.getDequantization(quantizedBlock, 1);

                //3. Apply inverse DCT transformation (With a matrix multiplication):
                double[][] block = Utils.getInverseDCT(dctBlock);

                //Get the Cb component pixels:
                int valCr;
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        if(blocki*8+i < cr.length && blockj*8+j < cr[0].length) {
                            valCr = (int) Math.round(block[i][j] + 128);
                            cr[blocki*8+i][blockj*8+j] = (byte) valCr;
                        }
                    }
                }

            }
        }

        //2: reverse Downsampling:
        byte[] ycbcrContent = new byte[3*numPixels];
        int actualPix = 0;
        for(int i = 0; i < file.getHeight(); ++i){
            for(int j = 0; j < file.getWidth(); ++j){
                ycbcrContent[3*actualPix] = y[i][j];
                switch (subType) {
                    case 0: //4:4:4
                        ycbcrContent[3 * actualPix + 1] = cb[i][j];
                        ycbcrContent[3 * actualPix + 2] = cr[i][j];
                        break;
                    case 1:
                        ycbcrContent[3 * actualPix + 1] = cb[i][j / 2];
                        ycbcrContent[3 * actualPix + 2] = cr[i][j / 2];
                        break;
                    case 2:
                        ycbcrContent[3 * actualPix + 1] = cb[i / 2][j / 2];
                        ycbcrContent[3 * actualPix + 2] = cr[i / 2][j / 2];
                        break;
                }
                ++actualPix;
            }
        }

        //From y[], cb[], cr[] to rgb[]
        actualPix = 0;
        for(int i = 0; i < file.getHeight();++i){
            for(int j = 0; j < file.getWidth(); ++j){
                int yy = ycbcrContent[3 * actualPix];
                int cbb = ycbcrContent[3 * actualPix + 1];
                int crr = ycbcrContent[3 * actualPix + 2];
                if(yy < 0) yy = 256 + yy;
                if(cbb < 0) cbb = 256 + cbb;
                if(crr < 0) crr = 256 + crr;
                byte[] rgbPix = Utils.YCbCrtoRGB(yy, cbb, crr);
                decompressedContent[3 * actualPix] = rgbPix[0];
                decompressedContent[3 * actualPix + 1] = rgbPix[1];
                decompressedContent[3 * actualPix + 2] = rgbPix[2];
                ++actualPix;
            }
        }

        byte[] decompressedFile = new byte[file.getHeaderSize() + 3 * numPixels];
        String header = file.getHeader();
        for(int i = 0; i < file.getHeaderSize(); ++i) decompressedFile[i] = (byte) header.charAt(i);
        System.arraycopy(decompressedContent, 0, decompressedFile, file.getHeaderSize(), 3 * numPixels);

        statistic.stopClock();
        statistic.setFinalSize(decompressedContent.length);
        statistic.calculateCompression();
        statistic.calculateCompressionSpeed();

        return decompressedFile;
    }

}
