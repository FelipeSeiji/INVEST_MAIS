document.addEventListener('DOMContentLoaded', () => {
    const token = localStorage.getItem('token');
    const userTokenElement = document.getElementById('userToken');
    const userNameElement = document.getElementById('userName');

    if (!token) {
        alert("Sessão expirada ou não encontrada. Faça login novamente.");
        window.location.href = "index.html";
        return;
    }

    userTokenElement.innerText = token;
});

async function handleLogout() {
    const token = localStorage.getItem('token');

    try {
        await fetch('/auth/logout', {
            method: 'POST',
            headers: { 
                'Authorization': `Bearer ${token}` 
            }
        });
    } catch (err) {
        console.warn("Servidor offline ou erro no logout, limpando dados locais...");
    } finally {
        localStorage.clear(); 
        window.location.href = "index.html";
    }
}