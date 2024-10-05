import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import MessageEntry from "@/component/Message/MessageEntry";
import MainMenu from "@/component/SystemMenu/MainMenu";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MainMenu>
    <MessageEntry />
  </MainMenu>
</CheckPermissionComponent>