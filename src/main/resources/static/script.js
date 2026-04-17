// API Base URL
const API_BASE = 'http://localhost:8080/api' || '';

// Tab Navigation
function showTab(tabName) {
    const contents = document.querySelectorAll('.tab-content');
    const buttons = document.querySelectorAll('.nav-btn');

    contents.forEach(content => content.classList.remove('active'));
    buttons.forEach(btn => btn.classList.remove('active'));

    document.getElementById(tabName).classList.add('active');
    
    // Try to add active class to the corresponding button
    if (event && event.target) {
        event.target.classList.add('active');
    } else {
        // Find button with matching text or content
        buttons.forEach(btn => {
            if (btn.onclick && btn.onclick.toString().includes(tabName)) {
                btn.classList.add('active');
            }
        });
    }

    // Load data when tab is opened
    if (tabName === 'trains') loadTrains();
    if (tabName === 'tickets') loadTickets();
    if (tabName === 'users') loadUsers();
    if (tabName === 'incidents') loadIncidents();
}

// Notification System
function showNotification(message, type = 'success') {
    const notif = document.getElementById('notification');
    notif.textContent = message;
    notif.className = `notification show ${type}`;

    setTimeout(() => {
        notif.classList.remove('show');
    }, 4000);
}

// AUTH STATE MANAGEMENT
function setLoggedIn(user) {
    sessionStorage.setItem('isLoggedIn', 'true');
    sessionStorage.setItem('currentUser', JSON.stringify(user));
    updateNavigation();
    updateDashboard(user);
    showTab('dashboard');
}

function logout() {
    sessionStorage.removeItem('isLoggedIn');
    sessionStorage.removeItem('currentUser');
    updateNavigation();
    showTab('login');
    showNotification('Logged out successfully', 'success');
}

function isLoggedIn() {
    return sessionStorage.getItem('isLoggedIn') === 'true';
}

function getCurrentUser() {
    const user = sessionStorage.getItem('currentUser');
    return user ? JSON.parse(user) : null;
}

function updateNavigation() {
    const authNav = document.getElementById('authNav');
    const mainNav = document.getElementById('mainNav');
    
    if (isLoggedIn()) {
        authNav.style.display = 'none';
        mainNav.style.display = 'flex';
    } else {
        authNav.style.display = 'flex';
        mainNav.style.display = 'none';
    }
}

function updateDashboard(user) {
    const greeting = document.getElementById('userGreeting');
    if (greeting) {
        greeting.textContent = `Welcome back, ${user.name}! 👋`;
    }
}

// TRAINS
function initializeTrainForm() {
    const form = document.getElementById('trainForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const train = {
                trainName: document.getElementById('trainName').value,
                route: document.getElementById('trainRoute').value,
                capacity: parseInt(document.getElementById('trainCapacity').value)
            };

            try {
                const response = await fetch('/trains', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(train)
                });

                if (response.ok) {
                    showNotification('Train added successfully!', 'success');
                    form.reset();
                    loadTrains();
                } else {
                    showNotification('Error adding train', 'error');
                }
            } catch (error) {
                showNotification('Error: ' + error.message, 'error');
                console.error(error);
            }
        });
    }
}

async function loadTrains() {
    try {
        const response = await fetch('/trains');
        const trains = await response.json();

        const list = document.getElementById('trainsList');
        if (!Array.isArray(trains) || trains.length === 0) {
            list.innerHTML = '<p class="loading">No trains available</p>';
            return;
        }

        list.innerHTML = trains.map(train => `
            <div class="list-item">
                <h4>🚆 ${train.trainName || train.name || 'N/A'}</h4>
                <p><strong>Route:</strong> ${train.route || 'N/A'}</p>
                <p><strong>Capacity:</strong> ${train.capacity || 'N/A'} passengers</p>
                <p><strong>Status:</strong> ${train.status || 'N/A'}</p>
                <p><strong>ID:</strong> ${train.id || 'N/A'}</p>
            </div>
        `).join('');
    } catch (error) {
        document.getElementById('trainsList').innerHTML = '<p class="loading">Error loading trains</p>';
        console.error('Error loading trains:', error);
    }
}

// TICKETS
function initializeTicketForm() {
    const form = document.getElementById('ticketForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const ticket = {
                passengerName: document.getElementById('passengerName').value,
                source: document.getElementById('source').value,
                destination: document.getElementById('destination').value,
                price: parseFloat(document.getElementById('ticketPrice').value)
            };

            try {
                const response = await fetch('/tickets', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(ticket)
                });

                if (response.ok) {
                    showNotification('Ticket booked successfully!', 'success');
                    form.reset();
                    loadTickets();
                } else {
                    showNotification('Error booking ticket', 'error');
                }
            } catch (error) {
                showNotification('Error: ' + error.message, 'error');
                console.error(error);
            }
        });
    }
}

async function loadTickets() {
    try {
        const response = await fetch('/tickets');
        const tickets = await response.json();

        const list = document.getElementById('ticketsList');
        if (!Array.isArray(tickets) || tickets.length === 0) {
            list.innerHTML = '<p class="loading">No tickets booked yet</p>';
            return;
        }

        list.innerHTML = tickets.map(ticket => `
            <div class="list-item">
                <h4>🎫 ${ticket.passengerName || 'N/A'}</h4>
                <p><strong>Route:</strong> ${ticket.source || 'N/A'} → ${ticket.destination || 'N/A'}</p>
                <p><strong>Price:</strong> ₹${ticket.price || ticket.fare || 'N/A'}</p>
                <p><strong>Status:</strong> ${ticket.status || 'Active'}</p>
                <p><strong>ID:</strong> ${ticket.id || 'N/A'}</p>
            </div>
        `).join('');
    } catch (error) {
        document.getElementById('ticketsList').innerHTML = '<p class="loading">Error loading tickets</p>';
        console.error('Error loading tickets:', error);
    }
}

