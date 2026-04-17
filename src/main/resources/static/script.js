const API_BASE = "http://localhost:8080";
const STORAGE_KEY = "namma-metro-session";

const state = {
    currentSection: "auth",
    session: null,
    isAdmin: false,
    routes: [],
    trains: [],
    incidents: [],
    userNotifications: [],
    incidentFilter: "ALL",
    draftStations: [],
    bookingStations: [],
    routeDetails: {
        route: null,
        stations: []
    },
    pricingConfigs: []
};

document.addEventListener("DOMContentLoaded", async () => {
    setMinDate();
    setupEventListeners();
    restoreSession();
    renderNavigation();
    renderSessionStatus();

    try {
        await loadRoutes();
    } catch (error) {
        showNotification(error.message, "error");
    }

    if (state.session && state.isAdmin) {
        await showSection("adminRoutes");
        return;
    }

    if (state.session) {
        await refreshRegularUserData();
        await showSection("dashboard");
        return;
    }

    await showSection("auth");
});

function setMinDate() {
    const travelDateInput = document.getElementById("travelDate");
    if (travelDateInput) {
        travelDateInput.min = new Date().toISOString().split("T")[0];
    }
}

function setupEventListeners() {
    document.getElementById("loginForm")?.addEventListener("submit", handleLogin);
    document.getElementById("registerForm")?.addEventListener("submit", handleRegister);
    document.getElementById("adminLoginForm")?.addEventListener("submit", handleAdminLogin);
    document.getElementById("ticketForm")?.addEventListener("submit", handleBookTicket);
    document.getElementById("addBalanceForm")?.addEventListener("submit", handleAddBalance);
    document.getElementById("reportIncidentForm")?.addEventListener("submit", handleReportIncident);
    document.getElementById("createRouteForm")?.addEventListener("submit", handleCreateRoute);
    document.getElementById("addStationForm")?.addEventListener("submit", handleAddStation);
    document.getElementById("createPricingForm")?.addEventListener("submit", handleCreatePricing);
    document.getElementById("createTrainForm")?.addEventListener("submit", handleCreateTrain);
    document.getElementById("addDraftStationBtn")?.addEventListener("click", handleAddDraftStation);

    document.getElementById("trainSelect")?.addEventListener("change", handleTrainSelection);
    document.getElementById("fromStation")?.addEventListener("change", updateFareEstimate);
    document.getElementById("toStation")?.addEventListener("change", updateFareEstimate);

    document.getElementById("refreshRoutesBtn")?.addEventListener("click", () => showSection("routes"));
    document.getElementById("refreshBookingBtn")?.addEventListener("click", () => showSection("bookTicket"));
    document.getElementById("refreshTicketsBtn")?.addEventListener("click", () => showSection("myTickets"));
    document.getElementById("refreshProfileBtn")?.addEventListener("click", () => showSection("profile"));
    document.getElementById("refreshIncidentsBtn")?.addEventListener("click", () => showSection("incidents"));
    document.getElementById("refreshAdminRoutesBtn")?.addEventListener("click", () => showSection("adminRoutes"));
    document.getElementById("refreshPricingBtn")?.addEventListener("click", () => showSection("adminPricing"));
    document.getElementById("refreshTrainsBtn")?.addEventListener("click", () => showSection("adminTrain"));

    document.querySelectorAll("[data-target]").forEach((element) => {
        element.addEventListener("click", () => {
            const target = element.dataset.target;
            if (target) {
                showSection(target);
            }
        });
    });

    document.getElementById("pricingList")?.addEventListener("click", handlePricingActions);
    document.getElementById("draftStationsList")?.addEventListener("click", handleDraftStationActions);
    document.getElementById("refreshAdminIncidentsBtn")?.addEventListener("click", () => showSection("adminIncidents"));
    
    // Incident filter buttons
    document.querySelectorAll(".filter-btn").forEach((btn) => {
        btn.addEventListener("click", (e) => {
            document.querySelectorAll(".filter-btn").forEach((b) => b.classList.remove("active"));
            e.target.classList.add("active");
            state.incidentFilter = e.target.dataset.filter || "ALL";
            renderAdminIncidents();
        });
    });

    // Notifications
    document.getElementById("refreshNotificationsBtn")?.addEventListener("click", () => showSection("notifications"));
    document.getElementById("markAllReadBtn")?.addEventListener("click", handleMarkAllNotificationsRead);

    document.querySelectorAll(".notif-filter-btn").forEach((btn) => {
        btn.addEventListener("click", (e) => {
            document.querySelectorAll(".notif-filter-btn").forEach((b) => b.classList.remove("active"));
            e.target.classList.add("active");
            renderNotifications(e.target.dataset.notifFilter || "ALL");
        });
    });
}

function restoreSession() {
    const raw = localStorage.getItem(STORAGE_KEY);
    if (!raw) {
        return;
    }

    try {
        const saved = JSON.parse(raw);
        state.session = saved.session || null;
        state.isAdmin = Boolean(saved.isAdmin);
    } catch (error) {
        localStorage.removeItem(STORAGE_KEY);
    }
}

function persistSession() {
    localStorage.setItem(
        STORAGE_KEY,
        JSON.stringify({
            session: state.session,
            isAdmin: state.isAdmin
        })
    );
}

function clearSession() {
    state.session = null;
    state.isAdmin = false;
    localStorage.removeItem(STORAGE_KEY);
    renderSessionStatus();
    renderNavigation();
}

function renderNavigation() {
    const nav = document.getElementById("navMenu");
    if (!nav) {
        return;
    }

    const items = [];
    if (!state.session) {
        items.push({ id: "auth", label: "Access" });
        items.push({ id: "routes", label: "Routes" });
    } else if (state.isAdmin) {
        items.push({ id: "adminRoutes", label: "Manage Routes" });
        items.push({ id: "adminPricing", label: "Pricing" });
        items.push({ id: "adminTrain", label: "Trains" });
        items.push({ id: "adminIncidents", label: "Incidents" });
        items.push({ id: "routes", label: "Public Routes" });
    } else {
        items.push({ id: "dashboard", label: "Dashboard" });
        items.push({ id: "routes", label: "Routes" });
        items.push({ id: "bookTicket", label: "Book Ticket" });
        items.push({ id: "myTickets", label: "My Tickets" });
        items.push({ id: "incidents", label: "Incidents" });
        items.push({ id: "notifications", label: "Notifications" });
        items.push({ id: "profile", label: "Profile" });
    }

    nav.innerHTML = "";
    items.forEach((item) => {
        const button = document.createElement("button");
        button.type = "button";
        button.className = `nav-btn${state.currentSection === item.id ? " active" : ""}`;
        button.textContent = item.label;
        button.addEventListener("click", () => showSection(item.id));
        nav.appendChild(button);
    });

    if (state.session) {
        const logoutButton = document.createElement("button");
        logoutButton.type = "button";
        logoutButton.className = "nav-btn logout-btn";
        logoutButton.textContent = "Logout";
        logoutButton.addEventListener("click", async () => {
            clearSession();
            resetFareDisplay();
            resetBookingForm();
            await showSection("auth");
            showNotification("Logged out successfully.");
        });
        nav.appendChild(logoutButton);
    }
}

