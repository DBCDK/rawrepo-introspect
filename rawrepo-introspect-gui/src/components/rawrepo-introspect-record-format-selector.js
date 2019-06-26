/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import {Button, ButtonGroup} from "react-bootstrap";

class RawrepoIntrospectRecordFormatSelector extends React.Component {

    constructor(props) {
        super(props);
    }


    render() {
        const format = this.props.format;

        return (
            <div>
                <div id='format-div'>
                    <ButtonGroup id='button-tool-bar-format'>
                        <Button onClick={this.props.onChangeFormat}
                                bsStyle={format === 'line' ? 'primary' : 'default'}
                                id='button-format-line'
                                value='line'
                                disabled={!this.props.recordLoaded}>Line</Button>
                        <Button onClick={this.props.onChangeFormat}
                                bsStyle={format === 'xml' ? 'primary' : 'default'}
                                id='button-format-xml'
                                value='xml'
                                disabled={!this.props.recordLoaded}>MarcXchange</Button>
                    </ButtonGroup>
                </div>
            </div>
        )
    }


}

export default RawrepoIntrospectRecordFormatSelector;