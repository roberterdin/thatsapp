# Whatistics

## Back-End

The back-end is written in plain Java 8. The philosophy is that the data is the interface, i.e. the relevant data will be exposed with a RESTful API running on top of the database. MongoDB and RESTHeart are used for the persistence and exposure, respectively.

### RESTful API
RESTHeart is currently installed and started seperately. This way it is easier to distribute the application to multiple cluster nodes compared to having an embedded API.


## Front-End
This section outlines the details of collaborating on the front-end Ember application.

A short introduction of this app could easily go here.

### Prerequisites

You will need the following things properly installed on your computer.

* [Git](http://git-scm.com/)
* [Node.js](http://nodejs.org/) (with NPM) and [Bower](http://bower.io/)

### Installation

* `git clone <repository-url>` this repository
* change into the front-end folder of the newly directory
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