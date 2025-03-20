import { observer, useMobxState } from 'mobx-react-use-autorun';
import { ReactNode } from 'react';
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { ReactRouterAppProvider } from '@toolpad/core/react-router'
import UserInfoMenu from '@/component/SystemMenu/UserInfoMenu';
import getNavigation from '@/component/SystemMenu/js/getNavigation';
import { stylesheet } from 'typestyle';


const css = stylesheet({
  dashboardLayoutContainer: {
    $nest: {
      "& > div.MuiBox-root": {
        display: "flex",
        flex: "1 1 auto",
        flexDirection: "column",
        height: "unset",
      }
    }
  }
});

export default observer((props: {
  children: ReactNode
}) => {

  const state = useMobxState(() => ({
    navigation: getNavigation(),
  }))

  return <ReactRouterAppProvider
    navigation={state.navigation}
    branding={{ title: "", logo: "" }}
  >
    <div className={`flex flex-col flex-auto ${css.dashboardLayoutContainer}`}>
      <DashboardLayout
        slots={{ toolbarActions: UserInfoMenu }}
      >
        {props.children}
      </DashboardLayout>
    </div>
  </ReactRouterAppProvider>
})
