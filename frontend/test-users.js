
async function testFetchUsers() {
    const token = localStorage.getItem("authToken");
    if (!token) {
        console.error("No auth token found in localStorage");
        return;
    }

    try {
        const response = await fetch('http://localhost:8080/api/auth/users', {
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const users = await response.json();
            console.log('Users fetched:', users);
            console.log('Count:', users.length);
        } else {
            console.error('Failed to fetch users:', response.status, response.statusText);
        }
    } catch (error) {
        console.error('Error:', error);
    }
}

testFetchUsers();
