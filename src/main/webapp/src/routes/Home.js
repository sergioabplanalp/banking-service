import React, {useEffect, useState} from 'react';
import Layout from "../components/Layout";
import {Link} from "react-router-dom";

function Home() {
    const [accounts, setAccounts] = useState([]);

    useEffect(() => {
        const options = {
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
        };

        fetch(`/api/accounts`, options)
            .then(response => response.json())
            .then((accountsResponse) => {
                setAccounts(accountsResponse);
            });
    }, []);

    const retrieveAccounts = (indebted) => {
        const options = {
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
        };

        fetch(`/api/accounts?indebted=${indebted}`, options)
            .then(response => response.json())
            .then((accountsResponse) => {
                setAccounts(accountsResponse);
            });
    };

    return (
        <Layout>
            <div className="container">
                <div className="row">
                    <div className="col-12">
                        List of accounts:&nbsp;
                        <button type="button" className="btn btn-link" onClick={() => retrieveAccounts(false)}>All</button>
                        <button type="button" className="btn btn-link" onClick={() => retrieveAccounts(true)}>Indebted</button>
                    </div>
                    <div hidden={accounts.length !== 0} className="col-12">
                        <h4 className="col-12 text-center" style={{marginTop: "50px"}}>
                            No accounts to display
                        </h4>
                    </div>
                    <div hidden={accounts.length === 0} className="col-12">
                        <table className="table">
                            <thead>
                            <tr>
                                <th className="w-25">ID</th>
                                <th className="w-50">Owner</th>
                                <th className="w-25">Balance</th>
                            </tr>
                            </thead>
                            <tbody>
                            {accounts.map(account => {
                                return (
                                    <tr key={account.accountId}>
                                        <td>{account.accountId}</td>
                                        <td><Link to={`/accounts/${account.accountId}`}>{account.owner}</Link></td>
                                        <td>{account.balance.currency}&nbsp;{account.balance.amount}</td>
                                    </tr>
                                );
                            })}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>
        </Layout>
    );
}

export default Home