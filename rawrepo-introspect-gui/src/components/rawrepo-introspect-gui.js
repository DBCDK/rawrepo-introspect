/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Tab, Tabs} from "react-bootstrap";
import RawrepoIntrospectRecordSelector from './rawrepo-introspect-record-selector';
import RawrepoIntrospectRecordView from './rawrepo-introspect-record-view';
import RawrepoIntrospectRelationsView from './rawrepo-introspect-relations-view';

const request = require('superagent');
const queryString = require('query-string');

class RawrepoIntrospectGUI extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            view: 'record',
            bibliographicRecordId: null,
            agencyId: null,
            agencyIdList: [],
            record: ''
        };

        this.handleSelect = this.handleSelect.bind(this);
        this.onChangeBibliographicRecordId = this.onChangeBibliographicRecordId.bind(this);
        this.onChangeAgencyId = this.onChangeAgencyId.bind(this);

        this.findAgenciesForBibliographicRecordId = this.findAgenciesForBibliographicRecordId.bind(this);
        this.getRecord = this.getRecord.bind(this);
    }

    componentDidMount() {
        const queryParams = queryString.parse(location.search);

        if (queryParams.view === undefined) {
            console.log("View missing - setting default");
            queryParams.view = 'record';
            location.search = queryString.stringify(queryParams);
        }

        if (queryParams.bibliographicRecordId !== undefined) {
            this.findAgenciesForBibliographicRecordId(queryParams.bibliographicRecordId);
        }

        if (queryParams.bibliographicRecordId !== undefined && queryParams.agencyId !== undefined) {
            this.getRecord(queryParams.bibliographicRecordId, queryParams.agencyId);
        }
    }

    handleSelect(view) {
        this.setState({view: view});
    }

    onChangeBibliographicRecordId(event) {
        const bibliographicRecordId = event.target.value;

        this.findAgenciesForBibliographicRecordId(bibliographicRecordId);
    }

    findAgenciesForBibliographicRecordId(bibliographicRecordId) {
        if (8 < bibliographicRecordId.length < 9) {
            this.setState({bibliographicRecordId: bibliographicRecordId});
            request
                .get('/api/v1/agencies-for/' + bibliographicRecordId)
                .then(res => {
                    console.log(res.body);
                    this.setState({agencyIdList: res.body});
                })
                .catch(err => {
                    alert(err.message);
                });
        } else {
            this.state.set({
                agencyIdList: []
            })
        }
    }

    getRecord(bibliographicRecordId, agencyId) {
        const queryParams = queryString.parse(location.search);
        const requestParams = {};

        if (queryParams.mode !== undefined) {
            requestParams.mode = queryParams.mode;
        }

        if (queryParams.format !== undefined) {
            requestParams.format = queryParams.format;
        }

        console.log('/api/v1/record/' + bibliographicRecordId + '/' + agencyId);
        request
            .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId)
            .set('Content-Type', 'text/plain')
            .query(requestParams)
            .then(res => {
                this.setState({record: res.text});
            })
            .catch(err => {
                alert(err.message);
            });
    }

    onChangeAgencyId(event) {
        const agencyId = event.target.value;
        const queryParams = queryString.parse(location.search);

        queryParams.bibliographicRecordId = this.state.bibliographicRecordId;
        queryParams.agencyId = agencyId;
        location.search = queryString.stringify(queryParams);
    }

    render() {
        const queryParams = queryString.parse(location.search);

        return (
            <div className="container-fluid">
                <h2>Rawrepo Introspect</h2>
                <hr/>
                <div><RawrepoIntrospectRecordSelector
                    onChangeBibliographicRecordId={this.onChangeBibliographicRecordId}
                    onChangeAgencyId={this.onChangeAgencyId}
                    bibliographicRecordId={queryParams.bibliographicRecordId}
                    agencyIdList={this.state.agencyIdList}
                    agencyId={queryParams.agencyId}/></div>
                <div>
                    <Tabs activeKey={this.state.view}
                          onSelect={this.handleSelect}
                          animation={false}
                          id="tabs">
                        <Tab eventKey={'record'} title="Post">
                            <div><p/><RawrepoIntrospectRecordView record={this.state.record}/></div>
                        </Tab>
                        <Tab eventKey={'relations'} title="Relationer">
                            <div><p/><RawrepoIntrospectRelationsView/></div>
                        </Tab>
                    </Tabs>
                </div>
            </div>
        )
    }

}

export default RawrepoIntrospectGUI;
