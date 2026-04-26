import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Auth.css';

export default function ResetPassword() {
    const navigate = useNavigate();
    
    const [step, setStep] = useState(1);
    const [email, setEmail] = useState('');
    const [token, setToken] = useState('');
    const [newPassword, setNewPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [msg, setMsg] = useState({ text: '', color: '' });
    const [isSubmitting, setIsSubmitting] = useState(false);

    const handleForgotPassword = async () => {
        if (!email) {
            setMsg({ text: "Por favor, insira um e-mail.", color: "red" });
            return;
        }

        setIsSubmitting(true);
        setMsg({ text: "Enviando código...", color: "gray" });

        try {
            const response = await fetch('/auth/forgot-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email })
            });

            if (response.ok) {
                setStep(2);
                setMsg({ text: "Código enviado! Verifique seu e-mail.", color: "green" });
            } else {
                const data = await response.json();
                setMsg({ text: data.message || data.detail || "Erro ao processar solicitação.", color: "red" });
            }
        } catch (error) {
            console.error(error);
            setMsg({ text: "Erro de conexão com o servidor.", color: "red" });
        } finally {
            setIsSubmitting(false);
        }
    };

    const handleResetPassword = async () => {
        if (!token || !newPassword || !confirmPassword) {
            setMsg({ text: "Preencha todos os campos.", color: "red" });
            return;
        }

        if (newPassword !== confirmPassword) {
            setMsg({ text: "As senhas não coincidem.", color: "red" });
            return;
        }

        setIsSubmitting(true);
        setMsg({ text: "Processando...", color: "gray" });

        try {
            const response = await fetch('/auth/reset-password', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ 
                    token: token, 
                    newPassword: newPassword 
                })
            });

            if (response.ok) {
                setMsg({ text: "Senha alterada com sucesso! Redirecionando...", color: "green" });
                
                setTimeout(() => {
                    navigate('/');
                }, 3000);
            } else {
                const data = await response.json();
                setMsg({ text: data.message || data.detail || "Token inválido ou expirado.", color: "red" });
                setIsSubmitting(false);
            }
        } catch (error) {
            console.error(error);
            setMsg({ text: "Erro de conexão com o servidor.", color: "red" });
            setIsSubmitting(false);
        }
    };

    return (
        <div className="container" style={{ maxWidth: '500px' }}>
            <div className="box step-box">
                <h2>Recuperar Senha</h2>
                
                {step === 1 && (
                    <div id="step1" style={{ display: 'flex', flexDirection: 'column', gap: '15px' }}>
                        <p>Digite seu e-mail para receber o código de recuperação.</p>
                        <div style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                            <input 
                                type="email" 
                                value={email} 
                                onChange={(e) => setEmail(e.target.value)} 
                                placeholder="E-mail" 
                                style={{ flex: 1, margin: 0 }}
                            />
                            <button 
                                onClick={handleForgotPassword} 
                                disabled={isSubmitting}
                                style={{ margin: 0, whiteSpace: 'nowrap' }}
                            >
                                {isSubmitting ? 'Enviando...' : 'Enviar Código'}
                            </button>
                        </div>
                        <div className="message" style={{ color: msg.color, minHeight: '20px' }}>{msg.text}</div>
                    </div>
                )}

                {step === 2 && (
                    <div id="step2" style={{ display: 'flex', flexDirection: 'column', gap: '15px', alignItems: 'center' }}>
                        <p style={{ color: '#27272a', fontWeight: 'bold' }}>Código enviado! Verifique seu e-mail.</p>
                        <input 
                            type="text" 
                            value={token} 
                            onChange={(e) => setToken(e.target.value)} 
                            placeholder="Digite o Token recebido" 
                            style={{ width: '100%', maxWidth: '320px' }}
                        />
                        <input 
                            type="password" 
                            value={newPassword} 
                            onChange={(e) => setNewPassword(e.target.value)} 
                            placeholder="Nova Senha" 
                            style={{ width: '100%', maxWidth: '320px' }}
                        />
                        <input 
                            type="password" 
                            value={confirmPassword} 
                            onChange={(e) => setConfirmPassword(e.target.value)} 
                            placeholder="Confirmar Nova Senha" 
                            style={{ width: '100%', maxWidth: '320px' }}
                        />
                        <button 
                            onClick={handleResetPassword} 
                            disabled={isSubmitting} 
                            style={{ backgroundColor: '#27272a', width: '100%', maxWidth: '320px' }}
                        >
                            Alterar Senha
                        </button>
                        <div className="message" style={{ color: msg.color }}>{msg.text}</div>
                    </div>
                )}

                <br />
                <Link to="/" style={{ textAlign: 'center', color: '#4b4b4b', textDecoration: 'none', display: 'block' }}>
                    Voltar ao Login
                </Link>
            </div>
        </div>
    );
}
