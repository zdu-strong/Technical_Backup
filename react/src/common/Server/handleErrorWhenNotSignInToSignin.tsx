import { existsWindow } from '@/common/exists-window/exists-window';
import { removeGlobalUserInfo } from '@/common/Server/get_global_user_info';

export function handleErrorWhenNotSignInToSignIn(error: any) {
  if (!existsWindow) {
    return;
  }
  if (!error) {
    return;
  }

  if (typeof error.status === "number" && error.status === 401) {
    toSignin();
  } else if (typeof error.message === "string" && (error.message as string).startsWith("401 ")) {
    toSignin();
  }

}

function toSignin() {
  removeGlobalUserInfo();
  window.location.href = "/sign_in";
}