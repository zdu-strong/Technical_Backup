import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import MessageEntry from "@/component/Message/MessageEntry";

export default <CheckPermissionComponent
  isAutoLogin={true}
  checkIsSignIn={true}
>
  <MessageEntry />
</CheckPermissionComponent>