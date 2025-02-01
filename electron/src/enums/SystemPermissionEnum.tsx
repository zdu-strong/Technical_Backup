import { $enum } from 'ts-enum-util'

export enum SystemPermissionEnum {
  SUPER_ADMIN = "SUPER_ADMIN",
  ORGANIZE_MANAGE = "ORGANIZE_MANAGE",
  ORGANIZE_VIEW = "ORGANIZE_VIEW",
}

async function main() {
  const permission = "SUPER_ADMIN";
  const all = $enum(SystemPermissionEnum).getValues();
  const permissionEnum = $enum(SystemPermissionEnum).asValueOrThrow(permission);
  console.log(all)
  console.log(permissionEnum)
}
