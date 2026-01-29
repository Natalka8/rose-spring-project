// src/main/resources/static/js/tasks.js
let tasks = [];

async function loadTasks() {
    try {
        const status = document.getElementById('statusFilter').value;
        const priority = document.getElementById('priorityFilter').value;
        const search = document.getElementById('searchTasks').value;

        const filters = {};
        if (status) filters.status = status;
        if (priority) filters.priority = priority;
        if (search) filters.search = search;

        tasks = await api.getTasks(filters);
        renderTasks();
    } catch (error) {
        console.error('Ошибка загрузки задач:', error);
        document.getElementById('tasksList').innerHTML =
            '<div class="error">Ошибка загрузки задач</div>';
    }
}

async function createTask() {
    const title = document.getElementById('taskTitle').value;
    const description = document.getElementById('taskDescription').value;
    const priority = document.getElementById('taskPriority').value;
    const dueDate = document.getElementById('taskDueDate').value;

    if (!title) {
        alert('Введите название задачи');
        return;
    }
// src/main/resources/static/js/tasks.js
    let tasks = [];

    async function loadTasks() {
        try {
            const status = document.getElementById('statusFilter').value;
            const priority = document.getElementById('priorityFilter').value;
            const search = document.getElementById('searchTasks').value;

            const filters = {};
            if (status) filters.status = status;
            if (priority) filters.priority = priority;
            if (search) filters.search = search;

            tasks = await api.getTasks(filters);
            renderTasks();
        } catch (error) {
            console.error('Ошибка загрузки задач:', error);
            document.getElementById('tasksList').innerHTML =
                '<div class="error">Ошибка загрузки задач</div>';
        }
    }

    async function createTask() {
        const title = document.getElementById('taskTitle').value;
        const description = document.getElementById('taskDescription').value;
        const priority = document.getElementById('taskPriority').value;
        const dueDate = document.getElementById('taskDueDate').value;

        if (!title) {
            alert('Введите название задачи');
            return;
        }

        try {
            const task = {
                title,
                description,
                priority,
                status: 'PENDING'
            };

            if (dueDate) {
                task.dueDate = dueDate;
            }

            await api.createTask(task);

            // Clear form
            document.getElementById('taskTitle').value = '';
            document.getElementById('taskDescription').value = '';
            document.getElementById('taskDueDate').value = '';

            // Update list
            loadTasks();
            loadStats();
        } catch (error) {
            alert('Ошибка создания задачи: ' + error.message);
        }
    }

    async function updateTaskStatus(id, newStatus) {
        try {
            const task = tasks.find(t => t.id === id);
            if (!task) return;

            await api.updateTask(id, { ...task, status: newStatus });
            loadTasks();
            loadStats();
        } catch (error) {
            alert('Ошибка обновления задачи: ' + error.message);
        }
    }

    async function deleteTask(id) {
        if (!confirm('Удалить задачу?')) return;

        try {
            await api.deleteTask(id);
            loadTasks();
            loadStats();
        } catch (error) {
            alert('Ошибка удаления задачи: ' + error.message);
        }
    }

    function renderTasks() {
        const container = document.getElementById('tasksList');

        if (!tasks || tasks.length === 0) {
            container.innerHTML = '<div class="no-tasks">Нет задач</div>';
            return;
        }

        container.innerHTML = tasks.map(task => `
        <div class="task-item" data-id="${task.id}">
            <div class="task-content">
                <h4>${escapeHtml(task.title)}</h4>
                ${task.description ? `<p>${escapeHtml(task.description)}</p>` : ''}
                <div class="task-meta">
                    <span class="task-priority priority-${task.priority}">
                        ${getPriorityLabel(task.priority)}
                    </span>
                    <span>Статус: ${getStatusLabel(task.status)}</span>
                    ${task.dueDate ? `<span>Срок: ${formatDate(task.dueDate)}</span>` : ''}
                    ${task.overdue ? '<span class="overdue">⏰ Просрочено!</span>' : ''}
                </div>
            </div>
            <div class="task-actions">
                ${task.status !== 'COMPLETED' ?
            `<button class="complete-btn" onclick="updateTaskStatus(${task.id}, 'COMPLETED')">
                        <i class="fas fa-check"></i> Завершить
                    </button>` : ''
        }
                <button class="edit-btn" onclick="editTask(${task.id})">
                    <i class="fas fa-edit"></i> Изменить
                </button>
                <button class="delete-btn" onclick="deleteTask(${task.id})">
                    <i class="fas fa-trash"></i> Удалить
                </button>
            </div>
        </div>
    `).join('');
    }

    async function loadStats() {
        try {
            // If there is an API for statistics
            const stats = await api.getStats().catch(() => null);

            if (stats) {
                document.getElementById('stats').innerHTML = `
                <div class="stat-item">
                    <div class="stat-value">${stats.total || tasks.length}</div>
                    <div class="stat-label">Всего задач</div>
                </div>
                <div class="stat-item">
                    <div class="stat-value">${stats.completed || tasks.filter(t => t.status === 'COMPLETED').length}</div>
                    <div class="stat-label">Выполнено</div>
                </div>
                <div class="stat-item">
                    <div class="stat-value">${stats.pending || tasks.filter(t => t.status === 'PENDING').length}</div>
                    <div class="stat-label">Ожидает</div>
                </div>
                <div class="stat-item">
                    <div class="stat-value">${stats.overdue || tasks.filter(t => t.overdue).length}</div>
                    <div class="stat-label">Просрочено</div>
                </div>
            `;
            }
        } catch (error) {
            console.error('Ошибка загрузки статистики:', error);
        }
    }

    function filterTasks() {
        loadTasks();
    }

    function showOverdue() {
        document.getElementById('statusFilter').value = '';
        document.getElementById('priorityFilter').value = '';
        document.getElementById('searchTasks').value = '';
        // Special API request or client-side filtering needed here
        loadTasks();
    }

// Helper functions
    function getPriorityLabel(priority) {
        const labels = {
            'LOW': 'Низкий',
            'MEDIUM': 'Средний',
            'HIGH': 'Высокий',
            'CRITICAL': 'Критический'
        };
        return labels[priority] || priority;
    }

    function getStatusLabel(status) {
        const labels = {
            'PENDING': 'Ожидание',
            'IN_PROGRESS': 'В работе',
            'COMPLETED': 'Завершено',
            'BLOCKED': 'Заблокировано',
            'CANCELLED': 'Отменено'
        };
        return labels[status] || status;
    }

    function formatDate(dateString) {
        const date = new Date(dateString);
        return date.toLocaleDateString('ru-RU') + ' ' + date.toLocaleTimeString('ru-RU', {
            hour: '2-digit',
            minute: '2-digit'
        });
    }

    function escapeHtml(text) {
        const div = document.createElement('div');
        div.textContent = text;
        return div.innerHTML;
    }

// Initialization
    document.addEventListener('DOMContentLoaded', () => {
        // Automatically load tasks if user is authenticated
        if (api.token) {
            loadTasks();
            loadStats();
        }

        // Autofill date (tomorrow)
        const tomorrow = new Date();
        tomorrow.setDate(tomorrow.getDate() + 1);
        tomorrow.setHours(23, 59, 0);

        const dateInput = document.getElementById('taskDueDate');
        dateInput.min = new Date().toISOString().slice(0, 16);
        dateInput.value = tomorrow.toISOString().slice(0, 16);
    });
    try {
        const task = {
            title,
            description,
            priority,
            status: 'PENDING'
        };

        if (dueDate) {
            task.dueDate = dueDate;
        }

        await api.createTask(task);

        // Очистить форму
        document.getElementById('taskTitle').value = '';
        document.getElementById('taskDescription').value = '';
        document.getElementById('taskDueDate').value = '';

        // Обновить список
        loadTasks();
        loadStats();
    } catch (error) {
        alert('Ошибка создания задачи: ' + error.message);
    }
}

