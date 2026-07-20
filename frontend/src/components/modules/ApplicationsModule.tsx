import React, { useState, useEffect, useRef } from 'react';
import { motion, AnimatePresence } from 'framer-motion';
import {
  Search, CheckCircle2, XCircle, ChevronLeft, ChevronRight,
  UserPlus, Eye, MoreVertical, CheckSquare, Square, Download
} from 'lucide-react';
import {
  searchApplications, approveApplication, rejectApplication,
  bulkApproveApplications, bulkRejectApplications,
  assignAdmin, fetchAdmins
} from '../../services/api';

interface ApplicationsModuleProps {
  token: string;
  onOpenGuestProfile: (publicId: string) => void;
  eventId?: string | null;
  /** When true, hides the "Assign Admin" option (used in Admin dashboard) */
  adminMode?: boolean;
}

type StatusTab = 'ALL' | 'PENDING' | 'APPROVED' | 'REJECTED' | 'WAITLISTED';

const STATUS_STYLES: Record<string, string> = {
  PENDING:   'bg-yellow-500/10 text-yellow-500 border-yellow-500/20',
  APPROVED:  'bg-emerald-500/10 text-emerald-400 border-emerald-500/20',
  REJECTED:  'bg-red-500/10 text-red-400 border-red-500/20',
  WAITLISTED:'bg-purple-500/10 text-purple-400 border-purple-500/20',
};

