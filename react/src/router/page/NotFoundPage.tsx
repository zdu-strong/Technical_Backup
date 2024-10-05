import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import NotFound from "@/component/NotFound/NotFound";
import MainMenu from "@/component/SystemMenu/MainMenu";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    {NotFound}
  </MainMenu>
</CheckPermissionComponent>