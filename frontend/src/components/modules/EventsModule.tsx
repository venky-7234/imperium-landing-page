import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Plus, Edit2, Archive, Trash2, PowerOff, CheckCircle2,
  ChevronLeft, ChevronRight, MoreVertical, Copy
} from 'lucide-react';
import { fetchEvents, createEvent, updateEvent, changeEventStatus, deleteEvent } from '../../services/api';

interface EventsModuleProps {
  token: string;
}

const STATUS_STYLE: Record<string, string> = {
  ACTIVE:   'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
  ARCHIVED: 'bg-gray-500/10   text-gray-400   border-gray-500/20',
  INACTIVE: 'bg-red-500/10    text-red-400    border-red-500/20',
  DRAFT:    'bg-yellow-500/10 text-yellow-400  border-yellow-500/20',
};

const EMPTY_FORM = {
  title: '', description: '', venue: '', venueAddress: '', city: '', country: '',
  startDateTime: '', endDateTime: '', maxGuests: 0, rsvpDeadline: '',
  eventType: 'CORPORATE', isPublic: false, theme: '', landingPageUrl: '',
  applicationFormUrl: '', registrationStart: '', registrationEnd: '',
  logoUrl: '', bannerUrl: '', invitationTemplate: '', emailTemplate: '', whatsappTemplate: ''
};

