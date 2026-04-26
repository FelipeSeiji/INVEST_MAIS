import { BrowserRouter, Routes, Route } from 'react-router-dom'
import Login from './pages/Login'
import Dashboard from './pages/Dashboard'
import Profile from './pages/Profile'
import Aportes from './pages/Aportes'
import ResetPassword from './pages/ResetPassword'
import Layout from './components/Layout'
import PublicLayout from './components/PublicLayout'

function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* Public Routes wrapped in PublicLayout */}
        <Route element={<PublicLayout />}>
          <Route path="/" element={<Login />} />
          <Route path="/reset-password" element={<ResetPassword />} />
        </Route>

        {/* Private Routes wrapped in Layout */}
        <Route element={<Layout />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/profile" element={<Profile />} />
          <Route path="/aportes" element={<Aportes />} />
        </Route>
      </Routes>
    </BrowserRouter>
  )
}

export default App
