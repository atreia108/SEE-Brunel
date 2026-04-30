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

package uk.ac.brunel.spaceport;

import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.see.skf.annotations.ObjectClass;
import org.see.skf.core.SKBaseFederate;
import uk.ac.brunel.exceptions.IncompleteObjectDataException;
import uk.ac.brunel.spaceport.listeners.*;
import uk.ac.brunel.spaceport.systems.CargoTransferSystem;
import uk.ac.brunel.spaceport.systems.PowerSystem;
import uk.ac.brunel.spaceport.systems.VehicleAssignmentRequestSystem;
import uk.ac.brunel.types.SpaceTimeCoordinateState;
import uk.ac.brunel.core.PhysicalEntity;
import uk.ac.brunel.core.SimulationEntity;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Simulated model of a Lunar Spaceport.
 *
 * @author Hridyanshu Aatreya
 */
@ObjectClass(name = "HLAobjectRoot.PhysicalEntity")
public class Spaceport extends PhysicalEntity implements SimulationEntity, Powerable {
    public static final String NAME_SEQUENCE = "brunel_spaceport_";
    private static final double LUNAR_GRAVITATIONAL_PULL = -1.625;

    // Power load of the spaceport that is incurred during its operational stages in kilowatts (kW).
    private static final double POWER_RATING = 0.092;

    private static final int LOW_PRIORITY_POWER_REQUEST  = 5;
    private static final int HIGH_PRIORITY_POWER_REQUEST = 0;

    private final SKBaseFederate federate;
    private final SpaceportArm arm;
    private final AtomicInteger operatingMode;

    private final LanderLiaison landerLiaison;
    private final PowerSystem powerSystem;
    private final CargoTransferSystem cargoTransferSystem;
    private final VehicleAssignmentRequestSystem vehicleAssignmentRequestSystem;

    private Spaceport(Builder builder) {
        federate = builder.federate;
        setName(builder.name);
        setType("Spaceport");
        setStatus("Available");
        setParentReferenceFrame(builder.parentReferenceFrame);
        setState(builder.state);
        setAcceleration(Vector3D.of(0, 0, LUNAR_GRAVITATIONAL_PULL));

        operatingMode = new AtomicInteger(0);
        landerLiaison = new LanderLiaison(this, federate);

        powerSystem = new PowerSystem(builder.name, this, federate);
        arm = new SpaceportArm(builder.armName, powerSystem);

        cargoTransferSystem = new CargoTransferSystem(this, arm, federate);
        vehicleAssignmentRequestSystem = new VehicleAssignmentRequestSystem(this, getState().getPosition(), federate);

        createEventListeners();
    }

    private void createEventListeners() {
        federate.addInteractionListener(new LandingRequestListener(landerLiaison));
        federate.addInteractionListener(new LanderTouchdownListener(landerLiaison, vehicleAssignmentRequestSystem));
        federate.addInteractionListener(new PowerAllocationListener(powerSystem));
        federate.addInteractionListener(new CargoTransferReadyListener(vehicleAssignmentRequestSystem, cargoTransferSystem));
        federate.addInteractionListener(new CargoPickupJobAcceptedListener(vehicleAssignmentRequestSystem, cargoTransferSystem));
    }

    @Override
    public void update() {
        powerSystem.consume(0.5 * POWER_RATING);

        vehicleAssignmentRequestSystem.exec();
        cargoTransferSystem.exec();
        arm.update();

        powerSystem.exec();
    }

    @Override
    public double powerConsumption() {
        return cargoTransferSystem.isEnabled()
                ? POWER_RATING + SpaceportArm.PEAK_POWER_RATING
                : POWER_RATING + SpaceportArm.IDLE_POWER_RATING;
    }

    @Override
    public int powerPriorityLevel() {
        return cargoTransferSystem.isEnabled()
                ? HIGH_PRIORITY_POWER_REQUEST
                : LOW_PRIORITY_POWER_REQUEST;
    }

    public static class Builder {
        private SKBaseFederate federate;
        private String name;
        private String parentReferenceFrame;
        private SpaceTimeCoordinateState state;
        private String armName;

        public Builder federate(SpaceportFederate federate) {
            this.federate = federate;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder parentReferenceFrame(String parentReferenceFrame) {
            this.parentReferenceFrame = parentReferenceFrame;
            return this;
        }

        public Builder spawnPoint(SpaceTimeCoordinateState state) {
            this.state = state;
            return this;
        }

        public Builder arm(String name) {
            this.armName = name;
            return this;
        }

        private void validate() {
            if (federate == null) {
                throw new IncompleteObjectDataException("Missing <federate> field for Spaceport object.");
            }

            if (name == null) {
                throw new IncompleteObjectDataException("Missing <name> field for Spaceport object.");
            }

            if (parentReferenceFrame == null) {
                throw new IncompleteObjectDataException("Missing <parentReferenceFrame> field for Spaceport object.");
            }

            if (state == null) {
                throw new IncompleteObjectDataException("Missing <spawnPoint> field for Spaceport object.");
            }

            if (armName == null) {
                throw new IncompleteObjectDataException("Missing <armName> field for Spaceport object.");
            }
        }

        public Spaceport build() {
            validate();
            return new Spaceport(this);
        }
    }

    public LanderLiaison getLanderLiaison() {
        return landerLiaison;
    }

    public VehicleAssignmentRequestSystem getVehicleAssignmentRequestSystem() {
        return vehicleAssignmentRequestSystem;
    }

    public SpaceportArm getArm() {
        return arm;
    }

    public AtomicInteger getOperatingMode() {
        return operatingMode;
    }

    public void flipOperatingMode() {
        operatingMode.set( (operatingMode.get() == 0) ? 1 : 0 );
    }
}
