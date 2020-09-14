/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 *  See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

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
