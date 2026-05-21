import { Outlet } from 'react-router-dom';
import Footer from './Footer';

export default function PublicLayout() {
  return (
    <div className="flex flex-col min-h-screen bg-bg-primary overflow-y-auto">
      <div className="flex-1 flex items-center justify-center p-4 sm:p-6 md:p-8">
        <Outlet />
      </div>
      <Footer />
    </div>
  );
}
