/*-
 * SPDX-License-Identifier: BSD-3-Clause
 * Copyright (c) 2026 Brunel University of London
 * All rights reserved.

 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright notice,
 *	  this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 * 	  contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED.  IN NO EVENT SHALL THE AUTHOR OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS
 * OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 *
 */

package uk.ac.brunel.lander.systems;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.core.SKBaseFederate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.ac.brunel.core.AbstractSimulationSystem;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.interactions.MSGLanderTakeoff;
import uk.ac.brunel.interactions.MSGLanderTouchdown;
import uk.ac.brunel.lander.Lander;
import uk.ac.brunel.types.SpaceTimeCoordinateState;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Used by a lander to manage movement and chart routes between a starting location and an assigned spaceport.
 *
 * @author Hridyanshu Aatreya
 */
public class NavigationSystem extends AbstractSimulationSystem {
    private static final Logger logger = LoggerFactory.getLogger(NavigationSystem.class);

    // The velocity of the lander expressed in m/s.
    // For a soft-moon landing, the lander should decelerate to less than 100 mph.
    private static final double VELOCITY = 50.0;
    private static final double DISTANCE_THRESHOLD = 85.0;

    private final AtomicBoolean transitInProgress;
    private final AtomicInteger operatingMode;
    private final Lander lander;
    private final Set<PhysicalEntity> spaceports;

    private Vector3D destinationWaypoint;
    private Vector3D positionDelta;
    private final SpaceportAllocationRequestSystem spaceportAllocationRequestSystem;
    private final SKBaseFederate federate;

    private String assignedSpaceportName;

    public NavigationSystem(Lander lander, Set<PhysicalEntity> spaceports, SpaceportAllocationRequestSystem spaceportAllocationRequestSystem, SKBaseFederate federate) {
        this.lander = lander;
        this.spaceports = spaceports;
        this.federate = federate;
        this.spaceportAllocationRequestSystem = spaceportAllocationRequestSystem;

        operatingMode = lander.getOperatingMode();
        transitInProgress = new AtomicBoolean(false);

        assignedSpaceportName = "";
    }

    @Override
    public void update() {
        if (transitInProgress.get()) {
            boolean changed = false;
            Vector3D landerPosition = lander.getState().getPosition();
            // Adjustments to the X-axis component.
            if (isCoordinateOutsideThreshold(landerPosition.getX(), destinationWaypoint.getX())) {
                Vector3D newPosition = lander.getState().getPosition().add(Vector3D.of(positionDelta.getX(), 0, 0));
                lander.getState().setPosition(newPosition);
                changed = true;
            }

            // Adjustments to the Y-axis component.
            if (isCoordinateOutsideThreshold(landerPosition.getY(), destinationWaypoint.getY())) {
                Vector3D newPosition = lander.getState().getPosition().add(Vector3D.of(0, positionDelta.getY(), 0));
                lander.getState().setPosition(newPosition);
                changed = true;
            }

            // Adjustments to the Z-axis component.
            if (isCoordinateOutsideThreshold(landerPosition.getZ(), destinationWaypoint.getZ())) {
                Vector3D newPosition = lander.getState().getPosition().add(Vector3D.of(0, 0, positionDelta.getZ()));
                lander.getState().setPosition(newPosition);
                changed = true;
            }

            logger.info("<{}> {} -> {}.", lander.getName(), lander.getState().getPosition(), destinationWaypoint);

            // The lander will never precisely reach its destination because that would require some serious precision.
            // So, if it fits within a specified threshold distance of the target point, we assume it has reached there.
            if (atDestination()) {
                lander.getState().setPosition(destinationWaypoint);
                lander.getState().setVelocity(Vector3D.of(0, 0, 0));

                logger.info("Lander <{}> has reached its destination {}.", lander.getName(), lander.getState().getPosition());
                changed = true;

                postExecutionTasks();
            }

            if (changed) {
                federate.updateObjectInstance(lander);
            }
        }
    }

    private void postExecutionTasks() {
        if (operatingMode.get() == 1) {
            logger.info("<{}> is departing from <{}>.", lander.getName(), assignedSpaceportName);

            assignedSpaceportName = "";
            updateLanderStatusAtRti("Approaching");
            spaceportAllocationRequestSystem.enable();
        } else {
            updateLanderStatusAtRti("Servicing");
            logger.info("<{}> has arrived at <{}>.", lander.getName(), assignedSpaceportName);
            onLanderTouchdown();
        }

        lander.flipOperatingMode();
        transitInProgress.set(false);
        disable();
    }

