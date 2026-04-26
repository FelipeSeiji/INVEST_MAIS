import { Link, useLocation, useNavigate } from 'react-router-dom';
import './Sidebar.css';

export default function Sidebar() {
  const location = useLocation();
  const navigate = useNavigate();

  const isActive = (path) => location.pathname === path ? 'active' : '';

  const handleLogout = () => {
    localStorage.clear();
    navigate('/');
  };

  return (
    <aside className="sidebar">
      <div className="sidebar-brand">
        <h2>Invest +</h2>
      </div>
      
      <nav className="sidebar-nav">
        <ul>
          <li>
            <Link to="/dashboard" className={isActive('/dashboard')}>
              Dashboard
            </Link>
          </li>
          <li>
            <Link to="/aportes" className={isActive('/aportes')}>
              Aportes
            </Link>
          </li>
          <li>
            <Link to="/profile" className={isActive('/profile')}>
              Perfil
            </Link>
          </li>
        </ul>
      </nav>
      
      <div className="sidebar-actions">
        <button className="btn-logout-sidebar" onClick={handleLogout}>
          Sair
        </button>
      </div>

      <div className="sidebar-footer">
        <p>Invest+</p>
      </div>
    </aside>
  );
}
