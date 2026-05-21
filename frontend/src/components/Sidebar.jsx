import { Link, useLocation, useNavigate } from 'react-router-dom';

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();

  const getLinkClass = (path) => {
    const baseClass = "flex items-center gap-3 py-3.5 px-4.5 font-semibold text-sm rounded-lg transition-all duration-200 hover:bg-white/5 hover:text-white hover:translate-x-1 decoration-none";
    const activeClass = "bg-[rgba(255,192,0,0.1)] text-[#a8a8b3] shadow-[inset_4px_0_0_#a8a8b3]";
    const inactiveClass = "text-[#a8a8b3]";
    return `${baseClass} ${location.pathname === path ? activeClass : inactiveClass}`;
  };

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  return (
    <aside className="w-[200px] bg-text-main text-[#e1e1e6] flex flex-col h-screen sticky top-0 shadow-[4px_0_15px_rgba(0,0,0,0.1)] z-50 transition-all duration-300 font-sans">
      <div className="py-8 px-6 border-b border-white/5">
        <h2 className="m-0 text-2xl font-extrabold tracking-tight text-white">Invest +</h2>
      </div>
      
      <nav className="flex-1 py-6 px-4 overflow-y-auto">
        <ul className="list-none p-0 m-0 flex flex-col gap-2">
          <li>
            <Link to="/aportes" className={getLinkClass('/aportes')}>
              Aportes
            </Link>
          </li>
          <li>
            <Link to="/profile" className={getLinkClass('/profile')}>
              Perfil
            </Link>
          </li>
        </ul>
      </nav>
      
      <div className="px-5 mb-2">
        <button 
          className="w-full p-3 bg-white/5 text-[#e1e1e6] border border-white/10 rounded-lg text-sm font-medium cursor-pointer transition-all duration-200 hover:bg-[#ff4757] hover:border-[#ff4757] hover:text-white" 
          onClick={handleLogout}
        >
          Sair
        </button>
      </div>

      <div className="py-4 px-5 pb-6 border-t border-white/5 text-center">
        <p className="m-0 text-xs text-[#737380]">Invest+</p>
      </div>
    </aside>
  );
}