    private void onLanderTouchdown() {
        MSGLanderTouchdown landerTouchdown = new MSGLanderTouchdown(lander.getName(), assignedSpaceportName);
        dispatchInteraction(landerTouchdown);
    }

    private void dispatchInteraction(Object interaction) {
        try {
            federate.sendInteraction(interaction);
        } catch (FederateNotExecutionMember | RTIinternalError | InteractionParameterNotDefined | RestoreInProgress |
                 InteractionClassNotDefined | InteractionClassNotPublished | NotConnected | SaveInProgress e) {
            throw new IllegalStateException(e);
        }
    }

    public synchronized void spaceportAssigned(String spaceportName) {
        Vector3D spaceportPosition = getSpaceportPosition(spaceportName);

        if (!transitInProgress.get() && spaceportPosition != null) {
            chartRoute(spaceportPosition);
            logger.info("[ARRIVAL] <{}> goal set: Spaceport ({}) {}.", lander.getName(), spaceportName, spaceportPosition);

            assignedSpaceportName = spaceportName;
        }
    }

    public synchronized void depart() {
        if (!transitInProgress.get()) {
            Vector3D spawnPoint = lander.getSpawnPoint();
            chartRoute(spawnPoint);

            logger.info("[DEPARTURE] <{}> goal set: {} (Spawn Point).", lander.getName(), spawnPoint);

            MSGLanderTakeoff takeoffNotification = new MSGLanderTakeoff(lander.getName(), assignedSpaceportName);
            dispatchInteraction(takeoffNotification);

            enable();
        }
    }

    private void chartRoute(Vector3D waypoint) {
        destinationWaypoint = waypoint;

        SpaceTimeCoordinateState landerState = lander.getState();
        Vector3D landerPosition = landerState.getPosition();
        int xPolarity = axisPolarity(destinationWaypoint.getX(), landerPosition.getX());
        int yPolarity = axisPolarity(destinationWaypoint.getY(), landerPosition.getY());
        int zPolarity = axisPolarity(destinationWaypoint.getZ(), landerPosition.getZ());

        double xWaypointDiff = destinationWaypoint.getX() - landerPosition.getX();
        double yWaypointDiff = destinationWaypoint.getY() - landerPosition.getY();
        double theta = Math.atan2(yWaypointDiff, xWaypointDiff);

        double deltaVXAxis = xPolarity * VELOCITY;
        double deltaVYAxis = yPolarity * VELOCITY;
        double deltaVZAxis = zPolarity * VELOCITY;
        landerState.setVelocity(Vector3D.of(deltaVXAxis, deltaVYAxis, deltaVZAxis));

        Vector3D accel = lander.getAcceleration();
        double deltaSXAxis = deltaVXAxis * Math.abs(Math.cos(theta));
        double deltaSYAxis = deltaVYAxis * Math.abs(Math.sin(theta));
        double deltaSZAxis = deltaVZAxis + (0.5 * accel.getZ());
        positionDelta = Vector3D.of(deltaSXAxis, deltaSYAxis, deltaSZAxis);

        transitInProgress.set(true);
    }

    private boolean isCoordinateOutsideThreshold(double a, double b) {
        return Math.abs(a - b) >= DISTANCE_THRESHOLD;
    }

    private boolean atDestination() {
        Vector3D landerPosition = lander.getState().getPosition();
        return (!isCoordinateOutsideThreshold(landerPosition.getX(), destinationWaypoint.getX())
                        && !isCoordinateOutsideThreshold(landerPosition.getY(), destinationWaypoint.getY())
                        && !isCoordinateOutsideThreshold(landerPosition.getZ(), destinationWaypoint.getZ()));
    }

    private int axisPolarity(double n1, double n2) {
        return (n1 > n2) ? 1 : -1;
    }

    private Vector3D getSpaceportPosition(String spaceportName) {
        for (PhysicalEntity spaceport : spaceports) {
            if (spaceport.getName().equals(spaceportName)) {
                return spaceport.getState().getPosition();
            }
        }

        return null;
    }

    private void updateLanderStatusAtRti(String status) {
        lander.setStatus(status);
        federate.updateObjectInstance(lander);
    }
}
