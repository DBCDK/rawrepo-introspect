/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Tab, Tabs} from "react-bootstrap";
import RawrepoIntrospectRecordView from './rawrepo-introspect-record-view';
import RawrepoIntrospectRelationsView from './rawrepo-introspect-relations-view';

class RawrepoIntrospectGUI extends React.Component {

    constructor(props) {
        super(props);

        this.state = {
            key: 1
        };

        this.handleSelect = this.handleSelect.bind(this);
    }

    handleSelect(key) {
        this.setState({key});
    }

    static renderRecord() {
        return (<div><p/><RawrepoIntrospectRecordView/></div>);
    }

    static renderRelations() {
        return (<div><p/><RawrepoIntrospectRelationsView/></div>);
    }

    render() {
        return (
            <div className="container-fluid">
                <h2>Rawrepo Introspect</h2>
                <hr/>
                <div>a</div>
                <div>b</div>
                <div>c</div>
                <Tabs activeKey={this.state.key}
                      onSelect={this.handleSelect}
                      animation={false}
                      id="tabs">
                    <Tab eventKey={1} title="Post">
                        {RawrepoIntrospectGUI.renderRecord()}
                    </Tab>
                    <Tab eventKey={2} title="Relationer">
                        {RawrepoIntrospectGUI.renderRelations()}
                    </Tab>
                </Tabs>
            </div>
        )
    }

}

export default RawrepoIntrospectGUI;
