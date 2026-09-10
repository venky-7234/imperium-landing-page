import React, { useEffect, useState, useRef } from "react";
import { motion as motionFramer } from "framer-motion";
import { ChevronDown, Volume2, VolumeX } from "lucide-react";
import logo from '../assets/logo-01.svg';

interface LandingPageProps {
  onNextPage: () => void;
}

export const LandingPage: React.FC<LandingPageProps> = ({ onNextPage }) => {
  const [isPlaying, setIsPlaying] = useState(true);
  const audioRef = useRef<HTMLAudioElement | null>(null);

  useEffect(() => {
    window.scrollTo(0, 0);

    // Auto-play audio on mount
    if (audioRef.current) {
      audioRef.current.play()
        .then(() => setIsPlaying(true))
        .catch(() => {
          // If browser blocks unmuted autoplay, trigger playback on first user interaction
          const startAudioOnInteraction = () => {
            if (audioRef.current) {
              audioRef.current.play()
                .then(() => setIsPlaying(true))
                .catch(() => {});
            }
            window.removeEventListener("click", startAudioOnInteraction);
            window.removeEventListener("touchstart", startAudioOnInteraction);
            window.removeEventListener("keydown", startAudioOnInteraction);
          };
          window.addEventListener("click", startAudioOnInteraction);
          window.addEventListener("touchstart", startAudioOnInteraction);
          window.addEventListener("keydown", startAudioOnInteraction);
        });
    }
  }, []);

  const toggleAudio = () => {
    if (!audioRef.current) return;
    if (isPlaying) {
      audioRef.current.pause();
      setIsPlaying(false);
    } else {
      audioRef.current.play()
        .then(() => setIsPlaying(true))
        .catch(err => console.log("Audio playback error:", err));
    }
  };

  const handleScrollDown = () => {
    window.scrollTo({
      top: window.innerHeight,
      behavior: "smooth"
    });
  };

  return (
    <div className="w-full bg-[#0A0A0A] text-[#F5F5F5] font-sans overflow-x-hidden selection:bg-[#D4AF37] selection:text-[#0A0A0A]">
      
      {/* Ambient background audio */}
      <audio
        ref={audioRef}
        autoPlay
        loop
        preload="auto"
        src="/audio/hero_ambient.mpeg"
      />

      {/* ────────────────────────────────────────────────
          SECTION 1: HERO
          ──────────────────────────────────────────────── */}
      <section className="relative h-screen w-full flex flex-col items-center justify-center px-6 overflow-hidden z-10">

        {/* Round Floating Luxury Sound ON/OFF Button (Top Right) */}
        <motionFramer.button
          onClick={toggleAudio}
          initial={{ opacity: 0, scale: 0.8 }}
          animate={{ opacity: 1, scale: 1 }}
          transition={{ duration: 0.8, delay: 0.8 }}
          className="fixed top-6 right-6 sm:top-8 sm:right-10 z-50 w-11 h-11 sm:w-12 sm:h-12 rounded-full border border-[#D4AF37]/40 bg-[#0A0A0A]/85 backdrop-blur-md flex items-center justify-center shadow-[0_0_20px_rgba(212,175,55,0.25)] hover:border-[#D4AF37] hover:scale-105 active:scale-95 transition-all duration-300 group cursor-pointer"
          title={isPlaying ? "Mute Ambient Audio" : "Play Ambient Audio"}
          aria-label={isPlaying ? "Mute ambient music" : "Play ambient music"}
        >
          {isPlaying ? (
            <div className="flex items-end justify-center gap-[2.5px] h-4 w-4">
              <span className="w-[2px] bg-[#D4AF37] group-hover:bg-[#F6D365] animate-[pulse_0.7s_ease-in-out_infinite] h-full" />
              <span className="w-[2px] bg-[#F6D365] animate-[pulse_1.1s_ease-in-out_infinite_0.15s] h-2/3" />
              <span className="w-[2px] bg-[#D4AF37] group-hover:bg-[#F6D365] animate-[pulse_0.85s_ease-in-out_infinite_0.3s] h-4/5" />
            </div>
          ) : (
            <VolumeX size={18} className="text-[#D4AF37]/70 group-hover:text-[#F6D365] transition-colors" />
          )}
        </motionFramer.button>

        {/* looping background zoom */}
        <motionFramer.div
          className="absolute inset-0 bg-cover bg-center bg-no-repeat opacity-100"
          style={{ backgroundImage: "url('/images/hero_bg.png')" }}
          animate={{ scale: [1, 1.05, 1] }}
          transition={{ repeat: Infinity, duration: 25, ease: "easeInOut" }}
        />
        <div className="absolute inset-0 bg-gradient-to-b from-[#0A0A0A]/10 via-transparent to-[#0A0A0A]/90 z-0" />
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_center,rgba(212,175,55,0.03)_0%,rgba(0,0,0,0)_60%)] z-0" />

        {/* ── Luxury corner decorations ───────────────────────────────── */}
        {(["tl","tr","bl","br"] as const).map((pos, i) => (
          <svg
            key={pos}
            className={`hero-corner hero-corner--${pos}`}
            viewBox="0 0 70 70"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            style={{ '--shimmer-delay': `${i * 1.8}s` } as React.CSSProperties}
          >
            <defs>
              <linearGradient id={`goldGrad-${pos}`} x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%"   stopColor="#D4AF37" stopOpacity="0.9" />
                <stop offset="100%" stopColor="#F6D365" stopOpacity="0.6" />
              </linearGradient>
              <filter id={`glow-${pos}`} x="-40%" y="-40%" width="180%" height="180%">
                <feGaussianBlur stdDeviation="2.5" result="blur" />
                <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
              </filter>
            </defs>

            {/* Base L-shaped lines */}
            <g filter={`url(#glow-${pos})`} className="corner-line-group" style={{ '--shimmer-delay': `${i * 1.8}s` } as React.CSSProperties}>
              <line x1="8" y1="8" x2="58" y2="8" stroke={`url(#goldGrad-${pos})`} strokeWidth="1" strokeLinecap="round" />
              <line x1="8" y1="8" x2="8"  y2="58" stroke={`url(#goldGrad-${pos})`} strokeWidth="1" strokeLinecap="round" />

              <line
                className="corner-shimmer-h"
                x1="8" y1="8" x2="58" y2="8"
                stroke="#F6D365" strokeWidth="1.5" strokeLinecap="round"
                style={{ '--shimmer-delay': `${i * 1.8}s` } as React.CSSProperties}
              />
              <line
                className="corner-shimmer-v"
                x1="8" y1="8" x2="8" y2="58"
                stroke="#F6D365" strokeWidth="1.5" strokeLinecap="round"
                style={{ '--shimmer-delay': `${i * 1.8}s` } as React.CSSProperties}
              />

              <line x1="14" y1="14" x2="44" y2="14" stroke="#D4AF37" strokeWidth="0.4" strokeLinecap="round" opacity="0.5" />
              <line x1="14" y1="14" x2="14" y2="44" stroke="#D4AF37" strokeWidth="0.4" strokeLinecap="round" opacity="0.5" />
            </g>

            {/* Glowing star at corner intersection */}
            <g filter={`url(#glow-${pos})`}>
              <circle cx="8" cy="8" r="2.2" fill="#F6D365" opacity="0.95" />
              <circle cx="8" cy="8" r="1"   fill="#fff"    opacity="0.8" />
            </g>

            {/* Tiny floating particles */}
            <circle className="corner-particle" cx="28" cy="6"  r="0.9" fill="#D4AF37"
              style={{ '--pdur':'4.2s','--pdelay':'0s','--dx':'3px','--dy':'-5px','--dx2':'6px','--dy2':'-10px' } as React.CSSProperties} />
            <circle className="corner-particle" cx="6"  cy="30" r="0.7" fill="#F6D365"
              style={{ '--pdur':'5.5s','--pdelay':'1.1s','--dx':'-4px','--dy':'-4px','--dx2':'-7px','--dy2':'-9px' } as React.CSSProperties} />
            <circle className="corner-particle" cx="40" cy="10" r="0.6" fill="#D4AF37"
              style={{ '--pdur':'3.8s','--pdelay':'2.3s','--dx':'2px','--dy':'-7px','--dx2':'4px','--dy2':'-13px' } as React.CSSProperties} />
            <circle className="corner-particle" cx="10" cy="42" r="0.8" fill="#F6D365"
              style={{ '--pdur':'6.1s','--pdelay':'0.7s','--dx':'-3px','--dy':'-6px','--dx2':'-5px','--dy2':'-11px' } as React.CSSProperties} />
          </svg>
        ))}
        {/* ── End corner decorations ───────────────────────────────────── */}

        <motionFramer.div 
          initial={{ opacity: 0, y: 30 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ duration: 1.2, delay: 0.3 }}
          className="flex flex-col items-center text-center space-y-12 relative z-10 w-full"
        >
          {/* Main title block — vertically centered and evenly spaced */}
          <div className="flex flex-col items-center gap-6 w-full mt-8 sm:mt-12">
            
            {/* Logo — replaces top small text ("VIORA ELITE / INVITE ONLY") */}
            <div className="flex items-center justify-center pointer-events-none select-none -mt-2 mb-1">
              <img
                src={logo}
                alt="Viora Logo"
                className="w-36 sm:w-48 md:w-56 h-auto object-contain filter drop-shadow-[0_0_12px_rgba(212,175,55,0.25)]"
              />
            </div>

            {/* THE IMPERIUM — dominant metallic centrepiece */}
            <div className="flex flex-col items-center gap-2 w-full mt-1">
              <div className="flex items-center gap-4 sm:gap-6 w-full justify-center">
                <div className="h-[1.5px] bg-[#D4AF37]/60 grow max-w-[40px] sm:max-w-[70px]" />
                <h1
                  className="font-serif text-4xl sm:text-7xl md:text-8xl tracking-[0.2em] sm:tracking-[0.3em] uppercase font-semibold shrink-0 pl-[0.2em] sm:pl-[0.3em]"
                  style={{
                    background: "linear-gradient(135deg, #D4AF37 0%, #F6D365 40%, #fff8dc 60%, #D4AF37 100%)",
                    WebkitBackgroundClip: "text",
                    WebkitTextFillColor: "transparent",
                    backgroundClip: "text",
                    filter: "drop-shadow(0 0 10px rgba(212,175,55,0.2))",
                    textShadow: "none",
                  }}
                >
                  THE IMPERIUM
                </h1>
                <div className="h-[1.5px] bg-[#D4AF37]/60 grow max-w-[40px] sm:max-w-[70px]" />
              </div>

              {/* Tagline under THE IMPERIUM */}
              <div className="flex items-center gap-3 mt-1">
                <div className="h-[0.5px] w-6 sm:w-10 bg-[#D4AF37]/40" />
                <span className="font-sans text-[9px] sm:text-[11px] tracking-[0.45em] text-[#D4AF37]/80 uppercase font-medium pl-[0.45em]">
                  Founders Dine &amp; Legacy
                </span>
                <div className="h-[0.5px] w-6 sm:w-10 bg-[#D4AF37]/40" />
              </div>
            </div>

            {/* Italic quote */}
            <span className="font-serif text-sm sm:text-base md:text-lg tracking-wide text-[#F5F5F5]/85 font-light max-w-xs sm:max-w-md leading-snug italic mt-6">
              &ldquo;Where Remarkable Minds Find Their Echo&rdquo;
            </span>

            {/* Description */}
            <p className="font-sans text-[11px] sm:text-sm text-[#BDBDBD] font-light leading-relaxed tracking-wider max-w-sm sm:max-w-md mx-auto">
              An invitation-only private residence experience by Viora. Crafted for the founders, investors, and visionaries who understand that the greatest breakthroughs begin with the right company.
            </p>
          </div>

          {/* Scroll indicator */}
          <motionFramer.div
            initial={{ opacity: 0 }}
            animate={{ opacity: [0, 1, 0] }}
            transition={{ repeat: Infinity, duration: 2.5, delay: 2 }}
            className="flex flex-col items-center gap-1 cursor-pointer text-[#BDBDBD]/30 mt-8"
            onClick={handleScrollDown}
          >
            <span className="font-sans text-[7px] tracking-[0.2em] uppercase">Scroll to Discover</span>
            <ChevronDown size={12} className="animate-bounce" />
          </motionFramer.div>
        </motionFramer.div>

      </section>

      {/* ────────────────────────────────────────────────
          SECTION 1.5: THE PHILOSOPHY
          ──────────────────────────────────────────────── */}
      <section className="relative py-24 sm:py-32 px-6 md:px-12 bg-[#0A0A0A] flex flex-col items-center justify-center text-center overflow-hidden">

        {/* ── Luxury corner decorations ───────────────────────────────── */}
        {(["tl","tr","bl","br"] as const).map((pos, i) => (
          <svg
            key={pos}
            className={`hero-corner hero-corner--${pos}`}
            viewBox="0 0 70 70"
            fill="none"
            xmlns="http://www.w3.org/2000/svg"
            style={{ '--shimmer-delay': `${i * 1.8}s` } as React.CSSProperties}
          >
            <defs>
              <linearGradient id={`goldGrad-p-${pos}`} x1="0%" y1="0%" x2="100%" y2="100%">
                <stop offset="0%"   stopColor="#D4AF37" stopOpacity="0.9" />
                <stop offset="100%" stopColor="#F6D365" stopOpacity="0.6" />
              </linearGradient>
              <filter id={`glow-p-${pos}`} x="-40%" y="-40%" width="180%" height="180%">
                <feGaussianBlur stdDeviation="2.5" result="blur" />
                <feMerge><feMergeNode in="blur"/><feMergeNode in="SourceGraphic"/></feMerge>
              </filter>
            </defs>

            {/* Base L-shaped lines */}
            <g filter={`url(#glow-p-${pos})`} className="corner-line-group" style={{ '--shimmer-delay': `${i * 1.8}s` } as React.CSSProperties}>
              <line x1="8" y1="8" x2="58" y2="8" stroke={`url(#goldGrad-p-${pos})`} strokeWidth="1" strokeLinecap="round" />
              <line x1="8" y1="8" x2="8"  y2="58" stroke={`url(#goldGrad-p-${pos})`} strokeWidth="1" strokeLinecap="round" />

              <line className="corner-shimmer-h" x1="8" y1="8" x2="58" y2="8" stroke="#F6D365" strokeWidth="1.5" strokeLinecap="round" style={{ '--shimmer-delay': `${i * 1.8}s` } as React.CSSProperties} />
              <line className="corner-shimmer-v" x1="8" y1="8" x2="8" y2="58" stroke="#F6D365" strokeWidth="1.5" strokeLinecap="round" style={{ '--shimmer-delay': `${i * 1.8}s` } as React.CSSProperties} />

              <line x1="14" y1="14" x2="44" y2="14" stroke="#D4AF37" strokeWidth="0.4" strokeLinecap="round" opacity="0.5" />
              <line x1="14" y1="14" x2="14" y2="44" stroke="#D4AF37" strokeWidth="0.4" strokeLinecap="round" opacity="0.5" />
            </g>

            <g filter={`url(#glow-p-${pos})`}>
              <circle cx="8" cy="8" r="2.2" fill="#F6D365" opacity="0.95" />
              <circle cx="8" cy="8" r="1"   fill="#fff"    opacity="0.8" />
            </g>

            <circle className="corner-particle" cx="28" cy="6"  r="0.9" fill="#D4AF37" style={{ '--pdur':'4.2s','--pdelay':'0s','--dx':'3px','--dy':'-5px','--dx2':'6px','--dy2':'-10px' } as React.CSSProperties} />
            <circle className="corner-particle" cx="6"  cy="30" r="0.7" fill="#F6D365" style={{ '--pdur':'5.5s','--pdelay':'1.1s','--dx':'-4px','--dy':'-4px','--dx2':'-7px','--dy2':'-9px' } as React.CSSProperties} />
            <circle className="corner-particle" cx="40" cy="10" r="0.6" fill="#D4AF37" style={{ '--pdur':'3.8s','--pdelay':'2.3s','--dx':'2px','--dy':'-7px','--dx2':'4px','--dy2':'-13px' } as React.CSSProperties} />
            <circle className="corner-particle" cx="10" cy="42" r="0.8" fill="#F6D365" style={{ '--pdur':'6.1s','--pdelay':'0.7s','--dx':'-3px','--dy':'-6px','--dx2':'-5px','--dy2':'-11px' } as React.CSSProperties} />
          </svg>
        ))}
        {/* ── End corner decorations ───────────────────────────────────── */}

        <div className="max-w-4xl mx-auto space-y-12 sm:space-y-16 relative z-10 px-4">
          
          <motionFramer.h3 
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 1, ease: "easeOut" }}
            className="font-serif text-3xl sm:text-4xl md:text-5xl uppercase tracking-[0.1em] leading-tight"
            style={{
              background: "linear-gradient(135deg, #D4AF37 0%, #F6D365 40%, #fff8dc 60%, #D4AF37 100%)",
              WebkitBackgroundClip: "text",
              WebkitTextFillColor: "transparent",
              backgroundClip: "text",
              filter: "drop-shadow(0 0 15px rgba(212,175,55,0.3))",
              textShadow: "none",
            }}
          >
            Some Rooms Are Meant<br/>To Be Remembered.
          </motionFramer.h3>

          {/* Decorative Divider */}
          <motionFramer.div
            initial={{ opacity: 0, scale: 0.8 }}
            whileInView={{ opacity: 1, scale: 1 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 1, delay: 0.1, ease: "easeOut" }}
            className="flex items-center justify-center gap-4 w-full max-w-[200px] sm:max-w-[280px] mx-auto -mt-4 sm:-mt-6 mb-4 sm:mb-6"
          >
            <div className="h-[1px] bg-gradient-to-r from-transparent to-[#D4AF37]/80 grow" />
            <div className="w-1.5 h-1.5 rotate-45 bg-[#D4AF37] shadow-[0_0_8px_rgba(212,175,55,0.6)]" />
            <div className="h-[1px] bg-gradient-to-l from-transparent to-[#D4AF37]/80 grow" />
          </motionFramer.div>

          <motionFramer.div 
            initial={{ opacity: 0, y: 20 }}
            whileInView={{ opacity: 1, y: 0 }}
            viewport={{ once: true, margin: "-100px" }}
            transition={{ duration: 1, delay: 0.2, ease: "easeOut" }}
            className="font-sans text-[#BDBDBD]/80 text-[13px] sm:text-[15px] tracking-wide font-light max-w-2xl mx-auto"
          >
            <div className="space-y-3 mb-10 sm:mb-12">
              <span className="block">There are spaces where conversations happen.</span>
              <span className="block">There are rooms where decisions are made.</span>
              <span className="block">And then, there are environments that quietly alter your trajectory.</span>
            </div>
            
            <div className="font-serif text-2xl sm:text-4xl italic tracking-[0.05em] mb-10 text-[#D4AF37] drop-shadow-[0_0_15px_rgba(212,175,55,0.3)]">
              The Imperium belongs to the latter.
            </div>

            <div className="leading-relaxed text-[#BDBDBD]/70 text-xs sm:text-sm">
              Created for those who value depth over introductions, presence over prestige, and resonance over attention. An evening that cannot be replicated, because it is defined entirely by the individuals within it.
            </div>

            {/* Request Access Button */}
            <div className="mt-12 sm:mt-16 pt-4 flex justify-center">
              <button
                onClick={onNextPage}
                className="group relative px-10 py-4 bg-transparent overflow-hidden rounded-sm transition-all duration-500 ease-out border border-[#D4AF37]/40 hover:border-[#D4AF37] hover:shadow-[0_0_20px_rgba(212,175,55,0.2)] hover:-translate-y-1 active:border-[#D4AF37] active:shadow-[0_0_20px_rgba(212,175,55,0.2)] active:scale-[0.98]"
              >
                <div className="absolute inset-0 bg-[#D4AF37]/5 group-hover:bg-[#D4AF37]/10 group-active:bg-[#D4AF37]/15 transition-colors duration-500" />
                <span className="relative z-10 font-sans text-xs sm:text-sm tracking-[0.2em] text-[#D4AF37] uppercase font-medium group-hover:text-[#F6D365] group-active:text-[#F6D365] transition-colors duration-300">
                  Request Access
                </span>
              </button>
            </div>
          </motionFramer.div>
        </div>


      </section>

    </div>
  );
};

