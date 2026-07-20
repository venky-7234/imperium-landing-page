import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { GoogleLogin } from '@react-oauth/google';
import toast from 'react-hot-toast';
import { googleLogin } from '../services/api';
import { Building2 } from 'lucide-react';
import { motion } from 'framer-motion';

interface LoginProps {
  processedLogo?: string;
}

const Login: React.FC<LoginProps> = ({ processedLogo }) => {
  const navigate = useNavigate();
  const [isLoading, setIsLoading] = useState(false);

  const handleGoogleSuccess = async (credentialResponse: any) => {
    setIsLoading(true);
    try {
      const idToken = credentialResponse.credential;
      const response = await googleLogin({ idToken });
      
      const { accessToken, roles, username } = response.data;
      
      // Store token
      localStorage.setItem('token', accessToken);
      if (roles) {
        localStorage.setItem('roles', JSON.stringify(roles));
      }
      
      toast.success(`Welcome back, ${username}!`);
      
      if (roles?.includes('ROLE_SUPER_ADMIN')) {
        navigate('/superadmin-dashboard');
      } else if (roles?.includes('ROLE_ADMIN')) {
        navigate('/admin-dashboard');
      } else if (roles?.includes('ROLE_USER')) {
        navigate('/dashboard');
      } else {
        toast.error('Unauthorized role');
        navigate('/');
      }
    } catch (error: any) {
      toast.error(error.message || 'Authentication failed');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="min-h-screen bg-[#0A0A0A] flex flex-col items-center justify-center p-4 relative overflow-hidden">
      {/* Background gradients */}
      <div className="absolute top-[-10%] left-[-10%] w-[40%] h-[40%] bg-[#D4AF37]/10 rounded-full blur-[120px]" />
      <div className="absolute bottom-[-10%] right-[-10%] w-[40%] h-[40%] bg-[#D4AF37]/5 rounded-full blur-[120px]" />
      
      <motion.div 
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ duration: 0.5 }}
        className="w-full max-w-md bg-[#0d0d0d]/80 backdrop-blur-xl border border-[#D4AF37]/20 p-8 rounded-2xl shadow-[0_0_30px_rgba(212,175,55,0.05)] relative z-10 flex flex-col items-center"
      >
        <div className="flex flex-col items-center mb-8 w-full">
          <div className="w-20 h-20 flex items-center justify-center mb-6 pointer-events-none">
            <img src={processedLogo || '/images/logo.png'} alt="Imperium Logo" className="w-full h-full object-contain filter drop-shadow-[0_0_12px_rgba(212,175,55,0.25)]" />
          </div>
          <h1 className="font-serif text-2xl tracking-[0.35em] uppercase text-[#F5F5F5] mb-2 text-center">IMPERIUM</h1>
          <span className="font-sans text-[9px] tracking-[0.45em] text-[#D4AF37] uppercase font-semibold mb-6">INVITE ONLY</span>
          <p className="text-[#BDBDBD] text-center text-sm font-light leading-relaxed px-4">
            Sign in with your enterprise Google account to access the dashboard.
          </p>
        </div>

        <div className="flex flex-col items-center justify-center space-y-4 w-full">
          {isLoading ? (
            <div className="flex items-center justify-center py-4">
              <div className="w-5 h-5 border-2 border-[#D4AF37] border-t-transparent rounded-full animate-spin"></div>
              <span className="ml-3 text-[#D4AF37] text-sm tracking-wide">Authenticating...</span>
            </div>
          ) : (
            <div className="w-full flex flex-col items-center gap-4 py-2">
              <GoogleLogin
                onSuccess={handleGoogleSuccess}
                onError={() => {
                  toast.error('Google Sign-In failed');
                }}
                theme="filled_black"
                size="large"
                shape="rectangular"
                text="continue_with"
                width="320"
              />
              

            </div>
          )}
        </div>
      </motion.div>
    </div>
  );
};

export default Login;
