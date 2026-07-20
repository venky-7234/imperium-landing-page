import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import {
  Users, CheckCircle2, XCircle, Clock, Download,
  BarChart3, Calendar, ChevronDown
} from "lucide-react";
import { searchApplications, fetchEvents, getUserProfile } from '../../services/api';

interface AdminReportsModuleProps {
  token: string;
}

type ReportTab = "guests" | "attendance";

export const AdminReportsModule: React.FC<AdminReportsModuleProps> = ({ token }) => {
  const [activeTab, setActiveTab]     = useState<ReportTab>("guests");
  const [assignedIds, setAssignedIds] = useState<string[]>([]);
  const [events, setEvents]           = useState<any[]>([]);
  const [selectedEventId, setEventId] = useState<string>("");
  const [data, setData]               = useState<any[]>([]);
  const [loading, setLoading]         = useState(false);
  const [summary, setSummary]         = useState({ total: 0, approved: 0, rejected: 0, pending: 0, waitlisted: 0 });

  // Load assigned event IDs
  useEffect(() => {
    getUserProfile(token)
      .then(res => {
        const ids: string[] = res.data?.assignedEventIds || [];
        setAssignedIds(ids);
        return fetchEvents(token, 0, 200);
      })
      .then(res => {
        const all: any[] = res.data?.content || [];
        setEvents(all.filter(ev => assignedIds.length === 0 || assignedIds.includes(ev.publicId)));
      })
      .catch(() => {});
  }, [token]);

  // Load report data
  useEffect(() => {
    if (!selectedEventId && events.length === 0) return;
    fetchReport();
  }, [activeTab, selectedEventId, events]);

  const fetchReport = async () => {
    setLoading(true);
    try {
      const req: any = {
        eventId: selectedEventId || null,
      };
      if (activeTab === "attendance") req.status = "APPROVED";

      const res = await searchApplications(req, 0, 1000, "createdAt", "DESC", token);
      const rows: any[] = res.data?.content || [];
      setData(rows);

      setSummary({
        total:      rows.length,
        approved:   rows.filter(r => r.status === "APPROVED").length,
        rejected:   rows.filter(r => r.status === "REJECTED").length,
        pending:    rows.filter(r => r.status === "PENDING").length,
        waitlisted: rows.filter(r => r.status === "WAITLISTED").length,
      });
    } catch { /* silent */ }
    finally { setLoading(false); }
  };

  const exportCSV = () => {
    const headers = activeTab === "guests"
      ? ["Name", "Email", "Phone", "Company", "Industry", "City", "Status", "Date"]
      : ["Name", "Email", "Phone", "Company", "Event", "Approved Date"];

    const rows = data.map(r => activeTab === "guests" ? [
      `${r.firstName} ${r.lastName}`,
      r.email,
      r.phone || "",
      r.company || "",
      r.industry || "",
      r.city || "",
      r.status,
      new Date(r.createdAt).toLocaleDateString(),
    ] : [
      `${r.firstName} ${r.lastName}`,
      r.email,
      r.phone || "",
      r.company || "",
      r.eventTitle || "",
      new Date(r.updatedAt || r.createdAt).toLocaleDateString(),
    ]);

    const csv = [headers, ...rows].map(row => row.join(",")).join("\n");
    const blob = new Blob([csv], { type: "text/csv" });
    const url  = URL.createObjectURL(blob);
    const a    = document.createElement("a");
    a.href = url;
    a.download = `${activeTab}-report-${new Date().toISOString().slice(0, 10)}.csv`;
    a.click();
    URL.revokeObjectURL(url);
  };

  const TABS: { id: ReportTab; label: string; icon: React.ElementType }[] = [
    { id: "guests",     label: "Guest Report",      icon: Users },
    { id: "attendance", label: "Attendance Report",  icon: CheckCircle2 },
  ];

  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className="space-y-5 max-w-7xl p-6"
    >
      {/* Header */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-[#F5F5F5]">Reports</h2>
          <p className="text-xs text-[#555] mt-0.5">Guest and attendance reports for your assigned events.</p>
        </div>
        <button
          onClick={exportCSV}
          disabled={loading || data.length === 0}
          className="flex items-center gap-2 bg-[#C5A059] text-black px-4 py-2 rounded-lg font-semibold text-sm hover:bg-[#D4B86A] transition-colors disabled:opacity-40"
        >
          <Download size={15} /> Export CSV
        </button>
      </div>

      {/* Tabs + Event Filter */}
      <div className="flex flex-col sm:flex-row sm:items-center gap-3">
        <div className="flex gap-1 bg-[#0d0d0d] border border-[#1a1a1a] rounded-lg p-1">
          {TABS.map(tab => (
            <button
              key={tab.id}
              onClick={() => setActiveTab(tab.id)}
              className={`flex items-center gap-2 px-4 py-2 rounded-md text-xs font-semibold tracking-wide transition-all ${
                activeTab === tab.id
                  ? "bg-[#C5A059] text-black"
                  : "text-[#666] hover:text-[#CCC] hover:bg-[#111]"
              }`}
            >
              <tab.icon size={13} />
              {tab.label}
            </button>
          ))}
        </div>

        {/* Event filter */}
        <div className="relative">
          <Calendar className="absolute left-3 top-1/2 -translate-y-1/2 text-[#555]" size={13} />
          <select
            value={selectedEventId}
            onChange={e => setEventId(e.target.value)}
            className="bg-[#0d0d0d] border border-[#1a1a1a] text-sm text-[#DDD] pl-9 pr-8 py-2 rounded-lg focus:outline-none focus:border-[#C5A059]/40 appearance-none transition-colors w-56"
          >
            <option value="">All Assigned Events</option>
            {events.map(ev => (
              <option key={ev.publicId} value={ev.publicId}>{ev.title}</option>
            ))}
          </select>
          <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 text-[#555] pointer-events-none" size={13} />
        </div>
      </div>

      {/* Summary KPIs */}
      {!loading && (
        <div className="grid grid-cols-2 sm:grid-cols-4 gap-3">
          {[
            { label: "Total",      value: summary.total,      color: "text-[#CCC]",    border: "border-[#1a1a1a]",       bg: "bg-[#0d0d0d]" },
            { label: "Approved",   value: summary.approved,   color: "text-emerald-400",border: "border-emerald-500/20",  bg: "bg-emerald-500/5" },
            { label: "Pending",    value: summary.pending,    color: "text-yellow-400", border: "border-yellow-500/20",   bg: "bg-yellow-500/5" },
            { label: "Rejected",   value: summary.rejected,   color: "text-red-400",    border: "border-red-500/20",      bg: "bg-red-500/5" },
          ].map(k => (
            <div key={k.label} className={`${k.bg} border ${k.border} rounded-xl px-4 py-3`}>
              <p className="text-[10px] text-[#555] uppercase tracking-widest font-semibold mb-1">{k.label}</p>
              <p className={`text-2xl font-light ${k.color}`}>{k.value}</p>
            </div>
          ))}
        </div>
      )}

      {/* Table */}
      <div className="bg-[#0d0d0d] border border-[#1a1a1a] rounded-xl overflow-hidden">
        <table className="w-full text-left text-sm">
          <thead>
            <tr className="border-b border-[#1a1a1a] bg-[#0a0a0a]">
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Guest</th>
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Contact</th>
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Company</th>
              {activeTab === "guests" && (
                <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Status</th>
              )}
              {activeTab === "attendance" && (
                <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Event</th>
              )}
              <th className="px-5 py-3 text-[10px] font-bold text-[#555] uppercase tracking-widest">Date</th>
            </tr>
          </thead>
          <tbody className="divide-y divide-[#111]">
            {loading ? (
              <tr>
                <td colSpan={5} className="py-16 text-center text-[#555] text-sm">
                  <div className="w-6 h-6 border-2 border-[#C5A059] border-t-transparent rounded-full animate-spin mx-auto mb-2" />
                  Loading report…
                </td>
              </tr>
            ) : data.length === 0 ? (
              <tr>
                <td colSpan={5} className="py-16 text-center text-[#555] text-sm">
                  No data found. {events.length === 0 && "No events assigned to you."}
                </td>
              </tr>
            ) : data.map(row => (
              <tr key={row.publicId} className="hover:bg-[#111]/50 transition-colors">
                <td className="px-5 py-3.5">
                  <div className="flex items-center gap-2.5">
                    <div className="w-7 h-7 rounded-full bg-[#1a1a1a] border border-[#2a2a2a] flex items-center justify-center text-[10px] text-[#C5A059] font-semibold shrink-0">
                      {row.firstName?.[0]}{row.lastName?.[0]}
                    </div>
                    <div>
                      <p className="text-sm text-[#DDD] font-medium">{row.firstName} {row.lastName}</p>
                      <p className="text-[11px] text-[#555] font-mono">{row.invitationNumber || row.publicId.substring(0, 8).toUpperCase()}</p>
                    </div>
                  </div>
                </td>
                <td className="px-5 py-3.5">
                  <p className="text-xs text-[#888]">{row.email}</p>
                  {row.phone && <p className="text-[11px] text-[#555]">{row.phone}</p>}
                </td>
                <td className="px-5 py-3.5 text-xs text-[#888]">
                  <p>{row.company || "—"}</p>
                  {row.industry && <p className="text-[11px] text-[#555]">{row.industry}</p>}
                </td>
                {activeTab === "guests" && (
                  <td className="px-5 py-3.5">
                    <span className={`px-2 py-0.5 text-[10px] font-semibold rounded-full border ${
                      row.status === "APPROVED"   ? "bg-emerald-500/10 text-emerald-400 border-emerald-500/20" :
                      row.status === "REJECTED"   ? "bg-red-500/10 text-red-400 border-red-500/20" :
                      row.status === "WAITLISTED" ? "bg-purple-500/10 text-purple-400 border-purple-500/20" :
                      "bg-yellow-500/10 text-yellow-400 border-yellow-500/20"
                    }`}>
                      {row.status}
                    </span>
                  </td>
                )}
                {activeTab === "attendance" && (
                  <td className="px-5 py-3.5 text-xs text-[#888]">{row.eventTitle || "—"}</td>
                )}
                <td className="px-5 py-3.5 text-xs text-[#666]">
                  {new Date(row.createdAt).toLocaleDateString("en-GB", { day: "numeric", month: "short", year: "numeric" })}
                </td>
              </tr>
            ))}
          </tbody>
        </table>

        {/* Row count */}
        {!loading && data.length > 0 && (
          <div className="px-5 py-3 border-t border-[#1a1a1a] bg-[#0a0a0a]">
            <span className="text-xs text-[#555]">
              Showing {data.length} record{data.length !== 1 ? "s" : ""}
            </span>
          </div>
        )}
      </div>
    </motion.div>
  );
};
