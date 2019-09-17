/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {BootstrapTable, TableHeaderColumn} from 'react-bootstrap-table';

const dateFormatter = function (cell, row) {
    let dateValue = new Date(cell);

    // Used for making date and time segments two chars long.
    let leftPad2 = function (val) {
        return ("00" + val).slice(-2)
    };

    return dateValue.getFullYear() +
        '-' + leftPad2(dateValue.getMonth() + 1) +
        '-' + leftPad2(dateValue.getDate()) +
        ' ' + leftPad2(dateValue.getHours()) +
        ':' + leftPad2(dateValue.getMinutes()) +
        ':' + leftPad2(dateValue.getSeconds());
};

class RawrepoIntrospectAttachmentView extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div>
                <div>
                    <h2>Moreinfo Danbib</h2>
                    <BootstrapTable data={this.props.attachmentInfoDanbib}
                                    options={{noDataText: 'Der blev ikke fundet nogen attachments for denne post'}}>
                        <TableHeaderColumn dataField='type' isKey={true} dataSort>TYPE</TableHeaderColumn>
                        <TableHeaderColumn dataField='sourceId' dataSort data>SOURCE_ID</TableHeaderColumn>
                        <TableHeaderColumn dataField='ajourDate'
                                           dataFormat={dateFormatter}>AJOURDATO</TableHeaderColumn>
                        <TableHeaderColumn dataField='createDate'
                                           dataFormat={dateFormatter}>OPRETDATO</TableHeaderColumn>
                        <TableHeaderColumn dataField='attachmentSize'>LGD</TableHeaderColumn>
                    </BootstrapTable>
                </div>
                <div>
                    <h2>Moreinfo Basis</h2>
                    <BootstrapTable data={this.props.attachmentInfoBasis}
                                    options={{noDataText: 'Der blev ikke fundet nogen attachments for denne post'}}>
                        <TableHeaderColumn dataField='type' isKey={true} dataSort>TYPE</TableHeaderColumn>
                        <TableHeaderColumn dataField='sourceId' dataSort data>SOURCE_ID</TableHeaderColumn>
                        <TableHeaderColumn dataField='ajourDate'
                                           dataFormat={dateFormatter}>AJOURDATO</TableHeaderColumn>
                        <TableHeaderColumn dataField='createDate'
                                           dataFormat={dateFormatter}>OPRETDATO</TableHeaderColumn>
                        <TableHeaderColumn dataField='attachmentSize'>LGD</TableHeaderColumn>
                    </BootstrapTable>
                </div>
                <div>
                    <h2>Moreinfo Update</h2>
                    <BootstrapTable data={this.props.attachmentInfoUpdate}
                                    options={{noDataText: 'Der blev ikke fundet nogen attachments for denne post'}}>
                        <TableHeaderColumn dataField='type' isKey={true} dataSort>TYPE</TableHeaderColumn>
                        <TableHeaderColumn dataField='sourceId' dataSort data>SOURCE_ID</TableHeaderColumn>
                        <TableHeaderColumn dataField='ajourDate'
                                           dataFormat={dateFormatter}>AJOURDATO</TableHeaderColumn>
                        <TableHeaderColumn dataField='createDate'
                                           dataFormat={dateFormatter}>OPRETDATO</TableHeaderColumn>
                        <TableHeaderColumn dataField='attachmentSize'>LGD</TableHeaderColumn>
                    </BootstrapTable>
                </div>
            </div>
        );
    }

}

export default RawrepoIntrospectAttachmentView;