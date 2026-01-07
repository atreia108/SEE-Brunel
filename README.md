# Brunel@Simulation Exploration Experience (SEE)

This repository contains federates developed by the Brunel University of London Modelling and Simulation Group (MSG) for the [Simulation Exploration Experience](https://www.simulationexplorationexperience.org) (SEE) program.
The federates in the `main` branch are reworked versions (of the originals shown in SEE 2025) by Hridyanshu Aatreya that use HLA 4 and the [new SEE HLA Starter Kit](https://www.github.com/atreia108/SEE-HLA-Starter-Kit) for demonstration at the Winter Simulation Conference 2025 in Seattle, WA:
* **Spaceport**: A command center situated on the Lunar South Pole Aitken basin. It coordinates the arrival and departure of spacecraft, and is connected to a lunar cable car network developed by FACENS Brazil for transit on the lunar surface.
* **Lander**: A simple craft designed to land and take off from the lunar surface, inspired by the Orion spacecraft. It spawns at an arbitrary point and mediates arrival/departure to and from the spaceport.

## Building From Source

The project uses Maven as its build system. Most dependencies will be fetched automatically by your IDE **except** the Pitch pRTI libraries which are proprietary. This leaves you with one of two options:

1. Link the pRTI JARs (located in the pRTI installation directory) to your project. You should delete these dependencies from `pom.xml` if you decide to go with this option.
2. Generate these dependencies for your local Maven repository. The instructions to do this can be found [here](https://maven.apache.org/guides/mini/guide-3rd-party-jars-local.html).

These options also apply to the SEE HLA Starter Kit dependency as well. Either generate it as a local dependency or link a downloaded release version of the JAR (obtainable from the project's GitHub page).