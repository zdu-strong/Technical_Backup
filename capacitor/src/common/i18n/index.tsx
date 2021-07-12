import WindowsLocale from 'windows-locale';
import zh_CN_JSON from '../../i18n/zh-CN.json';
import linq from 'linq';
import { observable } from 'mobx-react-use-autorun';

export const useI18nLocale = (): string => {
    return state.I18nLocale;
}

export const useI18nJson = () => {
    return zh_CN_JSON;
}

/* default language */
export const defaultI18nLocale = WindowsLocale['zh-cn'].tag;

const state = observable({
    I18nLocale: defaultI18nLocale,
});

export const I18nList = (() => {
    const all = [
        "zh-CN"
    ];
    const locales: any = WindowsLocale;
    return linq.from(Object.keys(WindowsLocale)).select((s): {
        language: string;
        location: string;
        id: number;
        tag: string;
        version: string;
    } => {
        return locales[s];
    }).where(s => all.includes(s.tag)).toArray();
})();

export const setI18nLocale = (locale: string) => {
    const result = linq.from(I18nList).where(s => s.tag === locale).firstOrDefault();
    if (result) {
        state.I18nLocale = result.tag;
    }
};


