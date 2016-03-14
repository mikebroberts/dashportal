# dashportal

Dashportal is a small application to display rotating / refreshing 'dashboard'
pages that might typically be displayed on 'information radiator' displays within
technical teams. Dashportal does not help you create those pages - it is merely
an app to rotate and/or refresh them. The only exception to this is that it can
serve static 'full screen' images - think a virtual poster displayer.

Dashportal only works with web pages that support being placed in an iframe.
Certain sites / apps don't allow this (using the `x-frame-options` response header.)
google.com is one if you want to see what happens when you try it.

## Warning!

If you used Dashportal prior to March 14 2016 you will want to upgrade your config
file format - some breaking changes were made on that date.

## Running

At present the best way to use this app is to fork, or download, the repository,
and then to edit the `project.clj` file. (If a few people actually like this app I'd
be happy to make this step a little more traditional.)

Within that file you'll see configuration within the `:env` section.
Follow the documentation / examples in that file and you should be good to go.

Once the file is setup you can test it out!

To run a development dashportal from a terminal run `bin/lein ring server`. Wait
a few seconds and after starting the app it should launch your default browser
showing the list of dashboards available. Click on one to see what happens 
(I recommend 'kittens' and waiting 20 seconds)

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

## Usage

Beyond the basic rotation of pages there's a few things worth knowing:

* You can add any image file to `/public/full-screen-images/[site]`, and this will be served
as part of `[site]`'s rotation set (at `/full-screen-images/[site]/[filename]`)
* You can have the root location (`/`) serve either a dashboard, or a list of dashboards available. See
the docs in project.clj for more. You can always see the dashboard list at `/list-dashboards`
* You can also send 'flash' messages / pages to any (or all) dashboards, which will override the regular rotation
for a brief period of time. Flash messages are great for alerts, shoutouts, etc.! To do this, POST a request
to `/api/flashes` with the following parameters:
  * dashboards (required) - set to the dashboard name (e.g. `kittens`) or `ALL` to flash to all dashboards
  * message (required, unless you specify url) - The message to display, e.g. `Hello World!`
  * url (required, unless you specify message) - The URL to show. Must follow the usual 'framable' rules
  * seconds (optional) - how many seconds to show the flash. Maxes out at 60 seconds (unless you change the source code)
  * alert-level (optional) - `regular` or `warning` - used to change the style of the flash page. May well be extended
  in future

Here's an example of a flash message: ![flash message](demo-photos/IMG_1775.jpg)

I'd welcome feedback in Github, or on twitter @mikebroberts
