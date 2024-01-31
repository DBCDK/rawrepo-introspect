package dk.dbc.rawrepo.dto;

public class ConfigDTO {

    private String instance;
    private String holdingsItemsIntrospectUrl;

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getHoldingsItemsIntrospectUrl() {
        return holdingsItemsIntrospectUrl;
    }

    public void setHoldingsItemsIntrospectUrl(String holdingsItemsIntrospectUrl) {
        this.holdingsItemsIntrospectUrl = holdingsItemsIntrospectUrl;
    }
}