async function updateTaskStatus(id, newStatus) {
    try {
        const task = tasks.find(t => t.id === id);
        if (!task) return;

        await api.updateTask(id, { ...task, status: newStatus });
        loadTasks();
        loadStats();
    } catch (error) {
        alert('Ошибка обновления задачи: ' + error.message);
    }
}

async function deleteTask(id) {
    if (!confirm('Удалить задачу?')) return;

    try {
        await api.deleteTask(id);
        loadTasks();
        loadStats();
    } catch (error) {
        alert('Ошибка удаления задачи: ' + error.message);
    }
}

function renderTasks() {
    const container = document.getElementById('tasksList');

    if (!tasks || tasks.length === 0) {
        container.innerHTML = '<div class="no-tasks">Нет задач</div>';
        return;
    }

    container.innerHTML = tasks.map(task => `
        <div class="task-item" data-id="${task.id}">
            <div class="task-content">
                <h4>${escapeHtml(task.title)}</h4>
                ${task.description ? `<p>${escapeHtml(task.description)}</p>` : ''}
                <div class="task-meta">
                    <span class="task-priority priority-${task.priority}">
                        ${getPriorityLabel(task.priority)}
                    </span>
                    <span>Статус: ${getStatusLabel(task.status)}</span>
                    ${task.dueDate ? `<span>Срок: ${formatDate(task.dueDate)}</span>` : ''}
                    ${task.overdue ? '<span class="overdue">⏰ Просрочено!</span>' : ''}
                </div>
            </div>
            <div class="task-actions">
                ${task.status !== 'COMPLETED' ?
        `<button class="complete-btn" onclick="updateTaskStatus(${task.id}, 'COMPLETED')">
                        <i class="fas fa-check"></i> Завершить
                    </button>` : ''
    }
                <button class="edit-btn" onclick="editTask(${task.id})">
                    <i class="fas fa-edit"></i> Изменить
                </button>
                <button class="delete-btn" onclick="deleteTask(${task.id})">
                    <i class="fas fa-trash"></i> Удалить
                </button>
            </div>
        </div>
    `).join('');
}

