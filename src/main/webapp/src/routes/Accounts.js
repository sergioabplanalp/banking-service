import React, {useEffect, useState} from 'react';
import {useParams} from "react-router-dom";
import Layout from "../components/Layout";
import {Button} from "react-bootstrap";

const Views = {
    ACCOUNT_DETAILS: 'ACCOUNT_DETAILS',
    DEPOSIT: 'DEPOSIT',
    WITHDRAW: 'WITHDRAW',
    TRANSACTION_HISTORY: 'TRANSACTION_HISTORY',
};

function Accounts() {
    let params = useParams();

    const [account, setAccount] = useState({});
    const [selectedView, setSelectedView] = useState(Views.ACCOUNT_DETAILS);

    useEffect(() => fetchData(), []);

    const fetchData = () => {
        const options = {
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
        };

        fetch(`/api/accounts/${params.id}`, options)
            .then(response => response.json())
            .then((accountResponse) => {
                const acc = {
                    accountId: accountResponse.accountId,
                    owner: accountResponse.owner,
                    balance: accountResponse.balance.currency + ' ' + accountResponse.balance.amount,
                };
                setAccount(acc);
            });
    };

    const showAccountDetails = () => {
        fetchData();
        setSelectedView(Views.ACCOUNT_DETAILS);
    };

    return (
        <Layout>
            <div className="container">
                <div className="row">
                    <div className="col-2" style={{borderRight: "1px solid #ddd"}}>
                        <button type="button" className="btn btn-link" onClick={() => setSelectedView(Views.ACCOUNT_DETAILS)}>Account Details</button><br/>
                        <button type="button" className="btn btn-link" onClick={() => setSelectedView(Views.DEPOSIT)}>Make a deposit</button><br/>
                        <button type="button" className="btn btn-link" onClick={() => setSelectedView(Views.WITHDRAW)}>Make a withdrawal</button><br/>
                        <button type="button" className="btn btn-link" onClick={() => setSelectedView(Views.TRANSACTION_HISTORY)}>View transactions</button><br/>
                    </div>
                    <div className="offset-1 col-9">
                        <AccountDetails account={account} view={selectedView}/>
                        <Deposit account={account} changeView={showAccountDetails} view={selectedView}/>
                        <Withdraw account={account} changeView={showAccountDetails} view={selectedView}/>
                        <TransactionHistory account={account} view={selectedView}/>
                    </div>
                </div>
            </div>
        </Layout>
    );
}

function AccountDetails(props) {
    return (
        <div hidden={props.view !== Views.ACCOUNT_DETAILS}>
            <strong>Account ID:</strong>&nbsp;{props.account.accountId}<br/>
            <strong>Owner:</strong>&nbsp;{props.account.owner}<br/>
            <strong>Balance:</strong>&nbsp;{props.account.balance}<br/>
        </div>
    );
}

function Deposit(props) {
    const [amount, setAmount] = useState('');

    useEffect(() => setAmount(''), [props.view]);

    const deposit = () => {
        const options = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({amount}),
        };

        fetch(`/api/accounts/${props.account.accountId}/deposits`, options)
            .then(async () => {
                await timeout(100);
                props.changeView();
            });
    };

    return (
        <div hidden={props.view !== Views.DEPOSIT}>
            Amount to deposit:&nbsp;
            <input value={amount} onChange={e => setAmount(e.target.value)}/><br/>
            <Button onClick={deposit}>Submit</Button>
        </div>
    );
}

function Withdraw(props) {
    const [amount, setAmount] = useState('');
    const [errorMessage, setErrorMessage] = useState('');
    const [showErrorMessage, setShowErrorMessage] = useState(false);

    useEffect(() => {
        setAmount('');
        setErrorMessage('');
        setShowErrorMessage(false);
    }, [props.view]);

    const withdraw = () => {
        const options = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({amount}),
        };

        fetch(`/api/accounts/${props.account.accountId}/withdrawals`, options)
            .then(async response => {
                if (!response.ok) {
                    throw await response.json();
                }

                await timeout(100);
                props.changeView();
            })
            .catch(error => {
                setErrorMessage(error.message);
                setShowErrorMessage(true);
            });
    };

    return (
        <div hidden={props.view !== Views.WITHDRAW}>
            Amount to withdraw:&nbsp;
            <input value={amount} onChange={e => setAmount(e.target.value)}/><br/>
            <Button onClick={withdraw}>Submit</Button><br/><br/>
            <div className="alert alert-danger" role="alert" hidden={!showErrorMessage}>
                {errorMessage}
            </div>
        </div>
    );
}

function TransactionHistory(props) {
    const [date, setDate] = useState('');
    const [transactions, setTransactions] = useState([]);

    useEffect(() => {
        setDate('');
        setTransactions([]);
    }, [props.view]);

    const retrieveTransactions = () => {
        const options = {
            method: 'GET',
            headers: {'Content-Type': 'application/json'},
        };

        fetch(`/api/accounts/${props.account.accountId}/transactions?date=${date}`, options)
            .then(response => response.json())
            .then((response) => {
                let t = response.map(r => {
                    return {
                        id: r.transactionId,
                        date: r.date,
                        amount: r.amount.currency + ' ' + r.amount.amount,
                    }
                });

                setTransactions(t);
            });
    };

    return (
        <div hidden={props.view !== Views.TRANSACTION_HISTORY}>
            Select date:&nbsp;
            <input value={date} onChange={e => setDate(e.target.value)} placeholder="YYYY-MM-DD"/><br/>
            <Button onClick={retrieveTransactions}>Submit</Button><br/><br/>
            <div hidden={transactions.length === 0}>
                Transaction history:<br/>
                <table className="table">
                    <thead>
                    <tr>
                        <th className="w-50">ID</th>
                        <th className="w-25">Date</th>
                        <th className="w-25">Amount</th>
                    </tr>
                    </thead>
                    <tbody>
                    {transactions.map(transaction => {
                        return (
                            <tr key={transaction.id}>
                                <td>{transaction.id}</td>
                                <td>{transaction.date}</td>
                                <td>{transaction.amount}</td>
                            </tr>
                        );
                    })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

function timeout(delay) {
    return new Promise(res => setTimeout(res, delay));
}

export default Accounts