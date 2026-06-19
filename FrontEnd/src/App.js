import Login from './pages/Login';
import Register from './pages/Register';
import Home from './pages/Home';
import Profile from './pages/Profile';
import Templates from './pages/templates/Templates';
import './App.css';
import { ClubHome } from './components/Clubs/ClubHome';
import { CreateClub } from './components/Clubs/CreateClub';
// import { Sportsclub } from './components/Clubs/club/Sportsclub';
import { Clubs } from './components/Clubs/club/Clubs';
import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import Templateshome from './pages/Templateshome';
import { JobHome } from './components/Jobs/JobHome';
import OnCampusJobs from './components/Jobs/OnCampusJobs';
import ErrorPage from './components/ErrorPage/ErrorPage';
import ApiTest from './components/ApiTest';
import ApiConfig from './components/ApiConfig';
import ApiDebugger from './components/ApiDebugger';
import { apiUtils } from './utils/api';

function App() {

  return (
    <section className='Appp'>
      <BrowserRouter>
        <ApiConfig />
        <Routes>
          <Route path="/" element={<ProtectedRoute><Home /></ProtectedRoute>} />
          <Route path="/home" element={<ProtectedRoute><Home /></ProtectedRoute>} />
          <Route path="/profile" element={<ProtectedRoute><Profile /></ProtectedRoute>} />
          <Route path="/templates/:id" element={<ProtectedRoute><Templates /></ProtectedRoute>} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/templates" element={<Templateshome />} />
          <Route path="/clubs" element={<ClubHome />} />
          <Route path="/CreateClub" element={<ProtectedRoute><CreateClub /></ProtectedRoute>} />
          <Route path="/Club/:id" element={<Clubs />} />
          <Route path="/jobs" element={<JobHome />} />
          <Route path="/oncampusjobs" element={<OnCampusJobs />} />
          <Route path="/api-test" element={<ApiTest />} />
          <Route path="/debug" element={<ApiDebugger />} />
          <Route path="*" element={<ErrorPage />} />
        </Routes>
      </BrowserRouter>
    </section>
  );
}

export default App;

// JWT-based Protected Routes with updated localStorage keys
export function ProtectedRoute(props) {
  console.log('🔒 ProtectedRoute check...');

  // Use our API utility to check authentication
  const isAuthenticated = apiUtils.isAuthenticated();
  const user = apiUtils.getCurrentUser();

  console.log('🔒 Is authenticated:', isAuthenticated);
  console.log('🔒 Current user:', user);

  // Check for both _id (MongoDB) and id (Spring Boot) for compatibility
  if (isAuthenticated && user && (user._id || user.id)) {
    console.log('✅ Access granted to protected route');
    return props.children;
  }

  console.log('❌ Access denied, redirecting to login');
  return <Navigate to="/login" replace />;
}