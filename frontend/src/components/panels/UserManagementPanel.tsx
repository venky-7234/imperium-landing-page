import React, { useState, useEffect, useRef } from "react";
import { motion, AnimatePresence } from "framer-motion";
import {
  Shield, UserPlus, Edit2, Trash2, X, Check, XCircle,
  Search, ShieldAlert, MoreVertical, CalendarDays
} from "lucide-react";
import {
  fetchEvents, fetchUsers, searchUsers, createUser,
  updateUser, deleteUser, activateUser, deactivateUser, blockUser
} from '../../services/api';

interface UserManagementPanelProps {
  token: string;
  /** "ADMIN" shows admin users; "USER" shows regular users. Defaults to "ADMIN". */
  roleFilter?: "ADMIN" | "USER";
}

export const UserManagementPanel: React.FC<UserManagementPanelProps> = ({
  token,
  roleFilter = "ADMIN"
}) => {
  const isAdminMode = roleFilter === "ADMIN";

  const [users, setUsers] = useState<any[]>([]);
  const [loading, setLoading] = useState(true);
  const [events, setEvents] = useState<any[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(1);
  const [searchQuery, setSearchQuery] = useState("");

  const [isModalOpen, setIsModalOpen] = useState(false);
  const [editingUser, setEditingUser] = useState<any | null>(null);
  const [openMenuId, setOpenMenuId] = useState<number | null>(null);
  const menuRef = useRef<HTMLDivElement>(null);

  const [formData, setFormData] = useState({
    email: "",
    firstName: "",
    lastName: "",
    phoneNumber: "",
    roleName: isAdminMode ? "ADMIN" : "USER",
    assignedEventIds: [] as string[],
  });

  // Close context menu on outside click
  useEffect(() => {
    const handler = (e: MouseEvent) => {
      if (menuRef.current && !menuRef.current.contains(e.target as Node)) {
        setOpenMenuId(null);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, []);

  const loadData = async () => {
    setLoading(true);
    try {
      const [usersRes, eventsRes] = await Promise.all([
        searchQuery.trim()
          ? searchUsers(searchQuery, token, page, 20)
          : fetchUsers(token, page, 20),
        isAdminMode ? fetchEvents(token, 0, 100) : Promise.resolve(null)
      ]);

      if (usersRes.data) {
        // Client-side role filter
        const all: any[] = usersRes.data.content || [];
        const filtered = all.filter((u: any) => {
          const hasAdmin = u.roles?.some((r: any) =>
            r.name === "ADMIN" || r.name === "SUPER_ADMIN"
          );
          return isAdminMode ? hasAdmin : !hasAdmin;
        });
        setUsers(filtered);
        setTotalPages(usersRes.data.totalPages || 1);
      }
      if (eventsRes?.data) setEvents(eventsRes.data.content || []);
    } catch (e) {
      console.error("Failed to load user management data", e);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadData();
  }, [token, page, searchQuery, roleFilter]);

  const handleOpenModal = (user: any = null) => {
    setEditingUser(user);
    if (user) {
      setFormData({
        email: user.email || "",
        firstName: user.firstName || "",
        lastName: user.lastName || "",
        phoneNumber: user.phoneNumber || "",
        roleName: user.roles?.[0]?.name || (isAdminMode ? "ADMIN" : "USER"),
        assignedEventIds: user.assignedEventIds || [],
      });
    } else {
      setFormData({
        email: "",
        firstName: "",
        lastName: "",
        phoneNumber: "",
        roleName: isAdminMode ? "ADMIN" : "USER",
        assignedEventIds: [],
      });
    }
    setIsModalOpen(true);
    setOpenMenuId(null);
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const payload: any = {
        firstName: formData.firstName,
        lastName: formData.lastName,
        phoneNumber: formData.phoneNumber,
        assignedEventIds: formData.assignedEventIds,
      };
      if (!editingUser) payload.email = formData.email;

      let data;
      if (editingUser) {
        data = await updateUser(editingUser.id, payload, token);
      } else {
        data = await createUser(payload, token, isAdminMode);
      }
      if (data.success) {
        setIsModalOpen(false);
        loadData();
      } else {
        alert(data.message || "Operation failed");
      }
    } catch (e: any) {
      alert(e.message || "Failed to save user");
    }
  };

  const handleDelete = async (id: number) => {
    if (!window.confirm("Delete this user? This cannot be undone.")) return;
    setOpenMenuId(null);
    try {
      await deleteUser(id, token);
      loadData();
    } catch { alert("Delete failed"); }
  };

  const handleToggleStatus = async (user: any, action: "activate" | "deactivate" | "suspend") => {
    if (!window.confirm(`${action.charAt(0).toUpperCase() + action.slice(1)} this user?`)) return;
    setOpenMenuId(null);
    try {
      if (action === "activate") await activateUser(user.id, token);
      else if (action === "deactivate") await deactivateUser(user.id, token);
      else await blockUser(user.id, token);
      loadData();
    } catch { alert(`Failed to ${action} user`); }
  };

  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className="space-y-5 max-w-7xl"
    >
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-[#F5F5F5]">
            {isAdminMode ? "Admin Management" : "User Management"}
          </h2>
          <p className="text-xs text-[#555] mt-0.5">
            {isAdminMode
              ? "Manage admin accounts and their event assignments."
              : "View, suspend, or remove user accounts."}
          </p>
        </div>
        <div className="flex items-center gap-3">
          {/* Search */}
          <div className="relative">
            <Search className="absolute left-3 top-1/2 -translate-y-1/2 w-3.5 h-3.5 text-[#555]" />
            <input
              type="text"
              placeholder={`Search ${isAdminMode ? "admins" : "users"}…`}
              value={searchQuery}
              onChange={e => { setSearchQuery(e.target.value); setPage(0); }}
              className="bg-[#0d0d0d] border border-[#1a1a1a] rounded-lg pl-9 pr-4 py-2 text-sm text-[#DDD] focus:border-[#C5A059]/50 outline-none w-56 transition-colors"
            />
          </div>
          {/* Add button */}
          <button
            onClick={() => handleOpenModal()}
            className="flex items-center gap-2 bg-[#C5A059] text-black px-4 py-2 rounded-lg font-semibold text-sm hover:bg-[#D4B86A] transition-colors"
          >
            {isAdminMode ? <Shield size={15} /> : <UserPlus size={15} />}
            {isAdminMode ? "Add Admin" : "Add User"}
          </button>
        </div>
      </div>

      {/* Table */}
      <div className="bg-[#0d0d0d] border border-[#1a1a1a] rounded-xl overflow-hidden">
        {loading ? (
          <div className="py-16 text-center text-[#555] text-sm">Loading…</div>
        ) : (
          <table className="w-full text-left text-sm">
            <thead>
              <tr className="border-b border-[#1a1a1a] bg-[#0a0a0a]">
                <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Name</th>
                <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Email</th>
                <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Role</th>
                {isAdminMode && (
                  <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Events</th>
                )}
                <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Status</th>
                <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-[#111]">
              {users.map(u => (
                <tr key={u.id} className="hover:bg-[#111]/50 transition-colors group">
                  <td className="px-5 py-3.5">
                    <div className="flex items-center gap-3">
                      <div className="w-8 h-8 rounded-full bg-[#1a1a1a] border border-[#2a2a2a] flex items-center justify-center text-xs text-[#C5A059] font-semibold shrink-0">
                        {(u.firstName?.[0] || "?").toUpperCase()}
                      </div>
                      <span className="text-sm text-[#DDD] font-medium">
                        {u.firstName} {u.lastName}
                      </span>
                    </div>
                  </td>
                  <td className="px-5 py-3.5 text-[#888] text-xs">{u.email}</td>
                  <td className="px-5 py-3.5">
                    <span className="px-2 py-0.5 text-[10px] rounded-full bg-[#1a1a1a] border border-[#2a2a2a] text-[#C5A059] font-semibold tracking-wide">
                      {u.roles?.[0]?.name || "—"}
                    </span>
                  </td>
                  {isAdminMode && (
                    <td className="px-5 py-3.5 text-xs text-[#666]">
                      <div className="flex items-center gap-1">
                        <CalendarDays size={12} className="text-[#444]" />
                        {u.assignedEventIds?.length || 0} event{u.assignedEventIds?.length !== 1 ? "s" : ""}
                      </div>
                    </td>
                  )}
                  <td className="px-5 py-3.5">
                    <span className={`px-2 py-0.5 text-[10px] font-semibold rounded-full border ${
                      u.status === "ACTIVE"
                        ? "bg-emerald-500/10 text-emerald-400 border-emerald-500/20"
                        : "bg-red-500/10 text-red-400 border-red-500/20"
                    }`}>
                      {u.status}
                    </span>
                  </td>
                  <td className="px-5 py-3.5 text-right relative" ref={openMenuId === u.id ? (menuRef as any) : undefined}>
                    <button
                      onClick={() => setOpenMenuId(openMenuId === u.id ? null : u.id)}
                      className="p-1.5 rounded-lg text-[#555] hover:text-[#CCC] hover:bg-[#1a1a1a] transition-colors opacity-0 group-hover:opacity-100"
                    >
                      <MoreVertical size={15} />
                    </button>
                    <AnimatePresence>
                      {openMenuId === u.id && (
                        <motion.div
                          initial={{ opacity: 0, scale: 0.95, y: -4 }}
                          animate={{ opacity: 1, scale: 1, y: 0 }}
                          exit={{ opacity: 0, scale: 0.95, y: -4 }}
                          transition={{ duration: 0.12 }}
                          className="absolute right-4 top-full mt-1 w-44 bg-[#111] border border-[#222] rounded-xl shadow-2xl z-50 overflow-hidden"
                        >
                          {isAdminMode && (
                            <button
                              onClick={() => handleOpenModal(u)}
                              className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                            >
                              <Edit2 size={13} className="text-[#C5A059]" /> Edit / Assign Events
                            </button>
                          )}
                          {u.status === "ACTIVE" ? (
                            <button
                              onClick={() => handleToggleStatus(u, "deactivate")}
                              className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                            >
                              <XCircle size={13} className="text-yellow-400" /> Deactivate
                            </button>
                          ) : (
                            <button
                              onClick={() => handleToggleStatus(u, "activate")}
                              className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                            >
                              <Check size={13} className="text-emerald-400" /> Activate
                            </button>
                          )}
                          <button
                            onClick={() => handleToggleStatus(u, "suspend")}
                            className="w-full flex items-center gap-3 px-4 py-2.5 text-sm text-[#CCC] hover:bg-[#1a1a1a] transition-colors"
                          >
                            <ShieldAlert size={13} className="text-orange-400" /> Suspend
                          </button>
                          <div className="border-t border-[#1a1a1a] mx-3 my-1" />
                          <button
                            onClick={() => handleDelete(u.id)}
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
              {users.length === 0 && (
                <tr>
                  <td colSpan={isAdminMode ? 6 : 5} className="py-16 text-center text-[#555] text-sm">
                    No {isAdminMode ? "admins" : "users"} found.
                  </td>
                </tr>
              )}
            </tbody>
          </table>
        )}

        {/* Pagination */}
        <div className="flex items-center justify-between px-5 py-3 border-t border-[#1a1a1a] bg-[#0a0a0a]">
          <span className="text-xs text-[#555]">Page {page + 1} of {Math.max(1, totalPages)}</span>
          <div className="flex gap-2">
            <button
              disabled={page === 0}
              onClick={() => setPage(p => p - 1)}
              className="px-3 py-1.5 bg-[#111] border border-[#1a1a1a] rounded-lg text-xs text-[#888] disabled:opacity-40 hover:bg-[#1a1a1a] transition-colors"
            >
              Prev
            </button>
            <button
              disabled={page >= totalPages - 1}
              onClick={() => setPage(p => p + 1)}
              className="px-3 py-1.5 bg-[#111] border border-[#1a1a1a] rounded-lg text-xs text-[#888] disabled:opacity-40 hover:bg-[#1a1a1a] transition-colors"
            >
              Next
            </button>
          </div>
        </div>
      </div>

      {/* Create / Edit Modal */}
      <AnimatePresence>
        {isModalOpen && (
          <div className="fixed inset-0 bg-black/80 z-50 flex items-center justify-center p-4 backdrop-blur-sm">
            <motion.div
              initial={{ opacity: 0, scale: 0.96 }}
              animate={{ opacity: 1, scale: 1 }}
              exit={{ opacity: 0, scale: 0.96 }}
              className="bg-[#0d0d0d] border border-[#222] rounded-2xl p-6 w-full max-w-lg max-h-[90vh] overflow-y-auto shadow-2xl"
            >
              <div className="flex justify-between items-center mb-6">
                <h3 className="text-base font-semibold text-[#F5F5F5]">
                  {editingUser ? `Edit ${isAdminMode ? "Admin" : "User"}` : `Add ${isAdminMode ? "Admin" : "User"}`}
                </h3>
                <button onClick={() => setIsModalOpen(false)} className="text-[#555] hover:text-[#CCC] transition-colors">
                  <X size={20} />
                </button>
              </div>

              <form onSubmit={handleSubmit} className="space-y-4">
                {!editingUser && (
                  <div>
                    <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Email *</label>
                    <input
                      required type="email"
                      value={formData.email}
                      onChange={e => setFormData({ ...formData, email: e.target.value })}
                      className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-sm text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors"
                    />
                  </div>
                )}

                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">First Name</label>
                    <input
                      value={formData.firstName}
                      onChange={e => setFormData({ ...formData, firstName: e.target.value })}
                      className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-sm text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors"
                    />
                  </div>
                  <div>
                    <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Last Name</label>
                    <input
                      value={formData.lastName}
                      onChange={e => setFormData({ ...formData, lastName: e.target.value })}
                      className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-sm text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors"
                    />
                  </div>
                </div>

                <div>
                  <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-1.5 font-semibold">Phone</label>
                  <input
                    value={formData.phoneNumber}
                    onChange={e => setFormData({ ...formData, phoneNumber: e.target.value })}
                    className="w-full bg-[#111] border border-[#222] rounded-lg px-4 py-2.5 text-sm text-[#DDD] focus:border-[#C5A059]/50 outline-none transition-colors"
                  />
                </div>

                {/* Event assignment — admin mode only */}
                {isAdminMode && events.length > 0 && (
                  <div>
                    <label className="block text-[10px] text-[#555] uppercase tracking-widest mb-2 font-semibold">
                      Assign Events
                    </label>
                    <div className="h-44 overflow-y-auto bg-[#111] border border-[#222] rounded-lg p-2 space-y-0.5">
                      {events.map(ev => (
                        <label key={ev.publicId} className="flex items-center gap-3 px-3 py-2 rounded-lg hover:bg-[#1a1a1a] cursor-pointer transition-colors">
                          <input
                            type="checkbox"
                            checked={formData.assignedEventIds.includes(ev.publicId)}
                            onChange={e => {
                              const ids = e.target.checked
                                ? [...formData.assignedEventIds, ev.publicId]
                                : formData.assignedEventIds.filter(id => id !== ev.publicId);
                              setFormData({ ...formData, assignedEventIds: ids });
                            }}
                            className="accent-[#C5A059]"
                          />
                          <span className="text-sm text-[#CCC]">{ev.title}</span>
                        </label>
                      ))}
                    </div>
                  </div>
                )}

                <div className="flex justify-end gap-3 pt-4 border-t border-[#1a1a1a]">
                  <button
                    type="button"
                    onClick={() => setIsModalOpen(false)}
                    className="px-5 py-2.5 rounded-lg border border-[#222] text-[#888] hover:text-[#CCC] hover:bg-[#111] transition-colors text-sm"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-5 py-2.5 rounded-lg bg-[#C5A059] text-black font-semibold hover:bg-[#D4B86A] transition-colors text-sm"
                  >
                    {editingUser ? "Save Changes" : `Create ${isAdminMode ? "Admin" : "User"}`}
                  </button>
                </div>
              </form>
            </motion.div>
          </div>
        )}
      </AnimatePresence>
    </motion.div>
  );
};
