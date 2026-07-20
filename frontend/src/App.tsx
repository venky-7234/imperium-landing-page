import React, { useState, useEffect, Suspense } from "react";
import { AnimatePresence, motion } from "framer-motion";
import { Routes, Route, useLocation, useNavigate, Navigate } from "react-router-dom";
import { Toaster } from "react-hot-toast";
import { CustomCursor } from './components/ui/CustomCursor';
import { AmbientBackground } from './components/ui/AmbientBackground';

// Lazy loaded route components
const LandingPage = React.lazy(() => import('./pages/LandingPage').then(m => ({ default: m.LandingPage })));
const DetailsPage = React.lazy(() => import('./pages/DetailsPage').then(m => ({ default: m.DetailsPage })));
const ApplicationPage = React.lazy(() => import('./pages/ApplicationPage').then(m => ({ default: m.ApplicationPage })));
const ThankYouPage = React.lazy(() => import('./pages/ThankYouPage').then(m => ({ default: m.ThankYouPage })));
const Login = React.lazy(() => import('./pages/Login'));
const AdminDashboard = React.lazy(() => import('./pages/AdminDashboard').then(m => ({ default: m.AdminDashboard })));
const SuperadminDashboard = React.lazy(() => import('./pages/SuperadminDashboard').then(m => ({ default: m.SuperadminDashboard })));
const UserProfile = React.lazy(() => import('./pages/UserProfile').then(m => ({ default: m.UserProfile })));

const PageFallback = () => (
  <div className="min-h-screen bg-[#0A0A0A] flex flex-col items-center justify-center text-[#D4AF37]">
    <div className="w-6 h-6 border-2 border-[#D4AF37] border-t-transparent rounded-full animate-spin"></div>
    <span className="mt-4 font-sans text-xs tracking-widest text-[#BDBDBD] uppercase animate-pulse">Loading...</span>
  </div>
);

