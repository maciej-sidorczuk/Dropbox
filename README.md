This program downloads folder content from Dropbox. It is useful especially in server tasks (cronjobs) on systems where there is no dropbox app e.g. Raspian Jessie.
Program works in linux cli and windows cmd.

After you clone project folder from git repo to eclipse's workspace:
1. Run eclipse. Click File and then Open Projects from File System...
2. Click Directory... and find your project folder. Select project and click Finish button.
There is problem with libraries so after you import project, click right on project and go to Build Path -> Configure Build Path...
Go to Libraries Tab and remove three jar files then click on "Add External JARs..." and add these three jar files from lib folder.

To run program you need to run it with one argument: path to folder on your system where program will download content from dropbox.
Program will ask for access token so you have to log in to your dropbox account and get it. You will also set folder which will be downloaded.