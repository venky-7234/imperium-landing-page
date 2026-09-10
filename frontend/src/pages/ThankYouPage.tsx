import React from "react";
import { motion as motionFramer } from "framer-motion";
import { ArrowLeft } from "lucide-react";
import webInvitation from "../assets/web invitations.png";

interface ThankYouPageProps {
  onReturnHome: () => void;
  onBack?: () => void;
}

export const ThankYouPage: React.FC<ThankYouPageProps> = ({ onReturnHome, onBack }) => {
  return (
    <div className="relative min-h-screen w-full flex flex-col items-center justify-between bg-[#0A0A0A] overflow-hidden select-none">

      {/* Background glow */}
      <div className="absolute top-1/2 left-1/2 -translate-x-1/2 -translate-y-1/2 w-[500px] h-[500px] rounded-full bg-[#D4AF37]/5 blur-[140px] pointer-events-none" />

      {/* Back Arrow */}
      {onBack && (
        <motionFramer.button
          onClick={onBack}
          initial={{ opacity: 0, x: -20 }}
          animate={{ opacity: 1, x: 0 }}
          transition={{ duration: 0.8, delay: 0.4, ease: "easeOut" }}
          className="absolute top-6 left-6 sm:top-10 sm:left-10 z-50 group flex items-center justify-center p-3 sm:p-4 rounded-full border border-[#D4AF37]/20 bg-[#0A0A0A]/80 backdrop-blur-sm hover:border-[#D4AF37] hover:bg-[#D4AF37]/10 active:scale-95 transition-all duration-300"
        >
          <ArrowLeft size={18} className="text-[#D4AF37]/80 group-hover:text-[#F6D365] group-hover:-translate-x-1 transition-all duration-300" />
        </motionFramer.button>
      )}

      {/* Invitation Image */}
      <motionFramer.div
        initial={{ opacity: 0, scale: 0.97 }}
        animate={{ opacity: 1, scale: 1 }}
        transition={{ duration: 1, ease: [0.16, 1, 0.3, 1] }}
        className="relative z-10 w-full flex-1 flex flex-col items-center justify-center px-4 sm:px-8 pt-16 pb-4"
      >
        <img
          src={webInvitation}
          alt="Viora Elite – Invitation"
          className="w-full max-w-xs sm:max-w-md md:max-w-lg lg:max-w-xl xl:max-w-2xl h-auto object-contain drop-shadow-[0_0_40px_rgba(212,175,55,0.15)]"
        />
      </motionFramer.div>

      {/* Return Home Button */}
      <motionFramer.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        transition={{ delay: 0.6, duration: 0.8, ease: "easeOut" }}
        className="relative z-10 w-full flex flex-col items-center px-6 pb-10 sm:pb-12"
      >
        <motionFramer.button
          onClick={onReturnHome}
          whileTap={{ scale: 0.97 }}
          className="w-full max-w-xs border border-[#D4AF37]/25 hover:border-[#D4AF37]/50 text-[#BDBDBD] hover:text-[#D4AF37] font-sans font-semibold text-[8px] uppercase tracking-[0.3em] py-4 rounded-xl bg-[#1A1515]/40 hover:bg-[#D4AF37]/5 backdrop-blur-sm transition-all duration-300 cursor-pointer"
        >
          Return to Home
        </motionFramer.button>
      </motionFramer.div>

    </div>
  );
};
