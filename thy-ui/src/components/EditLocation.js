import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import './Locations.css'; // Import the CSS file
import { fetchWithAuth } from '../api'; // Import the fetchWithAuth function
import Button from '@mui/material/Button'; // Import Material-UI Button

const EditLocation = () => {
    const { id } = useParams(); // Get the location ID from the URL
    const [location, setLocation] = useState({
        name: '',
        country: '',
        city: '',
        locationCode: ''
    });
    const navigate = useNavigate();

    useEffect(() => {
        const fetchLocation = async () => {
            try {
                const response = await fetchWithAuth(`/locations/${id}`, {
                    method: 'GET',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                });
                if (response) {
                    const data = await response.json();
                    setLocation(data);
                }
            } catch (error) {
                console.error('Failed to fetch location details');
            }
        };

        fetchLocation(); // Fetch location details when the component mounts
    }, [id]);

    const handleUpdateLocation = async () => {
        try {
            const response = await fetchWithAuth(`/locations/${id}`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(location),
            });

            if (response) {
                navigate('/locations'); // Redirect to the locations list after updating
            }
        } catch (error) {
            console.error('Failed to update location');
        }
    };

    return (
        <div className="locations-container" style={{ marginTop: '80px' }}>
            <h2 className="locations-header">Edit Location</h2>
            <div className="input-container">
                <input
                    className="input-field"
                    type="text"
                    value={location.name}
                    onChange={(e) => setLocation({ ...location, name: e.target.value })}
                    placeholder="Enter location name"
                />
                <input
                    className="input-field"
                    type="text"
                    value={location.country}
                    onChange={(e) => setLocation({ ...location, country: e.target.value })}
                    placeholder="Enter country"
                />
                <input
                    className="input-field"
                    type="text"
                    value={location.city}
                    onChange={(e) => setLocation({ ...location, city: e.target.value })}
                    placeholder="Enter city"
                />
                <input
                    className="input-field"
                    type="text"
                    value={location.locationCode}
                    onChange={(e) => setLocation({ ...location, locationCode: e.target.value })}
                    placeholder="Enter location code"
                />
                <Button variant="contained" color="error" onClick={handleUpdateLocation}>Update Location</Button>
            </div>
        </div>
    );
};

export default EditLocation; 