function renderSessionStatus() {
    const userStatus = document.getElementById("userStatus");
    const identity = document.getElementById("statusIdentity");
    const walletDisplay = document.getElementById("walletDisplay");

    if (!userStatus || !identity || !walletDisplay) {
        return;
    }

    if (!state.session) {
        userStatus.style.display = "none";
        return;
    }

    userStatus.style.display = "flex";
    if (state.isAdmin) {
        identity.textContent = "Administrator";
        walletDisplay.textContent = "Admin session";
        return;
    }

    identity.textContent = state.session.name || state.session.email || "Passenger";
    walletDisplay.textContent = formatCurrency(state.session.walletBalance);
    updateWalletDisplays();
}

async function showSection(sectionId) {
    state.currentSection = sectionId;

    document.querySelectorAll(".tab-content").forEach((section) => {
        section.classList.toggle("active", section.id === sectionId);
    });

    renderNavigation();

    switch (sectionId) {
        case "dashboard":
            ensureRegularSession();
            renderDashboard();
            break;
        case "routes":
            await loadRoutes();
            break;
        case "bookTicket":
            ensureRegularSession();
            await loadBookingData();
            break;
        case "myTickets":
            ensureRegularSession();
            await loadTickets();
            break;
        case "profile":
            ensureRegularSession();
            await loadProfile();
            break;
        case "incidents":
            ensureRegularSession();
            await loadIncidents();
            break;
        case "notifications":
            ensureRegularSession();
            await loadUserNotifications();
            break;
        case "adminRoutes":
            ensureAdminSession();
            await loadAdminRoutes();
            break;
        case "adminPricing":
            ensureAdminSession();
            await loadPricingConfigurations();
            break;
        case "adminTrain":
            ensureAdminSession();
            await loadAdminTrainData();
            break;
        case "adminIncidents":
            ensureAdminSession();
            await loadAdminIncidents();
            break;
        default:
            break;
    }
}

function renderDashboard() {
    const greeting = document.getElementById("userGreeting");
    if (!greeting || !state.session) {
        return;
    }

    const wallet = formatCurrency(state.session.walletBalance);
    const points = state.session.loyaltyPoints ?? 0;
    greeting.textContent = `${state.session.name}, your wallet balance is ${wallet} and your loyalty points are ${points}.`;
}

function ensureRegularSession() {
    if (!state.session || state.isAdmin) {
        throw new Error("Passenger login is required for this section.");
    }
}

function ensureAdminSession() {
    if (!state.session || !state.isAdmin) {
        throw new Error("Admin login is required for this section.");
    }
}

async function apiRequest(path, options = {}) {
    const url = path.startsWith("http") ? path : `${API_BASE}${path}`;
    const requestOptions = { ...options };

    if (requestOptions.body && typeof requestOptions.body !== "string") {
        requestOptions.body = JSON.stringify(requestOptions.body);
    }

    requestOptions.headers = {
        "Content-Type": "application/json",
        ...(requestOptions.headers || {})
    };

    const response = await fetch(url, requestOptions);
    const raw = await response.text();
    const data = tryParseJson(raw);

    if (!response.ok) {
        const message = extractErrorMessage(data, raw, response.status);
        throw new Error(message);
    }

    return data;
}

function tryParseJson(raw) {
    if (!raw) {
        return null;
    }

    try {
        return JSON.parse(raw);
    } catch (error) {
        return raw;
    }
}

function extractErrorMessage(data, raw, status) {
    if (typeof data === "string" && data.trim()) {
        return data;
    }

    if (data && typeof data === "object") {
        return data.message || data.error || `Request failed with status ${status}`;
    }

    if (raw && raw.trim()) {
        return raw;
    }

    return `Request failed with status ${status}`;
}

function showNotification(message, type = "success") {
    const notification = document.getElementById("notification");
    if (!notification) {
        return;
    }

    notification.textContent = message;
    notification.className = `notification show ${type}`;

    window.clearTimeout(showNotification.timeoutId);
    showNotification.timeoutId = window.setTimeout(() => {
        notification.classList.remove("show");
    }, 4000);
}

