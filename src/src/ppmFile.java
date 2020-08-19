public class ppmFile {
    protected byte[] content;
    protected int size; //In bytes
    private String header;
    private int headerSize;
    private int height;
    private int width;
    private int resolution;


    //Constructors:
    public ppmFile() {}

    public ppmFile(String path){

    }

    //Reads the header from the full file content and sets the values header, height, width and resolution:
    public void readHeader(byte[] content){
        String header = "";
        char c;
        int j = 0;
        for (int i = 0; i < 3; ++i){
            c = (char) content[j];
            header += c;
            ++j;
            if(c == '#')--i;
            while((c = (char) content[j]) != '\n') {
                header += c;
                ++j;
            }
            header += c;
            ++j;
        }
        this.header = header;
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
                    String width = "";
                    String height = "";
                    while(c != ' '){
                        width += c;
                        ++j;
                        c = header.charAt(j);
                    }
                    ++j;
                    c = header.charAt(j);
                    while(c != '\n' && c != '#' && c != ' '){
                        height += c;
                        ++j;
                        c = header.charAt(j);
                    }
                    while(header.charAt(j)!='\n')++j;
                    ++j;
                    this.width = Integer.parseInt(width);
                    this.height = Integer.parseInt(height);
                } else {
                    String resolution = "";
                    while(c != '\n'){
                        resolution += c;
                        ++j;
                        c = header.charAt(j);
                    }
                    this.resolution = Integer.parseInt(resolution);
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
