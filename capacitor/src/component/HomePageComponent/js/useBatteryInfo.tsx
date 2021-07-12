import { BatteryInfo, Device } from '@capacitor/device';
import { timeout, useAsyncExhaust, useLocalObservable } from 'mobx-react-use-autorun';
import { useMount } from 'react-use'

export const useBatteryInfo = () => {
    const state = useLocalObservable(() => ({
        batteryInfo: null as BatteryInfo | null
    }))

    const getBatteryInfo = useAsyncExhaust(async () => {
        state.batteryInfo = await Device.getBatteryInfo();
        await timeout(100);
        getBatteryInfo();
    })

    useMount(() => {
        getBatteryInfo();
    })

    return state.batteryInfo;
}