async function handleRegister(event) {
    event.preventDefault();

    const payload = {
        name: document.getElementById("regName").value.trim(),
        email: document.getElementById("regEmail").value.trim(),
        password: document.getElementById("regPassword").value,
        phoneNumber: document.getElementById("regPhone").value.trim()
    };

    try {
        const profile = await apiRequest("/api/users/regular/register", {
            method: "POST",
            body: payload
        });

        state.session = normalizeRegularUser(profile);
        state.isAdmin = false;
        persistSession();
        renderSessionStatus();
        renderNavigation();
        event.target.reset();
        await showSection("dashboard");
        showNotification("Passenger account created successfully.");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

async function handleLogin(event) {
    event.preventDefault();

    const credentials = {
        email: document.getElementById("loginEmail").value.trim(),
        password: document.getElementById("loginPassword").value
    };

    try {
        const baseUser = await apiRequest("/users/login", {
            method: "POST",
            body: credentials
        });

        const profile = await resolveRegularProfile(baseUser);
        state.session = profile;
        state.isAdmin = false;
        persistSession();
        renderSessionStatus();
        renderNavigation();
        event.target.reset();
        await refreshRegularUserData();
        await showSection("dashboard");
        showNotification("Passenger login successful.");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

async function resolveRegularProfile(baseUser) {
    if (!baseUser?.id) {
        throw new Error("Login succeeded, but the backend did not return a user id.");
    }

    try {
        const directProfile = await apiRequest(`/api/users/regular/${baseUser.id}`);
        return normalizeRegularUser(directProfile);
    } catch (error) {
        throw new Error(
            "Base user login succeeded, but a matching RegularUser profile could not be resolved. " +
            "The backend currently exposes `/users/login` and `/api/users/regular/{id}`, so the UI cannot guarantee that mapping for older accounts."
        );
    }
}

function normalizeRegularUser(profile) {
    return {
        id: profile.id,
        userId: profile.user?.id ?? profile.id,
        name: profile.user?.name ?? profile.name ?? "Passenger",
        email: profile.user?.email ?? profile.email ?? "",
        phoneNumber: profile.phoneNumber ?? "",
        walletBalance: Number(profile.walletBalance ?? 0),
        loyaltyPoints: Number(profile.loyaltyPoints ?? 0)
    };
}

async function refreshRegularUserData() {
    if (!state.session || state.isAdmin) {
        return;
    }

    try {
        const [profile, balance, points] = await Promise.all([
            apiRequest(`/api/users/regular/${state.session.id}`),
            apiRequest(`/api/users/regular/${state.session.id}/wallet/balance`),
            apiRequest(`/api/users/regular/${state.session.id}/loyalty-points`)
        ]);

        state.session = {
            ...normalizeRegularUser(profile),
            walletBalance: Number(balance?.balance ?? profile.walletBalance ?? 0),
            loyaltyPoints: Number(points?.loyalty_points ?? profile.loyaltyPoints ?? 0)
        };

        persistSession();
        renderSessionStatus();
    } catch (error) {
        showNotification(`Profile refresh warning: ${error.message}`, "info");
    }
}

async function handleAdminLogin(event) {
    event.preventDefault();

    const username = document.getElementById("adminUsername").value.trim();
    const password = document.getElementById("adminPassword").value;
    const validUser = username === "admin" || username === "admin@nammametro.com";

    if (!validUser || password !== "admin123") {
        showNotification("Invalid admin credentials.", "error");
        return;
    }

    state.session = {
        id: "admin-local",
        name: "Administrator",
        email: "admin@nammametro.com"
    };
    state.isAdmin = true;
    persistSession();
    renderSessionStatus();
    renderNavigation();
    event.target.reset();

    await showSection("adminRoutes");
    showNotification("Admin session started.");
}

async function loadRoutes() {
    try {
        const routes = await apiRequest("/api/routes");
        state.routes = Array.isArray(routes) ? routes : [];
        if (!Array.isArray(routes)) {
            throw new Error("Routes endpoint returned an unexpected response.");
        }
        renderRoutes("routesList", state.routes);
        if (state.isAdmin) {
            renderRoutes("adminRoutesList", state.routes, true);
            populateRouteSelects(state.routes);
        }
    } catch (error) {
        state.routes = [];
        renderEmpty("routesList", "Unable to load routes from the backend.");
        if (state.isAdmin) {
            renderEmpty("adminRoutesList", "Unable to load routes from the backend.");
        }
        throw error;
    }
}

function renderRoutes(containerId, routes, adminMode = false) {
    const container = document.getElementById(containerId);
    if (!container) {
        return;
    }

    const safeRoutes = Array.isArray(routes) ? routes : [];
    container.innerHTML = "";
    if (!safeRoutes.length) {
        renderEmpty(containerId, "No routes available yet.");
        return;
    }

    safeRoutes.forEach((route) => {
        const card = document.createElement("div");
        card.className = "route-card";
        card.innerHTML = `
            <h3>${escapeHtml(route.name || "Unnamed Route")}</h3>
            <p>${escapeHtml(route.description || "No description provided.")}</p>
            <span class="station-count">${route.stationCount ?? route.stations?.length ?? "?"} stations</span>
        `;

        card.addEventListener("click", async () => {
            try {
                await openRouteDetails(route.id, adminMode ? "adminRoutes" : "routes");
            } catch (error) {
                showNotification(error.message, "error");
            }
        });

        container.appendChild(card);
    });
}

async function openRouteDetails(routeId) {
    const [route, stations] = await Promise.all([
        apiRequest(`/api/routes/${routeId}`),
        apiRequest(`/api/routes/${routeId}/stations`)
    ]);

    state.routeDetails.route = route;
    state.routeDetails.stations = sortStations(stations);

    document.getElementById("routeTitle").textContent = route.name || "Route Details";
    document.getElementById("routeSubtitle").textContent = route.description || "Station list for the selected route.";
    renderRouteStations();
    await showSection("routeDetails");
}

function renderRouteStations() {
    const container = document.getElementById("stationsList");
    if (!container) {
        return;
    }

    const stations = state.routeDetails.stations || [];
    container.innerHTML = "";

    if (!stations.length) {
        container.innerHTML = '<div class="station-item"><div class="station-info"><h4>No stations found</h4><p>Add stations to this route from the admin section.</p></div></div>';
        return;
    }

    stations.forEach((station, index) => {
        const item = document.createElement("div");
        item.className = "station-item";
        item.innerHTML = `
            <div class="station-number">${index + 1}</div>
            <div class="station-info">
                <h4>${escapeHtml(station.name)} (${escapeHtml(station.code || "--")})</h4>
                <p>Order ${station.order ?? index + 1} | Cumulative ${formatKilometers(station.cumulativeDistance)}</p>
            </div>
            <div class="station-distance">${formatKilometers(station.distanceToNext)}</div>
        `;
        container.appendChild(item);
    });
}

async function loadBookingData() {
    await refreshRegularUserData();

    const routes = await apiRequest("/api/routes");
    state.routes = Array.isArray(routes) ? routes : [];
    const trains = await apiRequest("/api/trains");
    state.trains = Array.isArray(trains) ? trains : [];
    populateTrainSelect();
    updateWalletDisplays();
    resetFareDisplay();
}

function populateTrainSelect() {
    const trainSelect = document.getElementById("trainSelect");
    if (!trainSelect) {
        return;
    }

    trainSelect.innerHTML = '<option value="">Select a train</option>';
    state.trains.forEach((train) => {
        const resolvedRouteId = getTrainRouteId(train);
        const resolvedRouteName = getTrainRouteName(train);
        const option = document.createElement("option");
        option.value = train.id;
        option.textContent = `${getTrainName(train)} | ${resolvedRouteName} | ${train.departureTime || "--"} - ${train.arrivalTime || "--"}`;
        option.dataset.routeId = resolvedRouteId ? String(resolvedRouteId) : "";
        trainSelect.appendChild(option);
    });
}

async function handleTrainSelection() {
    const trainSelect = document.getElementById("trainSelect");
    const trainId = Number(trainSelect?.value);

    if (!trainId) {
        state.bookingStations = [];
        populateStationSelects([]);
        resetFareDisplay();
        return;
    }

    const selectedTrain = state.trains.find((train) => Number(train.id) === trainId);
    let routeId = getTrainRouteId(selectedTrain);

    if (!routeId) {
        const selectedOption = trainSelect?.selectedOptions?.[0];
        const datasetRouteId = Number(selectedOption?.dataset?.routeId);
        if (!Number.isNaN(datasetRouteId) && datasetRouteId > 0) {
            routeId = datasetRouteId;
        }
    }

    if (!routeId && trainId) {
        try {
            const trainDetails = await apiRequest(`/api/trains/${trainId}`);
            routeId = getTrainRouteId(trainDetails);
        } catch (error) {
            // Fall through to the user-facing error below.
        }
    }

    if (!routeId) {
        showNotification("Selected train is missing route information.", "error");
        return;
    }

    try {
        const stations = await apiRequest(`/api/routes/${routeId}/stations`);
        state.bookingStations = sortStations(stations);
        populateStationSelects(state.bookingStations);
        resetFareDisplay();
    } catch (error) {
        state.bookingStations = [];
        populateStationSelects([]);
        showNotification(error.message, "error");
    }
}

function populateStationSelects(stations) {
    const fromStation = document.getElementById("fromStation");
    const toStation = document.getElementById("toStation");

    [fromStation, toStation].forEach((select, index) => {
        if (!select) {
            return;
        }

        const placeholder = index === 0 ? "Select source station" : "Select destination station";
        select.innerHTML = `<option value="">${placeholder}</option>`;

        stations.forEach((station) => {
            const option = document.createElement("option");
            option.value = station.id;
            option.textContent = `${station.name} (${station.code || "--"})`;
            select.appendChild(option);
        });
    });
}

async function updateFareEstimate() {
    const fromStationId = document.getElementById("fromStation")?.value;
    const toStationId = document.getElementById("toStation")?.value;

    if (!fromStationId || !toStationId) {
        resetFareDisplay();
        return;
    }

    if (fromStationId === toStationId) {
        document.getElementById("affordabilityCheck").textContent = "Source and destination cannot be the same.";
        document.getElementById("affordabilityCheck").style.color = "#dc3545";
        return;
    }

    try {
        const [distance, fare] = await Promise.all([
            apiRequest(`/api/routes/distance?from=${fromStationId}&to=${toStationId}`),
            apiRequest(`/api/pricing/calculate?from=${fromStationId}&to=${toStationId}`)
        ]);

        document.getElementById("distanceDisplay").textContent = formatKilometers(distance?.distance_km);
        document.getElementById("fareDisplay").textContent = formatCurrency(fare?.fare);
        document.getElementById("finalFareDisplay").textContent = formatCurrency(fare?.fare);

        if (!state.session || state.isAdmin) {
            document.getElementById("affordabilityCheck").textContent = "Passenger session required for affordability checks.";
            return;
        }

        const affordability = await apiRequest(`/api/users/regular/${state.session.id}/can-afford?fare=${fare.fare}`);
        const badge = document.getElementById("affordabilityCheck");
        if (affordability.can_afford) {
            badge.textContent = "Sufficient wallet balance";
            badge.style.color = "#28a745";
        } else {
            badge.textContent = `Need ${formatCurrency(affordability.shortage)} more`;
            badge.style.color = "#dc3545";
        }
    } catch (error) {
        resetFareDisplay();
        showNotification(error.message, "error");
    }
}

async function handleBookTicket(event) {
    event.preventDefault();

    try {
        ensureRegularSession();

        const payload = {
            regularUserId: state.session.id,
            trainId: Number(document.getElementById("trainSelect").value),
            sourceStationId: Number(document.getElementById("fromStation").value),
            destinationStationId: Number(document.getElementById("toStation").value),
            passengerName: document.getElementById("passengerName").value.trim(),
            travelDate: document.getElementById("travelDate").value
        };

        if (!payload.trainId || !payload.sourceStationId || !payload.destinationStationId) {
            throw new Error("Train and station selections are required.");
        }

        const ticket = await apiRequest("/api/tickets/book", {
            method: "POST",
            body: payload
        });

        await refreshRegularUserData();
        resetBookingForm();
        resetFareDisplay();
        await showSection("myTickets");
        showNotification(`Ticket booked successfully for ${formatCurrency(ticket.finalPrice ?? ticket.fare)}.`);
    } catch (error) {
        showNotification(error.message, "error");
    }
}

async function loadTickets() {
    const tickets = await apiRequest(`/api/tickets/user/${state.session.id}`);
    renderTickets(tickets);
}

function renderTickets(tickets) {
    const container = document.getElementById("ticketsList");
    if (!container) {
        return;
    }

    container.innerHTML = "";
    if (!tickets?.length) {
        renderEmpty("ticketsList", "No tickets booked yet.");
        return;
    }

    tickets.forEach((ticket) => {
        const card = document.createElement("div");
        card.className = "ticket-card";
        const statusClass = (ticket.status || "ACTIVE").toLowerCase();
        card.innerHTML = `
            <div class="ticket-header">
                <h3>Ticket #${ticket.id}</h3>
                <p>${escapeHtml(ticket.passengerName || "Passenger")}</p>
            </div>
            <div class="ticket-body">
                <div class="ticket-info">
                    <span class="ticket-label">From</span>
                    <span class="ticket-value">${escapeHtml(getTicketStationName(ticket, "source"))}</span>
                </div>
                <div class="ticket-info">
                    <span class="ticket-label">To</span>
                    <span class="ticket-value">${escapeHtml(getTicketStationName(ticket, "destination"))}</span>
                </div>
                <div class="ticket-info">
                    <span class="ticket-label">Train</span>
                    <span class="ticket-value">${escapeHtml(ticket.trainName || getTrainName(ticket.train))}</span>
                </div>
                <div class="ticket-info">
                    <span class="ticket-label">Date</span>
                    <span class="ticket-value">${escapeHtml(ticket.travelDate || "--")}</span>
                </div>
                <div class="ticket-info">
                    <span class="ticket-label">Final Price</span>
                    <span class="ticket-value">${formatCurrency(ticket.finalPrice ?? ticket.fare ?? ticket.price)}</span>
                </div>
                <span class="ticket-status ${statusClass}">${escapeHtml(ticket.status || "ACTIVE")}</span>
            </div>
        `;
        container.appendChild(card);
    });
}

async function loadProfile() {
    await refreshRegularUserData();
    renderProfile();
}

function renderProfile() {
    const profileInfo = document.getElementById("profileInfo");
    if (!profileInfo || !state.session) {
        return;
    }

    profileInfo.innerHTML = `
        <div class="profile-row">
            <span class="profile-label">Regular User ID</span>
            <span class="profile-value">${state.session.id}</span>
        </div>
        <div class="profile-row">
            <span class="profile-label">Name</span>
            <span class="profile-value">${escapeHtml(state.session.name || "--")}</span>
        </div>
        <div class="profile-row">
            <span class="profile-label">Email</span>
            <span class="profile-value">${escapeHtml(state.session.email || "--")}</span>
        </div>
        <div class="profile-row">
            <span class="profile-label">Phone Number</span>
            <span class="profile-value">${escapeHtml(state.session.phoneNumber || "--")}</span>
        </div>
    `;

    updateWalletDisplays();
    document.getElementById("pointsDisplay").textContent = String(state.session.loyaltyPoints ?? 0);
}

async function handleAddBalance(event) {
    event.preventDefault();

    try {
        ensureRegularSession();
        const amount = Number(document.getElementById("addAmount").value);
        if (!amount || amount <= 0) {
            throw new Error("Enter a valid amount to add.");
        }

        await apiRequest(`/api/users/regular/${state.session.id}/wallet/add`, {
            method: "POST",
            body: { amount }
        });

        await refreshRegularUserData();
        renderProfile();
        event.target.reset();
        showNotification(`Added ${formatCurrency(amount)} to the wallet.`);
    } catch (error) {
        showNotification(error.message, "error");
    }
}

async function loadAdminRoutes() {
    await loadRoutes();
    populateRouteSelects(state.routes);
    renderDraftStations();
}

function populateRouteSelects(routes) {
    const safeRoutes = Array.isArray(routes) ? routes : [];
    const selects = [
        document.getElementById("routeSelect"),
        document.getElementById("trainRouteSelect")
    ];

    selects.forEach((select) => {
        if (!select) {
            return;
        }

        const placeholder = select.id === "routeSelect" ? "Select a route" : "Select a route";
        select.innerHTML = `<option value="">${placeholder}</option>`;
        safeRoutes.forEach((route) => {
            const option = document.createElement("option");
            option.value = route.id;
            option.textContent = route.name;
            select.appendChild(option);
        });
    });
}

function handleAddDraftStation() {
    try {
        const station = {
            stationName: document.getElementById("draftStationName").value.trim(),
            stationCode: document.getElementById("draftStationCode").value.trim(),
            order: Number(document.getElementById("draftStationOrder").value),
            distanceToNext: Number(document.getElementById("draftDistanceToNext").value)
        };

        validateDraftStation(station);

        if (state.draftStations.some((item) => item.order === station.order)) {
            throw new Error("Each station in the draft route must have a unique order.");
        }

        state.draftStations.push(station);
        state.draftStations.sort((a, b) => a.order - b.order);
        clearDraftStationInputs();
        renderDraftStations();
        showNotification("Station added to route draft.");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

function validateDraftStation(station) {
    if (!station.stationName) {
        throw new Error("Station name is required.");
    }
    if (!station.stationCode) {
        throw new Error("Station code is required.");
    }
    if (!station.order || station.order <= 0) {
        throw new Error("Station order must be greater than zero.");
    }
    if (Number.isNaN(station.distanceToNext) || station.distanceToNext < 0) {
        throw new Error("Distance to next station must be zero or more.");
    }
}

function clearDraftStationInputs() {
    document.getElementById("draftStationName").value = "";
    document.getElementById("draftStationCode").value = "";
    document.getElementById("draftStationOrder").value = "";
    document.getElementById("draftDistanceToNext").value = "";
}

function renderDraftStations() {
    const container = document.getElementById("draftStationsList");
    if (!container) {
        return;
    }

    container.innerHTML = "";
    if (!state.draftStations.length) {
        container.innerHTML = '<div class="pricing-item"><p>No stations added to this route draft yet.</p></div>';
        return;
    }

    state.draftStations.forEach((station, index) => {
        const item = document.createElement("div");
        item.className = "pricing-item";
        item.innerHTML = `
            <h4>${escapeHtml(station.stationName)} (${escapeHtml(station.stationCode)})</h4>
            <p>Order: ${station.order} | Distance to next: ${formatKilometers(station.distanceToNext)}</p>
            <button class="btn-secondary" type="button" data-action="remove-draft-station" data-index="${index}">Remove</button>
        `;
        container.appendChild(item);
    });
}

function handleDraftStationActions(event) {
    const button = event.target.closest("button[data-action='remove-draft-station']");
    if (!button) {
        return;
    }

    const index = Number(button.dataset.index);
    if (!Number.isNaN(index)) {
        state.draftStations.splice(index, 1);
        renderDraftStations();
    }
}

async function handleCreateRoute(event) {
    event.preventDefault();

    try {
        ensureAdminSession();
        if (!state.draftStations.length) {
            throw new Error("Add at least one station while creating the route.");
        }

        await apiRequest("/api/routes", {
            method: "POST",
            body: {
                name: document.getElementById("routeName").value.trim(),
                description: document.getElementById("routeDesc").value.trim(),
                stations: state.draftStations
            }
        });

        event.target.reset();
        state.draftStations = [];
        clearDraftStationInputs();
        renderDraftStations();
        await loadAdminRoutes();
        showNotification("Route and stations created successfully.");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

async function handleAddStation(event) {
    event.preventDefault();

    try {
        ensureAdminSession();
        const routeId = Number(document.getElementById("routeSelect").value);
        if (!routeId) {
            throw new Error("Select a route before adding a station.");
        }

        await apiRequest(`/api/routes/${routeId}/stations`, {
            method: "POST",
            body: {
                stationName: document.getElementById("stationName").value.trim(),
                stationCode: document.getElementById("stationCode").value.trim(),
                order: Number(document.getElementById("stationOrder").value),
                distanceToNext: Number(document.getElementById("distanceToNext").value)
            }
        });

        event.target.reset();
        document.getElementById("routeSelect").value = String(routeId);
        await loadAdminRoutes();
        showNotification("Station added successfully.");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

async function loadPricingConfigurations() {
    const configs = await apiRequest("/api/pricing/configurations");
    state.pricingConfigs = Array.isArray(configs) ? configs : [];
    renderPricingConfigurations();
}

function renderPricingConfigurations() {
    const container = document.getElementById("pricingList");
    if (!container) {
        return;
    }

    container.innerHTML = "";
    if (!state.pricingConfigs.length) {
        renderEmpty("pricingList", "No pricing configurations found.");
        return;
    }

    state.pricingConfigs.forEach((config) => {
        const item = document.createElement("div");
        item.className = "pricing-item";
        item.innerHTML = `
            <h4>${escapeHtml(config.name || "Unnamed Config")}</h4>
            <p>Type: ${escapeHtml(config.strategyType || "--")} | Rate: ${formatCurrency(config.baseRatePerKm)} | Minimum: ${formatCurrency(config.minimumFare)}</p>
            <p>Status: ${config.isActive ? "Active" : "Inactive"}</p>
            <button class="btn-secondary" type="button" data-action="activate" data-id="${config.id}" ${config.isActive ? "disabled" : ""}>Activate</button>
        `;
        container.appendChild(item);
    });
}

async function handleCreatePricing(event) {
    event.preventDefault();

    try {
        ensureAdminSession();
        await apiRequest("/api/pricing/distance-based", {
            method: "POST",
            body: {
                name: document.getElementById("pricingName").value.trim(),
                baseRatePerKm: Number(document.getElementById("baseRate").value),
                minimumFare: Number(document.getElementById("minFare").value),
                adminId: 1
            }
        });

        event.target.reset();
        await loadPricingConfigurations();
        showNotification("Pricing configuration created.");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

async function handlePricingActions(event) {
    const button = event.target.closest("button[data-action]");
    if (!button) {
        return;
    }

    try {
        ensureAdminSession();
        const configId = Number(button.dataset.id);
        const action = button.dataset.action;

        if (action === "activate") {
            await apiRequest(`/api/pricing/configurations/${configId}/activate`, {
                method: "POST"
            });
            await loadPricingConfigurations();
            showNotification("Pricing configuration activated.");
        }
    } catch (error) {
        showNotification(error.message, "error");
    }
}

async function loadAdminTrainData() {
    await loadRoutes();
    populateRouteSelects(state.routes);

    const trains = await apiRequest("/api/trains");
    state.trains = Array.isArray(trains) ? trains : [];
    renderAdminTrains();
}

function renderAdminTrains() {
    const summaryContainer = document.getElementById("currentTrainSummary");
    const container = document.getElementById("trainAdminList");
    if (!container || !summaryContainer) {
        return;
    }

    summaryContainer.innerHTML = "";
    container.innerHTML = "";
    if (!state.trains.length) {
        renderEmpty("currentTrainSummary", "No current train available.");
        renderEmpty("trainAdminList", "No trains created yet.");
        return;
    }

    const latestTrain = [...state.trains]
        .sort((a, b) => Number(b.id ?? 0) - Number(a.id ?? 0))[0];

    const summaryItem = document.createElement("div");
    summaryItem.className = "pricing-item";
    summaryItem.innerHTML = `
        <h4>${escapeHtml(getTrainName(latestTrain))}</h4>
        <p>Route: ${escapeHtml(latestTrain.routeName || latestTrain.route?.name || "Unknown route")}</p>
        <p>From ${escapeHtml(latestTrain.sourceStation || "Unknown")} to ${escapeHtml(latestTrain.destinationStation || "Unknown")}</p>
        <p>Schedule: ${escapeHtml(latestTrain.departureTime || "--")} to ${escapeHtml(latestTrain.arrivalTime || "--")} | Status: ${escapeHtml(latestTrain.status || "--")}</p>
    `;
    summaryContainer.appendChild(summaryItem);

    state.trains.forEach((train) => {
        const item = document.createElement("div");
        item.className = "pricing-item";
        item.innerHTML = `
            <h4>${escapeHtml(getTrainName(train))}</h4>
            <p>Route: ${escapeHtml(train.routeName || train.route?.name || "Unknown route")} | Capacity: ${train.capacity ?? "--"}</p>
            <p>From ${escapeHtml(train.sourceStation || "Unknown")} to ${escapeHtml(train.destinationStation || "Unknown")}</p>
            <p>Schedule: ${escapeHtml(train.departureTime || "--")} to ${escapeHtml(train.arrivalTime || "--")} | Status: ${escapeHtml(train.status || "--")}</p>
        `;
        container.appendChild(item);
    });
}

async function handleCreateTrain(event) {
    event.preventDefault();

    try {
        ensureAdminSession();
        await apiRequest("/api/trains", {
            method: "POST",
            body: {
                trainName: document.getElementById("trainName").value.trim(),
                routeId: Number(document.getElementById("trainRouteSelect").value),
                capacity: Number(document.getElementById("trainCapacity").value),
                departureTime: document.getElementById("departureTime").value,
                arrivalTime: document.getElementById("arrivalTime").value
            }
        });

        event.target.reset();
        await loadAdminTrainData();
        showNotification("Train created successfully.");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

function updateWalletDisplays() {
    const balance = state.session && !state.isAdmin ? state.session.walletBalance : null;
    const balanceText = balance == null ? "--" : formatCurrency(balance);

    const balanceDisplay = document.getElementById("balanceDisplay");
    const walletCheckDisplay = document.getElementById("walletCheckDisplay");
    const walletDisplay = document.getElementById("walletDisplay");

    if (balanceDisplay) {
        balanceDisplay.textContent = balanceText;
    }
    if (walletCheckDisplay) {
        walletCheckDisplay.textContent = balanceText;
    }
    if (walletDisplay && state.session && !state.isAdmin) {
        walletDisplay.textContent = balanceText;
    }
}

function renderEmpty(containerId, message) {
    const container = document.getElementById(containerId);
    if (!container) {
        return;
    }

    container.innerHTML = `<p style="grid-column: 1 / -1; text-align: center; color: #999;">${escapeHtml(message)}</p>`;
}

function resetFareDisplay() {
    document.getElementById("distanceDisplay").textContent = "--";
    document.getElementById("fareDisplay").textContent = "--";
    document.getElementById("finalFareDisplay").textContent = "--";
    document.getElementById("walletCheckDisplay").textContent = state.session && !state.isAdmin
        ? formatCurrency(state.session.walletBalance)
        : "--";

    const affordability = document.getElementById("affordabilityCheck");
    affordability.textContent = "Choose stations to calculate affordability";
    affordability.style.color = "#0c5460";
}

function resetBookingForm() {
    document.getElementById("ticketForm")?.reset();
    state.bookingStations = [];
    populateStationSelects([]);
}

function sortStations(stations) {
    return [...(stations || [])].sort((a, b) => Number(a.order ?? 0) - Number(b.order ?? 0));
}

function getTrainName(train) {
    if (!train) {
        return "--";
    }

    return train.trainName || train.name || "--";
}

function getTrainRouteId(train) {
    if (!train) {
        return null;
    }

    const routeId = Number(train.routeId ?? train.route?.id);
    if (!Number.isNaN(routeId) && routeId > 0) {
        return routeId;
    }

    const routeName = train.routeName || train.route?.name;
    if (routeName && Array.isArray(state.routes)) {
        const matchingRoute = state.routes.find((route) => route.name === routeName);
        if (matchingRoute?.id) {
            return Number(matchingRoute.id);
        }
    }

    return null;
}

function getTrainRouteName(train) {
    if (!train) {
        return "Unknown route";
    }

    if (train.routeName || train.route?.name) {
        return train.routeName || train.route?.name;
    }

    const routeId = getTrainRouteId(train);
    if (routeId && Array.isArray(state.routes)) {
        const matchingRoute = state.routes.find((route) => Number(route.id) === Number(routeId));
        if (matchingRoute?.name) {
            return matchingRoute.name;
        }
    }

    return "Unknown route";
}

function getTicketStationName(ticket, type) {
    if (type === "source") {
        return ticket.sourceStationName || ticket.sourceStation?.name || "--";
    }
    return ticket.destinationStationName || ticket.destinationStation?.name || "--";
}

function formatCurrency(value) {
    const amount = Number(value);
    if (Number.isNaN(amount)) {
        return "--";
    }
    return `₹${amount.toFixed(2)}`;
}

function formatKilometers(value) {
    const distance = Number(value);
    if (Number.isNaN(distance)) {
        return "--";
    }
    return `${distance.toFixed(1)} km`;
}

function escapeHtml(value) {
    return String(value)
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#39;");
}

// Incidents Management
async function loadIncidents() {
    try {
        const incidents = await apiRequest("/incidents");
        state.incidents = Array.isArray(incidents) ? incidents : [];
        renderIncidents();
    } catch (error) {
        state.incidents = [];
        renderIncidents();
        showNotification(`Unable to load incidents: ${error.message}`, "error");
    }
}

async function handleReportIncident(event) {
    event.preventDefault();

    try {
        ensureRegularSession();
        const payload = {
            description: document.getElementById("incidentDescription").value.trim(),
            location: document.getElementById("incidentLocation").value.trim()
        };

        if (!payload.description) {
            throw new Error("Please enter an incident description.");
        }
        if (!payload.location) {
            throw new Error("Please enter the incident location.");
        }

        const newIncident = await apiRequest("/incidents", {
            method: "POST",
            body: payload
        });

        state.incidents.unshift(newIncident);
        event.target.reset();
        renderIncidents();
        showNotification("Incident reported successfully.");
    } catch (error) {
        showNotification(error.message, "error");
    }
}

function renderIncidents() {
    const container = document.getElementById("incidentsList");
    if (!container) {
        return;
    }

    container.innerHTML = "";

    if (!state.incidents || state.incidents.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>No incidents reported yet.</p></div>';
        return;
    }

    state.incidents.forEach((incident) => {
        const card = document.createElement("div");
        card.className = "incident-item";
        card.innerHTML = `
            <div class="incident-header">
                <h4>${escapeHtml(incident.location || "Unknown Location")}</h4>
                <span class="incident-id">#${incident.id}</span>
            </div>
            <p class="incident-description">${escapeHtml(incident.description || "No description provided")}</p>
            <div class="incident-meta">
                <span class="incident-date">Reported: ${formatIncidentDate(incident)}</span>
            </div>
        `;
        container.appendChild(card);
    });
}

function formatIncidentDate(incident) {
    if (!incident) {
        return "--";
    }
    const dateStr = incident.createdAt || incident.reportedAt || incident.timestamp || new Date().toISOString();
    try {
        return new Date(dateStr).toLocaleDateString("en-IN", {
            year: "numeric",
            month: "short",
            day: "numeric",
            hour: "2-digit",
            minute: "2-digit"
        });
    } catch (error) {
        return "--";
    }
}

// Admin Incidents Management
async function loadAdminIncidents() {
    try {
        const incidents = await apiRequest("/incidents");
        state.incidents = Array.isArray(incidents) ? incidents : [];
        renderAdminIncidentsStats();
        renderAdminIncidents();
    } catch (error) {
        state.incidents = [];
        showNotification(`Unable to load incidents: ${error.message}`, "error");
    }
}

function renderAdminIncidentsStats() {
    const stats = {
        OPEN: 0,
        IN_PROGRESS: 0,
        RESOLVED: 0,
        CLOSED: 0
    };

    state.incidents.forEach((incident) => {
        const status = incident.status || "OPEN";
        if (stats[status] !== undefined) {
            stats[status]++;
        }
    });

    document.getElementById("totalIncidentsCount").textContent = state.incidents.length;
    document.getElementById("openIncidentsCount").textContent = stats.OPEN;
    document.getElementById("progressIncidentsCount").textContent = stats.IN_PROGRESS;
    document.getElementById("resolvedIncidentsCount").textContent = stats.RESOLVED;
}

function renderAdminIncidents() {
    const container = document.getElementById("adminIncidentsList");
    if (!container) {
        return;
    }

    container.innerHTML = "";

    let filtered = state.incidents;
    if (state.incidentFilter !== "ALL") {
        filtered = state.incidents.filter((i) => i.status === state.incidentFilter);
    }

    if (!filtered || filtered.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>No incidents found.</p></div>';
        return;
    }

    filtered.forEach((incident) => {
        const card = document.createElement("div");
        card.className = `incident-admin-card status-${(incident.status || "OPEN").toLowerCase()}`;
        card.innerHTML = `
            <div class="incident-admin-header">
                <div>
                    <h4>${escapeHtml(incident.location || "Unknown Location")}</h4>
                    <span class="incident-status-badge">${incident.status || "OPEN"}</span>
                </div>
                <span class="incident-admin-id">#${incident.id}</span>
            </div>
            <p class="incident-admin-description">${escapeHtml(incident.description || "No description")}</p>
            ${incident.resolutionNotes ? `<div class="incident-notes"><strong>Notes:</strong> ${escapeHtml(incident.resolutionNotes)}</div>` : ""}
            <div class="incident-admin-meta">
                <span>Reported: ${formatIncidentDate(incident)}</span>
            </div>
            <div class="incident-admin-actions">
                <select class="incident-status-select" data-incident-id="${incident.id}">
                    <option value="OPEN" ${incident.status === "OPEN" ? "selected" : ""}>Open</option>
                    <option value="IN_PROGRESS" ${incident.status === "IN_PROGRESS" ? "selected" : ""}>In Progress</option>
                    <option value="RESOLVED" ${incident.status === "RESOLVED" ? "selected" : ""}>Resolved</option>
                    <option value="CLOSED" ${incident.status === "CLOSED" ? "selected" : ""}>Closed</option>
                </select>
                <input type="text" class="incident-notes-input" data-incident-id="${incident.id}" placeholder="Add resolution notes..." value="${escapeHtml(incident.resolutionNotes || "")}">
                <button class="btn-secondary" data-action="save-incident" data-incident-id="${incident.id}">Save</button>
            </div>
        `;
        container.appendChild(card);
    });

    // Attach event listeners
    document.querySelectorAll("[data-action='save-incident']").forEach((btn) => {
        btn.addEventListener("click", handleSaveIncident);
    });
}

async function handleSaveIncident(e) {
    const incidentId = e.target.dataset.incidentId;
    const statusSelect = document.querySelector(`.incident-status-select[data-incident-id="${incidentId}"]`);
    const notesInput = document.querySelector(`.incident-notes-input[data-incident-id="${incidentId}"]`);

    if (!statusSelect || !notesInput) {
        return;
    }

    try {
        const payload = {
            status: statusSelect.value,
            resolutionNotes: notesInput.value.trim()
        };

        await apiRequest(`/incidents/${incidentId}`, {
            method: "PUT",
            body: payload
        });

        showNotification("Incident updated successfully.");
        await loadAdminIncidents();
    } catch (error) {
        showNotification(`Failed to update incident: ${error.message}`, "error");
    }
}

// Notifications Management
async function loadUserNotifications() {
    try {
        if (!state.session || state.isAdmin) {
            throw new Error("Login required to view notifications");
        }

        const notifications = await apiRequest(`/notifications/user/${state.session.id}`);
        state.userNotifications = Array.isArray(notifications) ? notifications : [];
        renderNotifications("ALL");
    } catch (error) {
        showNotification(`Unable to load notifications: ${error.message}`, "error");
        state.userNotifications = [];
    }
}

function renderNotifications(filterType = "ALL") {
    const container = document.getElementById("notificationsList");
    if (!container) {
        return;
    }

    container.innerHTML = "";

    let filtered = state.userNotifications || [];
    if (filterType !== "ALL") {
        filtered = filtered.filter((n) => n.type === filterType);
    }

    if (!filtered || filtered.length === 0) {
        container.innerHTML = '<div class="empty-state"><p>No notifications to display.</p></div>';
        return;
    }

    filtered.forEach((notification) => {
        const card = document.createElement("div");
        card.className = `notification-item ${notification.isRead ? "read" : "unread"} type-${(notification.type || "").toLowerCase()}`;
        card.innerHTML = `
            <div class="notification-header">
                <div class="notification-title-section">
                    <span class="notification-badge">${getNotificationIcon(notification.type)}</span>
                    <h4>${escapeHtml(notification.title || "Notification")}</h4>
                </div>
                <span class="notification-time">${formatNotificationDate(notification.createdAt)}</span>
            </div>
            <p class="notification-message">${escapeHtml(notification.message || "")}</p>
            <div class="notification-actions">
                ${!notification.isRead ? `<button class="btn-secondary" data-action="mark-read" data-notif-id="${notification.id}">Mark as Read</button>` : ""}
                <button class="btn-secondary" data-action="delete-notif" data-notif-id="${notification.id}">Delete</button>
            </div>
        `;
        container.appendChild(card);
    });

    // Attach event listeners
    document.querySelectorAll("[data-action='mark-read']").forEach((btn) => {
        btn.addEventListener("click", handleMarkNotificationRead);
    });

    document.querySelectorAll("[data-action='delete-notif']").forEach((btn) => {
        btn.addEventListener("click", handleDeleteNotification);
    });
}

function getNotificationIcon(type) {
    const icons = {
        "TICKET_BOOKING": "🎫",
        "TRAIN_DELAY": "⏰",
        "TRAIN_STATUS_UPDATE": "🚂",
        "TICKET_CANCELLATION": "❌",
        "USER_REGISTRATION": "✅",
        "INCIDENT_REPORT": "⚠️"
    };
    return icons[type] || "📢";
}

function formatNotificationDate(dateStr) {
    if (!dateStr) {
        return "Recently";
    }

    try {
        const date = new Date(dateStr);
        const now = new Date();
        const diff = now - date;
        const hours = Math.floor(diff / (1000 * 60 * 60));
        const days = Math.floor(hours / 24);

        if (hours < 1) {
            return "Just now";
        } else if (hours < 24) {
            return `${hours}h ago`;
        } else if (days < 7) {
            return `${days}d ago`;
        } else {
            return date.toLocaleDateString("en-IN");
        }
    } catch (error) {
        return "Recently";
    }
}

async function handleMarkNotificationRead(e) {
    const notifId = e.target.dataset.notifId;

    try {
        await apiRequest(`/notifications/${notifId}/read`, {
            method: "PUT"
        });

        await loadUserNotifications();
        showNotification("Notification marked as read.");
    } catch (error) {
        showNotification(`Error: ${error.message}`, "error");
    }
}

async function handleMarkAllNotificationsRead() {
    try {
        if (!state.session || state.isAdmin) {
            throw new Error("Login required");
        }

        await apiRequest(`/notifications/user/${state.session.id}/read-all`, {
            method: "PUT"
        });

        await loadUserNotifications();
        showNotification("All notifications marked as read.");
    } catch (error) {
        showNotification(`Error: ${error.message}`, "error");
    }
}

async function handleDeleteNotification(e) {
    const notifId = e.target.dataset.notifId;

    try {
        await apiRequest(`/notifications/${notifId}`, {
            method: "DELETE"
        });

        await loadUserNotifications();
        showNotification("Notification deleted.");
    } catch (error) {
        showNotification(`Error: ${error.message}`, "error");
    }
}
