package dk.dbc.rawrepo.dto;


import java.util.List;

public class RelationDTO {

    private List<RecordIdDTO> nodes;
    private List<EdgeDTO> edges;

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
}
