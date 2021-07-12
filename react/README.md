# Getting Started with Create React App

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app). If you have any questions, please contact zdu.strong@gmail.com.<br/>

## Development environment setup
1. From https://code.visualstudio.com install Visual Studio Code.<br/>
2. From https://nodejs.org/en/ install nodejs v16.<br/>

## Available Scripts

In the project directory, you can run:<br/>

### `npm start`

Runs the app in the development mode.<br/>
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.<br/>

The page will reload if you make edits.<br/>
You will also see any lint errors in the console.<br/>

### `npm test`

Run all unit tests.<br/>
See the section about [running tests](https://www.cypress.io) for more information.<br/>

### `npm run test:open`

Launches the test runner in the interactive watch mode.<br/>
See the section about [running tests](https://www.cypress.io) for more information.<br/>

### `npm run build`

Builds the app for production to the `build` folder.<br/>
It correctly bundles React in production mode and optimizes the build for the best performance.<br/>

The build is minified and the filenames include the hashes.<br/>
Your app is ready to be deployed!<br/>

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.<br/>

## Install new dependencies

    npm install react --save-dev

After installing new dependencies, please make sure that the project runs normally.<br/>
After installing new dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

## Upgrade dependency

You can use this command to check if a new version is available:<br/>
npm install -g npm-check && npm-check --update<br/>

After upgrading the dependencies, please make sure that the project runs normally.<br/>
After upgrading the dependencies, please make sure that the dependent versions in package.json are all accurate versions.<br/>

The following dependencies are currently unable to continue to be upgraded:<br/>
execa (Current project not support ES module)<br/>
get-port (Current project not support ES module)<br/>
react(The dependency of @mui/material not support the new version)<br/>
react-dom (The dependency of @mui/material not support the new version)<br/>
@types/react (The dependency of @mui/lab not support the new version)<br/>

## Notes - CSS In JS with style

Define componentized css, & represents the current class name.

    import { stylesheet } from 'typestyle';

    export default () => {
        return <div className={css.divStyle}></div>
    }

    const css = stylesheet({
        divStyle: {
            color: "green",
            $nest: {
                "&:hover": {
                    color: "yellow",
                },
                "& .modal .modal-body": {
                    color: "red",
                },
                "table tbody th&.blue-color:first-child": {
                    color: 'blue',
                }
            }
        }
    })}

## Notes - Define state with useLocalObservable

    import { useLocalObservable, observer } from 'mobx-react-use-autorun';

    export default observer(() => {

        const state = useLocalObservable(() => ({ randomNumber: 1 }));

        return <div onClick={() => state.randomNumber = Math.random()}>
            {state.randomNumber}
        </div>
    })

more usage:<br/>
表单验证<br/>

    import { Button, TextField } from '@mui/material';
    import { observer, useLocalObservable } from 'mobx-react-use-autorun';
    import { MessageService } from '../../common/MessageService';

    export default observer(() => {

        const state = useLocalObservable(() => ({
            name: "",
            submit: false,
            errors: {
                get name() {
                    return state.submit && !state.name && "请填写名称";
                },
                get hasError() {
                    return state.errors.name;
                }
            }
        }));

        const ok = async () => {
            state.submit = true;
            if (state.errors.hasError) {
                MessageService.error("错误");
            } else {
                MessageService.success("提交成功");
            }
        }

        return (<div className='flex flex-col' style={{ padding: "2em" }}>
            <TextField value={state.name} label="用户名" onChange={(e) => state.name = e.target.value} error={!!state.errors.name} helperText={state.errors.name} />
            <Button variant="contained" style={{ marginTop: "2em" }} onClick={ok} >提交</Button>
        </div>)
    })

## Notes - Using props and other hooks with useAsLocalSource

    import { observer, useAsLocalSource } from 'mobx-react-use-autorun';
    import { useLocation } from 'react-router-dom';

    export default observer((props: { name: string }) => {

        const source = useAsLocalSource({ location: useLocation(), ...props });

        return <div>
            {source.name}
            {source.location.pathname}
        </div>
    })

## Notes - Subscription property changes with useAutorun

    import { toJS, useLocalObservable, useAutorun, observer } from 'mobx-react-use-autorun';

    export default observer(() => {

        const state = useLocalObservable(() => ({ randomNumber: 1 }));

        useAutorun(() => {
            console.log(toJS(state))
        }, [state]);

        return <div onClick={() => state.randomNumber = Math.random()}>
            {state.randomNumber}
        </div>
    })

## Notes - Initialize loading network data with useMount

    import axios from "axios";
    import { useLocalObservable, observer } from "mobx-react-use-autorun";
    import { useMount } from "react-use";

    export default observer(() => {
        const state = useLocalObservable(() => ({
            ready: false,
            error: null as any,
            list: [],
        }))

        useMount(async () => {
            try {
                const list = (await axios.get("/")).data;
                state.ready = true;
            } catch (e) {
                state.error = e;
            }
        })

        return null;
    })

## Notes - Execute cleanup code when the component is unmounted with useUnmount

    import axios from "axios";
    import { useUnmount } from "react-use";

    export default observer(() => {

        useUnmount(async () => {
            console.log("unmounted)
        })

        return null;
    })


# Notes - Run a piece of code periodically with useInterval

    import { useLocalObservable, observer } from "mobx-react-use-autorun";
    import { useInterval } from "react-use";

    export default observer(() => {
        const state = useLocalObservable(() => ({
            age: 1,
        }))

        useInterval(() => {
            state.age++;
        }, 1000)

        return <div>
            {state.age}
        </div>;
    })

## Notes - Ignore frequent asynchronous calls with useAsyncExhaust

    import { TextField } from "@mui/material";
    import { useLocalObservable, observer, useAsyncExhaust } from 'mobx-react-use-autorun';
    import axios from 'axios';

    export default observer(() => {

        const state = useLocalObservable(() => ({
            list: [],
            searchText: ""
        }));

        const search = useAsyncExhaust(async () => {
            state.list = await axios.get("/search");
        });

        return (
            <TextField
                label="Please enter search content"
                variant="outlined"
                value={state.searchText}
                onChange={(e) => {
                    state.searchText = e.target.value;
                    search();
                }}
            />
        );
    })

## Notes - Define global mutable data

    import { observable } from 'mobx-react-use-autorun';

    const state = observable({});

## Notes - Define a delayed promise with timeout

    import { timeout } form 'mobx-react-use-autorun';

    async function(){
        await timeout(100);
        await timeout(new Date());
    }

## Notes - Get the real data of the proxy object with toJS

    import { observable, toJS } from 'mobx-react-use-autorun';

    const state = observable({});
    console.log(toJS(state));

## Learn More

1. React UI framework (https://reactjs.org)<br/>
2. Material UI Components (https://material-ui.com)<br/>
3. Material Icons (https://mui.com/zh/material-ui/material-icons)<br/>
4. Loading icon (https://mui.com/zh/material-ui/react-progress)<br/>
5. tailwindcss(https://tailwindcss.com)<br/>
6. Animate.css(https://animate.style)<br/>
7. UUID (https://www.npmjs.com/package/uuid)<br/>
8. typestyle (https://www.npmjs.com/package/typestyle)<br/>
9. linq (https://www.npmjs.com/package/linq)<br/>
10. date-fns (https://date-fns.org)
11. react-git-info (https://www.npmjs.com/package/react-git-info)
11. axios (https://axios-http.com/)
12. mobx-react-use-autorun (https://www.npmjs.com/package/mobx-react-use-autorun)