import React, { useState, useEffect } from "react";
import { motion } from "framer-motion";
import { Clock, CheckCircle2, XCircle, CalendarDays, Users, Activity } from "lucide-react";
import { getAdminDashboardSummary } from '../../services/api';

interface AdminOverviewPanelProps {
  token: string;
  onNavigate?: (tabId: string) => void;
}

interface Stats {
  assignedGuests:        number;
  pendingApplications:   number;
  approvedApplications:  number;
  rejectedApplications:  number;
  generatedInvitations:  number;
  todaysApplications:    number;
  todaysApprovals:       number;
}

export const AdminOverviewPanel: React.FC<AdminOverviewPanelProps> = ({ token, onNavigate }) => {
  const [stats, setStats]     = useState<Stats | null>(null);
  const [loading, setLoading] = useState(true);
  const [lastRefresh, setLastRefresh] = useState<Date>(new Date());

  const fetchStats = async () => {
    try {
      const data = await getAdminDashboardSummary(token);
      if (data) { setStats(data); setLastRefresh(new Date()); }
    } catch { /* silent */ }
    finally { setLoading(false); }
  };

  useEffect(() => {
    fetchStats();
    const id = setInterval(fetchStats, 30_000);
    return () => clearInterval(id);
  }, [token]);

  if (loading) {
    return (
      <div className="flex items-center justify-center h-64">
        <div className="w-8 h-8 border-2 border-[#C5A059] border-t-transparent rounded-full animate-spin" />
      </div>
    );
  }

  const kpis = [
    {
      label: "Assigned Guests",
      value: stats?.assignedGuests || 0,
      icon: Users,
      color: "text-blue-400",
      bg: "bg-blue-500/5",
      border: "border-blue-500/20",
      tab: "guests",
    },
    {
      label: "Pending",
      value: stats?.pendingApplications || 0,
      icon: Clock,
      color: "text-yellow-400",
      bg: "bg-yellow-500/5",
      border: "border-yellow-500/20",
      tab: "applications",
    },
    {
      label: "Approved",
      value: stats?.approvedApplications || 0,
      icon: CheckCircle2,
      color: "text-emerald-400",
      bg: "bg-emerald-500/5",
      border: "border-emerald-500/20",
      tab: "applications",
    },
    {
      label: "Rejected",
      value: stats?.rejectedApplications || 0,
      icon: XCircle,
      color: "text-red-400",
      bg: "bg-red-500/5",
      border: "border-red-500/20",
      tab: "applications",
    },
    {
      label: "Invitations Sent",
      value: stats?.generatedInvitations || 0,
      icon: CalendarDays,
      color: "text-[#C5A059]",
      bg: "bg-[#C5A059]/5",
      border: "border-[#C5A059]/20",
      tab: "invitations",
    },
    {
      label: "Today's Tasks",
      value: stats?.todaysApplications || 0,
      icon: Activity,
      color: "text-purple-400",
      bg: "bg-purple-500/5",
      border: "border-purple-500/20",
      tab: "applications",
    },
  ];

  return (
    <motion.div
      initial={{ opacity: 0, y: 8 }}
      animate={{ opacity: 1, y: 0 }}
      className="space-y-6 max-w-6xl p-6"
    >
      {/* Page title */}
      <div className="flex items-center justify-between">
        <div>
          <h2 className="text-lg font-semibold text-[#F5F5F5]">Dashboard</h2>
          <p className="text-xs text-[#555] mt-0.5">
            Your assigned events and guest application overview.
          </p>
        </div>
        <div className="flex items-center gap-3">
          <span className="text-[10px] text-[#444]">
            Updated {lastRefresh.toLocaleTimeString([], { hour: "2-digit", minute: "2-digit" })}
          </span>
          <button
            onClick={fetchStats}
            className="text-[11px] text-[#C5A059]/70 hover:text-[#C5A059] border border-[#C5A059]/20 hover:border-[#C5A059]/50 px-3 py-1.5 rounded-lg transition-colors"
          >
            Refresh
          </button>
        </div>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-2 sm:grid-cols-3 lg:grid-cols-6 gap-4">
        {kpis.map((card, i) => (
          <motion.div
            key={card.label}
            initial={{ opacity: 0, scale: 0.96 }}
            animate={{ opacity: 1, scale: 1 }}
            transition={{ delay: i * 0.05 }}
            onClick={() => onNavigate && onNavigate(card.tab)}
            className={`${card.bg} ${card.border} border rounded-xl p-4 cursor-pointer hover:brightness-110 transition-all`}
          >
            <div className={`w-8 h-8 rounded-lg ${card.bg} border ${card.border} flex items-center justify-center mb-3`}>
              <card.icon size={15} className={card.color} />
            </div>
            <p className={`text-2xl font-light ${card.color}`}>{card.value}</p>
            <p className="text-[10px] text-[#555] uppercase tracking-widest mt-1 font-semibold leading-tight">
              {card.label}
            </p>
          </motion.div>
        ))}
      </div>

      {/* Today's Tasks Summary */}
      <div className="bg-[#0d0d0d] border border-[#1a1a1a] rounded-xl overflow-hidden">
        <div className="px-5 py-4 border-b border-[#1a1a1a] flex items-center justify-between">
          <div className="flex items-center gap-2">
            <Activity size={15} className="text-[#C5A059]" />
            <h3 className="text-sm font-semibold text-[#DDD]">Today's Summary</h3>
          </div>
        </div>
        <div className="grid grid-cols-1 sm:grid-cols-2 divide-y sm:divide-y-0 sm:divide-x divide-[#1a1a1a]">
          {[
            {
              label: "Applications Today",
              value: stats?.todaysApplications || 0,
              note: "New applications received today",
              color: "text-purple-400",
            },
            {
              label: "Approvals Today",
              value: stats?.todaysApprovals || 0,
              note: "Applications approved today",
              color: "text-emerald-400",
            },
          ].map(item => (
            <div key={item.label} className="px-6 py-5">
              <p className="text-[10px] text-[#555] uppercase tracking-widest font-semibold mb-2">
                {item.label}
              </p>
              <p className={`text-4xl font-light ${item.color}`}>{item.value}</p>
              <p className="text-xs text-[#444] mt-2">{item.note}</p>
            </div>
          ))}
        </div>
      </div>
    </motion.div>
  );
};
