package dk.dbc.rawrepo.dto;

public class EdgeDTO {

    private RecordIdDTO parent;
    private RecordIdDTO child;

    public RecordIdDTO getParent() {
        return parent;
    }

    public void setParent(RecordIdDTO parent) {
        this.parent = parent;
    }

    public RecordIdDTO getChild() {
        return child;
    }

    public void setChild(RecordIdDTO child) {
        this.child = child;
    }
}
