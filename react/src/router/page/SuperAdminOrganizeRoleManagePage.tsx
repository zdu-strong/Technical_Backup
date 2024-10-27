import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import MainMenu from "@/component/SystemMenu/MainMenu";
import SuperAdminOrganizeRoleManage from "@/component/SuperAdminOrganizeRoleManage/SuperAdminOrganizeRoleManage";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <SuperAdminOrganizeRoleManage />
  </MainMenu>
</CheckPermissionComponent>