// ===== JWT COOKIE HELPERS =====
function setToken(token) {
    document.cookie = 'jwt=' + token + '; path=/; max-age=86400; SameSite=Lax';
}

function getToken() {
    const cookies = document.cookie.split(';');
    for (let c of cookies) {
        c = c.trim();
        if (c.startsWith('jwt=')) {
            return c.substring(4);
        }
    }
    return null;
}

function clearToken() {
    document.cookie = 'jwt=; path=/; max-age=0';
}

// ===== API HELPER =====
async function apiCall(url, method, body) {
    const token = getToken();
    const headers = { 'Content-Type': 'application/json' };
    if (token) {
        headers['Authorization'] = 'Bearer ' + token;
    }
    const options = { method: method, headers: headers };
    if (body) {
        options.body = JSON.stringify(body);
    }
    const response = await fetch(url, options);
    if (response.status === 401 || response.status === 403) {
        clearToken();
        window.location.href = '/login';
        return null;
    }
    return response;
}

// ===== SIGNUP =====
async function handleSignup(event) {
    event.preventDefault();
    const name = document.getElementById('name').value.trim();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const alertDiv = document.getElementById('alert');

    if (!name || !email || !password) {
        showAlert(alertDiv, 'Please fill in all fields', 'danger');
        return;
    }

    try {
        const res = await fetch('/auth/signup', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });
        const data = await res.json();
        if (res.ok) {
            setToken(data.token);
            window.location.href = '/dashboard';
        } else {
            showAlert(alertDiv, data.message || 'Signup failed', 'danger');
        }
    } catch (err) {
        showAlert(alertDiv, 'Network error. Please try again.', 'danger');
    }
}

// ===== LOGIN =====
async function handleLogin(event) {
    event.preventDefault();
    const email = document.getElementById('email').value.trim();
    const password = document.getElementById('password').value;
    const alertDiv = document.getElementById('alert');

    if (!email || !password) {
        showAlert(alertDiv, 'Please fill in all fields', 'danger');
        return;
    }

    try {
        const res = await fetch('/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });
        const data = await res.json();
        if (res.ok) {
            setToken(data.token);
            window.location.href = '/dashboard';
        } else {
            showAlert(alertDiv, data.message || 'Invalid credentials', 'danger');
        }
    } catch (err) {
        showAlert(alertDiv, 'Network error. Please try again.', 'danger');
    }
}

// ===== LOGOUT =====
function logout() {
    clearToken();
    window.location.href = '/login';
}

// ===== ALERT =====
function showAlert(element, message, type) {
    if (element) {
        element.className = 'alert alert-' + type;
        element.textContent = message;
        element.style.display = 'block';
        setTimeout(function() { element.style.display = 'none'; }, 5000);
    }
}

// ===== CREATE PROJECT =====
async function handleCreateProject(event) {
    event.preventDefault();
    const name = document.getElementById('projectName').value.trim();
    const description = document.getElementById('projectDesc').value.trim();
    const alertDiv = document.getElementById('modalAlert');

    if (!name) {
        showAlert(alertDiv, 'Project name is required', 'danger');
        return;
    }

    try {
        const res = await apiCall('/api/projects', 'POST', { name, description });
        if (res && res.ok) {
            window.location.reload();
        } else {
            const data = await res.json();
            showAlert(alertDiv, data.message || 'Failed to create project', 'danger');
        }
    } catch (err) {
        showAlert(alertDiv, 'Network error', 'danger');
    }
}

// ===== ADD MEMBER =====
async function handleAddMember(event, projectId) {
    event.preventDefault();
    const email = document.getElementById('memberEmail').value.trim();
    const role = document.getElementById('memberRole').value;
    const alertDiv = document.getElementById('memberAlert');

    if (!email) {
        showAlert(alertDiv, 'Email is required', 'danger');
        return;
    }

    try {
        const res = await apiCall('/api/projects/' + projectId + '/add-member', 'POST', { email, role });
        if (res && res.ok) {
            showAlert(alertDiv, 'Member added successfully!', 'success');
            document.getElementById('memberEmail').value = '';
            setTimeout(function() { window.location.reload(); }, 1000);
        } else {
            const data = await res.json();
            showAlert(alertDiv, data.message || 'Failed to add member', 'danger');
        }
    } catch (err) {
        showAlert(alertDiv, 'Network error', 'danger');
    }
}

// ===== CREATE TASK =====
async function handleCreateTask(event, projectId) {
    event.preventDefault();
    const title = document.getElementById('taskTitle').value.trim();
    const description = document.getElementById('taskDesc').value.trim();
    const dueDate = document.getElementById('taskDueDate').value;
    const assignedToId = document.getElementById('taskAssignee').value;
    const alertDiv = document.getElementById('taskAlert');

    if (!title) {
        showAlert(alertDiv, 'Task title is required', 'danger');
        return;
    }

    const body = {
        title: title,
        description: description,
        projectId: projectId
    };
    if (dueDate) body.dueDate = dueDate;
    if (assignedToId) body.assignedToId = parseInt(assignedToId);

    try {
        const res = await apiCall('/api/tasks', 'POST', body);
        if (res && res.ok) {
            window.location.reload();
        } else {
            const data = await res.json();
            showAlert(alertDiv, data.message || 'Failed to create task', 'danger');
        }
    } catch (err) {
        showAlert(alertDiv, 'Network error', 'danger');
    }
}

// ===== UPDATE TASK STATUS =====
async function updateTaskStatus(taskId, newStatus) {
    try {
        const res = await apiCall('/api/tasks/' + taskId + '/status', 'PUT', { status: newStatus });
        if (res && res.ok) {
            window.location.reload();
        }
    } catch (err) {
        console.error('Failed to update status:', err);
    }
}

// ===== MODAL CONTROLS =====
function openModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.add('active');
}

function closeModal(id) {
    const modal = document.getElementById(id);
    if (modal) modal.classList.remove('active');
}

// Close modals on overlay click
document.addEventListener('click', function(e) {
    if (e.target.classList.contains('modal-overlay')) {
        e.target.classList.remove('active');
    }
});

// Close modals on Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        document.querySelectorAll('.modal-overlay.active').forEach(function(m) {
            m.classList.remove('active');
        });
    }
});
