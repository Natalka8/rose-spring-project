// src/main/resources/static/js/auth.js
function showLogin() {
    document.getElementById('authForms').style.display = 'block';
    document.getElementById('loginForm').style.display = 'block';
    document.getElementById('registerForm').style.display = 'none';
}

function showRegister() {
    document.getElementById('authForms').style.display = 'block';
    document.getElementById('loginForm').style.display = 'none';
    document.getElementById('registerForm').style.display = 'block';
}

function hideForms() {
    document.getElementById('authForms').style.display = 'none';
}

async function login() {
    const username = document.getElementById('loginUsername').value;
    const password = document.getElementById('loginPassword').value;

    if (!username || !password) {
        alert('Пожалуйста, заполните все поля');
        return;
    }

    try {
        const result = await api.login(username, password);
        api.setToken(result.token);

        hideForms();
        document.getElementById('authButtons').style.display = 'none';
        document.getElementById('userInfo').style.display = 'flex';
        document.getElementById('username').textContent = username;
        document.getElementById('mainInterface').style.display = 'block';

        loadTasks();
        loadStats();
    } catch (error) {
        alert('Ошибка входа: ' + error.message);
    }
}

async function register() {
    const username = document.getElementById('registerUsername').value;
    const email = document.getElementById('registerEmail').value;
    const password = document.getElementById('registerPassword').value;

    if (!username || !email || !password) {
        alert('Пожалуйста, заполните все поля');
        return;
    }

    try {
        await api.register(username, email, password);
        alert('Регистрация успешна! Теперь войдите в систему.');
        showLogin();
    } catch (error) {
        alert('Ошибка регистрации: ' + error.message);
    }
}

function logout() {
    api.clearToken();
    document.getElementById('authButtons').style.display = 'block';
    document.getElementById('userInfo').style.display = 'none';
    document.getElementById('mainInterface').style.display = 'none';
    document.getElementById('tasksList').innerHTML = '';
}

// Проверка существующей сессии
document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('jwtToken');
    const username = localStorage.getItem('username');

    if (token && username) {
        api.setToken(token);
        document.getElementById('authButtons').style.display = 'none';
        document.getElementById('userInfo').style.display = 'flex';
        document.getElementById('username').textContent = username;
        document.getElementById('mainInterface').style.display = 'block';
        loadTasks();
        loadStats();
    }
});