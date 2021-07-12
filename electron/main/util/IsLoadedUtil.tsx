let isLoaded = false;

const getIsLoaded = async () => {
    return isLoaded;
};

const setIsLoadedToTrue = async () => {
    isLoaded = true;
};

export { getIsLoaded, setIsLoadedToTrue };