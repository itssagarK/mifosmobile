// ===== MIFOS FIELD OPERATIONS — OFFLINE-FIRST PLATFORM =====
// Phase 1: Dashboard + Offline System + Collection Sheet

// ===== DATA STORE =====
const DB_KEY = 'mifos_field_ops';

const defaultData = {
    isOnline: true,
    lastSyncTime: null,
    officer: { id: 'FO-1024', name: 'James Kariuki' },
    centers: [
        { id: 1, name: 'Kibera Women Group Center', groups: 3, members: 18, meetingDay: 'Monday', status: 'pending', pendingCollections: 5400 },
        { id: 2, name: 'Mathare Savings Center', groups: 2, members: 12, meetingDay: 'Tuesday', status: 'collected', pendingCollections: 0 },
        { id: 3, name: 'Dandora Enterprise Center', groups: 4, members: 24, meetingDay: 'Wednesday', status: 'due', pendingCollections: 8200 },
        { id: 4, name: 'Eastleigh Micro Center', groups: 2, members: 10, meetingDay: 'Thursday', status: 'pending', pendingCollections: 3100 }
    ],
    meetings: [
        { id: 1, centerId: 1, time: '09:00', centerName: 'Kibera Women Group Center', groupCount: 3, memberCount: 18, status: 'scheduled' },
        { id: 2, centerId: 3, time: '11:30', centerName: 'Dandora Enterprise Center', groupCount: 4, memberCount: 24, status: 'scheduled' },
        { id: 3, centerId: 4, time: '14:00', centerName: 'Eastleigh Micro Center', groupCount: 2, memberCount: 10, status: 'scheduled' }
    ],
    members: [
        { id: 1, name: 'Mary Wanjiku', group: 'Umoja A', centerId: 1, savings: 200, loanDue: 500, attendance: true },
        { id: 2, name: 'Grace Atieno', group: 'Umoja A', centerId: 1, savings: 150, loanDue: 400, attendance: true },
        { id: 3, name: 'Faith Njeri', group: 'Umoja A', centerId: 1, savings: 300, loanDue: 600, attendance: false },
        { id: 4, name: 'Joy Wairimu', group: 'Umoja B', centerId: 1, savings: 100, loanDue: 350, attendance: true },
        { id: 5, name: 'Hope Muthoni', group: 'Umoja B', centerId: 1, savings: 250, loanDue: 450, attendance: true },
        { id: 6, name: 'Esther Akinyi', group: 'Umoja B', centerId: 1, savings: 175, loanDue: 0, attendance: true },
        { id: 7, name: 'Ruth Kemunto', group: 'Tumaini', centerId: 1, savings: 400, loanDue: 700, attendance: true },
        { id: 8, name: 'Sarah Chebet', group: 'Tumaini', centerId: 1, savings: 220, loanDue: 500, attendance: false },
        { id: 9, name: 'Agnes Wambui', group: 'Tumaini', centerId: 1, savings: 180, loanDue: 300, attendance: true },
        { id: 10, name: 'John Omondi', group: 'Nguvu A', centerId: 3, savings: 500, loanDue: 1000, attendance: true },
        { id: 11, name: 'Peter Kiprop', group: 'Nguvu A', centerId: 3, savings: 350, loanDue: 800, attendance: true },
        { id: 12, name: 'David Mwangi', group: 'Nguvu A', centerId: 3, savings: 200, loanDue: 600, attendance: true },
        { id: 13, name: 'Samuel Otieno', group: 'Nguvu B', centerId: 3, savings: 450, loanDue: 900, attendance: false },
        { id: 14, name: 'Daniel Kamau', group: 'Nguvu B', centerId: 3, savings: 300, loanDue: 750, attendance: true },
        { id: 15, name: 'Joseph Wekesa', group: 'Juhudi A', centerId: 3, savings: 275, loanDue: 500, attendance: true },
        { id: 16, name: 'Moses Rotich', group: 'Juhudi A', centerId: 3, savings: 600, loanDue: 1200, attendance: true },
        { id: 17, name: 'Paul Nyamweya', group: 'Juhudi B', centerId: 3, savings: 150, loanDue: 400, attendance: true },
        { id: 18, name: 'James Barasa', group: 'Juhudi B', centerId: 3, savings: 320, loanDue: 650, attendance: true },
        { id: 19, name: 'Hassan Ali', group: 'Bidii A', centerId: 4, savings: 400, loanDue: 800, attendance: true },
        { id: 20, name: 'Ahmed Yusuf', group: 'Bidii A', centerId: 4, savings: 250, loanDue: 500, attendance: true },
        { id: 21, name: 'Omar Sheikh', group: 'Bidii A', centerId: 4, savings: 300, loanDue: 600, attendance: false },
        { id: 22, name: 'Fatima Abdi', group: 'Bidii B', centerId: 4, savings: 175, loanDue: 350, attendance: true },
        { id: 23, name: 'Amina Hassan', group: 'Bidii B', centerId: 4, savings: 225, loanDue: 450, attendance: true }
    ],
    syncQueue: [],
    collectionEntries: {}
};

