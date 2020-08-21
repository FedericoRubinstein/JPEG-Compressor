import java.util.ArrayList;
import java.util.List;

public class JPEG {

    protected Statistics statistic;

    JPEG(){this.statistic = new Statistics();}



    //File is the ppmFile representation of an image to compress.
    //subType is the type of Subsampling: 0 for 4:4:4, 1 for 4:2:2 and 2 for 4:2:0 (recommended)
    public byte[] compress(ppmFile file, int subType){

        statistic.setCompressionOrDecompression(0);
        statistic.setIniSize(file.getSize());
        statistic.setSubType(subType);
        statistic.startClock();

        int numPixels = file.getWidth()*file.getHeight();
        byte[] toCompress = file.getContent();
        byte[] compressedContent;
        List<Byte> encoding = new ArrayList<>();
        //Adding Compressing with JPEG ID:
        encoding.add((byte) 4);
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
        //3. Discrete Cosine Transformation:
        //      3.1. Divide in blocks of 8x8:
        //Codificamos secuencialmente, primero Y y después Cb y Cr
        int nBloquesYfil = file.getHeight()/8;
        int nBloquesYcol = file.getWidth()/8;
        if(file.getHeight()%8!=0) ++nBloquesYfil;
        if(file.getWidth()%8!=0) ++nBloquesYcol;
        int lastDCY = 0;
        //Recorremos todos los bloques 8x8 de Y:
        for(int bloquei = 0; bloquei < nBloquesYfil; ++bloquei){
            for(int bloquej = 0; bloquej < nBloquesYcol; ++bloquej) {
                int[][] bloque = new int[8][8];
                int valY;
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        if(bloquei*8+i >= y.length) valY = bloque[i-1][j];
                        else if(bloquej*8+j >= y[0].length) valY = bloque[i][j-1];
                        else valY = y[bloquei*8+i][bloquej*8+j];
                        if(valY < 0) valY = 256 + valY;

                        //      3.2. Shifted block centered around 0:
                        bloque[i][j] = valY-128;
                    }
                }

                //      3.3. Apply DCT transformation (With a matrix multiplication):
                double[][] dctBlock = Utils.getDCT(bloque);

                //4. Quantization (we divide each block by the Losheller Matrix, for a quality of 50%, as specified in the JPEG Standard):
                int[][] quantizedBlock = Utils.getQuantization(dctBlock, 0);

                //5. Entropy Coding
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

        int nBloquesCbCrfil = heightOfTables/8;
        int nBloquesCbCrcol = widthOfTables/8;
        if(heightOfTables%8!=0) ++nBloquesCbCrfil;
        if(widthOfTables%8!=0) ++nBloquesCbCrcol;
        int lastDCCb = 0;
        int lastDCCr = 0;
        List<Byte> encodingCb = new ArrayList<>();
        List<Byte> encodingCr = new ArrayList<>();
        //Recorremos todos los bloques 8x8 de Cb y Cr:
        for(int bloquei = 0; bloquei < nBloquesCbCrfil; ++bloquei){
            for(int bloquej = 0; bloquej < nBloquesCbCrcol; ++bloquej) {
                int[][] bloqueCb = new int[8][8];
                int[][] bloqueCr = new int[8][8];
                int valCb;
                int valCr;
                for (int i = 0; i < 8; ++i) {
                    for (int j = 0; j < 8; ++j) {
                        if(bloquei*8+i >= cb.length){
                            valCb = bloqueCb[i-1][j];
                            valCr = bloqueCr[i-1][j];
                        }
                        else if(bloquej*8+j >= cb[0].length) {
                            valCb = bloqueCb[i][j-1];
                            valCr = bloqueCr[i][j-1];
                        }
                        else {
                            valCb = cb[bloquei*8+i][bloquej*8+j];
                            valCr = cr[bloquei*8+i][bloquej*8+j];
                        }
                        if(valCb < 0) valCb = 256 + valCb;
                        if(valCr < 0) valCr = 256 + valCr;

                        //      3.2. Shifted block centered around 0:
                        bloqueCb[i][j] = valCb - 128;
                        bloqueCr[i][j] = valCr - 128;
                    }
                }

                //      3.3. Apply DCT transformation (With a matrix multiplication):
                double[][] dctBlockCb = Utils.getDCT(bloqueCb);
                double[][] dctBlockCr = Utils.getDCT(bloqueCr);

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

}
