import { useState } from 'react';
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
        <div className="w-full max-w-4xl flex flex-col items-center py-6">
            {/* Branding Header */}
            <div className="text-center mb-10 animate-in fade-in slide-in-from-top-4 duration-500">
                <h1 className="text-4xl font-extrabold tracking-tight bg-gradient-to-r from-zinc-900 via-zinc-800 to-zinc-600 bg-clip-text text-transparent">
                    Invest<span className="text-zinc-500 font-light">+</span>
                </h1>
                <p className="text-sm text-zinc-500 mt-2 font-medium">Sua plataforma inteligente de gestão de investimentos</p>
            </div>

            <div className="w-full grid md:grid-cols-2 gap-8 items-start animate-in fade-in zoom-in-95 duration-500">
                
                {/* Entrar Box */}
                <div className="bg-bg-card rounded-2xl p-8 shadow-md border border-zinc-200/60 transition-all duration-300 hover:shadow-lg">
                    <h2 className="text-2xl font-bold tracking-tight bg-gradient-to-r from-zinc-900 to-zinc-600 bg-clip-text text-transparent mb-6">Entrar</h2>
                    <div className="space-y-4">
                        <div className="space-y-1">
                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">E-mail</label>
                            <input 
                                type="email" 
                                value={loginEmail} 
                                onChange={(e) => setLoginEmail(e.target.value)} 
                                placeholder="exemplo@email.com" 
                                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                            />
                        </div>
                        <div className="space-y-1">
                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Senha</label>
                            <input 
                                type="password" 
                                value={loginPassword} 
                                onChange={(e) => setLoginPassword(e.target.value)} 
                                placeholder="Digite sua senha" 
                                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                            />
                        </div>
                        <button 
                            onClick={handleLogin} 
                            disabled={isLoadingLogin}
                            className="w-full py-3 mt-2 rounded-xl bg-zinc-900 hover:bg-zinc-800 text-white font-semibold active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-sm hover:shadow-md duration-200"
                        >
                            {isLoadingLogin ? 'Entrando...' : 'Entrar'}
                        </button>
                        
                        {loginMsg.text && (
                            <div className={`text-sm text-center font-medium ${loginMsg.color === 'red' ? 'text-red-500' : 'text-green-600'}`}>
                                {loginMsg.text}
                            </div>
                        )}
                        
                        {show2FA && (
                            <div className="mt-6 pt-6 border-t border-zinc-100 space-y-4 animate-in fade-in slide-in-from-top-4 duration-300">
                                <p className="text-sm font-semibold text-zinc-500 text-center uppercase tracking-wider">Verificação em 2 Passos</p>
                                <input 
                                    type="text" 
                                    value={code2FA} 
                                    onChange={(e) => setCode2FA(e.target.value)} 
                                    placeholder="Código de 6 dígitos" 
                                    className="w-full px-4 py-3 text-center tracking-[0.5em] font-mono rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                                />
                                <button 
                                    onClick={handleVerify2FA} 
                                    disabled={isLoading2FA}
                                    className="w-full py-3 rounded-xl bg-zinc-900 hover:bg-zinc-800 text-white font-semibold active:scale-[0.98] disabled:opacity-50 transition-all shadow-sm hover:shadow-md duration-200"
                                >
                                    {isLoading2FA ? 'Verificando...' : 'Confirmar'}
                                </button>
                            </div>
                        )}
                        
                        <div className="pt-4 text-center">
                            <Link to="/recuperar-senha" className="text-sm font-semibold text-zinc-500 hover:text-zinc-900 transition-colors">
                                Esqueceu a senha?
                            </Link>
                        </div>
                    </div>
                </div>

                {/* Criar Conta Box */}
                <div className="bg-bg-card rounded-2xl p-8 shadow-md border border-zinc-200/60 transition-all duration-300 hover:shadow-lg">
                    <h2 className="text-2xl font-bold tracking-tight bg-gradient-to-r from-zinc-900 to-zinc-600 bg-clip-text text-transparent mb-6">Criar Conta</h2>
                    <div className="space-y-4">
                        <div className="space-y-1">
                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Nome Completo</label>
                            <input 
                                type="text" 
                                value={regName} 
                                onChange={(e) => setRegName(e.target.value)} 
                                placeholder="Seu nome" 
                                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                            />
                        </div>
                        <div className="space-y-1">
                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">E-mail</label>
                            <input 
                                type="email" 
                                value={regEmail} 
                                onChange={(e) => setRegEmail(e.target.value)} 
                                placeholder="exemplo@email.com" 
                                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                            />
                        </div>
                        <div className="space-y-1">
                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Senha</label>
                            <input 
                                type="password" 
                                value={regPassword} 
                                onChange={(e) => setRegPassword(e.target.value)} 
                                placeholder="Crie uma senha forte" 
                                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                            />
                        </div>
                        <div className="space-y-1">
                            <label className="text-xs font-semibold text-zinc-500 uppercase tracking-wider">Confirmar Nova Senha</label>
                            <input 
                                type="password" 
                                value={confirmPassword} 
                                onChange={(e) => setConfirmPassword(e.target.value)} 
                                placeholder="Confirme sua senha" 
                                className="w-full px-4 py-3 rounded-xl bg-zinc-50/50 border border-zinc-200 text-zinc-900 placeholder-zinc-400 focus:outline-none focus:border-zinc-900 focus:ring-1 focus:ring-zinc-900 transition-all duration-200"
                            />
                        </div>
                        <button 
                            onClick={handleRegister} 
                            disabled={isLoadingRegister}
                            className="w-full py-3 mt-2 rounded-xl bg-zinc-900 hover:bg-zinc-800 text-white font-semibold active:scale-[0.98] disabled:opacity-50 disabled:cursor-not-allowed transition-all shadow-sm hover:shadow-md duration-200"
                        >
                            {isLoadingRegister ? 'Cadastrando...' : 'Cadastrar'}
                        </button>
                        
                        {regMsg.text && (
                            <div className={`text-sm text-center font-medium ${regMsg.color === 'red' ? 'text-red-500' : 'text-green-600'}`}>
                                {regMsg.text}
                            </div>
                        )}
                    </div>
                </div>
            </div>

            {/* Modal de Termos */}
            {showTerms && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4 bg-black/40 backdrop-blur-xs animate-in fade-in duration-200">
                    <div className="bg-bg-card rounded-2xl w-full max-w-lg overflow-hidden shadow-2xl border border-zinc-200/80 animate-in zoom-in-95 duration-200">
                        <div className="p-6 border-b border-zinc-100">
                            <h3 className="text-xl font-bold text-zinc-900">Termos e Condições</h3>
                        </div>
                        <div className="p-6 max-h-[50vh] overflow-y-auto text-zinc-600 space-y-4">
                            <p className="text-sm">Bem-vindo ao <strong className="text-zinc-900">INVEST +</strong>.</p>
                            <h4 className="font-semibold text-sm text-zinc-900">Consentimento</h4>
                            <p className="text-xs leading-relaxed text-zinc-500 text-justify">
                                Lorem ipsum dolor sit amet, consectetur adipiscing elit. Aenean augue purus, finibus sed ultrices vel, efficitur eget nisl. Phasellus vehicula in ex at dignissim. Suspendisse rhoncus, tellus quis cursus semper, velit diam ornare lorem, et aliquam justo felis at nulla. Cras dui felis, interdum sit amet sodales nec, lacinia sit amet nisl. Nullam sed nulla cursus, facilisis sapien eu, congue nisi. Proin id eros sollicitudin, elementum elit in, lacinia tortor. Vivamus condimentum rhoncus felis a faucibus. Cras at augue pretium, efficitur orci vitae, sollicitudin est. Fusce dictum libero pretium lectus auctor, at semper enim commodo. Nulla vitae ipsum id magna ornare aliquam. Nunc ut tortor ac mi consequat fringilla. Ut vel massa sed enim vehicula porttitor. Morbi dictum sem ut nulla porta porta. Quisque in lobortis arcu.
                            </p>
                        </div>
                        <div className="p-6 border-t border-zinc-100 flex gap-3 justify-end bg-zinc-50/50">
                            <button 
                                className="px-6 py-2.5 rounded-xl font-medium text-sm text-zinc-500 hover:bg-zinc-100 transition-colors" 
                                onClick={() => setShowTerms(false)}
                            >
                                Cancelar
                            </button>
                            <button 
                                className="px-6 py-2.5 rounded-xl font-semibold text-sm bg-zinc-900 hover:bg-zinc-800 text-white transition-colors shadow-md active:scale-[0.98]" 
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
