import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { fetchWithAuth } from '../api'; // Import the fetchWithAuth function
import './Locations.css'; // Import the CSS file
import Button from '@mui/material/Button'; // Import Material-UI Button

const Locations = () => {
    const [locations, setLocations] = useState([]);
    const [currentPage, setCurrentPage] = useState(0); // Current page state
    const [totalPages, setTotalPages] = useState(0); // Total pages state
    const [newLocation, setNewLocation] = useState({
        name: '',
        country: '',
        city: '',
        locationCode: ''
    });
    const [editIndex, setEditIndex] = useState(null);
    const navigate = useNavigate(); // Initialize useNavigate

    // Fetch locations when the component mounts or currentPage changes
    const fetchLocations = async (page = 0) => {
        try {
            const response = await fetchWithAuth(`/locations?page=${page}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (response) {
                const data = await response.json();
                setLocations(data.content); // Set locations data
                setTotalPages(data.totalPages); // Set total pages
            } else {
                console.error('Failed to fetch locations');
            }
        } catch (error) {
            console.error('Failed to fetch locations');
        }
    };

    useEffect(() => {
        fetchLocations(currentPage); // Call the function to fetch locations
    }, [currentPage]);

    const handleAddLocation = async () => {
        if (newLocation.name && newLocation.country && newLocation.city && newLocation.locationCode) {
            try {
                const response = await fetchWithAuth('/locations', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(newLocation),
                });

                if (response) {
                    const createdLocation = await response.json();
                    setLocations([...locations, createdLocation]);
                    setNewLocation({ name: '', country: '', city: '', locationCode: '' });
                } else {
                    console.error('Failed to create location');
                }
            } catch (error) {
                console.error('Failed to create location');
            }
        }
    };

    const handleEditLocation = (index) => {
        const locationToEdit = locations[index];
        navigate(`/edit-location/${locationToEdit.id}`); // Navigate to the edit location page with the ID
    };

    const handleDeleteLocation = async (index) => {
        const locationToDelete = locations[index];
        try {
            const response = await fetchWithAuth(`/locations/${locationToDelete.id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (response) {
                const updatedLocations = locations.filter((_, i) => i !== index);
                setLocations(updatedLocations);
            } else {
                console.error('Failed to delete location');
            }
        } catch (error) {
            console.error('Failed to delete location');
        }
    };

    const handleNextPage = () => {
        if (currentPage < totalPages - 1) {
            setCurrentPage(currentPage + 1);
        }
    };

    const handlePreviousPage = () => {
        if (currentPage > 0) {
            setCurrentPage(currentPage - 1);
        }
    };

    return (
        <div className="locations-container">
            <h2 className="locations-header">Locations</h2>
            <div className="input-container">
                <input
                    className="input-field"
                    type="text"
                    value={newLocation.name}
                    onChange={(e) => setNewLocation({ ...newLocation, name: e.target.value })}
                    placeholder="Enter location name"
                />
                <input
                    className="input-field"
                    type="text"
                    value={newLocation.country}
                    onChange={(e) => setNewLocation({ ...newLocation, country: e.target.value })}
                    placeholder="Enter country"
                />
                <input
                    className="input-field"
                    type="text"
                    value={newLocation.city}
                    onChange={(e) => setNewLocation({ ...newLocation, city: e.target.value })}
                    placeholder="Enter city"
                />
                <input
                    className="input-field"
                    type="text"
                    value={newLocation.locationCode}
                    onChange={(e) => setNewLocation({ ...newLocation, locationCode: e.target.value })}
                    placeholder="Enter location code"
                />
                <Button variant="contained" color="error" onClick={handleAddLocation}>Add Location</Button>
            </div>
            <table className="locations-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>City</th>
                        <th>Country</th>
                        <th>Location Code</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {locations.length > 0 ? (
                        locations.map((location, index) => (
                            <tr key={index}>
                                <td>{location.name}</td>
                                <td>{location.city}</td>
                                <td>{location.country}</td>
                                <td>{location.locationCode}</td>
                                <td>
                                    <Button variant="outlined" color="primary" onClick={() => handleEditLocation(index)}>Edit</Button>
                                    <Button variant="outlined" color="error" onClick={() => handleDeleteLocation(index)}>Delete</Button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="5" style={{ textAlign: 'center' }}>No locations available</td>
                        </tr>
                    )}
                </tbody>
            </table>
            <div className="pagination-controls">
                <Button 
                    variant="outlined" 
                    onClick={handlePreviousPage} 
                    disabled={currentPage === 0}
                >
                    Previous
                </Button>
                <span>Page {currentPage + 1} of {totalPages}</span>
                <Button 
                    variant="outlined" 
                    onClick={handleNextPage} 
                    disabled={currentPage === totalPages - 1}
                >
                    Next
                </Button>
            </div>
        </div>
    );
};

export default Locations; 