import CheckPermissionComponent from "@/common/checkPermission/CheckPagePermissionComponent";
import SignUp from '@/component/SignUp/SignUp';


export default <CheckPermissionComponent
  checkIsNotSignIn={true}
>
  <SignUp />
</CheckPermissionComponent>