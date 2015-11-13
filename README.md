# dashportal

Dashportal is a small application to display rotating / refreshing 'dashboard'
pages that might typically be displayed on 'information radiator' displays within
technical teams. Dashportal does not help you create those pages - it is merely
an app to rotate and/or refresh them.

Dashportal only works with web pages that support being placed in an iframe.
Certain sites / apps don't allow this (using the `x-frame-options` response header.)
google.com is one if you want to see what happens when you try it.

## Usage

At present the best way to use this app is to fork, or download, the repository,
and then to edit the `project.clj` file. (If a few people actually like this app I'd
be happy to make this step a little more traditional.)

Within that file you'll see 2 configuration keys within the `:env` section.
Follow the documentation in that file and you should be good to go.

Once the file is setup you can test it out!

To run a development dashportal from a terminal run `bin/lein ring server`. Wait
a few seconds and after starting the app it should launch your default browser
pointing to your default site.

To run in production you have a few options:
* Run `bin/lein with-profile production trampoline run -m dashportal.handler`. By
default the app will run on port 3001, but you can change that by setting a `PORT`
environment variable.
* If running on Heroku just push the source to a new Heroku app - the included
`Procfile` file should do all you need
* If running on Amazon Elastic Beanstalk create a generic 'Single Container' docker 
application for the application artifact run the following command, and use the 
resulting zip file: `git archive --format=zip HEAD > dashportal.zip`
* If you're more Clojure savvy you can compile an Uberjar or Uberwar and use that.

I'd welcome feedback in Github, or on twitter @mikebroberts