let store = {};

function loadStore() {
    try {
        const saved = localStorage.getItem(DB_KEY);
        store = saved ? JSON.parse(saved) : JSON.parse(JSON.stringify(defaultData));
    } catch(e) {
        store = JSON.parse(JSON.stringify(defaultData));
    }
    // Ensure syncQueue exists
    if (!store.syncQueue) store.syncQueue = [];
    if (!store.collectionEntries) store.collectionEntries = {};
}

function saveStore() {
    localStorage.setItem(DB_KEY, JSON.stringify(store));
}

// ===== INIT =====
loadStore();
renderDashboard();
renderSyncQueue();
populateCenterSelect();
updateTopBar();
setupOfflineDetection();
seedSyncQueueIfEmpty();

// ===== TAB NAVIGATION =====
document.querySelectorAll('.nav-tab').forEach(tab => {
    tab.addEventListener('click', () => switchTab(tab.dataset.tab));
});

function switchTab(tabId) {
    document.querySelectorAll('.nav-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.tab-content').forEach(c => c.classList.remove('active'));
    document.getElementById('tab-' + tabId).classList.add('active');
    document.getElementById('page-' + tabId).classList.add('active');
}

// ===== TOP BAR =====
function updateTopBar() {
    const pendingCount = store.syncQueue.filter(s => s.status === 'queued' || s.status === 'failed').length;
    document.getElementById('syncCount').textContent = pendingCount;
    if (pendingCount > 0) {
        document.getElementById('syncBadge').style.background = 'rgba(255,152,0,0.3)';
    } else {
        document.getElementById('syncBadge').style.background = 'rgba(255,255,255,0.15)';
    }
    if (store.lastSyncTime) {
        const ago = timeAgo(store.lastSyncTime);
        document.getElementById('lastSyncTime').textContent = 'Last sync: ' + ago;
    }
    updateNetworkUI(store.isOnline);
}

function updateNetworkUI(online) {
    const badge = document.getElementById('networkBadge');
    const dot = badge.querySelector('.net-dot');
    const label = badge.querySelector('.net-label');
    const banner = document.getElementById('offlineBanner');
    dot.classList.toggle('offline', !online);
    label.textContent = online ? 'Online' : 'Offline';
    banner.classList.toggle('show', !online);
    // Update connectivity card
    document.getElementById('connIcon').textContent = online ? '🟢' : '🔴';
    document.getElementById('connStatus').textContent = online ? 'Connected' : 'No Connection';
    document.getElementById('connDetail').textContent = online
        ? 'All sync operations will execute immediately'
        : 'Transactions will be queued for sync when connection restores';
    // Update metric
    document.getElementById('metricOfflineQueue').textContent = online ? 'Idle' : 'Active';
    const card = document.getElementById('metricOfflineCard');
    card.querySelector('.metric-value').style.color = online ? 'var(--success)' : 'var(--danger)';
}

