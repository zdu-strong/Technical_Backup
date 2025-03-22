import { observer, useMobxState } from 'mobx-react-use-autorun';
import { ReactNode } from 'react';
import { DashboardLayout } from '@toolpad/core/DashboardLayout';
import { ReactRouterAppProvider } from '@toolpad/core/react-router'
import UserInfoMenu from '@/component/SystemMenu/UserInfoMenu';
import { useReactRouterAppProviderNavigation } from '@/component/SystemMenu/js/useReactRouterAppProviderNavigation';
import { stylesheet } from 'typestyle';


const css = stylesheet({
  dashboardLayoutContainer: {
    display: "flex",
    flex: "1 1 auto",
    flexDirection: "row",
    $nest: {
      "& > div.MuiBox-root": {
        display: "flex",
        flex: "1 1 auto",
        flexDirection: "row",
        height: "unset",
        width: "unset",
      },
      "& > div.MuiBox-root > div.MuiBox-root": {
        display: "flex",
        flex: "1 1 auto",
        flexDirection: "column",
      },
      "& > div.MuiBox-root > div.MuiBox-root > .MuiBox-root": {
        display: "flex",
        flex: "1 1 auto",
        flexDirection: "column",
        overflow: "visible",
      }
    }
  }
});

export default observer((props: {
  children: ReactNode
}) => {

  const state = useMobxState({

  }, {
    navigation: useReactRouterAppProviderNavigation(),
  })

  return <ReactRouterAppProvider
    navigation={state.navigation}
    branding={{ title: "", logo: "" }}
  >
    <div className={css.dashboardLayoutContainer}>
      <DashboardLayout
        slots={{ toolbarActions: UserInfoMenu }}
      >
        {props.children}
      </DashboardLayout>
    </div>
  </ReactRouterAppProvider>
})
