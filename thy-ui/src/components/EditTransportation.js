import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import './Locations.css'; // Import the CSS file
import { fetchWithAuth } from '../api'; // Import the fetchWithAuth function
import Button from '@mui/material/Button'; // Import Material-UI Button

const EditTransportation = () => {
    const { id } = useParams(); // Get the transportation ID from the URL
    const [transportation, setTransportation] = useState({
        originLocationCode: '',
        destinationLocationCode: '',
        transportationType: '',
        operatingDays: []
    });
    const navigate = useNavigate();

    useEffect(() => {
        const fetchTransportation = async () => {
            try {
                const response = await fetchWithAuth(`/transportations/${id}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });
                if (response) {
                    const data = await response.json();
                    setTransportation(data);
                }
            } catch (error) {
                console.error('Failed to fetch transportation details');
            }
        };

        fetchTransportation(); // Fetch transportation details when the component mounts
    }, [id]);

    const handleUpdateTransportation = async () => {
        try {
            const response = await fetchWithAuth(`/transportations/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(transportation),
            });

            if (response) {
                navigate('/transportations'); // Redirect to the transportations list after updating
            }
        } catch (error) {
            console.error('Failed to update transportation');
        }
    };

    return (
        <div className="locations-container" style={{ marginTop: '80px' }}>
            <h2 className="locations-header">Edit Transportation</h2>
            <div className="input-container">
                <input
                    className="input-field"
                    type="text"
                    value={transportation.originLocationCode}
                    onChange={(e) => setTransportation({ ...transportation, originLocationCode: e.target.value })}
                    placeholder="Enter origin location code"
                />
                <input
                    className="input-field"
                    type="text"
                    value={transportation.destinationLocationCode}
                    onChange={(e) => setTransportation({ ...transportation, destinationLocationCode: e.target.value })}
                    placeholder="Enter destination location code"
                />
                <input
                    className="input-field"
                    type="text"
                    value={transportation.transportationType}
                    onChange={(e) => setTransportation({ ...transportation, transportationType: e.target.value })}
                    placeholder="Enter transportation type (FLIGHT, BUS, SUBWAY, UBER)"
                />
                <div className="operating-days-container">
                    <p>Operating Days:</p>
                    <div>
                        {Array.from({ length: 7 }, (_, i) => (
                            <div key={i}>
                                <input
                                    type="checkbox"
                                    checked={transportation.operatingDays.includes(i + 1)}
                                    onChange={() => {
                                        const newDays = transportation.operatingDays.includes(i + 1)
                                            ? transportation.operatingDays.filter(d => d !== i + 1)
                                            : [...transportation.operatingDays, i + 1];
                                        setTransportation({ ...transportation, operatingDays: newDays });
                                    }}
                                />
                                <label>{['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday','Sunday'][i]}</label>
                            </div>
                        ))}
                    </div>
                </div>
                <Button variant="contained" color="error" onClick={handleUpdateTransportation}>Update Transportation</Button>
            </div>
        </div>
    );
};

export default EditTransportation; 