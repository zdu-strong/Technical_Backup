import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import MainMenu from "@/component/SystemMenu/MainMenu";
import OrganizeRoleManage from "@/component/OrganizeRole/OrganizeRoleManage";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <OrganizeRoleManage />
  </MainMenu>
</CheckPermissionComponent>