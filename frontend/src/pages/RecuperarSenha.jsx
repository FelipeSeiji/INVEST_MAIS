import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';

export default function RecuperarSenha() {
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

    const getMsgColorClass = (color) => {
        if (color === 'red') return 'text-red-500 bg-red-50 border-red-200';
        if (color === 'green') return 'text-green-600 bg-green-50 border-green-200';
        return 'text-zinc-600 bg-zinc-50 border-zinc-200';
    };

    return (
        <div className="w-full max-w-[480px] bg-bg-card rounded-3xl border border-zinc-200/80 p-8 sm:p-10 shadow-2xl transition-all duration-300 font-sans mx-auto flex flex-col">
            <h2 className="text-3xl font-extrabold tracking-tight bg-gradient-to-br from-text-main to-zinc-500 bg-clip-text text-transparent mb-6 text-center">Recuperar Senha</h2>
            
            {step === 1 && (
                <div id="step1" className="flex flex-col gap-4">
                    <p className="text-sm text-text-muted mb-2 mt-0 text-center">Digite seu e-mail para receber o código de recuperação.</p>
                    <div className="flex flex-col gap-3">
                        <input 
                            type="email" 
                            value={email} 
                            onChange={(e) => setEmail(e.target.value)} 
                            placeholder="E-mail" 
                            required
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                        />
                        <button 
                            onClick={handleForgotPassword} 
                            disabled={isSubmitting}
                            className="w-full bg-text-main hover:bg-zinc-800 text-white py-3 px-6 rounded-xl font-semibold transition-all duration-300 hover:shadow-md hover:-translate-y-0.5 active:scale-[0.98] cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed flex items-center justify-center gap-2"
                        >
                            {isSubmitting ? 'Enviando...' : 'Enviar Código'}
                        </button>
                    </div>
                    {msg.text && (
                        <div className={`p-3 rounded-xl border text-xs font-semibold text-center transition-all duration-300 ${getMsgColorClass(msg.color)}`}>
                            {msg.text}
                        </div>
                    )}
                </div>
            )}

            {step === 2 && (
                <div id="step2" className="flex flex-col gap-4">
                    <p className="text-sm font-semibold text-green-600 bg-green-50 border border-green-200 px-4 py-3 rounded-xl text-center mb-2">Código enviado! Verifique seu e-mail.</p>
                    <input 
                        type="text" 
                        value={token} 
                        onChange={(e) => setToken(e.target.value)} 
                        placeholder="Digite o Token recebido" 
                        required
                        className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                    />
                    <input 
                        type="password" 
                        value={newPassword} 
                        onChange={(e) => setNewPassword(e.target.value)} 
                        placeholder="Nova Senha" 
                        required
                        className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                    />
                    <input 
                        type="password" 
                        value={confirmPassword} 
                        onChange={(e) => setConfirmPassword(e.target.value)} 
                        placeholder="Confirmar Nova Senha" 
                        required
                        className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                    />
                    <button 
                        onClick={handleResetPassword} 
                        disabled={isSubmitting} 
                        className="w-full bg-text-main hover:bg-zinc-800 text-white py-3 px-6 rounded-xl font-semibold transition-all duration-300 hover:shadow-md hover:-translate-y-0.5 active:scale-[0.98] cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed"
                    >
                        {isSubmitting ? 'Processando...' : 'Alterar Senha'}
                    </button>
                    {msg.text && (
                        <div className={`p-3 rounded-xl border text-xs font-semibold text-center transition-all duration-300 ${getMsgColorClass(msg.color)}`}>
                            {msg.text}
                        </div>
                    )}
                </div>
            )}

            <Link to="/" className="text-center text-sm font-semibold text-text-muted hover:text-text-main transition-colors duration-200 mt-6 block">
                Voltar ao Login
            </Link>
        </div>
    );
}
