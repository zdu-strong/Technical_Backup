import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import MainMenu from "@/component/SystemMenu/MainMenu";
import SuperAdminOrganizeManage from "@/component/SuperAdminOrganizeManage/SuperAdminOrganizeManage";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <SuperAdminOrganizeManage />
  </MainMenu>
</CheckPermissionComponent>