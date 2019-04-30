/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

const React = require("react");
const ReactDOM = require("react-dom");
import RawrepoIntrospectGUI from './components/rawrepo-introspect-gui';

ReactDOM.render(
    <RawrepoIntrospectGUI/>,
    document.getElementById('rawrepo-introspect-root')
);