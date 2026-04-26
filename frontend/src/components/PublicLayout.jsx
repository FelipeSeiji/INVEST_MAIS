import { Outlet } from 'react-router-dom';
import Footer from './Footer';

export default function PublicLayout() {
  return (
    <div className="fullscreen-layout" style={{ overflowY: 'auto' }}>
      <div className="public-layout-content">
        <Outlet />
      </div>
      <Footer />
    </div>
  );
}
