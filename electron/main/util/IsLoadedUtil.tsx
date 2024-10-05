let isLoaded = false;

export async function getIsLoaded() {
  return isLoaded;
}

export async function setIsLoadedToTrue() {
  isLoaded = true;
}
