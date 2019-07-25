/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonGroup} from "react-bootstrap";

class RawrepoIntrospectRecordModeSelector extends React.Component {

    constructor(props) {
        super(props);
    }

    render() {
        const mode = this.props.mode;

        return (
            <div>
                <ButtonGroup>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'raw' ? 'primary' : 'default'}
                            value='raw'
                            disabled={!this.props.recordLoaded || this.props.version !== 'current'}>Raw</Button>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'merged' ? 'primary' : 'default'}
                            value='merged'
                            disabled={!this.props.recordLoaded || this.props.version !== 'current'}>Merged</Button>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'expanded' ? 'primary' : 'default'}
                            value='expanded'
                            disabled={!this.props.recordLoaded || this.props.version !== 'current'}>Expanded</Button>
                </ButtonGroup>
            </div>
        )
    }


}

export default RawrepoIntrospectRecordModeSelector;