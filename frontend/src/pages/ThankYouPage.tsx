import React from "react";
import { motion as motionFramer } from "framer-motion";
import { Check, ArrowLeft } from "lucide-react";

interface ThankYouPageProps {
  onReturnHome: () => void;
  onBack?: () => void;
}

export const ThankYouPage: React.FC<ThankYouPageProps> = ({ onReturnHome, onBack }) => {
  return (
    <div className="relative h-screen w-full flex flex-col justify-between bg-[#0A0A0A] px-6 py-8 overflow-hidden select-none">
      
      {/* Background glow flares */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[350px] h-[350px] rounded-full bg-[#D4AF37]/5 blur-[120px] pointer-events-none" />

      {/* Top Back Arrow Navigation */}
      {onBack && (
        <motionFramer.button
          onClick={onBack}
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8, delay: 0.5, ease: "easeOut" }}
          className="absolute top-6 left-6 sm:top-10 sm:left-10 z-50 group flex items-center justify-center p-3 sm:p-4 rounded-full border border-[#D4AF37]/20 bg-[#0A0A0A]/80 backdrop-blur-sm hover:border-[#D4AF37] hover:bg-[#D4AF37]/10 active:border-[#D4AF37] active:bg-[#D4AF37]/15 active:scale-95 transition-all duration-300"
        >
          <ArrowLeft size={18} className="text-[#D4AF37]/80 group-hover:text-[#F6D365] group-hover:-translate-x-1 group-active:text-[#F6D365] group-active:-translate-x-1 transition-all duration-300" />
        </motionFramer.button>
      )}

      {/* Top spacing */}
      <div className="h-16 shrink-0" />

      {/* Main Success Visual Animation */}
      <div className="relative z-10 my-auto text-center flex flex-col items-center space-y-8 max-w-xl mx-auto">
        
        {/* Golden checkmark circle */}
        <div className="relative flex items-center justify-center">
          <motionFramer.div
            initial={{ scale: 0.5, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            transition={{ type: "spring", damping: 15, stiffness: 120 }}
            className="p-5 rounded-full bg-[#D4AF37]/15 border border-[#D4AF37]/30 relative z-10 shadow-[0_0_20px_rgba(212,175,55,0.25)]"
          >
            <Check size={40} className="text-[#D4AF37]" strokeWidth={3} />
          </motionFramer.div>
          
          {/* Concentric rings pulsing */}
          <motionFramer.div
            animate={{ scale: [1, 1.35, 1], opacity: [0.15, 0, 0.15] }}
            transition={{ repeat: Infinity, duration: 3, ease: "easeInOut" }}
            className="absolute inset-0 rounded-full border border-[#D4AF37]/20 pointer-events-none z-0"
          />
          <motionFramer.div
            animate={{ scale: [1, 1.6, 1], opacity: [0.08, 0, 0.08] }}
            transition={{ repeat: Infinity, duration: 4.5, ease: "easeInOut" }}
            className="absolute inset-0 rounded-full border border-[#D4AF37]/10 pointer-events-none z-0"
          />
        </div>

        {/* Glassmorphism thank-you card */}
        <motionFramer.div
          initial={{ opacity: 0, y: 15 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.3, duration: 0.8 }}
          className="glassmorphism p-6 sm:p-10 rounded-2xl border border-[#D4AF37]/15 w-full bg-[#1A1515]/40 space-y-8"
        >
          <div className="space-y-6">
            <h2 
              className="font-serif text-xl sm:text-2xl uppercase tracking-widest leading-tight"
              style={{
                background: "linear-gradient(135deg, #D4AF37 0%, #F6D365 40%, #fff8dc 60%, #D4AF37 100%)",
                WebkitBackgroundClip: "text",
                WebkitTextFillColor: "transparent",
                backgroundClip: "text",
                filter: "drop-shadow(0 0 10px rgba(212,175,55,0.25))",
                textShadow: "none",
              }}
            >
              Your Request Has Been Received
            </h2>
            
            <div className="space-y-4 font-sans text-xs sm:text-sm text-[#BDBDBD] font-light leading-relaxed tracking-wide text-center">
              <p>Our team will carefully consider your application before extending invitations.</p>
              <p>Each application is carefully evaluated to preserve the quality of every gathering. If selected, you will receive a private invitation with the next steps.</p>
              <p className="text-[#D4AF37] italic font-serif text-sm sm:text-base tracking-[0.05em] mt-6">
                We appreciate your interest in joining Viora Elite.
              </p>
            </div>
          </div>
        </motionFramer.div>
      </div>

      {/* Return Action */}
      <div className="relative z-10 flex flex-col items-center shrink-0 w-full max-w-xs mx-auto pt-6">
        <motionFramer.button
          onClick={onReturnHome}
          whileTap={{ scale: 0.97 }}
          className="w-full border border-[#D4AF37]/25 hover:border-[#D4AF37]/45 text-[#BDBDBD] font-sans font-semibold text-[8px] uppercase tracking-[0.3em] py-4 rounded-xl shadow-[0_0_15px_rgba(212,175,55,0.03)] cursor-pointer bg-[#1A1515]/40 transition-colors"
        >
          Return to Home
        </motionFramer.button>
      </div>
    </div>
  );
};
