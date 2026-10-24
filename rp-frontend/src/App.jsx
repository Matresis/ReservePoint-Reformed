import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';

// We will create these page components next
import Home from './pages/Home';
import LoginPage from './pages/LoginPage';
import RegisterPage from './pages/RegisterPage';
import AdminDashboard from './pages/AdminDashboard';

function App() {
  return (
      <Router>
        <Routes>
          {/* Public Routes */}
          <Route path="/" element={<Home />} />
          <Route path="/home" element={<Home />} />
          <Route path="/loginForm" element={<LoginPage />} />
          <Route path="/registerForm" element={<RegisterPage />} />

          {/* Admin Routes */}
          <Route path="/admin" element={<AdminDashboard />} />
        </Routes>
      </Router>
  );
}

export default App;