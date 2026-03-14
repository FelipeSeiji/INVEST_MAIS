const output = document.getElementById('output');
const authStatus = document.getElementById('authStatus');

function log(message, data = null) {
    let text = message;
    if (data) {
        text += '\n' + JSON.stringify(data, null, 2);
    }
    output.innerText = text;
    console.log(message, data);
}

function updateStatus() {
    const token = localStorage.getItem('jwt_token');
    if (token) {
        authStatus.innerText = "Logado (Token salvo)";
        authStatus.style.color = "green";
    } else {
        authStatus.innerText = "Deslogado";
        authStatus.style.color = "red";
    }
}

// REGISTRAR
async function register() {
    const name = document.getElementById('regName').value;
    const email = document.getElementById('regEmail').value;
    const password = document.getElementById('regPassword').value;

    log("Enviando requisição de registro...");

    try {
        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ name, email, password })
        });

        if (response.ok) {
            log("Registro realizado com sucesso!");
        } else {
            log(`Erro no registro. Status: ${response.status}`);
        }
    } catch (error) {
        log("Erro de conexão: " + error);
    }
}

// LOGIN
async function login() {
    const loginValue = document.getElementById('loginEmail').value;
    const password = document.getElementById('loginPassword').value;
    const totpCode = document.getElementById('loginTotp').value; 

    log("Enviando requisição de login...");

    try {
        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                login: loginValue, 
                password: password,
                totpCode: totpCode 
            })
        });

        if (response.ok) {
            const data = await response.json();
            localStorage.setItem('jwt_token', data.token); 
            log("Login efetuado! Token recebido:", data);
            updateStatus();
        } else if (response.status === 429) {
            const text = await response.text();
            log(`RATE LIMIT ATINGIDO (429): ${text}`);
        } else if (response.status === 403) {
            const text = await response.text();
            log(`2FA Necessário (403): ${text}`); 
        } else if (response.status === 401) {
            const text = await response.text();
            log(`Falha na autenticação (401): ${text || "Credenciais ou código 2FA inválidos"}`);
        } else {
            log(`Erro no login. Status: ${response.status}`);
        }
    } catch (error) {
        log("Erro de conexão: " + error);
    }
}

// LOGOUT
async function logout() {
    const token = localStorage.getItem('jwt_token');
    if (!token) {
        log("Nenhum token encontrado para fazer logout.");
        return;
    }

    log("Enviando requisição de logout...");

    try {
        const response = await fetch('/api/auth/logout', {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            log("Logout efetuado! Token invalidado no backend.");
            localStorage.removeItem('jwt_token');
            updateStatus();
        } else {
            log(`Erro no logout. Status: ${response.status}`);
        }
    } catch (error) {
        log("Erro de conexão: " + error);
    }
}

async function testSecureRoute() {
    const token = localStorage.getItem('jwt_token');
    log("Testando rota que não existe, mas exige autenticação...");

    try {
        const response = await fetch('/api/teste-qualquer', {
            method: 'GET',
            headers: token ? { 'Authorization': `Bearer ${token}` } : {}
        });

        if (response.status === 403 || response.status === 401) {
             log(`Acesso negado (${response.status}). O token é inválido, expirado ou inexistente.`);
        } else if (response.status === 404) {
             log("Acesso PERMITIDO pela segurança (Passou pelo Filtro)! Recebeu 404 porque a rota não existe.");
        } else {
             log(`Status retornado: ${response.status}`);
        }
    } catch (error) {
        log("Erro de conexão: " + error);
    }
}

// CONFIGURAR 2FA
async function setup2FA() {
    const token = localStorage.getItem('jwt_token');
    
    if (!token) {
        log("Erro: Você precisa fazer login primeiro para configurar o 2FA.");
        return;
    }

    log("Solicitando configuração de 2FA ao servidor...");

    try {
        const response = await fetch('/api/auth/setup-2fa', {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${token}`
            }
        });

        if (response.ok) {
            const qrCodeUrl = await response.text();
            log("URL do QR Code recebida com sucesso! Renderizando na tela...");

            const qrContainer = document.getElementById("qrcode");
            qrContainer.innerHTML = ""; 

            new QRCode(qrContainer, {
                text: qrCodeUrl,
                width: 150,
                height: 150
            });

            log("QR Code gerado! Escaneie com seu app (Google Authenticator/Authy).");
        } else {
            log(`Erro ao configurar 2FA. Status: ${response.status}`);
        }
    } catch (error) {
        log("Erro de conexão: " + error);
    }
}

updateStatus();