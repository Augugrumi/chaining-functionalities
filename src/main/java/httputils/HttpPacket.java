package httputils;

public class HttpPacket {
    private final String header;
    private final String postData;

    public HttpPacket(String header, String postData) {
        this.header = header;
        this.postData = postData;
    }

    public String getHeader() {
        return header;
    }

    public String getPostData() {
        return postData;
    }

    @Override
    public String toString() {
        return header + postData;
    }
}
