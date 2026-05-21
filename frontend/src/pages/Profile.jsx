import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';

export default function Profile() {
  const navigate = useNavigate();
  const [token] = useState(() => localStorage.getItem('token') || '');

  const [userData, setUserData] = useState({
    name: 'Carregando...',
    email: 'carregando...'
  });

  const [loading, setLoading] = useState(true);
  const [userId, setUserId] = useState(null);

  useEffect(() => {
    if (!token) {
      alert("Sessão expirada ou não encontrada. Faça login novamente.");
      navigate('/');
      return;
    }

    try {
      // Decodificar o token para pegar o ID do usuário (subject)
      const payloadBase64 = token.split('.')[1];
      const decodedPayload = JSON.parse(atob(payloadBase64));
      const subId = decodedPayload.sub;

      if (!subId) {
        throw new Error("ID do usuário não encontrado no token.");
      }
      
      setUserId(subId);

      fetch(`/api/users/${subId}`, {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      })
        .then(res => {
          if (!res.ok) throw new Error("Erro ao buscar dados do usuário");
          return res.json();
        })
        .then(data => {
          setUserData({
            name: data.name,
            email: data.email
          });
        })
        .catch(err => {
          console.error(err);
          // Opcional: mostrar erro na tela em vez de alert
        })
        .finally(() => setLoading(false));

    } catch (error) {
      console.error("Erro ao decodificar token:", error);
      alert("Sessão inválida. Por favor, faça login novamente.");
      navigate('/');
    }
  }, [navigate, token]);

  const [passwords, setPasswords] = useState({
    current: '',
    new: '',
    confirm: ''
  });

  const handlePasswordChange = (e) => {
    e.preventDefault();
    alert('Funcionalidade de alteração de senha em breve!');
  };

  if (loading) {
    return (
      <div className="p-10 max-w-[1000px] mx-auto text-left flex justify-center items-center min-h-[50vh] font-sans">
        <header className="w-full text-center">
          <h1 className="text-2xl font-semibold text-text-muted animate-pulse">Carregando perfil...</h1>
        </header>
      </div>
    );
  }

  return (
    <div className="p-10 max-w-[1000px] mx-auto text-left animate-in fade-in slide-in-from-bottom-2 duration-500 font-sans">
      <header className="flex flex-col sm:flex-row justify-between items-start sm:items-center gap-6 mb-10">
        <div>
          <h1 className="m-0 text-3xl font-extrabold tracking-tight bg-gradient-to-br from-text-main to-zinc-500 bg-clip-text text-transparent">Meu Perfil</h1>
          <p className="text-sm text-text-muted mt-1 m-0">Gerencie suas informações</p>
        </div>
      </header>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-8">
        <section className="bg-bg-card border border-zinc-200/80 rounded-2xl p-8 shadow-sm hover:border-text-main hover:shadow-md transition-all duration-300 flex flex-col">
          <h3 className="m-0 text-xl font-bold text-zinc-900 mb-6 pb-3 border-b border-zinc-100">Informações Básicas</h3>
          <div className="mb-5">
            <label className="block text-xs font-semibold text-zinc-500 uppercase tracking-wider mb-1.5">Nome Completo</label>
            <p className="m-0 text-base text-zinc-800 font-medium">{userData.name}</p>
          </div>
          <div className="mb-5 last:mb-0">
            <label className="block text-xs font-semibold text-zinc-500 uppercase tracking-wider mb-1.5">E-mail</label>
            <p className="m-0 text-base text-zinc-800 font-medium">{userData.email}</p>
          </div>
        </section>

        <section className="bg-bg-card border border-zinc-200/80 rounded-2xl p-8 shadow-sm hover:border-text-main hover:shadow-md transition-all duration-300 flex flex-col">
          <h3 className="m-0 text-xl font-bold text-zinc-900 mb-6 pb-3 border-b border-zinc-100">Segurança</h3>
          <form onSubmit={handlePasswordChange} className="flex flex-col gap-4">
            <div className="flex flex-col gap-1.5">
              <label htmlFor="current" className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Senha Atual</label>
              <input 
                type="password" 
                id="current"
                placeholder="••••••••"
                value={passwords.current}
                onChange={(e) => setPasswords({...passwords, current: e.target.value})}
                disabled
                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0 disabled:opacity-60 disabled:bg-zinc-100/50 disabled:cursor-not-allowed"
              />
            </div>
            <div className="flex flex-col gap-1.5">
              <label htmlFor="new" className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Nova Senha</label>
              <input 
                type="password" 
                id="new"
                placeholder="Nova senha"
                value={passwords.new}
                onChange={(e) => setPasswords({...passwords, new: e.target.value})}
                disabled
                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0 disabled:opacity-60 disabled:bg-zinc-100/50 disabled:cursor-not-allowed"
              />
            </div>
            <button 
              type="submit" 
              disabled 
              className="bg-text-main text-white py-3 px-6 rounded-xl font-semibold transition-all duration-300 hover:bg-zinc-800 hover:shadow-md hover:-translate-y-0.5 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed disabled:pointer-events-none self-start mt-2"
            >
              Atualizar Senha
            </button>
          </form>
        </section>
      </div>
    </div>
  );
}
