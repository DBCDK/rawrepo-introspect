/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo.dto;

import dk.dbc.rawrepo.RecordId;

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
