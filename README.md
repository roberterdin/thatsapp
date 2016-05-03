# Whatistics

## Back-End

The back-end is written in plain Java 8. The philosophy is that the data is the interface, i.e. the relevant data will be exposed with a RESTful API running on top of the database. [MongoDB](https://www.mongodb.org/) and [RESTHeart](http://restheart.org/) are used for the persistence and exposure, respectively.

### RESTful API
RESTHeart is embedded as a runtime dependency and spawned at startup. 

### Example data
In order to use the example data (e.g. for unit testing) you have to extract the password protected files in the resources directory.
```
mkdir chatHistories
gpg -d chatHistories.tar.gz.gpg | tar xzvf - -C chatHistories/
```
In case you need to add example data, compress files as follows:
```
tar czvpf - file1.txt file2.txt | gpg --symmetric --cipher-algo aes256 -o chatHistories.tar.gz.gpg
```
**NEVER EVER, UNDER ANY CIRCUMSTANCES, PUSH CHAT HISTORIES INTO THE REPOSITORY**

## Front-End
This section outlines the details of collaborating on the front-end Ember application.


### Prerequisites

You will need the following things properly installed on your computer.

* [Node.js](http://nodejs.org/) (with NPM) 
* [Bower](http://bower.io/)

### Installation

* `git clone <repository-url>` this repository
* navigate into the front-end folder of the newly directory
* `npm install`
* `bower install`

### Running / Development

* `ember server`
* Visit your app at http://localhost:4200.

#### Code Generators

Make use of the many generators for code, try `ember help generate` for more details

#### Running Tests

* `ember test`
* `ember test --server`

#### Building

* `ember build` (development)
* `ember build --environment production` (production)

#### Deploying

Specify what it takes to deploy your app.

## Further Reading / Useful Links

* ember: http://emberjs.com/
* ember-cli: http://www.ember-cli.com/
* Development Browser Extensions
  * [ember inspector for chrome](https://chrome.google.com/webstore/detail/ember-inspector/bmdblncegkenkacieihfhpjfppoconhi)
  * [ember inspector for firefox](https://addons.mozilla.org/en-US/firefox/addon/ember-inspector/)