// ===== OFFLINE DETECTION =====
function setupOfflineDetection() {
    window.addEventListener('online', () => { store.isOnline = true; saveStore(); updateNetworkUI(true); showToast('Connection restored — syncing...', 'success'); autoSync(); });
    window.addEventListener('offline', () => { store.isOnline = false; saveStore(); updateNetworkUI(false); showToast('Connection lost — offline mode active', 'warning'); });
}

// ===== DASHBOARD RENDERING =====
function renderDashboard() {
    // Metrics
    const pending = store.syncQueue.filter(s => s.status === 'queued' || s.status === 'failed').length;
    document.getElementById('metricPending').textContent = pending;
    document.getElementById('metricCenters').textContent = store.centers.length;
    document.getElementById('metricMeetings').textContent = store.meetings.length;

    // Centers
    const centersList = document.getElementById('centersList');
    centersList.innerHTML = store.centers.map(c => `
        <div class="center-card" onclick="switchTab('collections'); document.getElementById('centerSelect').value='${c.id}'; loadCollectionSheet();">
            <div>
                <div class="center-name">${c.name}</div>
                <div class="center-meta">
                    <span>${c.groups} groups</span>
                    <span>${c.members} members</span>
                    <span>${c.meetingDay}</span>
                </div>
            </div>
            <span class="center-status status-${c.status}">${c.status === 'collected' ? '✓ Collected' : c.status === 'pending' ? '⏳ Pending' : '⚠ Due'}</span>
        </div>
    `).join('');

    // Meetings
    const meetingsList = document.getElementById('meetingsList');
    meetingsList.innerHTML = store.meetings.map(m => `
        <div class="meeting-card" onclick="startMeetingForCenter(${m.centerId})">
            <div class="meeting-time">${m.time}</div>
            <div class="meeting-info">
                <div class="meeting-center">${m.centerName}</div>
                <div class="meeting-detail">${m.groupCount} groups · ${m.memberCount} members · ${m.status}</div>
            </div>
            <span class="center-status status-${m.status === 'scheduled' ? 'pending' : 'collected'}">${m.status}</span>
        </div>
    `).join('');
}

// ===== COLLECTION SHEET =====
function populateCenterSelect() {
    const sel = document.getElementById('centerSelect');
    store.centers.forEach(c => {
        const opt = document.createElement('option');
        opt.value = c.id;
        opt.textContent = c.name;
        sel.appendChild(opt);
    });
}

function loadCollectionSheet() {
    const centerId = parseInt(document.getElementById('centerSelect').value);
    if (!centerId) return;
    const members = store.members.filter(m => m.centerId === centerId);
    const body = document.getElementById('collectionBody');
    const saved = store.collectionEntries[centerId] || {};

    body.innerHTML = members.map(m => {
        const entry = saved[m.id] || {};
        const savingsVal = entry.savings !== undefined ? entry.savings : '';
        const loanVal = entry.loan !== undefined ? entry.loan : '';
        const status = entry.status || 'pending';
        return `
        <tr data-member-id="${m.id}">
            <td><div class="member-name">${m.name}</div><div class="member-group">${m.group}</div></td>
            <td><input type="number" min="0" step="0.01" placeholder="${m.savings}" value="${savingsVal}" onchange="onEntryChange(${centerId}, ${m.id}, 'savings', this)" id="sav-${m.id}"></td>
            <td><input type="number" min="0" step="0.01" placeholder="${m.loanDue}" value="${loanVal}" onchange="onEntryChange(${centerId}, ${m.id}, 'loan', this)" id="loan-${m.id}"></td>
            <td><span class="center-status status-${status}">${status}</span></td>
            <td><div class="row-actions">
                <button class="row-btn save-btn" title="Save" onclick="saveEntry(${centerId}, ${m.id})">
                    <svg width="14" height="14" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="20 6 9 17 4 12"/></svg>
                </button>
            </div></td>
        </tr>`;
    }).join('');

    document.getElementById('batchActions').style.display = 'flex';
    updateCollectionStats(centerId, members);
}