const EventsModuleBase: React.FC<EventsModuleProps> = ({ token }) => {
  const [events, setEvents] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [page, setPage] = useState(0);
  const [totalElements, setTotalElements] = useState(0);
  const PAGE_SIZE = 20;

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [selectedEvent, setSelectedEvent] = useState<any | null>(null);
  const [formData, setFormData] = useState({ ...EMPTY_FORM });

  const [openMenuId, setOpenMenuId] = useState<string | null>(null);
  const menuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setOpenMenuId(null);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const load = async () => {
    setLoading(true);
    try {
      const res = await fetchEvents(token, page, PAGE_SIZE);
      setEvents(res.data?.content || []);
      setTotalElements(res.data?.totalElements || 0);
    } catch (e) { console.error(e); }
    finally { setLoading(false); }
  };

  useEffect(() => { load(); }, [page, token]);

  const openModal = (event?: any) => {
    setOpenMenuId(null);
    if (event) {
      setSelectedEvent(event);
      setFormData({
        title: event.title || '', description: event.description || '',
        venue: event.venue || '', venueAddress: event.venueAddress || '',
        city: event.city || '', country: event.country || '',
        startDateTime: event.startDateTime || '', endDateTime: event.endDateTime || '',
        maxGuests: event.maxGuests || 0, rsvpDeadline: event.rsvpDeadline || '',
        eventType: event.eventType || 'CORPORATE', isPublic: event.isPublic || false,
        theme: event.theme || '', landingPageUrl: event.landingPageUrl || '',
        applicationFormUrl: event.applicationFormUrl || '', registrationStart: event.registrationStart || '',
        registrationEnd: event.registrationEnd || '', logoUrl: event.logoUrl || '',
        bannerUrl: event.bannerUrl || '', invitationTemplate: event.invitationTemplate || '',
        emailTemplate: event.emailTemplate || '', whatsappTemplate: event.whatsappTemplate || ''
      });
    } else {
      setSelectedEvent(null);
      setFormData({ ...EMPTY_FORM });
    }
    setIsModalOpen(true);
  };

  const handleDuplicate = (event: any) => {
    setOpenMenuId(null);
    setSelectedEvent(null);
    setFormData({
      title: `${event.title} (Copy)`, description: event.description || '',
      venue: event.venue || '', venueAddress: event.venueAddress || '',
      city: event.city || '', country: event.country || '',
      startDateTime: '', endDateTime: '',
      maxGuests: event.maxGuests || 0, rsvpDeadline: '',
      eventType: event.eventType || 'CORPORATE', isPublic: event.isPublic || false,
      theme: event.theme || '', landingPageUrl: event.landingPageUrl || '',
      applicationFormUrl: event.applicationFormUrl || '', registrationStart: '',
      registrationEnd: '', logoUrl: event.logoUrl || '',
      bannerUrl: event.bannerUrl || '', invitationTemplate: event.invitationTemplate || '',
      emailTemplate: event.emailTemplate || '', whatsappTemplate: event.whatsappTemplate || ''
    });
    setIsModalOpen(true);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      if (selectedEvent) await updateEvent(selectedEvent.publicId, formData, token);
      else await createEvent(formData, token);
      setIsModalOpen(false);
      load();
    } catch { alert('Failed to save event'); }
  };

  const handleStatusChange = async (publicId: string, action: 'publish' | 'cancel' | 'archive' | 'deactivate') => {
    setOpenMenuId(null);
    try {
      await changeEventStatus(publicId, action, token);
      load();
    } catch { alert(`Failed to ${action} event`); }
  };

  const handleDelete = async (publicId: string) => {
    setOpenMenuId(null);
    if (!window.confirm('Delete this event?')) return;
    try { await deleteEvent(publicId, token); load(); }
    catch { /* ignore */ }
  };

  const f = (key: keyof typeof formData, val: any) => setFormData(prev => ({ ...prev, [key]: val }));

  return (
    <div className="space-y-5 max-w-7xl">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-[#F5F5F5]">Events</h2>
          <p className="text-xs text-[#555] mt-0.5">Create and manage platform events.</p>
        </div>
        <button
          onClick={() => openModal()}
          className="flex items-center gap-2 bg-[#C5A059] text-black px-4 py-2 rounded-lg font-semibold text-sm hover:bg-[#D4B86A] transition-colors"
        >
          <Plus size={15} /> Create Event
        </button>
      </div>

      {/* Table */}
      <div className="bg-[#0d0d0d] border border-[#1a1a1a] rounded-xl overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-[#1a1a1a] bg-[#0a0a0a]">
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Event</th>
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Date</th>
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Venue</th>
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Capacity</th>
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Status</th>
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest text-right">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#111]">
            {loading ? (
              <tr><td colSpan={6} className="py-16 text-center text-[#555] text-sm">Loading…</td></tr>
            ) : events.length === 0 ? (
              <tr><td colSpan={6} className="py-16 text-center text-[#555] text-sm">No events found.</td></tr>
            ) : events.map(ev => (
              <tr key={ev.publicId} className="hover:bg-[#111]/50 transition-colors group">
                <td className="px-5 py-3.5">
                  <p className="text-sm font-semibold text-[#DDD]">{ev.title}</p>
                  <p className="text-[11px] text-[#555]">{ev.eventType}</p>
                </td>
                <td className="px-5 py-3.5 text-xs text-[#888]">
                  {ev.startDateTime ? new Date(ev.startDateTime).toLocaleDateString('en-GB', { day: 'numeric', month: 'short', year: 'numeric' }) : '—'}
                </td>
                <td className="px-5 py-3.5">
                  <p className="text-xs text-[#888]">{ev.venue || '—'}</p>
                  <p className="text-[11px] text-[#555]">{ev.city}</p>
                </td>
                <td className="px-5 py-3.5 text-xs text-[#888]">
                  {ev.maxGuests ? ev.maxGuests.toLocaleString() : '—'}
                </td>
                <td className="px-5 py-3.5">
                  <span className={`px-2 py-0.5 text-[10px] font-semibold rounded-full border ${STATUS_STYLE[ev.status] || STATUS_STYLE.DRAFT}`}>
                    {ev.status}
                  </span>
                </td>
                <td className="px-5 py-3.5 text-right relative" ref={openMenuId === ev.publicId ? (menuRef as any) : undefined}>
                  <button
                    onClick={() => setOpenMenuId(openMenuId === ev.publicId ? null : ev.publicId)}
                    className="p-1.5 rounded-lg text-[#555] hover:text-[#CCC] hover:bg-[#1a1a1a] transition-colors opacity-0 group-hover:opacity-100"
                  >
                    <MoreVertical size={15} />
                  </button>
                  <AnimatePresence>
                    {openMenuId === ev.publicId && (
                      <motion.div
                        initial={{ opacity: 0, scale: 0.95, y: -4 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.95, y: -4 }}
                        transition={{ duration: 0.12 }}
                        className="absolute right-4 top-full mt-1 w-48 bg-[#111] border border-[#222] rounded-xl shadow-2xl z-50 overflow-hidden"
                      >
                        <button
                          onClick={() => openModal(ev)}
                          className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                        >
                          <Edit2 size={13} className="text-[#C5A059]" /> Edit
                        </button>
                        <button
                          onClick={() => handleDuplicate(ev)}
                          className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                        >
                          <Copy size={13} className="text-blue-400" /> Duplicate
                        </button>
                        <div className="border-t border-[#1a1a1a] mx-3 my-1" />
                        {ev.status !== 'ACTIVE' && (
                          <button
                            onClick={() => handleStatusChange(ev.publicId, 'publish')}
                            className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                          >
                            <CheckCircle2 size={13} className="text-emerald-400" /> Publish
                          </button>
                        )}
                        {ev.status === 'ACTIVE' && (
                          <button
                            onClick={() => handleStatusChange(ev.publicId, 'deactivate')}
                            className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                          >
                            <PowerOff size={13} className="text-yellow-400" /> Deactivate
                          </button>
                        )}
                        <button
                          onClick={() => handleStatusChange(ev.publicId, 'archive')}
                          className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                        >
                          <Archive size={13} className="text-purple-400" /> Archive
                        </button>
                        <div className="border-t border-[#1a1a1a] mx-3 my-1" />
                        <button
                          onClick={() => handleDelete(ev.publicId)}
                          className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-red-400 hover:bg-red-500/10 transition-colors"
                        >
                          <Trash2 size={13} /> Delete
                        </button>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* Pagination */}
        <div className="flex items-center justify-between px-5 py-3 border-t border-[#1a1a1a] bg-[#0a0a0a]">
          <span className="text-xs text-[#555]">
            {events.length > 0 ? `${page * PAGE_SIZE + 1}–${Math.min((page + 1) * PAGE_SIZE, totalElements)} of ${totalElements}` : '0 events'}
          </span>
          <div className="flex gap-2">
            <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}
              className="p-1.5 rounded-lg bg-[#111] border border-[#1a1a1a] text-[#888] disabled:opacity-40 hover:bg-[#1a1a1a] transition-colors">
              <ChevronLeft size={15} />
            </button>
            <button onClick={() => setPage(p => p + 1)} disabled={(page + 1) * PAGE_SIZE >= totalElements}
              className="p-1.5 rounded-lg bg-[#111] border border-[#1a1a1a] text-[#888] disabled:opacity-40 hover:bg-[#1a1a1a] transition-colors">
              <ChevronRight size={15} />
            </button>
          </div>
        </div>
      </div>

      {/* Create / Edit Modal */}
      <AnimatePresence>
        {isModalOpen && (
          <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/80 backdrop-blur-sm p-4">
            <motion.div
              initial={{ opacity: 0, scale: 0.96 }} animate={{ opacity: 1, scale: 1 }} exit={{ opacity: 0, scale: 0.96 }}
              className="bg-[#0d0d0d] border border-[#222] rounded-2xl w-full max-w-3xl max-h-[90vh] overflow-hidden flex flex-col shadow-2xl"
            >
              <div className="px-6 py-5 border-b border-[#1a1a1a] flex justify-between items-center">
                <h3 className="text-base font-semibold text-[#DDD]">{selectedEvent ? 'Edit Event' : 'Create Event'}</h3>
                <button onClick={() => setIsModalOpen(false)} className="text-[#555] hover:text-[#CCC] transition-colors">
                  ✕
                </button>
              </div>

              <div className="p-6 overflow-y-auto flex-1">
                <form id="eventForm" onSubmit={handleSubmit} className="grid grid-cols-2 gap-5 text-sm">
                  {/* Basic */}
                  <div className="space-y-4">
                    <h4 className="text-[10px] font-bold text-[#555] uppercase tracking-widest border-b border-[#1a1a1a] pb-2">Basic Info</h4>
                    <div>
                      <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Title *</label>
                      <input required type="text" value={formData.title} onChange={e => f('title', e.target.value)}
                        className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors" />
                    </div>
                    <div>
                      <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Theme</label>
                      <input type="text" value={formData.theme} onChange={e => f('theme', e.target.value)}
                        className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors" />
                    </div>
                    <div>
                      <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Type</label>
                      <select value={formData.eventType} onChange={e => f('eventType', e.target.value)}
                        className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors">
                        <option value="CORPORATE">Corporate</option>
                        <option value="WEDDING">Wedding</option>
                        <option value="CONFERENCE">Conference</option>
                      </select>
                    </div>
                    <div>
                      <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Max Guests</label>
                      <input type="number" value={formData.maxGuests} onChange={e => f('maxGuests', Number(e.target.value))}
                        className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors" />
                    </div>
                  </div>

                  {/* Dates */}
                  <div className="space-y-4">
                    <h4 className="text-[10px] font-bold text-[#555] uppercase tracking-widest border-b border-[#1a1a1a] pb-2">Dates & Venue</h4>
                    <div>
                      <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Start *</label>
                      <input required type="datetime-local" value={formData.startDateTime} onChange={e => f('startDateTime', e.target.value)}
                        className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors [color-scheme:dark]" />
                    </div>
                    <div>
                      <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">End</label>
                      <input type="datetime-local" value={formData.endDateTime} onChange={e => f('endDateTime', e.target.value)}
                        className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors [color-scheme:dark]" />
                    </div>
                    <div>
                      <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Venue</label>
                      <input type="text" value={formData.venue} onChange={e => f('venue', e.target.value)}
                        className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors" />
                    </div>
                    <div>
                      <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">City</label>
                      <input type="text" value={formData.city} onChange={e => f('city', e.target.value)}
                        className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors" />
                    </div>
                  </div>

                  {/* Templates — full width */}
                  <div className="col-span-2 space-y-4">
                    <h4 className="text-[10px] font-bold text-[#555] uppercase tracking-widest border-b border-[#1a1a1a] pb-2">Communication Templates</h4>
                    <div className="grid grid-cols-2 gap-4">
                      <div>
                        <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Email Template</label>
                        <textarea rows={3} value={formData.emailTemplate} onChange={e => f('emailTemplate', e.target.value)}
                          className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors resize-none" />
                      </div>
                      <div>
                        <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">WhatsApp Template</label>
                        <textarea rows={3} value={formData.whatsappTemplate} onChange={e => f('whatsappTemplate', e.target.value)}
                          className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors resize-none" />
                      </div>
                    </div>
                  </div>
                </form>
              </div>

              <div className="px-6 py-4 border-t border-[#1a1a1a] flex justify-end gap-3 bg-[#0a0a0a]">
                <button onClick={() => setIsModalOpen(false)}
                  className="px-5 py-2.5 text-sm text-[#888] hover:text-[#CCC] border border-[#1a1a1a] rounded-lg hover:bg-[#111] transition-colors">
                  Cancel
                </button>
                <button type="submit" form="eventForm"
                  className="px-6 py-2.5 bg-[#C5A059] text-black font-semibold rounded-lg hover:bg-[#D4B86A] transition-colors text-sm">
                  {selectedEvent ? 'Save Changes' : 'Create Event'}
                </button>
              </div>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </div>
  );
};

export const EventsModule = React.memo(EventsModuleBase);
