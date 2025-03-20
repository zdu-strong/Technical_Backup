import { FormattedMessage } from 'react-intl';
import { type Navigation } from '@toolpad/core/AppProvider';
import DashboardIcon from '@mui/icons-material/Dashboard';
import ShoppingCartIcon from '@mui/icons-material/ShoppingCart';
import BarChartIcon from '@mui/icons-material/BarChart';
import DescriptionIcon from '@mui/icons-material/Description';
import LayersIcon from '@mui/icons-material/Layers';

export default () => [
  {
    kind: 'header',
    title: 'Main items',
  },
  {
    segment: 'dashboard',
    title: 'Dashboard' as any,
    icon: <DashboardIcon />,
  },
  {
    segment: 'orders',
    title: 'Orders',
    icon: <ShoppingCartIcon />,
  },
  {
    segment: 'chat',
    title: <FormattedMessage id="Chat" defaultMessage="Chat" />,
    icon: <ShoppingCartIcon />,
  },
  {
    segment: 'git',
    title: <FormattedMessage id="Git" defaultMessage="Git" />,
    icon: <ShoppingCartIcon />,
  },
  {
    segment: 'super_admin/user_role/manage',
    title: <FormattedMessage id="UserRoleManage" defaultMessage="User Role Manage" />,
    icon: <ShoppingCartIcon />,
  },
  {
    segment: 'super_admin/user/manage',
    title: <FormattedMessage id="UserManage" defaultMessage="User Manage" />,
    icon: <ShoppingCartIcon />,
  },
  {
    segment: 'super_admin/organize_role/manage',
    title: <FormattedMessage id="OrganizeRoleManage" defaultMessage="Organize Role Manage" />,
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