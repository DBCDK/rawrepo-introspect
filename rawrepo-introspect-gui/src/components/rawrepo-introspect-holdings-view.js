/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {BootstrapTable, TableHeaderColumn} from "react-bootstrap-table";

class RawrepoIntrospectHoldingsView extends React.Component {

    constructor(props) {
        super(props);

        this.linkFormatter = this.linkFormatter.bind(this);
    }

    linkFormatter(cell) {
        return `<a href='${this.props.holdingsItemsIntrospectUrl}/holdings-items-introspection/app/items/?agencyId=${cell}&bibRecId=${this.props.bibliographicRecordId}'} target="_blank">Åben i holdings items introspect</a>`
    }

    render() {
        return (
            <div>
                <h2>Biblioteker med beholding</h2><br/>
                <BootstrapTable data={this.props.holdingsItems}
                                options={{noDataText: 'Der blev ikke fundet nogen beholdning for denne post'}}>
                    <TableHeaderColumn dataField='agencyId'
                                       isKey={true}
                                       dataSort>Agency Id</TableHeaderColumn>
                    <TableHeaderColumn dataField='agencyId'
                                       dataSort
                                       dataFormat={this.linkFormatter}>Link</TableHeaderColumn>
                </BootstrapTable>
            </div>
        );
    }
}

export default RawrepoIntrospectHoldingsView;