async function loadStats() {
    try {
        // Если есть API для статистики
        const stats = await api.getStats().catch(() => null);

        if (stats) {
            document.getElementById('stats').innerHTML = `
                <div class="stat-item">
                    <div class="stat-value">${stats.total || tasks.length}</div>
                    <div class="stat-label">Всего задач</div>
                </div>
                <div class="stat-item">
                    <div class="stat-value">${stats.completed || tasks.filter(t => t.status === 'COMPLETED').length}</div>
                    <div class="stat-label">Выполнено</div>
                </div>
                <div class="stat-item">
                    <div class="stat-value">${stats.pending || tasks.filter(t => t.status === 'PENDING').length}</div>
                    <div class="stat-label">Ожидает</div>
                </div>
                <div class="stat-item">
                    <div class="stat-value">${stats.overdue || tasks.filter(t => t.overdue).length}</div>
                    <div class="stat-label">Просрочено</div>
                </div>
            `;
        }
    } catch (error) {
        console.error('Ошибка загрузки статистики:', error);
    }
}

function filterTasks() {
    loadTasks();
}

function showOverdue() {
    document.getElementById('statusFilter').value = '';
    document.getElementById('priorityFilter').value = '';
    document.getElementById('searchTasks').value = '';
    // Здесь нужен специальный API запрос или фильтрация на клиенте
    loadTasks();
}

// Вспомогательные функции
function getPriorityLabel(priority) {
    const labels = {
        'LOW': 'Низкий',
        'MEDIUM': 'Средний',
        'HIGH': 'Высокий',
        'CRITICAL': 'Критический'
    };
    return labels[priority] || priority;
}

function getStatusLabel(status) {
    const labels = {
        'PENDING': 'Ожидание',
        'IN_PROGRESS': 'В работе',
        'COMPLETED': 'Завершено',
        'BLOCKED': 'Заблокировано',
        'CANCELLED': 'Отменено'
    };
    return labels[status] || status;
}

function formatDate(dateString) {
    const date = new Date(dateString);
    return date.toLocaleDateString('ru-RU') + ' ' + date.toLocaleTimeString('ru-RU', {
        hour: '2-digit',
        minute: '2-digit'
    });
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Инициализация
document.addEventListener('DOMContentLoaded', () => {
    // Автоматически загружаем задачи если пользователь авторизован
    if (api.token) {
        loadTasks();
        loadStats();
    }

    // Автозаполнение даты (завтрашний день)
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    tomorrow.setHours(23, 59, 0);

    const dateInput = document.getElementById('taskDueDate');
    dateInput.min = new Date().toISOString().slice(0, 16);
    dateInput.value = tomorrow.toISOString().slice(0, 16);
});