function onEntryChange(centerId, memberId, field, input) {
    if (!store.collectionEntries[centerId]) store.collectionEntries[centerId] = {};
    if (!store.collectionEntries[centerId][memberId]) store.collectionEntries[centerId][memberId] = { status: 'pending' };
    store.collectionEntries[centerId][memberId][field] = parseFloat(input.value) || 0;
    saveStore();
    updateCollectionStats(centerId, store.members.filter(m => m.centerId === centerId));
}

function saveEntry(centerId, memberId) {
    const savInput = document.getElementById('sav-' + memberId);
    const loanInput = document.getElementById('loan-' + memberId);
    if (!store.collectionEntries[centerId]) store.collectionEntries[centerId] = {};
    store.collectionEntries[centerId][memberId] = {
        savings: parseFloat(savInput.value) || 0,
        loan: parseFloat(loanInput.value) || 0,
        status: 'queued',
        timestamp: Date.now()
    };

    // Add to sync queue
    addToSyncQueue({
        type: 'COLLECTION',
        description: `Collection for member #${memberId} at center #${centerId}`,
        data: { centerId, memberId, savings: store.collectionEntries[centerId][memberId].savings, loan: store.collectionEntries[centerId][memberId].loan }
    });

    saveStore();
    loadCollectionSheet();
    showToast('Entry saved locally', 'success');
}

function saveCollectionSheet() {
    const centerId = parseInt(document.getElementById('centerSelect').value);
    if (!centerId) { showToast('Select a center first', 'warning'); return; }
    const members = store.members.filter(m => m.centerId === centerId);
    let saved = 0;
    members.forEach(m => {
        const savInput = document.getElementById('sav-' + m.id);
        const loanInput = document.getElementById('loan-' + m.id);
        if (savInput.value || loanInput.value) {
            if (!store.collectionEntries[centerId]) store.collectionEntries[centerId] = {};
            store.collectionEntries[centerId][m.id] = {
                savings: parseFloat(savInput.value) || 0,
                loan: parseFloat(loanInput.value) || 0,
                status: 'queued',
                timestamp: Date.now()
            };
            saved++;
        }
    });

    if (saved > 0) {
        addToSyncQueue({
            type: 'BATCH_COLLECTION',
            description: `Batch collection: ${saved} entries for ${store.centers.find(c=>c.id===centerId).name}`,
            data: { centerId, count: saved }
        });
        saveStore();
        loadCollectionSheet();
        showToast(`${saved} entries saved offline & queued for sync`, 'success');
    } else {
        showToast('No entries to save', 'warning');
    }
}

function validateAll() {
    const centerId = parseInt(document.getElementById('centerSelect').value);
    if (!centerId) return;
    let valid = true;
    const members = store.members.filter(m => m.centerId === centerId);
    members.forEach(m => {
        const savInput = document.getElementById('sav-' + m.id);
        const loanInput = document.getElementById('loan-' + m.id);
        [savInput, loanInput].forEach(input => {
            input.classList.remove('invalid', 'valid');
            if (input.value && parseFloat(input.value) >= 0) {
                input.classList.add('valid');
            } else if (input.value && parseFloat(input.value) < 0) {
                input.classList.add('invalid');
                valid = false;
            }
        });
    });
    showToast(valid ? 'All entries valid ✓' : 'Some entries have errors', valid ? 'success' : 'error');
}

function submitBatch() {
    saveCollectionSheet();
    if (store.isOnline) {
        showToast('Batch submitted — syncing now...', 'success');
        simulateSync();
    } else {
        showToast('Batch queued — will sync when online', 'warning');
    }
}

function updateCollectionStats(centerId, members) {
    const entries = store.collectionEntries[centerId] || {};
    let totalSav = 0, totalLoan = 0, filled = 0;
    members.forEach(m => {
        const e = entries[m.id];
        if (e) {
            totalSav += e.savings || 0;
            totalLoan += e.loan || 0;
            if (e.savings || e.loan) filled++;
        }
    });
    document.getElementById('cstatMembers').textContent = members.length;
    document.getElementById('cstatSavings').textContent = '$' + totalSav.toLocaleString();
    document.getElementById('cstatLoans').textContent = '$' + totalLoan.toLocaleString();
    document.getElementById('cstatComplete').textContent = members.length ? Math.round((filled/members.length)*100) + '%' : '0%';
}

