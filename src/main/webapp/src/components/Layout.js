import React from 'react';
import {Link} from "react-router-dom";

function Layout(props) {
    return (
        <div>
            <nav className="container">
                <Link to="/">Home</Link>&nbsp;|&nbsp;
                <Link to="/open-account">Open account</Link>
            </nav>
            <h1 style={{textAlign: "center"}}>ABC Bank</h1>
            {props.children}
        </div>
    );
}

export default Layout