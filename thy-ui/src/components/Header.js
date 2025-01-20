import React from 'react';
import styled from 'styled-components';

const HeaderContainer = styled.header`
    background: #f8f9fa;
    padding: 15px 20px;
    color: #343a40;
    text-align: left;
    box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    position: fixed;
    top: 0;
    left: 0;
    right: 0;
    z-index: 1000;
`;

const Title = styled.h1`
    margin: 0;
    font-size: 24px;
    font-weight: 500;
`;

const Header = () => {
    return (
        <HeaderContainer>
            <Title>Route Planner</Title>
        </HeaderContainer>
    );
};

export default Header; 