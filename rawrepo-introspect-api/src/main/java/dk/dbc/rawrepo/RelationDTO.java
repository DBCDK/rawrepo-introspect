package dk.dbc.rawrepo;

import java.util.List;

public class RelationDTO {

    private List<RecordId> nodes;
    private List<EdgeDTO> edges;

    public List<RecordId> getNodes() {
        return nodes;
    }

    public void setNodes(List<RecordId> nodes) {
        this.nodes = nodes;
    }

    public List<EdgeDTO> getEdges() {
        return edges;
    }

    public void setEdges(List<EdgeDTO> edges) {
        this.edges = edges;
    }
}
