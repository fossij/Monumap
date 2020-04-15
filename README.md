# Monumap

## Introduction
GPS Navigation has become a staple in the 21st century, with many users being reliant on it for getting where they
need to be. While GPS based navigation services work great outdoors, the same directional network has not been
holistically implemented indoors. This poses a problem to many individuals who are new to a building, like new students trying to find their classes, people interviewing for a job, or even trying to find loved ones in a hospital. Another group that suffers from this issue are the visually impaired. Monumap fixes this issue, providing an app, specifically for Android, that will allow users to find their way from one keypoint to another indoors. Defining a start and end point, users will be given step-by-step directions on how to get from the starting point to the end point, including directions for each step and distances between both the points and the
different steps.

## Features
The features of the app include:

1. Enter a start location.
2. Enter a destination location. 
3. Query the pathfinder for instructions of the specified route.
4. Cancel the route.
5. Navigate through instructions.
6. Complete the route.
7. No route/invalid entries.
8. Change settings.
9. View recent routes.

Other features include wheelchair accessible routes, text-to-speech instructions, and a C# map-parsing app.

## Getting Started

### Installation and Setup
To install and setup Monumap, you must follow these steps:
1. First, install AndroidStudio if you do not already have it downloaded: [AndroidStudio Download](https://developer.android.com/studio)
   - The specifications are not important during download and can be set to whatever you like.
2. Once AndroidStudio has been downloaded, launch it. This will open the home/welcome page.
3. Click on **Check out project from Version Control > Git** in the right-hand panel.
4. In the popup window, paste the Monumap project repository URL, select the directory you'd like to save the project in, and click the **Clone** button.
   - The Monumap project repository URL can be copied from the project's home page on GitHub: Click on the green **Clone or download** button, specify to clone with HTTPS, and copy the link provided in the tab.
5. At this point, the project should load and open in the AndroidStudio editor. You have now cloned and setup the app as needed.


### Run
To run the app:
1. First create an Android virtual device (AVD) on which to run the app if you have not already done so. Click on **Tools > AVD Manager** in the top panel of the window. If you already have the emulator setup, skip this step and go to step 7.
2. In the bottom left, click on **Create Virtual Device...**
3. Ensuring you are in the **Phone** category on the left side of the window, scroll down and select the **Nexus 5X** device and click **Next** in the bottom right. 
4. Select the default system image with release name **Pie** (API Level 28), and click the **Next** button in the bottom right.
5. Change the AVD name if you'd like. Otherwise, click the **Finish** button in the bottom right. Your AVD device is now created and set to be run.
6. In the main AndroidStudio window, click on the dropdown to select your device. The dropdown is on the top panel next to the play button, in the center/right side of the panel.
7. Once you have selected your device from the dropdown, press the play button to the right of the dropdown to run the app. If you have just created your emulator, it may take some time to boot up but let it boot properly. The Monumap app will now open and can be used!

## Demo video
Demo video link: 

## Contributors
- Jose Fossi, UI & Backend
- Eric Scherfling, Map-Parsing
- Christian Arce, Bluetooth
- Cedric Kerley, Pathfinding
