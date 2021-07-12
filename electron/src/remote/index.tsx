import * as RemoteType from '@electron/remote';
import * as IsLoadedUtilType from '../../main/util/IsLoadedUtil';
import * as StorageManageRunType from '../../main/util/storage_manage/StorageManageRun';
import * as NodeOsUtilsType from 'node-os-utils';
import * as FSType from 'fs';

export const remote: typeof RemoteType = window.require("@electron/remote");
export const IsLoadedUtil: typeof IsLoadedUtilType = remote.require("./util/IsLoadedUtil");
export const NodeOsUtils: typeof NodeOsUtilsType = window.require("node-os-utils");
export const fs: typeof FSType = window.require('fs');
export const StorageManageRunUtil: typeof StorageManageRunType = remote.require("./util/storage_manage/StorageManageRun");