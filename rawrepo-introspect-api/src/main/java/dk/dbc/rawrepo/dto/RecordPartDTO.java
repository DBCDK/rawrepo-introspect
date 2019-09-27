package dk.dbc.rawrepo.dto;

public class RecordPartDTO {

    private String content;
    private String type;

    public RecordPartDTO() {
    }

    public RecordPartDTO(String content, String type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
