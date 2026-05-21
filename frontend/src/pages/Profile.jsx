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
          setFormData({
            name: data.name,
            email: data.email,
            currentPassword: ''
          });
        })
        .catch(err => {
          console.error(err);
        })
        .finally(() => setLoading(false));

    } catch (error) {
      console.error("Erro ao decodificar token:", error);
      alert("Sessão inválida. Por favor, faça login novamente.");
      navigate('/');
    }
  }, [navigate, token]);

  const [formData, setFormData] = useState({
    name: '',
    email: '',
    currentPassword: ''
  });
  const [isUpdating, setIsUpdating] = useState(false);
  const [updateError, setUpdateError] = useState('');
  const [updateSuccess, setUpdateSuccess] = useState('');

  const [passwords, setPasswords] = useState({
    current: '',
    new: '',
    confirm: ''
  });

  const handleProfileUpdate = (e) => {
    e.preventDefault();
    if (!formData.name || !formData.email || !formData.currentPassword) {
      setUpdateError("Todos os campos, incluindo a senha atual, são obrigatórios.");
      setUpdateSuccess("");
      return;
    }
    
    setIsUpdating(true);
    setUpdateError('');
    setUpdateSuccess('');

    fetch(`/api/users/${userId}`, {
      method: 'PUT',
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${token}`
      },
      body: JSON.stringify({
        name: formData.name,
        email: formData.email,
        currentPassword: formData.currentPassword
      })
    })
      .then(async res => {
        if (!res.ok) {
          const errData = await res.json().catch(() => ({}));
          throw new Error(errData.detail || errData.message || "Erro ao atualizar dados do usuário");
        }
        return res.json();
      })
      .then(data => {
        setUserData({
          name: data.name,
          email: data.email
        });
        setFormData(prev => ({
          ...prev,
          name: data.name,
          email: data.email,
          currentPassword: ''
        }));
        setUpdateSuccess("Perfil atualizado com sucesso!");
      })
      .catch(err => {
        console.error(err);
        setUpdateError(err.message);
      })
      .finally(() => setIsUpdating(false));
  };

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
          <form onSubmit={handleProfileUpdate} className="flex flex-col gap-4 flex-1">
            {updateError && (
              <div className="p-4 rounded-xl bg-red-50 border border-red-200 text-red-700 text-sm font-medium">
                {updateError}
              </div>
            )}
            {updateSuccess && (
              <div className="p-4 rounded-xl bg-green-50 border border-green-200 text-green-700 text-sm font-medium">
                {updateSuccess}
              </div>
            )}
            <div className="flex flex-col gap-1.5">
              <label htmlFor="name" className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Nome Completo</label>
              <input 
                type="text" 
                id="name"
                value={formData.name}
                onChange={(e) => setFormData({...formData, name: e.target.value})}
                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                required
              />
            </div>
            <div className="flex flex-col gap-1.5">
              <label htmlFor="email" className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">E-mail</label>
              <input 
                type="email" 
                id="email"
                value={formData.email}
                onChange={(e) => setFormData({...formData, email: e.target.value})}
                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                required
              />
            </div>
            <div className="flex flex-col gap-1.5">
              <label htmlFor="currentPasswordForUpdate" className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Senha Atual (Para Confirmar)</label>
              <input 
                type="password" 
                id="currentPasswordForUpdate"
                placeholder="Digite sua senha para salvar"
                value={formData.currentPassword}
                onChange={(e) => setFormData({...formData, currentPassword: e.target.value})}
                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200 m-0"
                required
              />
            </div>
            <button 
              type="submit" 
              disabled={isUpdating}
              className="bg-text-main text-white py-3 px-6 rounded-xl font-semibold transition-all duration-300 hover:bg-zinc-800 hover:shadow-md hover:-translate-y-0.5 active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed disabled:pointer-events-none self-start mt-2"
            >
              {isUpdating ? 'Salvando...' : 'Salvar Alterações'}
            </button>
          </form>
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
                onChange={(e) => setPasswords({ ...passwords, current: e.target.value })}
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
                onChange={(e) => setPasswords({ ...passwords, new: e.target.value })}
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
