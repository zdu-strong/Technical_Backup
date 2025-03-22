import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import MainMenu from "@/component/SystemMenu/MainMenu";
import SuperAdminRoleManage from "@/component/SuperAdminRole/SuperAdminRoleManage";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <SuperAdminRoleManage />
  </MainMenu>
</CheckPermissionComponent>