package dk.dbc.rawrepo.dto;

public class HoldingsItemsDTO {
    private int agencyId;
    // It shouldn't be necessary with bibliographicRecordId here as it is already on the outer DTO
    // However due to how the GUI works it saves a lot of fiddling to have values present on the same object
    private String bibliographicRecordId;

    public HoldingsItemsDTO() {

    }

    public HoldingsItemsDTO(String bibliographicRecordId, int agencyId) {
        this.bibliographicRecordId = bibliographicRecordId;
        this.agencyId = agencyId;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public String getBibliographicRecordId() {
        return bibliographicRecordId;
    }

    public void setBibliographicRecordId(String bibliographicRecordId) {
        this.bibliographicRecordId = bibliographicRecordId;
    }
}
