const API_BASE = "";

async function handleLogin() {
    const email = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    const msgDiv = document.getElementById('loginMsg');
    
    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, password })
        });

        const data = await response.json();

        if (response.ok) {
            msgDiv.style.color = "green";
            msgDiv.innerText = "Código 2FA enviado ao seu e-mail!";
            document.getElementById('2faSection').style.display = 'block';
        } else {
            msgDiv.style.color = "red";
            msgDiv.innerText = data.detail || "Erro nas credenciais.";
        }
    } catch (error) {
        msgDiv.innerText = "Erro de conexão com o servidor.";
    }
}

async function handleVerify2FA() {
    const email = document.getElementById('loginEmail').value;
    const code = document.getElementById('2faCode').value;

    try {
        const response = await fetch(`${API_BASE}/auth/verify-2fa`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email, code })
        });

        const data = await response.json();

        if (response.ok) {
            localStorage.setItem('token', data.token);
            window.location.href = "dashboard.html";
        } else {
            alert("Código inválido ou expirado.");
        }
    } catch (error) {
        alert("Erro na verificação.");
    }
}

async function handleRegister() {
    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;
    const msgDiv = document.getElementById('regMsg');

    try {
        const response = await fetch(`${API_BASE}/api/users`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        if (response.ok) {
            msgDiv.style.color = "green";
            msgDiv.innerText = "Cadastro realizado! Faça login acima.";
        } else {
            const data = await response.json();
            msgDiv.style.color = "red";
            msgDiv.innerText = data.detail || "Erro no cadastro.";
        }
    } catch (error) {
        msgDiv.innerText = "Erro de rede.";
    }
}
