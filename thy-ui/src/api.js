const API_BASE_URL = 'http://localhost:8080/api';

export const fetchWithAuth = async (url, options) => {
    try {
        const response = await fetch(`${API_BASE_URL}${url}`, {
            ...options,
            headers: {
                ...options.headers,
                'Authorization': `Bearer ${localStorage.getItem('token')}`,
            },
        });

        if (response.status === 403) {
            // Remove token and redirect to login
            localStorage.removeItem('token');
            window.location.href = '/login'; // Redirect to login page
            return null; // Return null to indicate an error
        }

        return response;
    } catch (error) {
        console.error('Error:', error);
        throw error; // Rethrow the error for further handling if needed
    }
}; 