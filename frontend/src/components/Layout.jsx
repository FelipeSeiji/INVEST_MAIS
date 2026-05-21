import { Outlet } from 'react-router-dom';
import Sidebar from './Sidebar';
import Footer from './Footer';

export default function Layout() {
  return (
    <div className="flex flex-row w-full min-h-screen bg-bg-card">
      <Sidebar />
      <div className="flex-1 flex flex-col overflow-y-auto bg-[#fafafa]">
        <main className="flex-1 p-10 box-border">
          <Outlet />
        </main>
        <Footer />
      </div>
    </div>
  );
}
