import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Footer from './Footer';
import './Layout.css';

export default function Layout() {
  return (
    <div className="fullscreen-layout" style={{ flexDirection: 'row' }}>
      <Sidebar />
      <div className="layout-content">
        <main className="main-area">
          <Outlet />
        </main>
        <Footer />
      </div>
    </div>
  );
}
