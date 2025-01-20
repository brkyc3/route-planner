import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Locations.css'; 
import { fetchWithAuth } from '../api'; 
import Button from '@mui/material/Button'; 
import RefreshIcon from '@mui/icons-material/Refresh';

const Transportations = () => {
    const [transportations, setTransportations] = useState([]);
    const [currentPage, setCurrentPage] = useState(0); // Current page state
    const [totalPages, setTotalPages] = useState(0); // Total pages state
    const [newTransportation, setNewTransportation] = useState({
        originLocationCode: '',
        destinationLocationCode: '',
        transportationType: '',
        operatingDays: []
    });
    const [editIndex, setEditIndex] = useState(null);
    const [isLoading, setIsLoading] = useState(false); // State to manage loading
    const navigate = useNavigate();

    // Fetch transportations when the component mounts or currentPage changes
    const fetchTransportations = async (page = 0) => {
        setIsLoading(true); // Set loading state to true
        try {
            const response = await fetchWithAuth(`/transportations?page=${page}`, {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json',
                },
            });
            if (response) {
                const data = await response.json();
                setTransportations(data.content); // Set transportations data
                setTotalPages(data.totalPages); // Set total pages
            }
        } catch (error) {
            console.error('Failed to fetch transportations');
        } finally {
            setIsLoading(false); // Set loading state to false
        }
    };

    useEffect(() => {
        fetchTransportations(currentPage); // Call the function to fetch transportations
    }, [currentPage]);

    const handleAddTransportation = async () => {
        if (newTransportation.originLocationCode && newTransportation.destinationLocationCode && newTransportation.transportationType) {
            try {
                const response = await fetchWithAuth('/transportations', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(newTransportation),
                });

                if (response) {
                    const createdTransportation = await response.json();
                    setTransportations([...transportations, createdTransportation]);
                    setNewTransportation({ originLocationCode: '', destinationLocationCode: '', transportationType: '', operatingDays: [] });
                }
            } catch (error) {
                console.error('Failed to create transportation');
            }
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

    const handleEditTransportation = (index) => {
        const transportationToEdit = transportations[index];
        setNewTransportation({
            originLocationCode: transportationToEdit.originLocation.locationCode,
            destinationLocationCode: transportationToEdit.destinationLocation.locationCode,
            transportationType: transportationToEdit.transportationType,
            operatingDays: transportationToEdit.operatingDays
        });
        setEditIndex(index);
        navigate(`/edit-transportation/${transportationToEdit.id}`);
    };

    const handleDeleteTransportation = async (index) => {
        const transportationToDelete = transportations[index];
        try {
            const response = await fetchWithAuth(`/transportations/${transportationToDelete.id}`, {
                method: 'DELETE',
                headers: {
                    'Content-Type': 'application/json',
                },
            });

            if (response) {
                const updatedTransportations = transportations.filter((_, i) => i !== index);
                setTransportations(updatedTransportations);
            }
        } catch (error) {
            console.error('Failed to delete transportation');
        }
    };

    const handleDayChange = (day) => {
        setNewTransportation((prev) => {
            const newDays = prev.operatingDays.includes(day)
                ? prev.operatingDays.filter(d => d !== day)
                : [...prev.operatingDays, day];
            return { ...prev, operatingDays: newDays };
        });
    };

    return (
        <div className="locations-container">
            <h2 className="locations-header">Transportations</h2>
            <div className="input-container">
                <input
                    className="input-field"
                    type="text"
                    value={newTransportation.originLocationCode}
                    onChange={(e) => setNewTransportation({ ...newTransportation, originLocationCode: e.target.value })}
                    placeholder="Enter origin location code"
                />
                <input
                    className="input-field"
                    type="text"
                    value={newTransportation.destinationLocationCode}
                    onChange={(e) => setNewTransportation({ ...newTransportation, destinationLocationCode: e.target.value })}
                    placeholder="Enter destination location code"
                />
                <input
                    className="input-field"
                    type="text"
                    value={newTransportation.transportationType}
                    onChange={(e) => setNewTransportation({ ...newTransportation, transportationType: e.target.value })}
                    placeholder="Enter transportation type (FLIGHT, BUS, SUBWAY, UBER)"
                />
                <div className="operating-days-container">
                    <p>Operating Days:</p>
                    <div>
                        {Array.from({ length: 7 }, (_, i) => (
                            <div key={i}>
                                <input
                                    type="checkbox"
                                    checked={newTransportation.operatingDays.includes(i + 1)}
                                    onChange={() => handleDayChange(i + 1)}
                                />
                                <label>{[ 'Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday','Sunday'][i]}</label>
                            </div>
                        ))}
                    </div>
                </div>
                {editIndex !== null ? (
                    <Button variant="contained" color="error" onClick={handleAddTransportation}>Update Transportation</Button>
                ) : (
                    <Button variant="contained" color="error" onClick={handleAddTransportation}>Add Transportation</Button>
                )}
            </div>
            <Button 
                variant="outlined" 
                color="error"
                onClick={fetchTransportations} 
                startIcon={<RefreshIcon style={{ color: 'red' }} />}
                style={{ marginTop: '10px' }}
                className={isLoading ? 'spin' : ''} // Add spin class if loading
            >
                {/* Only the icon will be displayed */}
            </Button>
            <table className="locations-table">
                <thead>
                    <tr>
                        <th>Origin Location Code</th>
                        <th>Destination Location Code</th>
                        <th>Transportation Type</th>
                        <th>Operating Days</th>
                        <th>Actions</th>
                    </tr>
                </thead>
                <tbody>
                    {transportations.length > 0 ? (
                        transportations.map((transportation, index) => (
                            <tr key={index}>
                                <td>{transportation.originLocation.locationCode}</td>
                                <td>{transportation.destinationLocation.locationCode}</td>
                                <td>{transportation.transportationType}</td>
                                <td>{transportation.operatingDays.join(', ')}</td>
                                <td>
                                    <Button variant="outlined" color="primary" onClick={() => handleEditTransportation(index)}>Edit</Button>
                                    <Button variant="outlined" color="error" onClick={() => handleDeleteTransportation(index)}>Delete</Button>
                                </td>
                            </tr>
                        ))
                    ) : (
                        <tr>
                            <td colSpan="5" style={{ textAlign: 'center' }}>No transportations available</td>
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

export default Transportations; 