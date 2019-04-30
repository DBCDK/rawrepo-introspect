/*
 * Copyright Dansk Bibliotekscenter a/s. Licensed under GNU GPL v3
 * See license text at https://opensource.dbc.dk/licenses/gpl-3.0
 */

const packageJSON = require('./package.json');
const path = require('path');
const webpack = require('webpack');

const PATHS = {
    build: path.join(__dirname, 'target', 'classes', 'META-INF', 'resources', 'webjars', packageJSON.name, packageJSON.version)
};

const plugins = [
    new webpack.NamedModulesPlugin(),
    new webpack.HotModuleReplacementPlugin()
];

if (process.env.NODE_ENV === "production") {
    plugins.push(
        new webpack.optimize.UglifyJsPlugin({
            beautify: false,
            minimize: true,
            mangle: {
                screw_ie8: true,
                keep_fnames: false
            },
            compress: {
                screw_ie8: true,
                //drop_console: true, // strips console statements
                unused: true,
                dead_code: true // big one--strip code that will never execute
            },
            comments: false
        })
    );
}

module.exports = {
    entry: './app/index.js',

    output: {
        path: PATHS.build,
        publicPath: "/",
        filename: 'app-bundle.js'
    },
    devtool: "inline-source-map",
    plugins: plugins,
    module: {
        rules: [
            {
                test: /\.js$/,
                use: ['babel-loader'],
                exclude: /node_modules/
            }
        ]
    },
    devServer: {
        hot: true,
        port: 8090,
        // Send API requests on localhost to API server get around CORS.
        proxy: {
            "/api": {
                target: {
                    host: "localhost",
                    protocol: "http:",
                    port: 28088
                }
            }
        }
    }
};