package hr.fer.ruazosa.audionotes;


public class ResponseFile {
    private String name;
    private String url;
    private String description;
    private long size;

    public ResponseFile(String name, String description, String url, long size) {
        this.name = name;
        this.description = description;
        this.url = url;
        this.size = size;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }
}