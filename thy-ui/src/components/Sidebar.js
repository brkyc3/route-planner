import React from 'react';
import styled from 'styled-components';
import { Link } from 'react-router-dom';

const SidebarContainer = styled.div`
    width: 250px;
    background: #f9f9f9;
    height: calc(100vh - 80px);
    position: fixed;
    top: 50px;
    box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
    display: flex;
    flex-direction: column;
    padding: 20px;
`;

const SidebarLink = styled(Link)`
    color: #343a40;
    text-decoration: none;
    padding: 10px 15px;
    border-radius: 5px;
    margin: 5px 0;
    transition: background 0.3s;
    display: block;

    &:hover {
        background: #e9ecef;
    }
`;

const Sidebar = () => {
    return (
        <SidebarContainer>
            <nav>
                <ul style={{ listStyleType: 'none', padding: 0 }}>
                    <li>
                        <SidebarLink to="/locations">Locations</SidebarLink>
                    </li>
                    <li>
                        <SidebarLink to="/transportations">Transportations</SidebarLink>
                    </li>
                    <li>
                        <SidebarLink to="/routes">Routes</SidebarLink>
                    </li>
                </ul>
            </nav>
        </SidebarContainer>
    );
};

export default Sidebar; 