// ===== SYNC QUEUE =====
function seedSyncQueueIfEmpty() {
    if (store.syncQueue.length === 0) {
        store.syncQueue = [
            { id: 1, type: 'COLLECTION', description: 'Collection for Mary Wanjiku — Kibera Center', status: 'queued', retries: 0, timestamp: Date.now() - 300000, data: {} },
            { id: 2, type: 'COLLECTION', description: 'Collection for Grace Atieno — Kibera Center', status: 'queued', retries: 0, timestamp: Date.now() - 240000, data: {} },
            { id: 3, type: 'ATTENDANCE', description: 'Attendance record — Dandora Center meeting', status: 'failed', retries: 2, timestamp: Date.now() - 600000, data: {} },
            { id: 4, type: 'LOAN_PAYMENT', description: 'Loan payment $500 — John Omondi', status: 'queued', retries: 0, timestamp: Date.now() - 180000, data: {} },
            { id: 5, type: 'MEMBER_UPDATE', description: 'Profile update — Hassan Ali', status: 'synced', retries: 0, timestamp: Date.now() - 900000, data: {} },
            { id: 6, type: 'BATCH_COLLECTION', description: 'Batch: 8 entries — Eastleigh Center', status: 'failed', retries: 3, timestamp: Date.now() - 1200000, data: {} },
            { id: 7, type: 'SAVINGS_DEPOSIT', description: 'Savings $200 — Ruth Kemunto', status: 'synced', retries: 0, timestamp: Date.now() - 1500000, data: {} },
        ];
        saveStore();
    }
    renderSyncQueue();
    updateTopBar();
    renderDashboard();
}

function addToSyncQueue(item) {
    const id = Date.now();
    store.syncQueue.unshift({
        id, type: item.type, description: item.description,
        status: 'queued', retries: 0, timestamp: Date.now(), data: item.data || {}
    });
    saveStore();
    renderSyncQueue();
    updateTopBar();
    renderDashboard();
}

function renderSyncQueue() {
    const list = document.getElementById('syncItemsList');
    const queued = store.syncQueue.filter(s => s.status === 'queued').length;
    const failed = store.syncQueue.filter(s => s.status === 'failed').length;
    const synced = store.syncQueue.filter(s => s.status === 'synced').length;
    const retries = store.syncQueue.reduce((a, s) => a + s.retries, 0);

    document.getElementById('syncQueued').textContent = queued;
    document.getElementById('syncFailed').textContent = failed;
    document.getElementById('syncSuccess').textContent = synced;
    document.getElementById('syncRetries').textContent = retries;

    if (store.syncQueue.length === 0) {
        list.innerHTML = '<div style="text-align:center;padding:40px;color:var(--text-secondary);">No items in sync queue</div>';
        return;
    }

    list.innerHTML = store.syncQueue.map(s => `
        <div class="sync-item">
            <div class="sync-item-info">
                <div class="sync-item-type">${s.type.replace(/_/g, ' ')}</div>
                <div class="sync-item-detail">${s.description}</div>
                <div class="sync-item-time">${timeAgo(s.timestamp)}${s.retries > 0 ? ' · ' + s.retries + ' retries' : ''}</div>
            </div>
            <div class="sync-item-right">
                <span class="center-status status-${s.status}">${s.status === 'synced' ? '✓ Synced' : s.status === 'failed' ? '✗ Failed' : s.status === 'syncing' ? '↻ Syncing' : '⏳ Queued'}</span>
                ${s.status === 'failed' ? `<button class="retry-btn" onclick="retrySync(${s.id})">Retry</button>` : ''}
            </div>
        </div>
    `).join('');
}

