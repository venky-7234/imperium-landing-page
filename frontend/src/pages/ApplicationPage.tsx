import React, { useState, useEffect } from "react";
import { motion as motionFramer } from "framer-motion";
import { ArrowLeft, Send } from "lucide-react";
import { submitApplication } from '../services/api';

interface ApplicationPageProps {
  onSubmit: () => void;
  onBack: () => void;
}

export const ApplicationPage: React.FC<ApplicationPageProps> = ({ onSubmit, onBack }) => {
  useEffect(() => {
    window.scrollTo(0, 0);
  }, []);

  const [formData, setFormData] = useState({
    name: "",
    email: "",
    contactNumber: "",
    dob: "",
    company: "",
    industry: "",
    linkedin: "",
    instagram: "",
    annualRevenue: "",
    yearsInBusiness: "",
    whyAttend: "",
    whatValue: "",
    referredBy: "",
    confirmed: false,
  });

  const [errorMsg, setErrorMsg] = useState("");

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    const { 
      name, email, contactNumber, dob, company, industry, 
      linkedin, instagram, whyAttend, whatValue, confirmed 
    } = formData;

    if (
      !name.trim() ||
      !email.trim() ||
      !contactNumber.trim() ||
      !dob.trim() ||
      !company.trim() ||
      !industry.trim() ||
      !linkedin.trim() ||
      !instagram.trim() ||
      !whyAttend.trim() ||
      !whatValue.trim()
    ) {
      setErrorMsg("All mandatory fields marked with * must be completed.");
      return;
    }

    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    if (!emailRegex.test(email.trim())) {
      setErrorMsg("Please enter a valid secure email address.");
      return;
    }

    if (!confirmed) {
      setErrorMsg("Please confirm that the information provided is accurate.");
      return;
    }

    setErrorMsg("");
    submitApplication(formData)
      .then(() => {
        onSubmit();
      })
      .catch((err) => {
        setErrorMsg(err.message || "An error occurred while submitting.");
      });
  };

  const handleInputChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    setErrorMsg("");
    const { name, value, type } = e.target;
    if (type === "checkbox") {
      const target = e.target as HTMLInputElement;
      setFormData({ ...formData, [name]: target.checked });
    } else {
      setFormData({ ...formData, [name]: value });
    }
  };

  return (
    <div className="min-h-screen w-full bg-[#0A0A0A] text-[#F5F5F5] font-sans flex flex-col items-center justify-start px-4 sm:px-6 py-8 sm:py-20 relative select-none overflow-x-hidden">
      
      {/* Background glow flares */}
      <div className="absolute top-[20%] left-[-10%] w-[300px] h-[300px] rounded-full bg-[#D4AF37]/3 blur-[120px] pointer-events-none z-0" />
      <div className="absolute bottom-[20%] right-[-10%] w-[300px] h-[300px] rounded-full bg-[#D4AF37]/2 blur-[120px] pointer-events-none z-0" />

      {/* Floating Back Navigation */}
      <motionFramer.button
        onClick={onBack}
        initial={{ opacity: 0, x: -20 }}
        animate={{ opacity: 1, x: 0 }}
        transition={{ duration: 0.8, delay: 0.5, ease: "easeOut" }}
        className="fixed bottom-6 left-6 sm:bottom-10 sm:left-10 z-50 group flex items-center justify-center p-3 sm:p-4 rounded-full border border-[#D4AF37]/20 bg-[#0A0A0A]/80 backdrop-blur-sm hover:border-[#D4AF37] hover:bg-[#D4AF37]/10 active:border-[#D4AF37] active:bg-[#D4AF37]/15 active:scale-95 transition-all duration-300"
      >
        <ArrowLeft size={18} className="text-[#D4AF37]/80 group-hover:text-[#F6D365] group-hover:-translate-x-1 group-active:text-[#F6D365] group-active:-translate-x-1 transition-all duration-300" />
      </motionFramer.button>

      <motionFramer.div
        initial={{ opacity: 0, y: 30, filter: "blur(8px)" }}
        animate={{ opacity: 1, y: 0, filter: "blur(0px)" }}
        transition={{ duration: 1.0, ease: [0.16, 1, 0.3, 1] }}
        className="relative z-10 w-full max-w-2xl rounded-2xl p-6 sm:p-10 border border-[#D4AF37]/15 shadow-2xl bg-[#1A1515]/60 mt-4 backdrop-blur-sm"
      >
        {/* Title Header */}
        <div className="text-center mb-10">
          <h2 className="font-serif text-3xl sm:text-4xl text-[#F5F5F5] tracking-[0.1em] uppercase">
            Application Form
          </h2>
          <div className="flex items-center justify-center gap-4 mt-4 mb-2">
            <div className="h-[1px] bg-gradient-to-r from-transparent to-[#D4AF37]/80 w-16" />
            <div className="w-1.5 h-1.5 rotate-45 bg-[#D4AF37] shadow-[0_0_8px_rgba(212,175,55,0.6)]" />
            <div className="h-[1px] bg-gradient-to-l from-transparent to-[#D4AF37]/80 w-16" />
          </div>
          <p className="font-sans text-[10px] sm:text-xs text-[#BDBDBD]/60 tracking-widest uppercase mt-4">
            Curated exclusively for visionary founders and leaders
          </p>
        </div>

        {/* Form elements */}
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
            
            {/* Name */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
                Name *
              </label>
              <input
                type="text"
                name="name"
                value={formData.name}
                onChange={handleInputChange}
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>

            {/* Email */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
                Email *
              </label>
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleInputChange}
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>

            {/* Contact Number */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
                Contact Number *
              </label>
              <input
                type="tel"
                name="contactNumber"
                value={formData.contactNumber}
                onChange={handleInputChange}
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>

            {/* DOB */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
                DOB *
              </label>
              <input
                type="date"
                name="dob"
                value={formData.dob}
                onChange={handleInputChange}
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all [color-scheme:dark]"
              />
            </div>

            {/* Company */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
                Company *
              </label>
              <input
                type="text"
                name="company"
                value={formData.company}
                onChange={handleInputChange}
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>

            {/* Industry */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
                Industry *
              </label>
              <input
                type="text"
                name="industry"
                value={formData.industry}
                onChange={handleInputChange}
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>

            {/* LinkedIn */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
                LinkedIn *
              </label>
              <input
                type="url"
                name="linkedin"
                value={formData.linkedin}
                onChange={handleInputChange}
                placeholder="https://linkedin.com/in/..."
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>

            {/* Instagram */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
                Instagram *
              </label>
              <input
                type="text"
                name="instagram"
                value={formData.instagram}
                onChange={handleInputChange}
                placeholder="@username"
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>

            {/* Annual Revenue */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold text-opacity-80">
                Annual Revenue <span className="text-[#BDBDBD]/50 normal-case tracking-normal">(optional)</span>
              </label>
              <input
                type="text"
                name="annualRevenue"
                value={formData.annualRevenue}
                onChange={handleInputChange}
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>

            {/* Years in Business */}
            <div className="space-y-1.5">
              <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold text-opacity-80">
                Years in Business <span className="text-[#BDBDBD]/50 normal-case tracking-normal">(optional)</span>
              </label>
              <input
                type="text"
                name="yearsInBusiness"
                value={formData.yearsInBusiness}
                onChange={handleInputChange}
                className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
              />
            </div>
          </div>

          {/* Why do you want to attend? */}
          <div className="space-y-1.5 mt-6">
            <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
              Why do you want to attend? *
            </label>
            <textarea
              name="whyAttend"
              value={formData.whyAttend}
              onChange={handleInputChange}
              rows={3}
              className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3.5 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all resize-none"
            />
          </div>

          {/* What Value can you Bring? */}
          <div className="space-y-1.5">
            <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold">
              What value can you bring? *
            </label>
            <textarea
              name="whatValue"
              value={formData.whatValue}
              onChange={handleInputChange}
              rows={3}
              className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3.5 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all resize-none"
            />
          </div>

          {/* Who referred you? */}
          <div className="space-y-1.5">
            <label className="font-sans text-[9px] sm:text-[10px] tracking-wider text-[#D4AF37] uppercase font-semibold text-opacity-80">
              Who referred you? <span className="text-[#BDBDBD]/50 normal-case tracking-normal">(optional)</span>
            </label>
            <input
              type="text"
              name="referredBy"
              value={formData.referredBy}
              onChange={handleInputChange}
              className="w-full bg-[#0A0A0A]/60 border border-[#D4AF37]/15 focus:border-[#D4AF37] py-3 px-4 rounded-lg text-sm text-[#F5F5F5] placeholder-[#BDBDBD]/30 focus:outline-none focus:ring-1 focus:ring-[#D4AF37] transition-all"
            />
          </div>

          {/* Consent Checkbox */}
          <div className="flex items-start gap-4 pt-4">
            <div className="flex items-center h-5">
              <input
                type="checkbox"
                name="confirmed"
                checked={formData.confirmed}
                onChange={handleInputChange}
                className="w-4 h-4 rounded border-[#D4AF37]/30 bg-[#0A0A0A] text-[#D4AF37] focus:ring-[#D4AF37] focus:ring-offset-0 focus:ring-1 cursor-pointer appearance-none checked:bg-[#D4AF37] relative after:content-[''] after:absolute after:hidden checked:after:block after:left-[5px] after:top-[2px] after:w-[5px] after:h-[10px] after:border-r-[2px] after:border-b-[2px] after:border-[#0A0A0A] after:rotate-45"
              />
            </div>
            <label className="font-sans text-xs text-[#BDBDBD]/80 leading-tight pt-[1px] cursor-pointer" onClick={() => setFormData({ ...formData, confirmed: !formData.confirmed })}>
              I confirm that the information provided is accurate and agree to maintaining the discretion expected within the community.
            </label>
          </div>

          {/* Error Message */}
          {errorMsg && (
            <motionFramer.div
              initial={{ opacity: 0, y: -10 }}
              animate={{ opacity: 1, y: 0 }}
              className="text-red-400/90 text-xs font-sans tracking-wide p-3 bg-red-900/10 border border-red-900/20 rounded-lg text-center"
            >
              {errorMsg}
            </motionFramer.div>
          )}

          {/* Submit Button */}
          <div className="pt-6">
            <button
              type="submit"
              className="group relative w-full px-8 py-4 bg-[#D4AF37]/10 overflow-hidden rounded-lg transition-all duration-500 ease-out border border-[#D4AF37]/40 hover:border-[#D4AF37] hover:shadow-[0_0_20px_rgba(212,175,55,0.2)] active:border-[#D4AF37] active:shadow-[0_0_20px_rgba(212,175,55,0.2)] active:scale-[0.99] flex items-center justify-center gap-3"
            >
              <div className="absolute inset-0 bg-[#D4AF37]/5 group-hover:bg-[#D4AF37]/10 group-active:bg-[#D4AF37]/15 transition-colors duration-500" />
              <span className="relative z-10 font-sans text-xs sm:text-sm tracking-[0.2em] text-[#D4AF37] uppercase font-semibold group-hover:text-[#F6D365] transition-colors duration-300">
                Request an Invitation
              </span>
              <Send size={14} className="relative z-10 text-[#D4AF37] group-hover:text-[#F6D365] transition-colors duration-300" />
            </button>
          </div>
          
        </form>
      </motionFramer.div>
    </div>
  );
};
