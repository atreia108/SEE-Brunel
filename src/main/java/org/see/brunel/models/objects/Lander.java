/*-
 * Copyright (c) 2026 Hridyanshu Aatreya
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in the
 *    documentation and/or other materials provided with the distribution.
 * 3. Neither the name of the copyright holder nor the names of its
 *    contributors may be used to endorse or promote products derived from
 *    this software without specific prior written permission.
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

package org.see.brunel.models.objects;

import hla.rti1516_2025.exceptions.*;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.core.Precision;
import org.see.brunel.models.interactions.FederateMessage;
import org.see.brunel.types.SpaceTimeCoordinateState;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.core.SKFederateInterface;

import java.util.Random;

import static org.see.brunel.utils.Cloner.cloneVec3D;

@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class Lander extends PhysicalEntity {
    private static final Vector3D POINT_CHARLIE = Vector3D.of(-500.0, -200.0, -5200.0);
    private static final Vector3D POINT_FOXTROT = Vector3D.of(600.0, 500.0, -4900.0);
    private static final Vector3D POINT_ROMEO = Vector3D.of(400.0, -100.0, -5000.0);

    private static final double MAX_SPEED = 20.0;
    private static final double ALT_ADJUSTMENT_SPEED = 10.0;
    private static final  double MIN_Z_ALTITUDE = -5387.0;
    private static final Precision.DoubleEquivalence PRECISION_THRESHOLD = Precision.doubleEquivalenceOfEpsilon(1e-4);

    private final Random rand;
    private Vector3D waypoint;
    private MissionStage missionStage;
    private final SKFederateInterface federate;

    // Beware, this movement algorithm is very choppy.
    // It adds and subtracts the lander's current position from the target position until it's "at" the target position.
    // Needs some serious improvement in future SEE events.
    public void move() {
        if (missionStage == MissionStage.ARRIVAL) {
            forward();
        } else if (missionStage == MissionStage.DEPARTURE) {
            reverse();
        } else {
            return;
        }

        federate.updateObjectInstance(this);
    }

    private void forward() {
        SpaceTimeCoordinateState state = getState();
        Vector3D position = state.getPosition();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        if (position.eq(waypoint, PRECISION_THRESHOLD) && getMissionStage() != MissionStage.LANDED) {
            setMissionStage(MissionStage.LANDED);
            setStatus("Landed");
            announceArrival();
            waypoint = null;
            return;
        }

        double targetX = waypoint.getX();
        double targetY = waypoint.getY();
        double targetZ = waypoint.getZ();

        // First complete adjusting course on the X and Y axes.
        if (!horizontalAdjustmentComplete()) {
            Vector3D deltaV;
            double polarityXAxis = 1;

            if (targetX < x) {
                polarityXAxis = -1;
            }

            deltaV = position.add(Vector3D.of(polarityXAxis * MAX_SPEED, 0, 0));
            state.setPosition(deltaV);

            // We're having to re-reference everytime because it's memory location continually changes in this scope.
            // Otherwise, we'd be stuck using an older position value.
            position = state.getPosition();
            double polarityYAxis = 1;

            if (targetY < y) {
                polarityYAxis = -1;
            }

            deltaV = position.add(Vector3D.of(0, polarityYAxis * MAX_SPEED, 0));
            state.setPosition(deltaV);

            if (z > MIN_Z_ALTITUDE) {
                position = state.getPosition();
                deltaV = position.add(Vector3D.of(0, 0, -MAX_SPEED));
                state.setPosition(deltaV);
            }
        } else if (!verticalAdjustmentComplete()) {
            // Then jump into altitude adjustment on the Z axis.
            position = state.getPosition();
            double polarityZAxis = 1;

            if (targetZ < z) {
                polarityZAxis = -1;
            }

            Vector3D deltaV = position.add(Vector3D.of(0,0, polarityZAxis * ALT_ADJUSTMENT_SPEED));
            state.setPosition(deltaV);
        }
    }

    private void reverse() {
        SpaceTimeCoordinateState state = getState();
        Vector3D position = state.getPosition();
        double x = position.getX();
        double y = position.getY();
        double z = position.getZ();

        if (position.eq(waypoint, PRECISION_THRESHOLD) && getMissionStage() != MissionStage.AWAITING_SCHEDULER_PROCESSING) {
            setMissionStage(MissionStage.AWAITING_SCHEDULER_PROCESSING);
            setStatus("Holding");
            waypoint = null;
            return;
        }

        double targetX = waypoint.getX();
        double targetY = waypoint.getY();
        double targetZ = waypoint.getZ();

        // Adjust course on the Z axis, we've got to ascend first to the established minimum altitude before any other
        // action.
        if (z < MIN_Z_ALTITUDE) {
            Vector3D deltaV = position.add(Vector3D.of(0, 0, ALT_ADJUSTMENT_SPEED));
            state.setPosition(deltaV);

            position = state.getPosition();
            if (position.getZ() >= MIN_Z_ALTITUDE) {
                announceDeparture();
            }

            return;
        }

        if (!horizontalAdjustmentComplete()) {
            Vector3D deltaV;
            double polarityXAxis = 1;

            if (targetX < x) {
                polarityXAxis = -1;
            }

            deltaV = position.add(Vector3D.of(polarityXAxis * MAX_SPEED, 0, 0));
            state.setPosition(deltaV);

            position = state.getPosition();
            double polarityYAxis = 1;

            if (targetY < y) {
                polarityYAxis = -1;
            }

            deltaV = position.add(Vector3D.of(0, polarityYAxis * MAX_SPEED, 0));
            state.setPosition(deltaV);
        }

        if (!verticalAdjustmentComplete()) {
            position = state.getPosition();
            double polarityZAxis = 1;

            if (targetZ < z) {
                polarityZAxis = -1;
            }

            Vector3D deltaV = position.add(Vector3D.of(0,0, polarityZAxis * ALT_ADJUSTMENT_SPEED));
            state.setPosition(deltaV);
        }
    }

    private boolean horizontalAdjustmentComplete() {
        SpaceTimeCoordinateState state = getState();
        Vector3D position = state.getPosition();

        double x = position.getX();
        double targetX = waypoint.getX();
        double y = position.getY();
        double targetY = waypoint.getY();

        boolean withinXAxisRange = Math.abs((targetX - x)) < 15.0;
        boolean withinYAxisRange = Math.abs((targetY - y)) < 15.0;
        if (withinXAxisRange && withinYAxisRange) {
            state.setPosition(Vector3D.of(targetX, targetY, position.getZ()));
        }

        return withinXAxisRange && withinYAxisRange;
    }

    private boolean verticalAdjustmentComplete() {
        SpaceTimeCoordinateState state = getState();
        Vector3D position = state.getPosition();

        double z = position.getZ();
        double targetZ = waypoint.getZ();

        boolean withinZAxisRange = Math.abs((targetZ - z)) < 15.0;
        if (withinZAxisRange) {
            state.setPosition(Vector3D.of(position.getX(), position.getY(), targetZ));
        }

        return withinZAxisRange;
    }

    private void announceDeparture() {
        FederateMessage message = new FederateMessage(
                getName(),
                "Spaceport",
                "BRUNEL_LANDER_SPACEPORT_DEPARTURE_COMPLETED",
                "Departing from spaceport. Goodbye!"
        );

        try {
            federate.sendInteraction(message);
        } catch (FederateNotExecutionMember | SaveInProgress | RTIinternalError | NotConnected |
                 InteractionClassNotPublished | InteractionClassNotDefined | RestoreInProgress |
                 InteractionParameterNotDefined e) {
            throw new IllegalStateException("Error encountered while trying to send interaction.", e);
        }
    }

    private void announceArrival() {
        FederateMessage message = new FederateMessage(
                getName(),
                "Spaceport",
                "BRUNEL_LANDER_SPACEPORT_TOUCHDOWN",
                "Landed at spaceport!"
        );

        try {
            federate.sendInteraction(message);
        } catch (FederateNotExecutionMember | SaveInProgress | RTIinternalError | NotConnected |
                 InteractionClassNotPublished | InteractionClassNotDefined | RestoreInProgress |
                 InteractionParameterNotDefined e) {
            throw new IllegalStateException("Error encountered while trying to send interaction.", e);
        }
    }

    public Vector3D getWaypoint() {
        return waypoint;
    }

    public MissionStage getMissionStage() {
        return missionStage;
    }

    public void setMissionStage(MissionStage missionStage) {
        this.missionStage = missionStage;

        if (missionStage ==  MissionStage.DEPARTURE) {
            waypoint = randomDestination();
        }
    }

    public void setWaypoint(Vector3D waypoint) {
        this.waypoint = waypoint;
    }

    private Vector3D randomDestination() {
        int num = rand.nextInt(3);
        if (num == 1) {
            return cloneVec3D(POINT_FOXTROT);
        } else if (num == 2) {
            return cloneVec3D(POINT_ROMEO);
        } else {
            return cloneVec3D(POINT_CHARLIE);
        }
    }

    private Lander(String name, String type, String status, String parentReferenceFrame, SKFederateInterface federate) {
        rand = new Random();
        setName(name);
        setType(type);
        setStatus(status);
        setParentReferenceFrame(parentReferenceFrame);
        this.federate = federate;

        // The lander spawns in one of the three points - CHARLIE, FOXTROT, or ROMEO.
        getState().setPosition(randomDestination());
        missionStage = MissionStage.AWAITING_SCHEDULER_PROCESSING;
    }

    public static LanderBuilder builder() {
        return new LanderBuilder();
    }

    public static class LanderBuilder {
        private String name;
        private String type;
        private String status;
        private String parentReferenceFrame;
        private SKFederateInterface federate;

        private LanderBuilder() {}

        public LanderBuilder withName(String name) {
            if (name == null) {
                throw new IllegalArgumentException("Name field for Lander object cannot be null");
            }

            this.name = name;
            return this;
        }

        public LanderBuilder withType(String type) {
            if (type == null) {
                throw new IllegalArgumentException("Type field for Lander object cannot be null");
            }

            this.type = type;
            return this;
        }

        public LanderBuilder withStatus(String status) {
            if (status == null) {
                throw new IllegalArgumentException("Status field for Lander object cannot be null");
            }

            this.status = status;
            return this;
        }

        public LanderBuilder withParentReferenceFrame(String parentReferenceFrame) {
            if (parentReferenceFrame == null) {
                throw new IllegalArgumentException("Parent reference frame field for Lander object cannot be null");
            }

            this.parentReferenceFrame = parentReferenceFrame;
            return this;
        }

        public LanderBuilder withFederate(SKFederateInterface federate) {
            if (federate == null) {
                throw new IllegalArgumentException("Federate field for Lander object cannot be null");
            }

            this.federate = federate;
            return this;
        }

        private void validate() {
            if (name == null || type == null || status == null || parentReferenceFrame == null || federate == null) {
                throw new IllegalArgumentException("Missing one more required arguments for constructing the lander object.");
            }
        }

        public Lander build() {
            validate();
            return new Lander(name, type, status, parentReferenceFrame, federate);
        }
    }

    public enum MissionStage {
        AWAITING_SPACEPORT_ACK,
        AWAITING_SCHEDULER_PROCESSING,
        ARRIVAL,
        DEPARTURE,
        LANDED
    }
}
