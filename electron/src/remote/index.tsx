import * as ElectronRemote from '@electron/remote';
import * as NodeOsUtils from 'node-os-utils';
import * as fs from 'fs';
import * as GetBuildFolderPathUtil from '@/../main/util/GetBuildFolderPathUtil'
import * as GetPublicFolderPathUtil from '@/../main/util/GetPublicFolderPathUtil'
import * as IsLoadedUtil from '@/../main/util/IsLoadedUtil'
import * as IsNotShowForTestUtil from '@/../main/util/IsNotShowForTestUtil'
import * as IsPackagedUtil from '@/../main/util/IsPackagedUtil';
import * as LoadWindowFromRelativeUrlUtil from '@/../main/util/LoadWindowFromRelativeUrlUtil'
import * as StorageUtil from '@/../main/util/StorageUtil'

const electronRemote = window.require("@electron/remote") as typeof ElectronRemote;

export default {
  ...electronRemote,
  NodeOsUtils: electronRemote.require("node-os-utils") as typeof NodeOsUtils,
  fs: electronRemote.require('fs') as typeof fs,
  ...(electronRemote.require("./util/GetBuildFolderPathUtil") as typeof GetBuildFolderPathUtil),
  ...(electronRemote.require("./util/GetPublicFolderPathUtil") as typeof GetPublicFolderPathUtil),
  ...(electronRemote.require("./util/IsLoadedUtil") as typeof IsLoadedUtil),
  ...(electronRemote.require("./util/IsNotShowForTestUtil") as typeof IsNotShowForTestUtil),
  ...(electronRemote.require("./util/IsPackagedUtil") as typeof IsPackagedUtil),
  ...(electronRemote.require("./util/LoadWindowFromRelativeUrlUtil") as typeof LoadWindowFromRelativeUrlUtil),
  ElectronStorage: electronRemote.require("./util/StorageUtil") as typeof StorageUtil,
}