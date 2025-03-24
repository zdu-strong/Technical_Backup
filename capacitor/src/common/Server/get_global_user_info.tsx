import { UserModel } from '@/model/UserModel';
import { observable } from 'mobx-react-use-autorun';
import { from, fromEvent, retry, switchMap } from 'rxjs';
import { existsWindow } from '@/common/exists-window/exists-window';
import { UserEmailModel } from '@/model/UserEmailModel';
import { plainToInstance } from 'class-transformer';

export const GlobalUserInfo = observable({
  id: '',
  username: '',
  accessToken: '',
  userEmailList: [] as UserEmailModel[],
  menuOpen: true,
} as UserModel);

export async function setGlobalUserInfo(user?: UserModel): Promise<void> {
  const hasParam = !!user;
  if (!hasParam) {
    const jsonStringOfLocalStorage = window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage);
    if (jsonStringOfLocalStorage) {
      user = plainToInstance(UserModel, JSON.parse(jsonStringOfLocalStorage));
    } else {
      removeGlobalUserInfo();
      return;
    }
  }

  GlobalUserInfo.id = user!.id;
  GlobalUserInfo.username = user!.username;
  GlobalUserInfo.accessToken = user!.accessToken;
  GlobalUserInfo.userEmailList = user!.userEmailList;
  if (typeof user!.menuOpen === "boolean") {
    GlobalUserInfo.menuOpen = user!.menuOpen;
  }
  if (hasParam) {
    window.localStorage.setItem(keyOfGlobalUserInfoOfLocalStorage, JSON.stringify(GlobalUserInfo));
  }
}

export function removeGlobalUserInfo() {
  GlobalUserInfo.id = '';
  GlobalUserInfo.username = '';
  GlobalUserInfo.accessToken = '';
  GlobalUserInfo.userEmailList = [] as UserEmailModel[];
  if (window.localStorage.getItem(keyOfGlobalUserInfoOfLocalStorage)) {
    window.localStorage.clear();
  }
}

const keyOfGlobalUserInfoOfLocalStorage = 'GlobalUserInfo-c12e6be9-e969-4a54-b5d4-b451755bf49a';

function main() {
  if (!existsWindow) {
    return;
  }
  setGlobalUserInfo();
  fromEvent(window, "storage").pipe(
    switchMap(() => {
      return from(setGlobalUserInfo());
    }),
    retry(),
  ).subscribe();
}

export default main()

