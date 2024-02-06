package dk.dbc.rawrepo.dto;

public class AttachmentDataDTO {

    private byte[] data;
    private String mimetype;

    public AttachmentDataDTO(byte[] data, String mimetype) {
        this.data = data;
        this.mimetype = mimetype;
    }

    public byte[] getData() {
        return data;
    }

    public String getMimetype() {
        return mimetype;
    }
}
