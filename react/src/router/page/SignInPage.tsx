import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import SignIn from '@/component/SignIn/SignIn';


export default <CheckPermissionComponent
  checkIsNotSignIn={true}
>
  <SignIn />
</CheckPermissionComponent>