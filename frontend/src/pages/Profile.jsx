import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Profile.css';

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
      <div className="profile-container">
        <header className="profile-header">
          <div className="profile-title">
            <h1>Carregando perfil...</h1>
          </div>
        </header>
      </div>
    );
  }

  return (
    <div className="profile-container">
      <header className="profile-header">
        <div className="profile-title">
          <h1>Meu Perfil</h1>
          <p>Gerencie suas informações</p>
        </div>
      </header>

      <div className="profile-grid">
        <section className="profile-card">
          <h3>Informações Básicas</h3>
          <div className="info-group">
            <label>Nome Completo</label>
            <p>{userData.name}</p>
          </div>
          <div className="info-group">
            <label>E-mail</label>
            <p>{userData.email}</p>
          </div>
        </section>

        <section className="profile-card">
          <h3>Segurança</h3>
          <form onSubmit={handlePasswordChange} className="profile-form">
            <div className="form-group">
              <label htmlFor="current">Senha Atual</label>
              <input 
                type="password" 
                id="current"
                placeholder="••••••••"
                value={passwords.current}
                onChange={(e) => setPasswords({...passwords, current: e.target.value})}
                disabled
              />
            </div>
            <div className="form-group">
              <label htmlFor="new">Nova Senha</label>
              <input 
                type="password" 
                id="new"
                placeholder="Nova senha"
                value={passwords.new}
                onChange={(e) => setPasswords({...passwords, new: e.target.value})}
                disabled
              />
            </div>
            <button type="submit" className="btn-save" disabled>Atualizar Senha</button>
          </form>
        </section>
      </div>
    </div>
  );
}
