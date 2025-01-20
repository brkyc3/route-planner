import React, { useState } from 'react';
import { BrowserRouter as Router, Route, Routes, Navigate } from 'react-router-dom';
import Header from './components/Header';
import Sidebar from './components/Sidebar';
import Locations from './components/Locations';
import EditLocation from './components/EditLocation';
import Transportations from './components/Transportations';
import EditTransportation from './components/EditTransportation';
import RoutesPage from './components/RoutesPage';
import Login from './components/Login';

const App = () => {
    const [isLoggedIn, setIsLoggedIn] = useState(!!localStorage.getItem('token'));

    const handleLogin = () => {
        setIsLoggedIn(true);
    };

    return (
        <Router>
            {isLoggedIn && <Header />}
            {isLoggedIn && <Sidebar />}
            <div style={{ marginLeft: isLoggedIn ? '220px' : '0', padding: '20px' }}>
                <Routes>
                    <Route path="/" element={<Navigate to="/routes" />} />
                    <Route path="/login" element={<Login onLogin={handleLogin} />} />
                    <Route path="/locations" element={isLoggedIn ? <Locations /> : <Navigate to="/login" />} />
                    <Route path="/edit-location/:id" element={isLoggedIn ? <EditLocation /> : <Navigate to="/login" />} />
                    <Route path="/transportations" element={isLoggedIn ? <Transportations /> : <Navigate to="/login" />} />
                    <Route path="/edit-transportation/:id" element={isLoggedIn ? <EditTransportation /> : <Navigate to="/login" />} />
                    <Route path="/routes" element={isLoggedIn ? <RoutesPage /> : <Navigate to="/login" />} />
                </Routes>
            </div>
        </Router>
    );
};

export default App;
