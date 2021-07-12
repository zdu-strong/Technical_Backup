This project is to do some knowledge backup. The following is the knowledge recorded in each catalog. If you have any questions, please contact zdu.strong@gmail.com.

### `Environmental description`

This repository uses git as its version management tool..

1. From https://git-scm.com/ install git.
2. Set Git user name and email:<br/>

        git config --global user.name "your username"
        git config --global user.email "your email"

3. SSH Key generation:<br/>

        ssh-keygen -t rsa -C "your email"

4. Log in to github. Open setting -> SSH keys, click New SSH key in the upper right corner, put the generated public key id_rsa.pub into the key input box, and assign a title to the current key to distinguish each key.
5. Use git gui to clone the source code.

### `springboot`

Use springboot, mysql to build back-end projects.

### `react`

Use React, typescript, html, css to build front-end projects.

### `docker`

Use docker, centos to build docker images of back-end projects and front-end projects.

### `electron`

Use electron, react, typescript, html, css, nodejs to build desktop programs for mac, linux, windows.

### `capacitor`

Use capacitor, react, typescript, html, css to build ios apps and android apps.

## Notes - Easier to use git

Sometimes, you'll see a prompt like this: This repository currently has approximately 1500 loose objects.<br/>
The warning dialog can be disabled with the following command.<br/>

    git config --global gui.gcwarning false