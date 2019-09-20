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

        this.attachmentTypeFormatterDanbib = this.attachmentTypeFormatterDanbib.bind(this);
        this.attachmentTypeFormatterUpdate = this.attachmentTypeFormatterUpdate.bind(this);
        this.attachmentTypeFormatterBasis = this.attachmentTypeFormatterBasis.bind(this);
    }

    attachmentTypeFormatterDanbib(cell, row) {
        return <a href={'/api/v1/attachment/danbib/' + this.props.bibliographicRecordId + '/' + row.sourceId +'/' + row.type} target='_blank'>{cell}</a>
    }

    attachmentTypeFormatterUpdate(cell, row) {
        return <a href={'/api/v1/attachment/update/' + this.props.bibliographicRecordId + '/' + row.sourceId +'/' + row.type} target='_blank'>{cell}</a>
    }

    attachmentTypeFormatterBasis(cell, row) {
        return <a href={'/api/v1/attachment/basis/' + this.props.bibliographicRecordId + '/' + row.sourceId +'/' + row.type} target='_blank'>{cell}</a>
    }


    render() {
        return (
            <div>
                <div>
                    <h2>Moreinfo Danbib</h2>
                    <BootstrapTable data={this.props.attachmentInfoDanbib}
                                    options={{noDataText: 'Der blev ikke fundet nogen attachments for denne post'}}>
                        <TableHeaderColumn dataField='type'
                                           isKey={true}
                                           dataSort
                                           dataFormat={this.attachmentTypeFormatterDanbib}>TYPE</TableHeaderColumn>
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
                        <TableHeaderColumn dataField='type'
                                           isKey={true}
                                           dataSort
                                           dataFormat={this.attachmentTypeFormatterBasis}>TYPE</TableHeaderColumn>
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
                        <TableHeaderColumn dataField='type'
                                           isKey={true}
                                           dataSort
                                           dataFormat={this.attachmentTypeFormatterUpdate}>TYPE</TableHeaderColumn>
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