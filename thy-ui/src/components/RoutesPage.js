import React, { useState, useEffect } from 'react';
import { Button, Autocomplete, TextField, CircularProgress } from '@mui/material'; // Import CircularProgress for loading indicator
import './Locations.css'; // Import the CSS file
import SidePanel from './SidePanel'; // Import the SidePanel component
import { fetchWithAuth } from '../api'; // Import the fetchWithAuth function

const RoutesPage = () => {
    const [originLocationCode, setOriginLocationCode] = useState('');
    const [destinationLocationCode, setDestinationLocationCode] = useState('');
    const [travelDate, setTravelDate] = useState('');
    const [availableRoutes, setAvailableRoutes] = useState([]);
    const [locations, setLocations] = useState([]); // State to hold locations
    const [selectedRoute, setSelectedRoute] = useState(null); // State to hold the selected route
    const [isPanelOpen, setIsPanelOpen] = useState(false); // State to control the side panel visibility
    const [loading, setLoading] = useState(false); // State to manage loading status

    // Function to fetch locations based on search input
    const fetchLocations = async (name) => {
        if (!name) return; // Prevent fetching if input is empty
        console.log(`Fetching locations for: ${name}`); // Log the input
        try {
            const response = await fetchWithAuth(`/locations/search?name=${name}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (response.ok) {
                const data = await response.json();
                setLocations(data); // Set locations data
            } else {
                console.error('Failed to fetch locations');
            }
        } catch (error) {
            console.error('Failed to fetch locations', error);
        }
    };

    const handleSearch = async () => {
        setLoading(true); // Set loading to true before starting the search
        try {
            const response = await fetchWithAuth('/routes/search', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    originLocationCode,
                    destinationLocationCode,
                    travelDate,
                }),
            });
            if (response) {
                const data = await response.json();
                setAvailableRoutes(data); // Set available routes
            }
        } catch (error) {
            console.error('Failed to fetch routes');
        } finally {
            setLoading(false); // Set loading to false after the search is complete
        }
    };

    const openPanel = (route) => {
        setSelectedRoute(route);
        setIsPanelOpen(true);
    };

    const closePanel = () => {
        setIsPanelOpen(false);
        setSelectedRoute(null);
    };

    return (
        <div className="locations-container">
            <h2 className="locations-header">Find Routes</h2>
            <div className="input-container">
                <Autocomplete
                    options={locations}
                    getOptionLabel={(option) => option.name} // Display name in the dropdown
                    onInputChange={(event, newInputValue) => {
                        console.log(`Input changed: ${newInputValue}`); // Log input change
                        fetchLocations(newInputValue); // Fetch locations based on input
                    }}
                    onChange={(event, newValue) => {
                        setOriginLocationCode(newValue ? newValue.locationCode : '');
                    }}
                    renderInput={(params) => (
                        <TextField {...params} label="Select Origin" variant="outlined" />
                    )}
                />
                <Autocomplete
                    options={locations}
                    getOptionLabel={(option) => option.name} // Display name in the dropdown
                    onInputChange={(event, newInputValue) => {
                        console.log(`Input changed: ${newInputValue}`); // Log input change
                        fetchLocations(newInputValue); // Fetch locations based on input
                    }}
                    onChange={(event, newValue) => {
                        setDestinationLocationCode(newValue ? newValue.locationCode : '');
                    }}
                    renderInput={(params) => (
                        <TextField {...params} label="Select Destination" variant="outlined" />
                    )}
                />
                <TextField
                    type="date"
                    value={travelDate}
                    onChange={(e) => setTravelDate(e.target.value)}
                    className="input-field"
                />
                <Button variant="contained" color="error" onClick={handleSearch}>Search</Button>
            </div>
            <h3>Available Routes:</h3>
            <ul className="available-routes-list">
                {loading ? ( // Show loading indicator while loading
                    <li style={{ textAlign: 'center' }}>
                        <CircularProgress />
                    </li>
                ) : (
                    availableRoutes.map((route, index) => (
                        <li key={index} onClick={() => openPanel(route)} className="route-item">
                            {route.segments
                                .filter(segment => segment.transportationType === "FLIGHT") // Filter for flights
                                .map((segment, segIndex) => (
                                    <div key={segIndex}>
                                        <p>
                                            Via {segment.originLocation.name} ({segment.originLocation.locationCode}) 
                                        </p>
                                    </div>
                                ))}
                        </li>
                    ))
                )}
            </ul>
            {isPanelOpen && selectedRoute && (
                <SidePanel route={selectedRoute} onClose={closePanel} />
            )}
        </div>
    );
};

export default RoutesPage; 