package dk.dbc.rawrepo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class RelationDTO {

    private List<RecordIdDTO> nodes;
    private List<EdgeDTO> edges;
    private List<RecordIdDTO> deletedAttachments;

    public List<RecordIdDTO> getNodes() {
        return nodes;
    }

    public void setNodes(List<RecordIdDTO> nodes) {
        this.nodes = nodes;
    }

    public List<EdgeDTO> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeDTO> edges) {
        this.edges = edges;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public List<RecordIdDTO> getDeletedAttachments() {
        return deletedAttachments;
    }

    public void setDeletedAttachments(List<RecordIdDTO> deletedAttachments) {
        this.deletedAttachments = deletedAttachments;
    }
}
