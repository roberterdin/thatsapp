# README #

### Get Whatistics ###
```
#!basic

git clone https://[bitbucketusername]@bitbucket.org/roberterdin/whatistics.git
```
Navigate to `/src/front-end/`
```
#!basic
npm install
```
Installs project build dependencies to `node_modules`

### Build Whatistics ###
Navigate to `/src/front-end/`
```
#!basic
grunt

```
Builds to `/bin/dev/front-end`


### Deploy Whatistics ###

```
#!basic
grunt prod
```
Builds to `/bin/prod/front-end`

```
#!basic
grunt deploy-dev
```
Deploys to test-installation