import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import './Profile.css';

export default function Profile() {
  const navigate = useNavigate();
  const [token] = useState(() => localStorage.getItem('token') || '');

  useEffect(() => {
    if (!token) {
      alert("Sessão expirada ou não encontrada. Faça login novamente.");
      navigate('/');
    }
  }, [navigate, token]);

  const [userData, setUserData] = useState({
    name: 'Usuário',
    email: 'usuario@email.com'
  });

  const [passwords, setPasswords] = useState({
    current: '',
    new: '',
    confirm: ''
  });

  const handlePasswordChange = (e) => {
    e.preventDefault();
    alert('Funcionalidade de alteração de senha seria chamada aqui!');
  };

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
          <h3>Informações Básicas WIP</h3>
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
          <h3>Segurança WIP</h3>
          <form onSubmit={handlePasswordChange} className="profile-form">
            <div className="form-group">
              <label htmlFor="current">Senha Atual</label>
              <input 
                type="password" 
                id="current"
                placeholder="••••••••"
              />
            </div>
            <div className="form-group">
              <label htmlFor="new">Nova Senha</label>
              <input 
                type="password" 
                id="new"
                placeholder="Nova senha"
              />
            </div>
            <button type="submit" className="btn-save">Atualizar Senha</button>
          </form>
        </section>
      </div>
    </div>
  );
}
