import java.util.Arrays;

public class ppmFile {
    private byte[] content;
    private int size; //In bytes
    private String header;
    private int headerSize;
    private int height;
    private int width;
    private int resolution;


    //Constructors:
    public ppmFile() {}

    public ppmFile(String path){
        byte[] content = IO.getFile(path);
        readHeader(content);
        this.content = Arrays.copyOfRange(content, this.headerSize, content.length);
        this.size = this.content.length;
    }

    //Reads the header from the full file content and sets the values header, height, width and resolution:
    private void readHeader(byte[] content){
        StringBuilder header = new StringBuilder();
        char c;
        int j = 0;
        for (int i = 0; i < 3; ++i){
            c = (char) content[j];
            header.append(c);
            ++j;
            if(c == '#')--i;
            while((c = (char) content[j]) != '\n') {
                header.append(c);
                ++j;
            }
            header.append(c);
            ++j;
        }
        this.header = header.toString();
        this.headerSize = header.length();

        j = 0;
        for(int i = 0; i < 3; ++i){
            c = header.charAt(j);
            if(c == '#'){
                while(header.charAt(j) != '\n')++j;
                ++j;
                --i;
            } else{
                if(i == 0){
                    while(header.charAt(j) != '\n')++j;
                    ++j;
                } else if(i==1){
                    StringBuilder width = new StringBuilder();
                    StringBuilder height = new StringBuilder();
                    while(c != ' '){
                        width.append(c);
                        ++j;
                        c = header.charAt(j);
                    }
                    ++j;
                    c = header.charAt(j);
                    while(c != '\n' && c != '#' && c != ' '){
                        height.append(c);
                        ++j;
                        c = header.charAt(j);
                    }
                    while(header.charAt(j)!='\n')++j;
                    ++j;
                    this.width = Integer.parseInt(width.toString());
                    this.height = Integer.parseInt(height.toString());
                } else {
                    StringBuilder resolution = new StringBuilder();
                    while(c != '\n'){
                        resolution.append(c);
                        ++j;
                        c = header.charAt(j);
                    }
                    this.resolution = Integer.parseInt(resolution.toString());
                }
            }
        }
    }

    //Setters:
    public void setContent(byte[] content) { this.content = content; }
    public void setSize(int size) { this.size = size; }
    public void setHeader(String header) { this.header = header; }
    public void setHeaderSize(int headerSize) { this.headerSize = headerSize; }
    public void setHeight(int height) {this.height = height;}
    public void setWidth(int width) {this.width = width;}
    public void setResolution(int resolution) {this.resolution = resolution;}

    //Getters:
    public byte[] getContent() { return this.content; }
    public int getSize() { return this.size; }
    public String getHeader() { return this.header; }
    public int getHeaderSize() {return this.headerSize;}
    public int getHeight() {return this.height;}
    public int getWidth() {return this.width;}
    public int getResolution() {return this.resolution;}
}
