import React, {useState} from 'react';
import Layout from "../components/Layout";
import {Button} from "react-bootstrap";
import {useNavigate} from "react-router-dom";

function OpenAccount() {
    const navigate = useNavigate();
    const [owner, setOwner] = useState('');
    const [depositAmount, setDepositAmount] = useState(0.0);

    const openAccount = () => {
        const options = {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({owner, depositAmount}),
        };

        fetch(`/api/accounts`, options)
            .then(response => response.json())
            .then(async () => {
                await timeout(100);
                navigate('/');
            });
    };

    return (
        <Layout>
            <div className="container">
                <div className="row">
                    <div className="col-12">
                        <h4 className="col-12 text-center" style={{marginTop: "50px", marginBottom: "30px"}}>
                            Open new account
                        </h4>
                    </div>
                </div>
                <div className="row">
                    <div className="offset-3 col-3" style={{textAlign: "right"}}>
                        Account owner:&nbsp;
                    </div>
                    <div className="col-2">
                        <input onChange={e => setOwner(e.target.value)} />
                    </div>
                </div>
                <div className="row">
                    <div className="offset-3 col-3" style={{textAlign: "right"}}>
                        Deposit amount:&nbsp;
                    </div>
                    <div className="col-2">
                        <input onChange={e => setDepositAmount(e.target.value)}/>
                    </div>
                </div>
                <div className="row">
                    <div className="offset-6 col-1">
                        <Button onClick={openAccount}>Submit</Button>
                    </div>
                </div>
            </div>
        </Layout>
    );
}

function timeout(delay) {
    return new Promise(res => setTimeout(res, delay));
}

export default OpenAccount