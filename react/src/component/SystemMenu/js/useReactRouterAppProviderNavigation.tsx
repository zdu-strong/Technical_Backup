import { useIntl } from 'react-intl';
import { type Navigation } from '@toolpad/core/AppProvider';
import DashboardIcon from '@mui/icons-material/Dashboard';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import BarChartIcon from '@mui/icons-material/BarChart';
import DescriptionIcon from '@mui/icons-material/Description';
import LayersIcon from '@mui/icons-material/Layers';

export function useReactRouterAppProviderNavigation() {

  const { formatMessage } = useIntl();


  return [
    {
      kind: 'header',
      title: 'Main items',
    },
    {
      segment: 'dashboard',
      title: 'Dashboard',
      icon: <DashboardIcon />,
    },
    {
      segment: 'orders',
      title: 'Orders',
      icon: <ShoppingCartIcon />,
    },
    {
      segment: 'chat',
      title: formatMessage({ id: "Chat", defaultMessage: "Chat" }),
      icon: <ShoppingCartIcon />,
    },
    {
      segment: 'git',
      title: formatMessage({ id: "Git", defaultMessage: "Git" }),
      icon: <ShoppingCartIcon />,
    },
    {
      segment: 'super_admin/role/manage',
      title: formatMessage({ id: "RoleManage", defaultMessage: "Role Manage" }),
      icon: <ShoppingCartIcon />,
    },
    {
      segment: 'super_admin/user/manage',
      title: formatMessage({ id: "UserManage", defaultMessage: "User Manage" }),
      icon: <ShoppingCartIcon />,
    },
    {
      segment: 'super_admin/organize/manage',
      title: formatMessage({ id: "OrganizeManage", defaultMessage: "Organize Manage" }),
      icon: <ShoppingCartIcon />,
    },
    {
      kind: 'divider',
    },
    {
      kind: 'header',
      title: 'Analytics',
    },
    {
      segment: '.',
      title: 'Reports',
      icon: <BarChartIcon />,
      children: [
        {
          segment: 'sales',
          title: 'Sales',
          icon: <DescriptionIcon />,
        },
        {
          segment: 'traffic',
          title: 'Traffic',
          icon: <DescriptionIcon />,
        },
      ],
    },
    {
      segment: 'integrations',
      title: 'Integrations',
      icon: <LayersIcon />,
    },
  ] as Navigation;
}