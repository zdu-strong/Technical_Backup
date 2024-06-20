import { existsWindow } from '@/common/exists-window/exists-window';

export function handleErrorWhenNotSignInToSignIn(error: any) {
  if (!existsWindow) {
    return;
  }
  if (!error) {
    return;
  }
  if (typeof error.status === "number" && error.status === 401) {
    window.location.href = "/sign_in";
  } else if (typeof error.message === "string" && (error.message as string).startsWith("401 ")) {
    window.location.href = "/sign_in";
  }

}