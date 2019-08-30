/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

import React from "react";
import ReactDOM from "react-dom";
import dagreD3 from "dagre-d3";
import * as d3 from 'd3'

const g = new dagreD3.graphlib.Graph().setGraph({});

const HEIGHT_OFFSET = 175;
const WIDTH_OFFSET = 20;

class RawrepoIntrospectRelationsView extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            nodes: [],
            edges: [],
            height: window.innerHeight - HEIGHT_OFFSET,
            width: window.innerWidth - WIDTH_OFFSET,
        };

        this.drawGraph = this.drawGraph.bind(this);
        this.updateDimensions = this.updateDimensions.bind(this);
    }

    componentWillMount() {
        this.updateDimensions();
    };

    componentDidMount() {
        window.addEventListener("resize", this.updateDimensions);
    };

    componentWillUnmount() {
        window.removeEventListener("resize", this.updateDimensions);
    }

    componentWillReceiveProps(nextProps) {
        let hasChanges = false;

        if (nextProps.relationNodes !== undefined && nextProps.relationNodes !== this.state.nodes) {
            this.setState({nodes: nextProps.relationNodes});
            hasChanges = true;
        }

        if (nextProps.relationEdges !== undefined && nextProps.relationEdges !== this.state.edges) {
            this.setState({edges: nextProps.relationEdges});
            hasChanges = true;
        }

        if (hasChanges) {
            this.drawGraph(nextProps.relationNodes, nextProps.relationEdges, nextProps.onLoadRelations);
        }
    }

    updateDimensions() {
        this.setState({
            height: window.innerHeight - HEIGHT_OFFSET,
            width: window.innerWidth - WIDTH_OFFSET,
        });
    };


    drawGraph(nodes, edges, callBack) {
        nodes.forEach(function (item) {
            const label = item.bibliographicRecordId + ':' + item.agencyId;
            const url = '?bibliographicRecordId=' + item.bibliographicRecordId + '&agencyId=' + item.agencyId;
            const value = {
                labelType: "html",
                // TODO Make the a href more react like
                //label: <a href={{url}} target='_blank'>{{label}}</a>,
                label: "<a href='?bibliographicRecordId=" + item.bibliographicRecordId + "&agencyId=" + item.agencyId + "' target='_blank'>" + label + "</a>",
                rx: 5, // Curved corners
                ry: 5,
                callBack: callBack,
                bibliographicRecordId: item.bibliographicRecordId,
                agencyId: item.agencyId
            };

            g.setNode(label, value);
        });

        edges.forEach(function (item) {
            const childLabel = item.child.bibliographicRecordId + ':' + item.child.agencyId;
            const parentLabel = item.parent.bibliographicRecordId + ':' + item.parent.agencyId;

            g.setEdge(childLabel, parentLabel, {})
        });

        // Create the renderer
        var render = new dagreD3.render();


        // Set up an SVG group so that we can translate the final graph.
        var svg = d3.select(ReactDOM.findDOMNode(this.refs.nodeTree));
        var svgGroup = d3.select(ReactDOM.findDOMNode(this.refs.nodeTreeGroup));

        var zoom = d3.zoom()
            .on("zoom", function () {
                svgGroup.attr("transform", d3.event.transform);
            });
        svg.call(zoom);

        // Run the renderer. This is what draws the final graph.
        render(svgGroup, g);

        // Add onClick event handler to each node
        svgGroup.selectAll("g.node")
            .on("click", function (v) {
                const node = g.node(v);
                node.callBack(node.bibliographicRecordId, node.agencyId);
            });

        // Center the graph
        var xCenterOffset = (svg.attr("width") - g.graph().width) / 2;
        svgGroup.attr("transform", "translate(" + xCenterOffset + ", 20)");

        var initialScale = 1.5;
        svg.call(zoom.transform, d3.zoomIdentity.translate((svg.attr("width") - g.graph().width) / 2, 30).scale(initialScale));
    }

    render() {
        return (
            <div>
                <svg id="nodeTree" ref="nodeTree" width={this.state.width} height={this.state.height}>
                    <g ref="nodeTreeGroup"/>
                </svg>
            </div>
        )
    }
}

export default RawrepoIntrospectRelationsView;
