/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonGroup} from "react-bootstrap";

class RawrepoIntrospectRecordModeSelector extends React.Component {

    constructor(props) {
        super(props);

        this.historyIsCurrent = this.historyIsCurrent.bind(this);
        this.recordIsLoaded = this.recordIsLoaded.bind(this);
    }

    historyIsCurrent() {
        return this.props.version.length === 1 && this.props.version[0] === 'current';
    }

    recordIsLoaded() {
        return this.props.recordLoaded;
    }

    render() {
        const mode = this.props.mode;

        return (
            <div>
                <ButtonGroup>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'raw' || !this.historyIsCurrent() ? 'primary' : 'default'}
                            value='raw'
                            disabled={!this.recordIsLoaded()}>Raw</Button>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'merged' && this.historyIsCurrent() ? 'primary' : 'default'}
                            value='merged'
                            disabled={!this.recordIsLoaded() || !this.historyIsCurrent()}>Merged</Button>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'expanded' && this.historyIsCurrent() ? 'primary' : 'default'}
                            value='expanded'
                            disabled={!this.recordIsLoaded() || !this.historyIsCurrent()}>Expanded</Button>
                </ButtonGroup>
            </div>
        )
    }


}

export default RawrepoIntrospectRecordModeSelector;