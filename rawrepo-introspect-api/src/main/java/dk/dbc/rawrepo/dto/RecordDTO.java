package dk.dbc.rawrepo.dto;

import java.util.List;

public class RecordDTO {

    private List<RecordPartDTO> recordParts;

    public List<RecordPartDTO> getRecordParts() {
        return recordParts;
    }

    public void setRecordParts(List<RecordPartDTO> recordParts) {
        this.recordParts = recordParts;
    }
}
