import { observer } from 'mobx-react-use-autorun';
import { ReactNode } from 'react';
import { type Navigation } from '@toolpad/core/AppProvider';
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { ReactRouterAppProvider } from '@toolpad/core/react-router'
import UserInfoMenu from '@/component/SystemMenu/UserInfoMenu';
import getNavigation from '@/component/SystemMenu/js/getNavigation';

const NAVIGATION: Navigation = getNavigation();

export default observer((props: {
  children: ReactNode
}) => {
  return <ReactRouterAppProvider
    navigation={NAVIGATION}
  >
    <DashboardLayout
      slots={{ toolbarActions: UserInfoMenu }}
    >
      {props.children}
    </DashboardLayout>
  </ReactRouterAppProvider>
})
