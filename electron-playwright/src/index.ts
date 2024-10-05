import { ElectronApplication, Page } from "playwright";
import * as action from "@/action";
import { page } from '@/page'


let GLOBAL_APPLICATION: ElectronApplication;
let GLObAL_WINDOW: Page;

export const electron = {
  get application() {
    if(!GLOBAL_APPLICATION){
      throw new Error("Application does not exist");
    }
    return GLOBAL_APPLICATION;
  },
  get window() {
    if(!GLObAL_WINDOW){
      throw new Error("Window does not exist");
    }
    return GLObAL_WINDOW;
  }
};

export function setApplication(application: ElectronApplication) {
  GLOBAL_APPLICATION = application;
}

export function setWindow(window: Page) {
  GLObAL_WINDOW = window;
}

export {
  action,
  page,
}