/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

package dk.dbc.rawrepo.dto;

import java.util.ArrayList;
import java.util.List;

public class HoldingsItemsListDTO {
    private String bibliographicRecordId;
    private List<HoldingsItemsDTO> holdingsAgencies;

    public HoldingsItemsListDTO() {
        holdingsAgencies = new ArrayList<>();
    }

    public String getBibliographicRecordId() {
        return bibliographicRecordId;
    }

    public void setBibliographicRecordId(String bibliographicRecordId) {
        this.bibliographicRecordId = bibliographicRecordId;
    }

    public List<HoldingsItemsDTO> getHoldingsAgencies() {
        return holdingsAgencies;
    }

    public void setHoldingsAgencies(List<HoldingsItemsDTO> holdingsAgencies) {
        this.holdingsAgencies = holdingsAgencies;
    }
}
