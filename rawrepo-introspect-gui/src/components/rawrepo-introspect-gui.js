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
const queryString = require('querystring');

const COOKIE_PREFIX = 'recId=';

class RawrepoIntrospectGUI extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            view: 'record',
            bibliographicRecordId: '',
            bibliographicRecordIdCache: [],
            agencyId: '',
            agencyIdList: [],
            record: '',
            format: 'line',
            mode: 'raw',
            recordLoaded: false
        };

        this.handleSelect = this.handleSelect.bind(this);

        this.onChangeBibliographicRecordId = this.onChangeBibliographicRecordId.bind(this);
        this.onChangeAgencyId = this.onChangeAgencyId.bind(this);
        this.onChangeMode = this.onChangeMode.bind(this);
        this.onChangeFormat = this.onChangeFormat.bind(this);

        this.findAgenciesForBibliographicRecordId = this.findAgenciesForBibliographicRecordId.bind(this);
        this.getRecord = this.getRecord.bind(this);
        this.getRecordById = this.getRecordById.bind(this);
        this.getRecordByMode = this.getRecordByMode.bind(this);
        this.getRecordByFormat = this.getRecordByFormat.bind(this);

        this.clearRecord = this.clearRecord.bind(this);
        RawrepoIntrospectGUI.getExpirationDate = RawrepoIntrospectGUI.getExpirationDate.bind(this);
        this.addToCookie = this.addToCookie.bind(this);
        this.readCookie = this.readCookie.bind(this);
    }

    componentDidMount() {
        const queryParams = queryString.parse(location.search);

        this.readCookie();

        // Sanitize the raw values in queryParams as the first value will be prefixed with '?'
        for (var key in queryParams) {
            if (key.startsWith('?')) {
                queryParams[key.substring(1, key.length)] = queryParams[key];
            }
        }

        if (queryParams.view === undefined || queryParams.view === 'record') { // TODO implement other views
            if (queryParams.bibliographicRecordId !== undefined) {
                this.setState({bibliographicRecordId: queryParams.bibliographicRecordId})
            }

            if (queryParams.agencyId !== undefined) {
                this.setState({agencyId: queryParams.agencyId})
            }

            if (queryParams.mode !== undefined) {
                this.setState({mode: queryParams.mode})
            }

            if (queryParams.format !== undefined) {
                this.setState({format: queryParams.format})
            }

            if (queryParams.bibliographicRecordId !== undefined && queryParams.agencyId !== undefined) {
                this.findAgenciesForBibliographicRecordId(queryParams.bibliographicRecordId);
                this.getRecordById(queryParams.bibliographicRecordId, queryParams.agencyId);
            }
        }
    }

    handleSelect(view) {
        this.setState({view: view});
    }

    onChangeBibliographicRecordId(event) {
        const bibliographicRecordId = event.target.value;

        this.setState({bibliographicRecordId: bibliographicRecordId});

        if (8 <= bibliographicRecordId.length && 9 >= bibliographicRecordId.length) {
            this.findAgenciesForBibliographicRecordId(bibliographicRecordId);
        } else {
            this.clearRecord();
        }
    }

    clearRecord() {
        this.setState({
            agencyIdList: [],
            record: '',
            recordLoaded: false
        })
    }

    onChangeAgencyId(event) {
        const agencyId = event.target.value;

        this.setState({agencyId: agencyId});

        this.getRecordById(this.state.bibliographicRecordId, agencyId);
    }

    onChangeMode(event) {
        const mode = event.target.value;

        this.setState({mode: mode});

        this.getRecordByMode(mode);
    }

    onChangeFormat(event) {
        const format = event.target.value;

        this.setState({format: format});

        this.getRecordByFormat(format);
    }

    findAgenciesForBibliographicRecordId(bibliographicRecordId) {
        request
            .get('/api/v1/agencies-for/' + bibliographicRecordId)
            .then(res => {
                const agencyIdList = res.body;

                if (agencyIdList.length > 0) {
                    const agencyId = agencyIdList[0];
                    this.setState({agencyIdList: agencyIdList, agencyId: agencyId});
                    this.getRecordById(bibliographicRecordId, agencyId)
                } else {
                    this.clearRecord();
                }
            })
            .catch(err => {
                alert(err.message);
            });
    }

    getRecordByMode(mode) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const format = this.state.format;

        this.getRecord(bibliographicRecordId, agencyId, mode, format);
    }

    getRecordByFormat(format) {
        const bibliographicRecordId = this.state.bibliographicRecordId;
        const agencyId = this.state.agencyId;
        const mode = this.state.mode;

        this.getRecord(bibliographicRecordId, agencyId, mode, format);
    }

    getRecordById(bibliographicRecordId, agencyId) {
        const mode = this.state.mode;
        const format = this.state.format;

        this.getRecord(bibliographicRecordId, agencyId, mode, format);
    }

    getRecord(bibliographicRecordId, agencyId, mode, format) {
        const params = {mode: mode, format: format};

        request
            .get('/api/v1/record/' + bibliographicRecordId + '/' + agencyId)
            .set('Content-Type', 'text/plain')
            .query(params)
            .then(res => {
                this.setState({
                    record: res.text,
                    recordLoaded: true
                });

                const urlParams = {
                    bibliographicRecordId: bibliographicRecordId,
                    agencyId: agencyId,
                    mode: mode,
                    format: format,
                    view: 'record'
                };

                // This seems to be the only way to get the full URL without URL params
                // Alternatively location.href could be used by that includes previous URL params
                const URL = location.protocol + '//' + location.host + location.pathname;
                window.history.replaceState(null, null, URL + '?' + queryString.stringify(urlParams));
                this.addToCookie(bibliographicRecordId);
            })
            .catch(err => {
                alert(err.message);
            });
    }

    // Constructs 'expires' message for cookies
    static getExpirationDate() {
        const date = new Date();
        date.setTime(date.getTime() + (7 * 24 * 60 * 60 * 1000));
        return "; expires=" + date.toGMTString();
    }

    addToCookie(recId) {
        const expires = RawrepoIntrospectGUI.getExpirationDate();
        const bibliographicRecordIdCache = this.state.bibliographicRecordIdCache;

        // Only add the value if it isn't already present
        if (bibliographicRecordIdCache.indexOf(recId) === -1) {
            bibliographicRecordIdCache.push(recId);
            this.setState({bibliographicRecordIdCache: bibliographicRecordIdCache});
            document.cookie = COOKIE_PREFIX + bibliographicRecordIdCache.join(',') + expires;
        }
    }

    readCookie() {
        let bibliographicRecordIdCache = [];
        const documentCookies = document.cookie.split(';'); // Split cookie into separate elements

        for (let i = 0; i < documentCookies.length; i++) {
            if (documentCookies[i].indexOf(COOKIE_PREFIX) > -1) {
                const bibliographicRecordIdCookie = documentCookies[i];

                // Remove 'recId='
                const bibliographicRecordIdCookieValues = bibliographicRecordIdCookie.split('=').pop();

                bibliographicRecordIdCache = bibliographicRecordIdCookieValues.split(',');
            }
        }

        this.setState({bibliographicRecordIdCache: bibliographicRecordIdCache});
    }

    render() {
        return (
            <div className="container-fluid">
                <div style={{height: '115px'}}>
                    <RawrepoIntrospectRecordSelector
                        onChangeBibliographicRecordId={this.onChangeBibliographicRecordId}
                        onChangeAgencyId={this.onChangeAgencyId}
                        bibliographicRecordId={this.state.bibliographicRecordId}
                        agencyIdList={this.state.agencyIdList}
                        agencyId={this.state.agencyId}
                        bibliographicRecordIdCache={this.state.bibliographicRecordIdCache}/>
                </div>
                <div>
                    <Tabs activeKey={this.state.view}
                          onSelect={this.handleSelect}
                          animation={false}
                          id="tabs">
                        <Tab eventKey={'record'} title="Post">
                            <div>
                                <p/>
                                <RawrepoIntrospectRecordView
                                    record={this.state.record}
                                    format={this.state.format}
                                    mode={this.state.mode}
                                    onChangeFormat={this.onChangeFormat}
                                    onChangeMode={this.onChangeMode}
                                    recordLoaded={this.state.recordLoaded}/>
                            </div>
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
