/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo.dto;

public class AttachmentInfoDTO {

    private String type;
    private int sourceId;
    private String ajourDate;
    private String createDate;
    private int attachmentSize;

    public AttachmentInfoDTO(String type, int sourceId, String ajourDate, String createDate, int attachmentSize) {
        this.type = type;
        this.sourceId = sourceId;
        this.ajourDate = ajourDate;
        this.createDate = createDate;
        this.attachmentSize = attachmentSize;
    }

    public AttachmentInfoDTO() {

    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAjourDate() {
        return ajourDate;
    }

    public void setAjourDate(String ajourDate) {
        this.ajourDate = ajourDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getAttachmentSize() {
        return attachmentSize;
    }

    public void setAttachmentSize(int attachmentSize) {
        this.attachmentSize = attachmentSize;
    }

}
