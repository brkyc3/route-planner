import React from 'react';
import './Locations.css'; // Import the CSS file

const SidePanel = ({ route, onClose }) => {
    return (
        <div className="side-panel">
            <button className="close-button" onClick={onClose}>Close</button>
            <h3>Route Details</h3>
            {route.segments.map((segment, index) => (
                <div key={index} className="segment">
                    <p>
                        From: {segment.originLocation.name} ({segment.originLocation.locationCode}) <br />
                        To: {segment.destinationLocation.name} ({segment.destinationLocation.locationCode}) <br />
                        Type: {segment.transportationType} <br />
                    </p>
                </div>
            ))}
        </div>
    );
};

export default SidePanel; 