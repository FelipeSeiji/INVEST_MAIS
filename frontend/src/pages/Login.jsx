import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useNavigate, Link } from 'react-router-dom';

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
    const [showTerms, setShowTerms] = useState(false);


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
                navigate('/aportes');
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

        setShowTerms(true);
    };

    const confirmRegistration = async () => {
        setShowTerms(false);
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
        <div className="min-h-screen w-full flex items-center justify-center bg-zinc-50 dark:bg-zinc-950 p-4">
            <div className="w-full max-w-4xl grid md:grid-cols-2 gap-8 items-start">
                
                {/* Entrar Box */}
                <div className="bg-white dark:bg-zinc-900 rounded-2xl p-8 shadow-xl shadow-zinc-200/50 dark:shadow-black/50 border border-zinc-100 dark:border-zinc-800">
                    <h2 className="text-3xl font-bold text-zinc-900 dark:text-white mb-6">Entrar</h2>
                    <div className="space-y-4">
                        <input 
                            type="email" 
                            value={loginEmail} 
                            onChange={(e) => setLoginEmail(e.target.value)} 
                            placeholder="E-mail" 
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 dark:focus:ring-white transition-all"
                        />
                        <input 
                            type="password" 
                            value={loginPassword} 
                            onChange={(e) => setLoginPassword(e.target.value)} 
                            placeholder="Senha" 
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 dark:focus:ring-white transition-all"
                        />
                        <button 
                            onClick={handleLogin} 
                            disabled={isLoadingLogin}
                            className="w-full py-3 mt-2 rounded-xl bg-zinc-900 dark:bg-white text-white dark:text-zinc-900 font-semibold hover:bg-zinc-800 dark:hover:bg-zinc-100 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                        >
                            {isLoadingLogin ? 'Entrando...' : 'Entrar'}
                        </button>
                        
                        {loginMsg.text && (
                            <div className={`text-sm text-center ${loginMsg.color === 'red' ? 'text-red-500' : 'text-green-500'}`}>
                                {loginMsg.text}
                            </div>
                        )}
                        
                        {show2FA && (
                            <div className="mt-6 pt-6 border-t border-zinc-100 dark:border-zinc-800 space-y-4 animate-in fade-in slide-in-from-top-4 duration-300">
                                <p className="text-sm text-zinc-500 text-center">Verificação em 2 Passos</p>
                                <input 
                                    type="text" 
                                    value={code2FA} 
                                    onChange={(e) => setCode2FA(e.target.value)} 
                                    placeholder="Código de 6 dígitos" 
                                    className="w-full px-4 py-3 text-center tracking-[0.5em] font-mono rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white focus:outline-none focus:ring-2 focus:ring-zinc-900 transition-all"
                                />
                                <button 
                                    onClick={handleVerify2FA} 
                                    disabled={isLoading2FA}
                                    className="w-full py-3 rounded-xl bg-indigo-600 text-white font-semibold hover:bg-indigo-700 disabled:opacity-50 transition-all"
                                >
                                    {isLoading2FA ? 'Verificando...' : 'Confirmar'}
                                </button>
                            </div>
                        )}
                        
                        <div className="pt-4 text-center">
                            <Link to="/reset-password" className="text-sm font-medium text-zinc-500 hover:text-zinc-900 dark:hover:text-white transition-colors">
                                Esqueceu a senha?
                            </Link>
                        </div>
                    </div>
                </div>

                {/* Criar Conta Box */}
                <div className="bg-white dark:bg-zinc-900 rounded-2xl p-8 shadow-xl shadow-zinc-200/50 dark:shadow-black/50 border border-zinc-100 dark:border-zinc-800">
                    <h2 className="text-3xl font-bold text-zinc-900 dark:text-white mb-6">Criar Conta</h2>
                    <div className="space-y-4">
                        <input 
                            type="text" 
                            value={regName} 
                            onChange={(e) => setRegName(e.target.value)} 
                            placeholder="Nome Completo" 
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 dark:focus:ring-white transition-all"
                        />
                        <input 
                            type="email" 
                            value={regEmail} 
                            onChange={(e) => setRegEmail(e.target.value)} 
                            placeholder="E-mail" 
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 dark:focus:ring-white transition-all"
                        />
                        <input 
                            type="password" 
                            value={regPassword} 
                            onChange={(e) => setRegPassword(e.target.value)} 
                            placeholder="Senha" 
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 dark:focus:ring-white transition-all"
                        />
                        <input 
                            type="password" 
                            value={confirmPassword} 
                            onChange={(e) => setConfirmPassword(e.target.value)} 
                            placeholder="Confirmar Nova Senha" 
                            className="w-full px-4 py-3 rounded-xl bg-zinc-50 dark:bg-zinc-800 border border-zinc-200 dark:border-zinc-700 text-zinc-900 dark:text-white placeholder-zinc-400 focus:outline-none focus:ring-2 focus:ring-zinc-900 dark:focus:ring-white transition-all"
                        />
                        <button 
                            onClick={handleRegister} 
                            disabled={isLoadingRegister}
                            className="w-full py-3 mt-2 rounded-xl bg-zinc-900 dark:bg-white text-white dark:text-zinc-900 font-semibold hover:bg-zinc-800 dark:hover:bg-zinc-100 disabled:opacity-50 disabled:cursor-not-allowed transition-all"
                        >
                            {isLoadingRegister ? 'Cadastrando...' : 'Cadastrar'}
                        </button>
                        
                        {regMsg.text && (
                            <div className={`text-sm text-center ${regMsg.color === 'red' ? 'text-red-500' : 'text-green-500'}`}>
                                {regMsg.text}
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Modal de Termos */}
            {showTerms && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/60 backdrop-blur-sm animate-in fade-in duration-200">
                    <div className="bg-white dark:bg-zinc-900 rounded-2xl w-full max-w-lg overflow-hidden shadow-2xl border border-zinc-200 dark:border-zinc-800 animate-in zoom-in-95 duration-200">
                        <div className="p-6 border-b border-zinc-100 dark:border-zinc-800">
                            <h3 className="text-xl font-bold text-zinc-900 dark:text-white">Termos e Condições</h3>
                        </div>
                        <div className="p-6 max-h-[50vh] overflow-y-auto text-zinc-600 dark:text-zinc-400 space-y-4">
                            <p>Bem-vindo ao <strong className="text-zinc-900 dark:text-white">INVEST +</strong>.</p>
                            <h4 className="font-semibold text-zinc-900 dark:text-white">Consentimento</h4>
                            <p className="text-sm leading-relaxed">
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean augue purus, finibus sed ultrices vel, efficitur eget nisl. Phasellus vehicula in ex at dignissim. Suspendisse rhoncus, tellus quis cursus semper, velit diam ornare lorem, et aliquam justo felis at nulla. Cras dui felis, interdum sit amet sodales nec, lacinia sit amet nisl. Nullam sed nulla cursus, facilisis sapien eu, congue nisi. Proin id eros sollicitudin, elementum elit in, lacinia tortor. Vivamus condimentum rhoncus felis a faucibus. Cras at augue pretium, efficitur orci vitae, sollicitudin est. Fusce dictum libero pretium lectus auctor, at semper enim commodo. Nulla vitae ipsum id magna ornare aliquam. Nunc ut tortor ac mi consequat fringilla. Ut vel massa sed enim vehicula porttitor. Morbi dictum sem ut nulla porta porta. Quisque in lobortis arcu.
                            </p>
                        </div>
                        <div className="p-6 border-t border-zinc-100 dark:border-zinc-800 flex gap-3 justify-end bg-zinc-50 dark:bg-zinc-900/50">
                            <button 
                                className="px-6 py-2.5 rounded-xl font-medium text-zinc-600 dark:text-zinc-400 hover:bg-zinc-200 dark:hover:bg-zinc-800 transition-colors" 
                                onClick={() => setShowTerms(false)}
                            >
                                Cancelar
                            </button>
                            <button 
                                className="px-6 py-2.5 rounded-xl font-semibold bg-zinc-900 dark:bg-white text-white dark:text-zinc-900 hover:bg-zinc-800 dark:hover:bg-zinc-100 transition-colors shadow-md" 
                                onClick={confirmRegistration}
                            >
                                Concordar e Criar Conta
                            </button>
                        </div>
                    </div>
                </div>
            )}
        </div>
    );
}
