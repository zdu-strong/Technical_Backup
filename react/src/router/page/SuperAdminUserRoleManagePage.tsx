import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import MainMenu from "@/component/SystemMenu/MainMenu";
import SuperAdminUserRoleManage from "@/component/SuperAdminUserRole/SuperAdminUserRoleManage";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <SuperAdminUserRoleManage />
  </MainMenu>
</CheckPermissionComponent>