function App() {
  const [isLoading, setIsLoading] = useState(true);
  const [processedLogo, setProcessedLogo] = useState<string>("/images/logo.png");
  const [adminToken, setAdminToken] = useState<string | null>(() => localStorage.getItem('token'));
  const location = useLocation();
  const navigate = useNavigate();

  useEffect(() => {
    if (window.location.search.includes('admin=true')) {
      navigate('/login');
    }
  }, [navigate]);
  
  // Keep adminToken logic simple for now, relying on localStorage
  useEffect(() => {
    const token = localStorage.getItem('token');
    if (token) {
      setAdminToken(token);
    }
  }, [location.pathname]);

  useEffect(() => {
    const img = new Image();
    img.src = "/images/logo.png";
    img.onload = () => {
      const canvas = document.createElement("canvas");
      canvas.width = img.width;
      canvas.height = img.height;
      const ctx = canvas.getContext("2d");
      if (!ctx) return;
      ctx.drawImage(img, 0, 0);
      const imageData = ctx.getImageData(0, 0, canvas.width, canvas.height);
      const data = imageData.data;
      
      for (let i = 0; i < data.length; i += 4) {
        const r = data[i];
        const g = data[i + 1];
        const b = data[i + 2];
        
        const dist = Math.sqrt((r - 28) ** 2 + (g - 7) ** 2 + (b - 18) ** 2);
        
        if (dist < 40) {
          data[i + 3] = 0;
        } else if (dist < 65) {
          const ratio = (dist - 40) / (65 - 40);
          data[i + 3] = Math.round(ratio * 255);
        }
      }
      ctx.putImageData(imageData, 0, 0);
      setProcessedLogo(canvas.toDataURL());
    };
  }, []);

  useEffect(() => {
    const timer = setTimeout(() => {
      setIsLoading(false);
    }, 2000);
    return () => clearTimeout(timer);
  }, []);

  const pageVariants = {
    initial: {
      opacity: 0,
      filter: "blur(6px)",
      scale: 0.98,
    },
    animate: {
      opacity: 1,
      filter: "blur(0px)",
      scale: 1,
      transition: {
        duration: 0.6,
        ease: [0.16, 1, 0.3, 1] as const,
      },
    },
    exit: {
      opacity: 0,
      filter: "blur(6px)",
      scale: 0.98,
      transition: {
        duration: 0.5,
        ease: [0.16, 1, 0.3, 1] as const,
      },
    },
  };

  const PageWrapper = ({ children, pageKey }: { children: React.ReactNode; pageKey: string }) => (
    <motion.div key={pageKey} variants={pageVariants} initial="initial" animate="animate" exit="exit" className="w-full">
      {children}
    </motion.div>
  );

  return (
    <>
      <AnimatePresence>
        {isLoading && (
          <motion.div
            key="preloader"
            initial={{ opacity: 1 }}
            exit={{ opacity: 0, transition: { duration: 0.8, ease: "easeInOut" } }}
            className="fixed inset-0 z-50 bg-[#0A0A0A] flex flex-col items-center justify-center text-[#F5F5F5]"
          >
            <motion.div
              initial={{ opacity: 0, scale: 0.95 }}
              animate={{ opacity: 1, scale: 1 }}
              transition={{ duration: 1.2 }}
              className="flex flex-col items-center gap-4 text-center"
            >
              <div className="relative mb-6 flex items-center justify-center select-none pointer-events-none">
                <img src={processedLogo} alt="Viora Elite Logo" className="h-20 sm:h-24 object-contain filter drop-shadow-[0_0_20px_rgba(212,175,55,0.35)]" />
              </div>
              
              <h2 className="font-serif text-md tracking-[0.4em] uppercase text-[#F5F5F5] mt-4">
                IMPERIUM
              </h2>
              <span className="font-sans text-[7px] tracking-[0.55em] text-[#D4AF37] uppercase font-semibold animate-pulse">
                INVITE ONLY
              </span>
              
              <div className="w-24 h-[1px] bg-[#D4AF37]/10 mt-6 relative overflow-hidden">
                <motion.div
                  initial={{ left: "-100%" }}
                  animate={{ left: "100%" }}
                  transition={{ repeat: Infinity, duration: 1.5, ease: "easeInOut" }}
                  className="absolute top-0 bottom-0 w-1/3 bg-[#D4AF37]"
                />
              </div>
            </motion.div>
          </motion.div>
        )}
      </AnimatePresence>

      <div className="relative min-h-screen w-full bg-[#0A0A0A] text-[#F5F5F5] overflow-x-hidden selection:bg-[#D4AF37] selection:text-[#0A0A0A] z-10">
        
        <CustomCursor />
        <AmbientBackground />
        <Toaster position="top-right" toastOptions={{ style: { background: '#111', color: '#DDD', border: '1px solid #222' } }} />

        <AnimatePresence mode="wait">
          <Suspense fallback={<PageFallback />}>
            <Routes location={location} key={location.pathname}>
            <Route path="/" element={
              <PageWrapper pageKey="landing">
                <LandingPage 
                  onNextPage={() => navigate('/details')} 
                  onAdminClick={() => navigate('/login')}
                />
              </PageWrapper>
            } />
            <Route path="/details" element={
              <PageWrapper pageKey="details">
                <DetailsPage 
                  onRequestInvitation={() => navigate('/application')} 
                  onBack={() => navigate('/')}
                />
              </PageWrapper>
            } />
            <Route path="/application" element={
              <PageWrapper pageKey="application">
                <ApplicationPage 
                  onSubmit={() => navigate('/thankyou')} 
                  onBack={() => navigate('/details')} 
                />
              </PageWrapper>
            } />
            <Route path="/thankyou" element={
              <PageWrapper pageKey="thankyou">
                <ThankYouPage 
                  onReturnHome={() => navigate('/')} 
                  onBack={() => navigate('/application')}
                />
              </PageWrapper>
            } />
            <Route path="/login" element={
              <PageWrapper pageKey="login">
                <Login processedLogo={processedLogo} />
              </PageWrapper>
            } />
            <Route path="/dashboard" element={
              adminToken ? (
                <PageWrapper pageKey="user-dashboard">
                  <UserProfile 
                    token={adminToken}
                    processedLogo={processedLogo}
                    onLogout={() => {
                      setAdminToken(null);
                      localStorage.removeItem('token');
                      localStorage.removeItem('roles');
                      navigate('/');
                    }}
                  />
                </PageWrapper>
              ) : <Navigate to="/login" />
            } />
            
            <Route path="/admin-dashboard" element={
              adminToken ? (
                <PageWrapper pageKey="admin-dashboard">
                  <AdminDashboard 
                    token={adminToken}
                    processedLogo={processedLogo}
                    onLogout={() => {
                      setAdminToken(null);
                      navigate('/');
                    }}
                  />
                </PageWrapper>
              ) : <Navigate to="/login" />
            } />
            <Route path="/superadmin-dashboard" element={
              adminToken ? (
                <PageWrapper pageKey="superadmin-dashboard">
                  <SuperadminDashboard 
                    token={adminToken}
                    processedLogo={processedLogo}
                    onLogout={() => {
                      setAdminToken(null);
                      navigate('/');
                    }}
                  />
                </PageWrapper>
              ) : <Navigate to="/login" />
            } />
            
            {/* Redirects and Catch-all */}
            <Route path="/admin" element={<Navigate to="/login" replace />} />
            <Route path="*" element={<Navigate to="/" replace />} />
            </Routes>
          </Suspense>
        </AnimatePresence>
      </div>
    </>
  );
}

export default App;

