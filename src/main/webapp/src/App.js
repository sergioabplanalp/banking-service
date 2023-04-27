import './App.css';
import {Route, Routes} from "react-router-dom";
import Home from "./routes/Home";
import OpenAccount from "./routes/OpenAccount";
import Accounts from "./routes/Accounts";

function App() {
  return (
    <div className="App">
      <Routes>
        <Route path="/" element={<Home/>}/>
        <Route path="/accounts/:id" element={<Accounts/>}/>
        <Route path="/open-account" element={<OpenAccount/>}/>
      </Routes>
    </div>
  );
}

export default App;
