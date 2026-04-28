import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

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
        <main className="flex-1 p-8 bg-zinc-50 dark:bg-zinc-950">
            <div className="max-w-7xl mx-auto space-y-6">
                <header className="flex flex-col gap-2">
                    <h2 className="text-3xl font-bold text-zinc-900 dark:text-white">Dashboard</h2>
                    <p className="text-zinc-500 dark:text-zinc-400">Autenticação Ativa (JWT)</p>
                </header>

                <div className="bg-white dark:bg-zinc-900 border border-zinc-200 dark:border-zinc-800 rounded-2xl p-6 shadow-sm">
                    <h3 className="text-lg font-medium text-zinc-900 dark:text-white mb-4">Sessão Atual</h3>
                    <div className="bg-zinc-50 dark:bg-zinc-950 border border-zinc-200 dark:border-zinc-800 rounded-xl p-4 break-all">
                        <code id="userToken" className="text-sm font-mono text-zinc-600 dark:text-zinc-400">
                            {token || 'Verificando credenciais...'}
                        </code>
                    </div>
                </div>
            </div>
        </main>
    );
}
