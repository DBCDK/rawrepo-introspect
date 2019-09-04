/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonGroup} from "react-bootstrap";

class RawrepoIntrospectRecordModeSelector extends React.Component {

    constructor(props) {
        super(props);

        this.disableButton = this.disableButton.bind(this);
    }

    disableButton() {
        // If no record is loaded disable buttons
        if (!this.props.recordLoaded) {
            return true;
        } else {
            // If record is loaded and version contains exactly one value and that value is 'current and enable the buttons
            return !(this.props.version.length === 1 && this.props.version[0] === 'current');
        }
    }

    render() {
        const mode = this.props.mode;

        return (
            <div>
                <ButtonGroup>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'raw' ? 'primary' : 'default'}
                            value='raw'
                            disabled={this.disableButton()}>Raw</Button>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'merged' ? 'primary' : 'default'}
                            value='merged'
                            disabled={this.disableButton()}>Merged</Button>
                    <Button onClick={this.props.onChangeMode}
                            bsStyle={mode === 'expanded' ? 'primary' : 'default'}
                            value='expanded'
                            disabled={this.disableButton()}>Expanded</Button>
                </ButtonGroup>
            </div>
        )
    }


}

export default RawrepoIntrospectRecordModeSelector;