import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import GitInfo from "@/component/GitInfo";
import MainMenu from "@/component/SystemMenu/MainMenu";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <GitInfo />
  </MainMenu>
</CheckPermissionComponent>