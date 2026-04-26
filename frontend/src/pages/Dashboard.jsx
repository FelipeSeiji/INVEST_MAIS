import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Dashboard.css';

export default function Dashboard() {
    const navigate = useNavigate();
    const [token] = useState(() => localStorage.getItem('token') || '');

    useEffect(() => {
        if (!token) {
            alert("Sessão expirada ou não encontrada. Faça login novamente.");
            navigate('/');
        }
    }, [navigate, token]);

    return (
        <>
            <main className="token-section">
                <h2>Autenticação Ativa (JWT)</h2>
                <div className="token-container">
                    <span id="userToken">{token || 'Verificando credenciais...'}</span>
                </div>
            </main>
        </>
    );
}
