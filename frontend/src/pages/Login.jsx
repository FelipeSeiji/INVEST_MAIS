import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import './Auth.css';

export default function Login() {
    const navigate = useNavigate();
    
    const [loginEmail, setLoginEmail] = useState('');
    const [loginPassword, setLoginPassword] = useState('');
    const [loginMsg, setLoginMsg] = useState({ text: '', color: '' });
    
    const [show2FA, setShow2FA] = useState(false);
    const [code2FA, setCode2FA] = useState('');
    
    const [regName, setRegName] = useState('');
    const [regEmail, setRegEmail] = useState('');
    const [regPassword, setRegPassword] = useState('');
    const [confirmPassword, setConfirmPassword] = useState('');
    const [regMsg, setRegMsg] = useState({ text: '', color: '' });
    
    const [isLoadingLogin, setIsLoadingLogin] = useState(false);
    const [isLoading2FA, setIsLoading2FA] = useState(false);
    const [isLoadingRegister, setIsLoadingRegister] = useState(false);


    const handleLogin = async () => {
        if (!loginEmail || !loginPassword) {
            setLoginMsg({ text: "Preencha todos os campos.", color: "red" });
            return;
        }

        setIsLoadingLogin(true);
        setLoginMsg({ text: '', color: '' });
        
        try {
            const response = await fetch('/auth/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: loginEmail, password: loginPassword })
            });

            const data = await response.json();

            if (response.ok) {
                setLoginMsg({ text: "Código 2FA enviado ao seu e-mail!", color: "green" });
                setShow2FA(true);
            } else {
                setLoginMsg({ text: data.detail || "Erro nas credenciais.", color: "red" });
            }
        } catch (error) {
            console.error(error);
            setLoginMsg({ text: "Erro de conexão com o servidor.", color: "red" });
        } finally {
            setIsLoadingLogin(false);
        }
    };


    const handleVerify2FA = async () => {
        setIsLoading2FA(true);
        try {
            const response = await fetch('/auth/verify-2fa', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ email: loginEmail, code: code2FA })
            });

            const data = await response.json();

            if (response.ok) {
                localStorage.setItem('token', data.token);
                navigate('/dashboard');
            } else {
                alert("Código inválido ou expirado.");
            }
        } catch (error) {
            console.error(error);
            alert("Erro na verificação.");
        } finally {
            setIsLoading2FA(false);
        }
    };


    const handleRegister = async () => {
        if (!regName || !regEmail || !regPassword || !confirmPassword) {
            setRegMsg({ text: "Preencha todos os campos.", color: "red" });
            return;
        }

        if (regPassword !== confirmPassword) {
            setRegMsg({ text: "As senhas não coincidem.", color: "red" });
            return;
        }

        setIsLoadingRegister(true);
        setRegMsg({ text: '', color: '' });

        try {
            const response = await fetch('/api/users', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify({ name: regName, email: regEmail, password: regPassword })
            });

            if (response.ok) {
                setRegMsg({ text: "Cadastro realizado! Faça login acima.", color: "green" });
                setRegName(''); setRegEmail(''); setRegPassword(''); setConfirmPassword('');
            } else {
                const data = await response.json();
                setRegMsg({ text: data.detail || "Erro no cadastro.", color: "red" });
            }
        } catch (error) {
            console.error(error);
            setRegMsg({ text: "Erro de rede.", color: "red" });
        } finally {
            setIsLoadingRegister(false);
        }
    };


    return (
        <div className="container">
            <div className="box">
                <h2>Entrar</h2>
                <input 
                    type="email" 
                    value={loginEmail} 
                    onChange={(e) => setLoginEmail(e.target.value)} 
                    placeholder="E-mail" 
                />
                <input 
                    type="password" 
                    value={loginPassword} 
                    onChange={(e) => setLoginPassword(e.target.value)} 
                    placeholder="Senha" 
                />
                <button onClick={handleLogin} disabled={isLoadingLogin}>
                    {isLoadingLogin ? 'Entrando...' : 'Entrar'}
                </button>
                <div className="message" style={{ color: loginMsg.color }}>{loginMsg.text}</div>
                
                {show2FA && (
                    <div id="twoFaSection">
                        <input 
                            type="text" 
                            value={code2FA} 
                            onChange={(e) => setCode2FA(e.target.value)} 
                            placeholder="Código de 6 dígitos" 
                        />
                        <button 
                            onClick={handleVerify2FA} 
                            disabled={isLoading2FA}
                            style={{ backgroundColor: '#27272a', width: '100%' }}
                        >
                            {isLoading2FA ? 'Verificando...' : 'Confirmar'}
                        </button>
                    </div>
                )}
                
                <Link to="/reset-password" style={{ textAlign: 'center', marginTop: '10px', color: '#4b4b4b', textDecoration: 'none' }}>
                    Esqueceu a senha?
                </Link>
            </div>

            <div className="box">
                <h2>Criar Conta</h2>
                <input 
                    type="text" 
                    value={regName} 
                    onChange={(e) => setRegName(e.target.value)} 
                    placeholder="Nome Completo" 
                />
                <input 
                    type="email" 
                    value={regEmail} 
                    onChange={(e) => setRegEmail(e.target.value)} 
                    placeholder="E-mail" 
                />
                <input 
                    type="password" 
                    value={regPassword} 
                    onChange={(e) => setRegPassword(e.target.value)} 
                    placeholder="Senha" 
                />
                <input 
                    type="password" 
                    value={confirmPassword} 
                    onChange={(e) => setConfirmPassword(e.target.value)} 
                    placeholder="Confirmar Nova Senha" 
                />
                <button onClick={handleRegister} disabled={isLoadingRegister}>
                    {isLoadingRegister ? 'Cadastrando...' : 'Cadastrar'}
                </button>
                <div className="message" style={{ color: regMsg.color }}>{regMsg.text}</div>
            </div>
        </div>
    );
}
