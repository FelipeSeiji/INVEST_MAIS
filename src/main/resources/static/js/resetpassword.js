const API_BASE = ""; 

async function handleForgotPassword() {
    const email = document.getElementById('forgotEmail').value;
    const msgDiv = document.getElementById('forgotMsg');
    const step1 = document.getElementById('step1');
    const step2 = document.getElementById('step2');

    if (!email) {
        showError(msgDiv, "Por favor, insira um e-mail.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/auth/forgot-password`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ email })
        });

        if (response.ok) {
            step1.classList.add('hidden');
            step2.classList.remove('hidden');
        } else {
            const data = await response.json();
            showError(msgDiv, data.message || data.detail || "Erro ao processar solicitação.");
        }
    } catch (error) {
        showError(msgDiv, "Erro de conexão com o servidor.");
    }
}

async function handleResetPassword() {
    const token = document.getElementById('resetToken').value;
    const newPassword = document.getElementById('newPassword').value;
    const confirmPassword = document.getElementById('confirmPassword').value;
    const msgDiv = document.getElementById('resetMsg');

    if (!token || !newPassword || !confirmPassword) {
        showError(msgDiv, "Preencha todos os campos.");
        return;
    }

    if (newPassword !== confirmPassword) {
        showError(msgDiv, "As senhas não coincidem.");
        return;
    }

    try {
        const response = await fetch(`${API_BASE}/auth/reset-password`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ 
                token: token, 
                newPassword: newPassword 
            })
        });

        if (response.ok) {
            msgDiv.style.color = "#28a745";
            msgDiv.innerText = "Senha alterada com sucesso! Redirecionando...";
            
            document.querySelector('#step2 button').disabled = true;

            setTimeout(() => {
                window.location.href = "index.html";
            }, 3000);
        } else {
            const data = await response.json();
            showError(msgDiv, data.message || data.detail || "Token inválido ou expirado.");
        }
    } catch (error) {
        showError(msgDiv, "Erro de conexão com o servidor.");
    }
}

function showError(element, message) {
    element.style.color = "#dc3545";
    element.innerText = message;
}