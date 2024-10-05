import { existsWindow } from '@/common/exists-window/exists-window';
import { removeGlobalUserInfo } from '@/common/Server/get_global_user_info';

export function handleErrorWhenNotSignInToSignIn(error: any) {
  if (!existsWindow) {
    return;
  }
  if (!error) {
    return;
  }

  if (typeof error === "object" && typeof error.status === "number" && error.status === 401) {
    toSignIn();
  } else if (typeof error === "object" && typeof error.message === "string" && (error.message as string).startsWith("401 ")) {
    toSignIn();
  }
}

export function toSignIn() {
  removeGlobalUserInfo();
  window.location.href = "/sign_in";
}