const ApplicationsModuleBase: React.FC<ApplicationsModuleProps> = ({
  token, onOpenGuestProfile, eventId, adminMode = false
}) => {
  const [activeTab, setActiveTab]   = useState<StatusTab>('ALL');
  const [applications, setApps]     = useState<any[]>([]);
  const [totalElements, setTotal]   = useState(0);
  const [page, setPage]             = useState(0);
  const [loading, setLoading]       = useState(false);
  const [searchQuery, setSearch]    = useState('');
  const [selectedIds, setSelected]  = useState<Set<string>>(new Set());
  const [admins, setAdmins]         = useState<any[]>([]);
  const [openMenuId, setOpenMenu]   = useState<string | null>(null);
  const [assignDropdownFor, setAssignDD] = useState<string | null>(null);
  const menuRef = useRef<HTMLDivElement>(null);

  // Close menus on outside click
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setOpenMenu(null);
        setAssignDD(null);
      }
    };
    document.addEventListener('mousedown', handler);
    return () => document.removeEventListener('mousedown', handler);
  }, []);

  const fetchApps = async () => {
    setLoading(true);
    try {
      const req = {
        query: searchQuery || null,
        status: activeTab === 'ALL' ? null : activeTab,
        eventId: eventId || null,
        assignedToMe: adminMode ? true : undefined
      };
      const res = await searchApplications(req, page, 20, 'createdAt', 'DESC', token);
      setApps(res.data?.content || []);
      setTotal(res.data?.totalElements || 0);
    } catch { /* silent */ }
    finally { setLoading(false); }
  };

  useEffect(() => { fetchApps(); }, [page, activeTab, searchQuery, token, eventId]);
  useEffect(() => {
    fetchAdmins(token).then(r => setAdmins(r.data?.content || [])).catch(() => {});
  }, [token]);

  // Selection helpers
  const toggle = (id: string) => {
    const next = new Set(selectedIds);
    next.has(id) ? next.delete(id) : next.add(id);
    setSelected(next);
  };
  const toggleAll = () => {
    if (selectedIds.size === applications.length) setSelected(new Set());
    else setSelected(new Set(applications.map(a => a.publicId)));
  };

  // Actions
  const handleApprove = async (id: string) => {
    setOpenMenu(null);
    await approveApplication(id, token).catch(() => {});
    fetchApps();
  };
  const handleReject = async (id: string) => {
    setOpenMenu(null);
    const reason = window.prompt('Reason for rejection:');
    if (reason === null) return;
    await rejectApplication(id, reason, token).catch(() => {});
    fetchApps();
  };
  const handleAssign = async (appId: string, adminId: string) => {
    setAssignDD(null);
    setOpenMenu(null);
    await assignAdmin(appId, adminId, token).catch(() => alert('Assign failed'));
    fetchApps();
  };
  const handleBulkApprove = async () => {
    if (!selectedIds.size) return;
    await bulkApproveApplications(Array.from(selectedIds), token).catch(() => alert('Bulk approve failed'));
    setSelected(new Set());
    fetchApps();
  };
  const handleBulkReject = async () => {
    if (!selectedIds.size) return;
    const reason = window.prompt('Reason:');
    if (reason === null) return;
    await bulkRejectApplications(Array.from(selectedIds), reason, token).catch(() => alert('Bulk reject failed'));
    setSelected(new Set());
    fetchApps();
  };

  const TABS: StatusTab[] = ['ALL', 'PENDING', 'APPROVED', 'REJECTED', 'WAITLISTED'];

  return (
    <div className="space-y-5 max-w-7xl pb-64">
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-[#F5F5F5]">Applications</h2>
          <p className="text-xs text-[#555] mt-0.5">Review and process guest applications.</p>
        </div>
        {selectedIds.size > 0 && (
          <div className="flex items-center gap-2">
            <span className="text-xs text-[#888]">{selectedIds.size} selected</span>
            <button
              onClick={handleBulkApprove}
              className="flex items-center gap-1.5 text-xs bg-emerald-500/10 text-emerald-400 border border-emerald-500/20 hover:bg-emerald-500/20 px-3 py-1.5 rounded-lg transition-colors"
            >
              <CheckCircle2 size={12} /> Approve All
            </button>
            <button
              onClick={handleBulkReject}
              className="flex items-center gap-1.5 text-xs bg-red-500/10 text-red-400 border border-red-500/20 hover:bg-red-500/20 px-3 py-1.5 rounded-lg transition-colors"
            >
              <XCircle size={12} /> Reject All
            </button>
          </div>
        )}
      </div>

      {/* Tabs + Search */}
      <div className="flex items-center justify-between gap-4">
        <div className="flex gap-1 bg-[#0d0d0d] border border-[#1a1a1a] rounded-lg p-1">
          {TABS.map(tab => (
            <button
              key={tab}
              onClick={() => { setActiveTab(tab); setPage(0); setSelected(new Set()); }}
              className={`px-3 py-1.5 rounded-md text-xs font-semibold tracking-wide transition-all ${
                activeTab === tab
                  ? 'bg-[#C5A059] text-black'
                  : 'text-[#666] hover:text-[#CCC] hover:bg-[#111]'
              }`}
            >
              {tab}
            </button>
          ))}
        </div>
        <div className="relative">
          <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[#555]" size={14} />
          <input
            type="text"
            placeholder="Search applications…"
            value={searchQuery}
            onChange={e => { setSearch(e.target.value); setPage(0); }}
            className="bg-[#0d0d0d] border border-[#1a1a1a] text-sm text-[#DDD] px-4 py-2 pl-9 rounded-lg focus:outline-none focus:border-[#C5A059]/40 transition-colors w-60"
          />
        </div>
      </div>

      {/* Table */}
      <div className="bg-[#0d0d0d] border border-[#1a1a1a] rounded-xl">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-[#1a1a1a] bg-[#0a0a0a]">
              <th className="px-4 py-3 w-10">
                <button onClick={toggleAll} className="text-[#555] hover:text-[#C5A059] transition-colors">
                  {applications.length > 0 && selectedIds.size === applications.length
                    ? <CheckSquare size={16} className="text-[#C5A059]" />
                    : <Square size={16} />}
                </button>
              </th>
              <th className="px-4 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">ID</th>
              <th className="px-4 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Guest</th>
              <th className="px-4 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Event</th>
              <th className="px-4 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Status</th>
              <th className="px-4 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Assigned</th>
              <th className="px-4 py-3 text-right text-[10px] font-bold text-[#555] uppercase tracking-widest">Actions</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#111]">
            {applications.length === 0 ? (
              <tr>
                <td colSpan={7} className="py-16 text-center text-[#555] text-sm">
                  {loading ? 'Loading…' : 'No applications found.'}
                </td>
              </tr>
            ) : applications.map(app => (
              <motion.tr
                key={app.publicId}
                initial={{ opacity: 0 }} animate={{ opacity: 1 }}
                className="hover:bg-[#111]/50 transition-colors group"
              >
                <td className="px-4 py-3.5">
                  <button onClick={() => toggle(app.publicId)} className="text-[#555] hover:text-[#C5A059] transition-colors">
                    {selectedIds.has(app.publicId)
                      ? <CheckSquare size={15} className="text-[#C5A059]" />
                      : <Square size={15} />}
                  </button>
                </td>
                <td className="px-4 py-3.5">
                  <span className="font-mono text-xs text-[#C5A059]">{app.publicId.substring(0, 8).toUpperCase()}</span>
                </td>
                <td className="px-4 py-3.5">
                  <div className="flex items-center gap-2.5">
                    <div className="w-7 h-7 rounded-full bg-[#1a1a1a] border border-[#2a2a2a] flex items-center justify-center text-[10px] text-[#C5A059] font-semibold shrink-0">
                      {app.firstName?.[0]}{app.lastName?.[0]}
                    </div>
                    <div>
                      <button 
                        onClick={() => onOpenGuestProfile(app.publicId)}
                        className="text-sm text-[#DDD] font-medium group-hover:text-[#C5A059] hover:underline transition-colors text-left"
                      >
                        {app.firstName} {app.lastName}
                      </button>
                      <p className="text-[11px] text-[#555]">{app.company || app.email}</p>
                    </div>
                  </div>
                </td>
                <td className="px-4 py-3.5">
                  <p className="text-xs text-[#DDD]">{app.eventTitle || '—'}</p>
                  <p className="text-[11px] text-[#555]">{new Date(app.createdAt).toLocaleDateString()}</p>
                </td>
                <td className="px-4 py-3.5">
                  <span className={`px-2 py-0.5 text-[10px] font-semibold rounded-full border ${STATUS_STYLES[app.status] || 'bg-gray-500/10 text-gray-400 border-gray-500/20'}`}>
                    {app.status}
                  </span>
                </td>
                <td className="px-4 py-3.5 text-xs text-[#555]">
                  {app.assignedAdminName || <span className="italic opacity-40">Unassigned</span>}
                </td>
                <td className="px-4 py-3.5 text-right relative" ref={openMenuId === app.publicId ? (menuRef as any) : undefined}>
                  <div className="flex items-center justify-end gap-1.5 opacity-0 group-hover:opacity-100 transition-opacity">
                    {/* Primary: Approve/Reject for pending */}
                    {app.status === 'PENDING' && (
                      <>
                        <button
                          onClick={() => handleApprove(app.publicId)}
                          className="p-1.5 rounded-lg text-[#555] hover:text-emerald-400 hover:bg-emerald-500/10 transition-colors"
                          title="Approve"
                        >
                          <CheckCircle2 size={14} />
                        </button>
                        <button
                          onClick={() => handleReject(app.publicId)}
                          className="p-1.5 rounded-lg text-[#555] hover:text-red-400 hover:bg-red-500/10 transition-colors"
                          title="Reject"
                        >
                          <XCircle size={14} />
                        </button>
                      </>
                    )}
                    {/* Three-dot menu */}
                    <button
                      onClick={() => setOpenMenu(openMenuId === app.publicId ? null : app.publicId)}
                      className="p-1.5 rounded-lg text-[#555] hover:text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                    >
                      <MoreVertical size={14} />
                    </button>
                  </div>

                  <AnimatePresence>
                    {openMenuId === app.publicId && (
                      <motion.div
                        initial={{ opacity: 0, scale: 0.95, y: -4 }}
                        animate={{ opacity: 1, scale: 1, y: 0 }}
                        exit={{ opacity: 0, scale: 0.95, y: -4 }}
                        transition={{ duration: 0.12 }}
                        className="absolute right-4 top-full mt-1 w-48 bg-[#111] border border-[#222] rounded-xl shadow-2xl z-50"
                      >
                        <button
                          onClick={() => { setOpenMenu(null); onOpenGuestProfile(app.publicId); }}
                          className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                        >
                          <Eye size={13} className="text-blue-400" /> View Profile
                        </button>
                        {/* Assign submenu — visible for both superadmin and admin */}
                        <div className="relative">
                          <button
                            onClick={() => setAssignDD(assignDropdownFor === app.publicId ? null : app.publicId)}
                            className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors rounded-b-xl"
                          >
                            <UserPlus size={13} className="text-[#C5A059]" /> Assign Guest to...
                          </button>
                          <AnimatePresence>
                            {assignDropdownFor === app.publicId && (
                              <motion.div
                                initial={{ opacity: 0, x: 4 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 4 }}
                                className="absolute right-full top-0 mr-1 w-48 bg-[#111] border border-[#222] rounded-xl shadow-2xl z-50 overflow-hidden"
                              >
                                <div className="max-h-40 overflow-y-auto">
                                  {admins.length === 0 ? (
                                    <p className="px-3 py-3 text-xs text-[#555] text-center">No reviewers found</p>
                                  ) : (
                                    <>
                                      <div className="px-3 py-2 bg-[#1a1a1a] border-b border-[#222]">
                                        <p className="text-[10px] text-[#C5A059] font-bold uppercase tracking-widest">Admins</p>
                                      </div>
                                      {admins.filter(a => a.roles?.some((r: any) => r.name === 'ADMIN' || r.name === 'SUPER_ADMIN')).map(admin => (
                                        <button
                                          key={admin.id}
                                          onClick={() => handleAssign(app.publicId, admin.email)}
                                          className="w-full text-left px-3 py-2.5 text-xs text-[#CCC] hover:bg-[#1a1a1a] hover:text-[#C5A059] transition-colors"
                                        >
                                          {admin.firstName} {admin.lastName}
                                        </button>
                                      ))}
                                      
                                      <div className="px-3 py-2 bg-[#1a1a1a] border-y border-[#222]">
                                        <p className="text-[10px] text-blue-400 font-bold uppercase tracking-widest">Users</p>
                                      </div>
                                      {admins.filter(a => !a.roles?.some((r: any) => r.name === 'ADMIN' || r.name === 'SUPER_ADMIN')).map(user => (
                                        <button
                                          key={user.id}
                                          onClick={() => handleAssign(app.publicId, user.email)}
                                          className="w-full text-left px-3 py-2.5 text-xs text-[#CCC] hover:bg-[#1a1a1a] hover:text-blue-400 transition-colors"
                                        >
                                          {user.firstName} {user.lastName}
                                        </button>
                                      ))}
                                    </>
                                  )}
                                </div>
                              </motion.div>
                            )}
                          </AnimatePresence>
                        </div>
                      </motion.div>
                    )}
                  </AnimatePresence>
                </td>
              </motion.tr>
            ))}
          </tbody>
        </table>

        {/* Pagination */}
        <div className="flex items-center justify-between px-5 py-3 border-t border-[#1a1a1a] bg-[#0a0a0a]">
          <span className="text-xs text-[#555]">
            {totalElements > 0
              ? `${page * 20 + 1}–${Math.min((page + 1) * 20, totalElements)} of ${totalElements}`
              : '0 applications'}
          </span>
          <div className="flex gap-2">
            <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}
              className="p-1.5 rounded-lg bg-[#111] border border-[#1a1a1a] text-[#888] disabled:opacity-40 hover:bg-[#1a1a1a] transition-colors">
              <ChevronLeft size={15} />
            </button>
            <button onClick={() => setPage(p => p + 1)} disabled={(page + 1) * 20 >= totalElements}
              className="p-1.5 rounded-lg bg-[#111] border border-[#1a1a1a] text-[#888] disabled:opacity-40 hover:bg-[#1a1a1a] transition-colors">
              <ChevronRight size={15} />
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};

export const ApplicationsModule = React.memo(ApplicationsModuleBase);
