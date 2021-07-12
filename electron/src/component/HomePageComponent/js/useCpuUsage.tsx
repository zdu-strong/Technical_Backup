import { observable, timeout, useAsyncExhaust, useLocalObservable } from "mobx-react-use-autorun";
import { useMount } from 'react-use'
import { NodeOsUtils } from "../../../remote";

const state = observable({
    cpuUsage: null as number | null
})

export const useCpuUsage = () => {

    const getCpuUsage = useAsyncExhaust(async () => {
        state.cpuUsage = await NodeOsUtils.cpu.usage();
        await timeout(100);
        getCpuUsage();
    })

    useMount(() => {
        getCpuUsage()
    })

    return state.cpuUsage;
}