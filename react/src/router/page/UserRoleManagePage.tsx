import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import MainMenu from "@/component/SystemMenu/MainMenu";
import UserRoleManage from "@/component/UserRole/UserRoleManage";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <UserRoleManage />
  </MainMenu>
</CheckPermissionComponent>