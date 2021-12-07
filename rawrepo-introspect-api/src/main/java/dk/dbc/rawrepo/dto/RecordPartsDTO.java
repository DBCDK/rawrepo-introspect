/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo.dto;

import java.util.List;

public class RecordPartsDTO {

    private List<RecordPartDTO> recordParts;

    public List<RecordPartDTO> getRecordParts() {
        return recordParts;
    }

    public void setRecordParts(List<RecordPartDTO> recordParts) {
        this.recordParts = recordParts;
    }
}