// USERS
function initializeUserForm() {
    const form = document.getElementById('userForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const user = {
                name: document.getElementById('userName').value,
                email: document.getElementById('userEmail').value,
                password: document.getElementById('userPassword').value
            };

            try {
                const response = await fetch('/users', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(user)
                });

                if (response.ok) {
                    showNotification('User created successfully!', 'success');
                    form.reset();
                    loadUsers();
                } else {
                    showNotification('Error creating user', 'error');
                }
            } catch (error) {
                showNotification('Error: ' + error.message, 'error');
                console.error(error);
            }
        });
    }
}

async function loadUsers() {
    try {
        const response = await fetch('/users');
        const users = await response.json();

        const list = document.getElementById('usersList');
        if (!Array.isArray(users) || users.length === 0) {
            list.innerHTML = '<p class="loading">No users available</p>';
            return;
        }

        list.innerHTML = users.map(user => `
            <div class="list-item">
                <h4>👤 ${user.name || 'N/A'}</h4>
                <p><strong>Email:</strong> ${user.email || 'N/A'}</p>
                <p><strong>ID:</strong> ${user.id || 'N/A'}</p>
            </div>
        `).join('');
    } catch (error) {
        document.getElementById('usersList').innerHTML = '<p class="loading">Error loading users</p>';
        console.error('Error loading users:', error);
    }
}

// INCIDENTS
function initializeIncidentForm() {
    const form = document.getElementById('incidentForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const incident = {
                description: document.getElementById('incidentDesc').value,
                location: document.getElementById('incidentLocation').value,
                severity: document.getElementById('incidentSeverity').value
            };

            try {
                const response = await fetch('/incidents', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(incident)
                });

                if (response.ok) {
                    showNotification('Incident reported successfully!', 'success');
                    form.reset();
                    loadIncidents();
                } else {
                    showNotification('Error reporting incident', 'error');
                }
            } catch (error) {
                showNotification('Error: ' + error.message, 'error');
                console.error(error);
            }
        });
    }
}

async function loadIncidents() {
    try {
        const response = await fetch('/incidents');
        const incidents = await response.json();

        const list = document.getElementById('incidentsList');
        if (!Array.isArray(incidents) || incidents.length === 0) {
            list.innerHTML = '<p class="loading">No incidents reported</p>';
            return;
        }

        list.innerHTML = incidents.map(incident => `
            <div class="list-item">
                <h4>⚠️ ${incident.description || 'N/A'}</h4>
                <p><strong>Location:</strong> ${incident.location || 'N/A'}</p>
                <p><strong>Severity:</strong> <span style="color: ${incident.severity === 'High' ? '#f44336' : incident.severity === 'Medium' ? '#ff9800' : '#4caf50'}">${incident.severity || 'N/A'}</span></p>
                <p><strong>ID:</strong> ${incident.id || 'N/A'}</p>
            </div>
        `).join('');
    } catch (error) {
        document.getElementById('incidentsList').innerHTML = '<p class="loading">Error loading incidents</p>';
        console.error('Error loading incidents:', error);
    }
}

// AUTH - LOGIN
function initializeLoginForm() {
    const form = document.getElementById('loginForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const user = {
                email: document.getElementById('loginEmail').value,
                password: document.getElementById('loginPassword').value
            };

            try {
                const response = await fetch('/users/login', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(user)
                });

                if (response.ok) {
                    const userData = await response.json();
                    showNotification('Login successful! Welcome ' + userData.name, 'success');
                    form.reset();
                    // Set logged in state and redirect to dashboard
                    setTimeout(() => setLoggedIn(userData), 500);
                } else {
                    showNotification('Invalid email or password', 'error');
                }
            } catch (error) {
                showNotification('Error: ' + error.message, 'error');
                console.error(error);
            }
        });
    }
}

// AUTH - REGISTER
function initializeRegisterForm() {
    const form = document.getElementById('registerForm');
    if (form) {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();

            const password = document.getElementById('regPassword').value;
            const confirmPassword = document.getElementById('regConfirmPassword').value;

            if (password !== confirmPassword) {
                showNotification('Passwords do not match', 'error');
                return;
            }

            const user = {
                name: document.getElementById('regName').value,
                email: document.getElementById('regEmail').value,
                password: password
            };

            try {
                const response = await fetch('/users/register', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/json' },
                    body: JSON.stringify(user)
                });

                if (response.ok) {
                    showNotification('Registration successful! Please sign in.', 'success');
                    form.reset();
                    // Redirect to login tab
                    setTimeout(() => showTab('login'), 1500);
                } else if (response.status === 400) {
                    showNotification('Email already exists', 'error');
                } else {
                    showNotification('Error registering user', 'error');
                }
            } catch (error) {
                showNotification('Error: ' + error.message, 'error');
                console.error(error);
            }
        });
    }
}

// Initialize all forms on page load
document.addEventListener('DOMContentLoaded', () => {
    initializeLoginForm();
    initializeRegisterForm();
    initializeTrainForm();
    initializeTicketForm();
    initializeUserForm();
    initializeIncidentForm();
    
    // Check if user is already logged in
    updateNavigation();
    if (isLoggedIn()) {
        const user = getCurrentUser();
        updateDashboard(user);
        showTab('dashboard');
    } else {
        loadTrains();
    }
});
