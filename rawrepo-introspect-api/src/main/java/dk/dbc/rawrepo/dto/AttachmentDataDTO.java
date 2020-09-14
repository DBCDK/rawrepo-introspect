/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

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
