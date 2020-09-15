/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo.dto;

import dk.dbc.rawrepo.RecordId;

public class EdgeDTO {

    private RecordId parent;
    private RecordId child;

    public RecordId getParent() {
        return parent;
    }

    public void setParent(RecordId parent) {
        this.parent = parent;
    }

    public RecordId getChild() {
        return child;
    }

    public void setChild(RecordId child) {
        this.child = child;
    }
}