function retrySync(id) {
    const item = store.syncQueue.find(s => s.id === id);
    if (!item) return;
    item.status = 'syncing';
    item.retries++;
    saveStore();
    renderSyncQueue();
    showToast('Retrying sync...', 'info');

    // Simulate sync
    setTimeout(() => {
        if (store.isOnline && Math.random() > 0.3) {
            item.status = 'synced';
            showToast('Sync successful ✓', 'success');
        } else {
            item.status = 'failed';
            showToast('Sync failed — will retry', 'error');
        }
        store.lastSyncTime = Date.now();
        saveStore();
        renderSyncQueue();
        updateTopBar();
        renderDashboard();
    }, 1500);
}

function retrySyncAll() {
    const pending = store.syncQueue.filter(s => s.status === 'queued' || s.status === 'failed');
    if (pending.length === 0) { showToast('Nothing to sync', 'info'); return; }

    if (!store.isOnline) {
        showToast('Cannot sync — no connection', 'error');
        return;
    }

    // Show progress
    const wrap = document.getElementById('syncProgressWrap');
    wrap.style.display = 'block';
    const fill = document.getElementById('syncProgressFill');
    const pct = document.getElementById('syncProgressPct');

    pending.forEach(s => { s.status = 'syncing'; });
    saveStore();
    renderSyncQueue();

    let completed = 0;
    pending.forEach((s, i) => {
        setTimeout(() => {
            if (Math.random() > 0.2) {
                s.status = 'synced';
            } else {
                s.status = 'failed';
                s.retries++;
            }
            completed++;
            const percent = Math.round((completed / pending.length) * 100);
            fill.style.width = percent + '%';
            pct.textContent = percent + '%';

            saveStore();
            renderSyncQueue();
            updateTopBar();

            if (completed === pending.length) {
                store.lastSyncTime = Date.now();
                saveStore();
                updateTopBar();
                renderDashboard();
                const syncedCount = pending.filter(p => p.status === 'synced').length;
                showToast(`Sync complete: ${syncedCount}/${pending.length} succeeded`, syncedCount === pending.length ? 'success' : 'warning');
                setTimeout(() => { wrap.style.display = 'none'; fill.style.width = '0'; }, 2000);
            }
        }, (i + 1) * 800);
    });
}

function autoSync() {
    setTimeout(() => retrySyncAll(), 1000);
}

function simulateSync() {
    setTimeout(() => retrySyncAll(), 500);
}

// ===== OFFLINE DEMO FLOW =====
// Toggle offline mode for demonstration
document.getElementById('networkBadge').addEventListener('click', () => {
    store.isOnline = !store.isOnline;
    saveStore();
    updateNetworkUI(store.isOnline);
    if (store.isOnline) {
        showToast('Simulated: Back online — syncing queued items...', 'success');
        autoSync();
    } else {
        showToast('Simulated: Gone offline — changes will queue locally', 'warning');
    }
});

// ===== QUICK ACTION HANDLERS =====
function startMeeting() {
    showToast('Select a meeting from the dashboard to begin', 'info');
}

function startMeetingForCenter(centerId) {
    switchTab('collections');
    document.getElementById('centerSelect').value = centerId;
    loadCollectionSheet();
    showToast('Meeting started — enter collections below', 'success');
}

function addNewMember() {
    showToast('Member creation will be available in Phase 2', 'info');
}

// ===== TOAST NOTIFICATIONS =====
function showToast(msg, type = 'info') {
    const container = document.getElementById('toastContainer');
    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = msg;
    container.appendChild(toast);
    setTimeout(() => {
        toast.classList.add('removing');
        setTimeout(() => toast.remove(), 300);
    }, 3000);
}

// ===== UTILITIES =====
function timeAgo(ts) {
    const diff = Date.now() - ts;
    const mins = Math.floor(diff / 60000);
    if (mins < 1) return 'just now';
    if (mins < 60) return mins + 'm ago';
    const hrs = Math.floor(mins / 60);
    if (hrs < 24) return hrs + 'h ago';
    return Math.floor(hrs / 24) + 'd ago';
}

// Click sync badge to go to sync queue tab
document.getElementById('syncBadge').addEventListener('click', () => switchTab('syncqueue'));
