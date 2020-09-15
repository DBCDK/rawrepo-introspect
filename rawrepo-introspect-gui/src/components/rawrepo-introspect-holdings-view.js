/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";

class RawrepoIntrospectHoldingsView extends React.Component {

    constructor(props) {
        super(props);

        this.linkFormatter = this.linkFormatter.bind(this);
    }

    linkFormatter(cell, row) {
        return `<a href='${this.props.holdingsItemsIntrospectUrl}/holdings-items-introspection/app/items/?agencyId=${row.agencyId}&bibRecId=${row.bibliographicRecordId}'} target="_blank">Åben i holdings items introspect</a>`
    }

    render() {
        return (
            <div key='holdingsitems'>
                {this.props.holdingsItems.map(holdingsItem =>
                    <div key={holdingsItem.bibliographicRecordId}>
                        <h2>Biblioteker med beholding
                            på {holdingsItem.bibliographicRecordId === this.props.bibliographicRecordId ? 'denne post' : 'tidligere post (' + holdingsItem.bibliographicRecordId + ')'}</h2>
                        <BootstrapTable data={holdingsItem.holdingsAgencies}
                                        options={{noDataText: 'Der blev ikke fundet aktive beholdninger i holdings-items-DB for denne post'}}>
                            <TableHeaderColumn dataField='agencyId'
                                               isKey={true}
                                               dataSort>Agency Id</TableHeaderColumn>
                            <TableHeaderColumn dataField='agencyId'
                                               dataFormat={this.linkFormatter}>Link</TableHeaderColumn>
                        </BootstrapTable>
                    </div>
                )
                }
            </div>
        );
    }
}

export default RawrepoIntrospectHoldingsView;

